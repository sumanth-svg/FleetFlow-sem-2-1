package com.fleetflow.optimization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Bitmask Dynamic Programming for small-scale vehicle delivery route optimization (Travelling Salesperson Problem).
 * Time Complexity: O(2^N * N^2).
 * Space Complexity: O(2^N * N).
 * Used in FleetFlow to find the optimal delivery sequence for a fleet vehicle visiting a set of drop-off points.
 */
public class RouteBitmaskDP {

    public static class RouteResult {
        private final List<Integer> stopIndices;
        private final List<String> stopNames;
        private final double totalDistanceKm;
        private final double estimatedFuelLiters;

        public RouteResult(List<Integer> stopIndices, List<String> stopNames, double totalDistanceKm, double estimatedFuelLiters) {
            this.stopIndices = stopIndices;
            this.stopNames = stopNames;
            this.totalDistanceKm = totalDistanceKm;
            this.estimatedFuelLiters = estimatedFuelLiters;
        }

        public List<Integer> getStopIndices() { return stopIndices; }
        public List<String> getStopNames() { return stopNames; }
        public double getTotalDistanceKm() { return totalDistanceKm; }
        public double getEstimatedFuelLiters() { return estimatedFuelLiters; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Optimal Route (Dist: %.2f km, Fuel: %.2f L):\n", totalDistanceKm, estimatedFuelLiters));
            for (int i = 0; i < stopNames.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(stopNames.get(i));
                if (i < stopNames.size() - 1) sb.append(" ->\n");
            }
            return sb.toString();
        }
    }

    /**
     * Solves the optimal round-trip delivery tour starting from depot (index 0), visiting all stops, and returning to depot.
     * @param names List of stop names (index 0 is depot)
     * @param dist Distance matrix dist[i][j] in km
     */
    public static RouteResult optimizeRoute(List<String> names, double[][] dist) {
        int n = names.size();
        if (n <= 1) {
            return new RouteResult(Arrays.asList(0), names, 0.0, 0.0);
        }

        int totalStates = 1 << n;
        double[][] dp = new double[totalStates][n];
        int[][] parent = new int[totalStates][n];

        for (int i = 0; i < totalStates; i++) {
            Arrays.fill(dp[i], Double.POSITIVE_INFINITY);
            Arrays.fill(parent[i], -1);
        }

        // Base case: starting at depot (node 0) with mask 1
        dp[1][0] = 0.0;

        for (int mask = 1; mask < totalStates; mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0 || dp[mask][u] == Double.POSITIVE_INFINITY) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0) { // v is not visited yet
                        int nextMask = mask | (1 << v);
                        double newCost = dp[mask][u] + dist[u][v];
                        if (newCost < dp[nextMask][v]) {
                            dp[nextMask][v] = newCost;
                            parent[nextMask][v] = u;
                        }
                    }
                }
            }
        }

        // Complete the tour back to depot (node 0)
        int finalMask = totalStates - 1;
        double minTotalDist = Double.POSITIVE_INFINITY;
        int lastNode = -1;

        for (int u = 1; u < n; u++) {
            if (dp[finalMask][u] != Double.POSITIVE_INFINITY) {
                double tourCost = dp[finalMask][u] + dist[u][0];
                if (tourCost < minTotalDist) {
                    minTotalDist = tourCost;
                    lastNode = u;
                }
            }
        }

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        path.add(0); // return to depot

        int currMask = finalMask;
        int currNode = lastNode;

        while (currNode != -1) {
            path.add(0, currNode);
            int prevNode = parent[currMask][currNode];
            currMask ^= (1 << currNode);
            currNode = prevNode;
        }

        List<String> routeNames = new ArrayList<>();
        for (int idx : path) {
            routeNames.add(names.get(idx));
        }

        // Assume average delivery vehicle fuel consumption of 0.28 L/km
        double estimatedFuel = minTotalDist * 0.28;

        return new RouteResult(path, routeNames, minTotalDist, estimatedFuel);
    }

    /**
     * Helper to compute Euclidean or Great-Circle distance matrix from latitudes and longitudes.
     */
    public static double[][] computeDistanceMatrix(double[] lats, double[] lons) {
        int n = lats.length;
        double[][] dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else {
                    dist[i][j] = haversineKm(lats[i], lons[i], lats[j], lons[j]);
                }
            }
        }
        return dist;
    }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
