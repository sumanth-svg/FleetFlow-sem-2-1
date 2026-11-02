package com.fleetflow.constraints;

import java.util.*;

/**
 * Logistics Conflict Modeling and Reductions:
 * CLIQUE -> INDEPENDENT SET -> VERTEX COVER.
 *
 * Mathematical Foundations:
 * For any undirected graph G = (V, E) with complement G_bar = (V, E_bar):
 * 1. Subset S is a CLIQUE in G <=> S is an INDEPENDENT SET in G_bar.
 * 2. Subset S is an INDEPENDENT SET in G <=> V \ S is a VERTEX COVER in G.
 *
 * Logistics Application:
 * - Vertices: Scheduled delivery trips / vehicle routes.
 * - Edges in Conflict Graph G: Direct resource/time conflicts where two trips cannot be executed simultaneously.
 * - Independent Set in G: The largest subset of non-conflicting delivery trips that can safely be scheduled.
 * - Vertex Cover in G: The minimum set of conflicting trips/locations that must be rescheduled or monitored to eliminate all conflicts.
 */
public class ConflictGraphModeler {

    public static class ConflictNode {
        private final int id;
        private final String label;
        private final String timeWindow;
        private final String requiredResource;

        public ConflictNode(int id, String label, String timeWindow, String requiredResource) {
            this.id = id;
            this.label = label;
            this.timeWindow = timeWindow;
            this.requiredResource = requiredResource;
        }

        public int getId() { return id; }
        public String getLabel() { return label; }
        public String getTimeWindow() { return timeWindow; }
        public String getRequiredResource() { return requiredResource; }

        @Override
        public String toString() {
            return String.format("[%s (%s, %s)]", label, timeWindow, requiredResource);
        }
    }

    public static class TripletReductionResult {
        private final List<ConflictNode> maxIndependentSet; // safe schedule
        private final List<ConflictNode> minVertexCover;     // conflict resolution set
        private final List<ConflictNode> maxCliqueInComplement; // mutual incompatibility / complement clique

        public TripletReductionResult(List<ConflictNode> maxIndependentSet, List<ConflictNode> minVertexCover, List<ConflictNode> maxCliqueInComplement) {
            this.maxIndependentSet = maxIndependentSet;
            this.minVertexCover = minVertexCover;
            this.maxCliqueInComplement = maxCliqueInComplement;
        }

        public List<ConflictNode> getMaxIndependentSet() { return maxIndependentSet; }
        public List<ConflictNode> getMinVertexCover() { return minVertexCover; }
        public List<ConflictNode> getMaxCliqueInComplement() { return maxCliqueInComplement; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Reduction Triplet: CLIQUE <-> INDEPENDENT SET <-> VERTEX COVER\n");
            sb.append(String.format("1. Maximum Independent Set (%d conflict-free tasks):\n   %s\n",
                    maxIndependentSet.size(), maxIndependentSet));
            sb.append(String.format("2. Minimum Vertex Cover (|V| - |IS| = %d critical conflict nodes):\n   %s\n",
                    minVertexCover.size(), minVertexCover));
            sb.append(String.format("3. Corresponding Maximum Clique in Complement Graph (%d nodes):\n   %s\n",
                    maxCliqueInComplement.size(), maxCliqueInComplement));
            sb.append(String.format("Theorem Verified: |Independent Set| + |Vertex Cover| = %d + %d = %d (Total Vertices)\n",
                    maxIndependentSet.size(), minVertexCover.size(), maxIndependentSet.size() + minVertexCover.size()));
            return sb.toString();
        }
    }

    /**
     * Solves the exact maximum independent set, minimum vertex cover, and complement clique.
     */
    public static TripletReductionResult analyzeConflicts(List<ConflictNode> nodes, boolean[][] conflictAdj) {
        int n = nodes.size();
        int bestMask = 0;
        int maxISSize = 0;

        // Exact exponential check for conflict-free independent sets
        int total = 1 << n;
        for (int mask = 0; mask < total; mask++) {
            boolean isIndependent = true;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    for (int j = i + 1; j < n; j++) {
                        if ((mask & (1 << j)) != 0 && conflictAdj[i][j]) {
                            isIndependent = false;
                            break;
                        }
                    }
                    if (!isIndependent) break;
                }
            }

            if (isIndependent) {
                int sz = Integer.bitCount(mask);
                if (sz > maxISSize) {
                    maxISSize = sz;
                    bestMask = mask;
                }
            }
        }

        List<ConflictNode> indepSet = new ArrayList<>();
        List<ConflictNode> vertCover = new ArrayList<>();
        List<ConflictNode> compClique = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if ((bestMask & (1 << i)) != 0) {
                indepSet.add(nodes.get(i));
                compClique.add(nodes.get(i)); // In complement graph, independent set is a clique!
            } else {
                vertCover.add(nodes.get(i)); // V \ S is vertex cover
            }
        }

        return new TripletReductionResult(indepSet, vertCover, compClique);
    }
}
