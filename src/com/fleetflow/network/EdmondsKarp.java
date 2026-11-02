package com.fleetflow.network;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Edmonds-Karp algorithm for Maximum Network Flow using BFS for shortest augmenting paths.
 * Time Complexity: O(V * E^2).
 * Used in FleetFlow for bandwidth- and capacity-based logistics networks to ensure polynomial-time convergence.
 */
public class EdmondsKarp {

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
            return String.format("Max Flow (Edmonds-Karp): %.2f tons/hr (Found %d augmenting paths)",
                    maxFlow, augmentingPathsCount);
        }
    }

    public static FlowResult computeMaxFlow(FlowNetwork network, int source, int sink) {
        network.resetFlows();
        double maxFlow = 0.0;
        int pathCount = 0;
        int n = network.getVertexCount();

        FlowEdge[] parentEdge = new FlowEdge[n];

        while (bfs(network, source, sink, parentEdge)) {
            // Find bottleneck capacity along path
            double bottleneck = Double.POSITIVE_INFINITY;
            for (int v = sink; v != source; v = parentEdge[v].getFrom()) {
                bottleneck = Math.min(bottleneck, parentEdge[v].getResidualCapacity());
            }

            // Augment flow
            for (int v = sink; v != source; v = parentEdge[v].getFrom()) {
                parentEdge[v].addResidualFlow(bottleneck);
            }

            maxFlow += bottleneck;
            pathCount++;
        }

        return new FlowResult(maxFlow, pathCount);
    }

    private static boolean bfs(FlowNetwork network, int source, int sink, FlowEdge[] parentEdge) {
        Arrays.fill(parentEdge, null);
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (u == sink) return true;

            for (FlowEdge edge : network.getAdj(u)) {
                int v = edge.getTo();
                if (v != source && parentEdge[v] == null && edge.getResidualCapacity() > 1e-9) {
                    parentEdge[v] = edge;
                    queue.add(v);
                }
            }
        }

        return parentEdge[sink] != null;
    }
}
