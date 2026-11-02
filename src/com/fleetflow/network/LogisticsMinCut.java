package com.fleetflow.network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Min-Cut Algorithm for Logistics Network Bottleneck Detection.
 * Based on the Max-Flow Min-Cut Theorem.
 * Identifies the saturated bottleneck corridors and critical warehouse partitions that restrict overall cargo flow.
 */
public class LogisticsMinCut {

    public static class MinCutResult {
        private final double minCutCapacity;
        private final List<String> sourcePartitionNodes;
        private final List<String> sinkPartitionNodes;
        private final List<FlowEdge> bottleneckEdges;

        public MinCutResult(double minCutCapacity, List<String> sourcePartitionNodes,
                            List<String> sinkPartitionNodes, List<FlowEdge> bottleneckEdges) {
            this.minCutCapacity = minCutCapacity;
            this.sourcePartitionNodes = sourcePartitionNodes;
            this.sinkPartitionNodes = sinkPartitionNodes;
            this.bottleneckEdges = bottleneckEdges;
        }

        public double getMinCutCapacity() { return minCutCapacity; }
        public List<String> getSourcePartitionNodes() { return sourcePartitionNodes; }
        public List<String> getSinkPartitionNodes() { return sinkPartitionNodes; }
        public List<FlowEdge> getBottleneckEdges() { return bottleneckEdges; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Minimum Cut Capacity: %.2f tons/hr\n", minCutCapacity));
            sb.append(String.format("Bottleneck Corridors (%d critical links saturated):\n", bottleneckEdges.size()));
            for (FlowEdge e : bottleneckEdges) {
                sb.append(String.format("  [!] Link: %s (Cap: %.1f, Flow: %.1f)\n", e.getLabel(), e.getCapacity(), e.getFlow()));
            }
            sb.append(String.format("Source-Reachable Partition (%d nodes): %s\n", sourcePartitionNodes.size(), sourcePartitionNodes));
            sb.append(String.format("Sink Partition (%d nodes): %s\n", sinkPartitionNodes.size(), sinkPartitionNodes));
            return sb.toString();
        }
    }

    /**
     * Finds the minimum cut after computing maximum flow on the network.
     */
    public static MinCutResult findMinCut(FlowNetwork network, int source, int sink) {
        // Ensure max flow has been computed
        DinicsFlow.computeMaxFlow(network, source, sink);

        int n = network.getVertexCount();
        boolean[] reachableFromSource = new boolean[n];

        // BFS on residual graph: follow edges with residual capacity > 1e-9
        Queue<Integer> queue = new ArrayDeque<>();
        reachableFromSource[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (FlowEdge edge : network.getAdj(u)) {
                int v = edge.getTo();
                if (!reachableFromSource[v] && edge.getResidualCapacity() > 1e-9) {
                    reachableFromSource[v] = true;
                    queue.add(v);
                }
            }
        }

        List<String> sNodes = new ArrayList<>();
        List<String> tNodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (reachableFromSource[i]) {
                sNodes.add(network.getNodeName(i));
            } else {
                tNodes.add(network.getNodeName(i));
            }
        }

        List<FlowEdge> cutEdges = new ArrayList<>();
        double cutCapacity = 0.0;

        for (int u = 0; u < n; u++) {
            if (reachableFromSource[u]) {
                for (FlowEdge edge : network.getAdj(u)) {
                    int v = edge.getTo();
                    // Original forward edge from S-side to T-side
                    if (!reachableFromSource[v] && edge.getCapacity() > 0) {
                        cutEdges.add(edge);
                        cutCapacity += edge.getCapacity();
                    }
                }
            }
        }

        return new MinCutResult(cutCapacity, sNodes, tNodes, cutEdges);
    }
}
