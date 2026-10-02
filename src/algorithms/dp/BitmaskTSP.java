package algorithms.dp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Bitmask Dynamic Programming - Travelling Salesperson Problem (Held-Karp Algorithm).
 * 
 * Computes the optimal (minimum cost / shortest distance) route that visits all N logistics hubs
 * exactly once and returns to the origin hub.
 * 
 * Subproblem State Representation:
 * DP(mask, u) = minimum distance to visit all remaining unvisited hubs represented by bitmask 'mask',
 * ending at current hub 'u'.
 * 
 * Time Complexity: O(N^2 * 2^N) where N is number of hubs (vs O(N!) brute force).
 * Space Complexity: O(N * 2^N) state table.
 */
public class BitmaskTSP {

    private static final int INF = 1_000_000_000;

    public static class TSPResult {
        private final double minDistance;
        private final List<Integer> routeIndices;
        private final List<String> routeNames;
        private final double[][] distanceMatrix;

        public TSPResult(double minDistance, List<Integer> routeIndices, List<String> routeNames, double[][] distanceMatrix) {
            this.minDistance = minDistance;
            this.routeIndices = routeIndices;
            this.routeNames = routeNames;
            this.distanceMatrix = distanceMatrix;
        }

        public double getMinDistance() {
            return minDistance;
        }

        public List<Integer> getRouteIndices() {
            return routeIndices;
        }

        public List<String> getRouteNames() {
            return routeNames;
        }

        public double[][] getDistanceMatrix() {
            return distanceMatrix;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Optimal TSP Delivery Tour Distance: %.2f km/units\n", minDistance));
            sb.append("Optimal Hub Sequence: ");
            for (int i = 0; i < routeNames.size(); i++) {
                sb.append(routeNames.get(i));
                if (i < routeNames.size() - 1) sb.append(" -> ");
            }
            return sb.toString();
        }
    }

    /**
     * Solves TSP for a distance matrix using Bitmask Dynamic Programming.
     *
     * @param dist     N x N distance matrix between hubs
     * @param hubNames Optional descriptive names of hubs (length N)
     * @return TSPResult object
     */
    public static TSPResult solveTSP(double[][] dist, String[] hubNames) {
        if (dist == null || dist.length == 0 || dist.length != dist[0].length) {
            return new TSPResult(0, new ArrayList<>(), new ArrayList<>(), new double[0][0]);
        }

        int n = dist.length;
        if (n == 1) {
            List<Integer> singleIdx = Arrays.asList(0, 0);
            List<String> singleName = Arrays.asList(
                    hubNames != null && hubNames.length > 0 ? hubNames[0] : "Hub-0",
                    hubNames != null && hubNames.length > 0 ? hubNames[0] : "Hub-0"
            );
            return new TSPResult(0, singleIdx, singleName, dist);
        }

        int fullMask = (1 << n) - 1;
        double[][] dp = new double[1 << n][n];
        int[][] parent = new int[1 << n][n];

        for (int mask = 0; mask < (1 << n); mask++) {
            Arrays.fill(dp[mask], INF);
            Arrays.fill(parent[mask], -1);
        }

        // Base case: starting at hub 0 with only hub 0 visited (mask = 1)
        dp[1][0] = 0;

        // Iterate over all subset masks
        for (int mask = 1; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0 || dp[mask][u] >= INF) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) != 0) continue; // Already visited v

                    int nextMask = mask | (1 << v);
                    double newDist = dp[mask][u] + dist[u][v];

                    if (newDist < dp[nextMask][v]) {
                        dp[nextMask][v] = newDist;
                        parent[nextMask][v] = u;
                    }
                }
            }
        }

        // Complete closed loop back to hub 0
        double minTotalDist = INF;
        int lastNode = -1;

        for (int u = 1; u < n; u++) {
            if (dp[fullMask][u] < INF) {
                double tourDist = dp[fullMask][u] + dist[u][0];
                if (tourDist < minTotalDist) {
                    minTotalDist = tourDist;
                    lastNode = u;
                }
            }
        }

        // Reconstruct optimal route sequence
        List<Integer> routeIndices = new ArrayList<>();
        List<String> routeNames = new ArrayList<>();

        if (lastNode != -1) {
            int currentMask = fullMask;
            int curr = lastNode;

            List<Integer> revRoute = new ArrayList<>();
            while (curr != -1) {
                revRoute.add(curr);
                int prev = parent[currentMask][curr];
                currentMask ^= (1 << curr);
                curr = prev;
            }

            // Reverse to get path starting from 0
            for (int i = revRoute.size() - 1; i >= 0; i--) {
                int nodeIdx = revRoute.get(i);
                routeIndices.add(nodeIdx);
                routeNames.add(hubNames != null && nodeIdx < hubNames.length ? hubNames[nodeIdx] : "Hub-" + nodeIdx);
            }

            // Return to start
            routeIndices.add(0);
            routeNames.add(hubNames != null && hubNames.length > 0 ? hubNames[0] : "Hub-0");
        }

        return new TSPResult(minTotalDist, routeIndices, routeNames, dist);
    }
}
