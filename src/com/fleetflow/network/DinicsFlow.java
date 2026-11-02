package com.fleetflow.network;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Dinic's Algorithm for Maximum Network Flow.
 * Time Complexity: O(V^2 * E), and O(E * sqrt(V)) on unit networks.
 * Uses BFS to construct a level graph and DFS with dead-end pruning to find blocking flows.
 * Used in FleetFlow for large, multi-tier logistics distribution networks.
 */
public class DinicsFlow {

    public static class FlowResult {
        private final double maxFlow;
        private final int phaseCount;

        public FlowResult(double maxFlow, int phaseCount) {
            this.maxFlow = maxFlow;
            this.phaseCount = phaseCount;
        }

        public double getMaxFlow() { return maxFlow; }
        public int getPhaseCount() { return phaseCount; }

        @Override
        public String toString() {
            return String.format("Max Flow (Dinic's): %.2f tons/hr (Level Graph Phases: %d)", maxFlow, phaseCount);
        }
    }

    public static FlowResult computeMaxFlow(FlowNetwork network, int source, int sink) {
        network.resetFlows();
        double maxFlow = 0.0;
        int phases = 0;
        int n = network.getVertexCount();
        int[] level = new int[n];
        int[] ptr = new int[n];

        while (bfsLevelGraph(network, source, sink, level)) {
            phases++;
            Arrays.fill(ptr, 0);

            while (true) {
                double pushed = dfsBlockingFlow(network, source, sink, Double.POSITIVE_INFINITY, level, ptr);
                if (pushed <= 1e-9) {
                    break;
                }
                maxFlow += pushed;
            }
        }

        return new FlowResult(maxFlow, phases);
    }

    private static boolean bfsLevelGraph(FlowNetwork network, int source, int sink, int[] level) {
        Arrays.fill(level, -1);
        level[source] = 0;
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (FlowEdge edge : network.getAdj(u)) {
                int v = edge.getTo();
                if (level[v] == -1 && edge.getResidualCapacity() > 1e-9) {
                    level[v] = level[u] + 1;
                    queue.add(v);
                }
            }
        }

        return level[sink] != -1;
    }

    private static double dfsBlockingFlow(FlowNetwork network, int u, int sink, double currentFlow, int[] level, int[] ptr) {
        if (u == sink) return currentFlow;

        for (; ptr[u] < network.getAdj(u).size(); ptr[u]++) {
            FlowEdge edge = network.getAdj(u).get(ptr[u]);
            int v = edge.getTo();

            if (level[v] == level[u] + 1 && edge.getResidualCapacity() > 1e-9) {
                double bottleneck = Math.min(currentFlow, edge.getResidualCapacity());
                double pushed = dfsBlockingFlow(network, v, sink, bottleneck, level, ptr);

                if (pushed > 1e-9) {
                    edge.addResidualFlow(pushed);
                    return pushed;
                }
            }
        }

        return 0.0;
    }
}
