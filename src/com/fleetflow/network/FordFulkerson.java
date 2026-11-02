package com.fleetflow.network;

import java.util.Arrays;

/**
 * Ford-Fulkerson algorithm for Maximum Network Flow using DFS to find augmenting paths.
 * Used in FleetFlow to calculate maximum warehouse-to-destination shipment flow.
 */
public class FordFulkerson {

    public static class FlowResult {
        private final double maxFlow;
        private final int augmentingPathsCount;

        public FlowResult(double maxFlow, int augmentingPathsCount) {
            this.maxFlow = maxFlow;
            this.augmentingPathsCount = augmentingPathsCount;
        }

        public double getMaxFlow() { return maxFlow; }
        public int getAugmentingPathsCount() { return augmentingPathsCount; }

        @Override
        public String toString() {
            return String.format("Max Flow (Ford-Fulkerson): %.2f tons/hr (Found %d augmenting paths)",
                    maxFlow, augmentingPathsCount);
        }
    }

    public static FlowResult computeMaxFlow(FlowNetwork network, int source, int sink) {
        network.resetFlows();
        double maxFlow = 0.0;
        int pathCount = 0;
        int n = network.getVertexCount();
        boolean[] visited = new boolean[n];

        while (true) {
            Arrays.fill(visited, false);
            double push = dfs(network, source, sink, Double.POSITIVE_INFINITY, visited);
            if (push <= 1e-9) {
                break;
            }
            maxFlow += push;
            pathCount++;
        }

        return new FlowResult(maxFlow, pathCount);
    }

    private static double dfs(FlowNetwork network, int u, int sink, double currentFlow, boolean[] visited) {
        if (u == sink) return currentFlow;
        visited[u] = true;

        for (FlowEdge edge : network.getAdj(u)) {
            if (!visited[edge.getTo()] && edge.getResidualCapacity() > 1e-9) {
                double bottleneck = Math.min(currentFlow, edge.getResidualCapacity());
                double pushed = dfs(network, edge.getTo(), sink, bottleneck, visited);
                if (pushed > 1e-9) {
                    edge.addResidualFlow(pushed);
                    return pushed;
                }
            }
        }
        return 0.0;
    }
}
