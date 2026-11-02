package com.fleetflow.constraints;

import com.fleetflow.model.DeliveryConstraint;

import java.util.*;

/**
 * 3-SAT to CLIQUE Polynomial-Time Reduction Demonstration.
 * Theoretical Foundation:
 * Given a 3-CNF formula with m clauses:
 * 1. Create a vertex for each of the 3 literals in each clause (3m vertices).
 * 2. Put an edge between vertices v_(i, j) and v_(k, r) iff:
 *    - i != k (they belong to different clauses)
 *    - literal(v_(i, j)) != NOT literal(v_(k, r)) (they are consistent)
 * 3. The formula is satisfiable IF AND ONLY IF the constructed graph contains a clique of size m.
 * Used in FleetFlow to prove NP-hardness and reduce delivery constraint checking to maximum clique.
 */
public class CliqueReducer {

    public static class CliqueVertex {
        private final int id;
        private final int clauseIndex;
        private final int literal;

        public CliqueVertex(int id, int clauseIndex, int literal) {
            this.id = id;
            this.clauseIndex = clauseIndex;
            this.literal = literal;
        }

        public int getId() { return id; }
        public int getClauseIndex() { return clauseIndex; }
        public int getLiteral() { return literal; }

        @Override
        public String toString() {
            return String.format("V%d[Clause %d: %s%d]", id, clauseIndex + 1, (literal > 0 ? "x" : "~x"), Math.abs(literal));
        }
    }

    public static class ReductionGraph {
        private final int clauseCount;
        private final List<CliqueVertex> vertices;
        private final boolean[][] adjMatrix;

        public ReductionGraph(int clauseCount, List<CliqueVertex> vertices, boolean[][] adjMatrix) {
            this.clauseCount = clauseCount;
            this.vertices = vertices;
            this.adjMatrix = adjMatrix;
        }

        public int getClauseCount() { return clauseCount; }
        public List<CliqueVertex> getVertices() { return vertices; }
        public boolean[][] getAdjMatrix() { return adjMatrix; }

        public int countEdges() {
            int count = 0;
            int n = vertices.size();
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (adjMatrix[i][j]) count++;
                }
            }
            return count;
        }
    }

    public static class ReductionResult {
        private final ReductionGraph graph;
        private final List<CliqueVertex> clique;
        private final boolean kCliqueFound;
        private final Map<Integer, Boolean> derivedSatAssignment;

        public ReductionResult(ReductionGraph graph, List<CliqueVertex> clique, boolean kCliqueFound, Map<Integer, Boolean> derivedSatAssignment) {
            this.graph = graph;
            this.clique = clique;
            this.kCliqueFound = kCliqueFound;
            this.derivedSatAssignment = derivedSatAssignment;
        }

        public ReductionGraph getGraph() { return graph; }
        public List<CliqueVertex> getClique() { return clique; }
        public boolean isKCliqueFound() { return kCliqueFound; }
        public Map<Integer, Boolean> getDerivedSatAssignment() { return derivedSatAssignment; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("3-SAT -> CLIQUE Reduction:\n"));
            sb.append(String.format("  * Input 3-CNF Clauses: m = %d\n", graph.clauseCount));
            sb.append(String.format("  * Transformed Graph: |V| = %d vertices, |E| = %d edges\n",
                    graph.vertices.size(), graph.countEdges()));
            sb.append(String.format("  * Target Clique Size: k = %d (one vertex per clause)\n", graph.clauseCount));
            sb.append(String.format("  * Result: %s\n", kCliqueFound ? "FOUND k-Clique (Satisfiable!)" : "NO k-Clique exists (Unsatisfiable)"));
            if (kCliqueFound) {
                sb.append("  * Discovered Clique Vertices: ").append(clique).append("\n");
                sb.append("  * Satisfying Truth Assignment derived from Clique:\n");
                for (Map.Entry<Integer, Boolean> e : derivedSatAssignment.entrySet()) {
                    sb.append(String.format("      x%d = %s\n", e.getKey(), e.getValue()));
                }
            }
            return sb.toString();
        }
    }

    /**
     * Performs the reduction from 3-SAT delivery constraints to CLIQUE.
     */
    public static ReductionResult reduceAndSolve(List<DeliveryConstraint> constraints) {
        List<List<Integer>> clauses = new ArrayList<>();
        for (DeliveryConstraint dc : constraints) {
            if (!dc.getLiterals().isEmpty()) {
                clauses.add(dc.getLiterals());
            }
        }

        int m = clauses.size();
        List<CliqueVertex> vertices = new ArrayList<>();
        int vid = 0;

        for (int c = 0; c < m; c++) {
            List<Integer> lits = clauses.get(c);
            for (int lit : lits) {
                vertices.add(new CliqueVertex(vid++, c, lit));
            }
        }

        int totalV = vertices.size();
        boolean[][] adj = new boolean[totalV][totalV];

        // Connect vertices in different clauses with non-contradictory literals
        for (int i = 0; i < totalV; i++) {
            for (int j = i + 1; j < totalV; j++) {
                CliqueVertex u = vertices.get(i);
                CliqueVertex v = vertices.get(j);

                if (u.getClauseIndex() != v.getClauseIndex() && u.getLiteral() != -v.getLiteral()) {
                    adj[i][j] = true;
                    adj[j][i] = true;
                }
            }
        }

        ReductionGraph graph = new ReductionGraph(m, vertices, adj);

        // Search for a clique of size m using backtracking
        List<Integer> currentClique = new ArrayList<>();
        List<Integer> bestClique = new ArrayList<>();
        findClique(0, currentClique, bestClique, m, adj, vertices);

        boolean found = bestClique.size() == m;
        List<CliqueVertex> cliqueVertices = new ArrayList<>();
        Map<Integer, Boolean> assignment = new HashMap<>();

        if (found) {
            for (int idx : bestClique) {
                CliqueVertex v = vertices.get(idx);
                cliqueVertices.add(v);
                int var = Math.abs(v.getLiteral());
                assignment.put(var, v.getLiteral() > 0);
            }
        }

        return new ReductionResult(graph, cliqueVertices, found, assignment);
    }

    private static boolean findClique(int start, List<Integer> current, List<Integer> best, int targetK, boolean[][] adj, List<CliqueVertex> vertices) {
        if (current.size() == targetK) {
            best.addAll(current);
            return true;
        }

        int n = vertices.size();
        for (int i = start; i < n; i++) {
            if (current.size() + (n - i) < targetK) {
                return false; // pruning
            }

            // Check if vertex i is connected to all vertices currently in clique
            boolean connected = true;
            for (int u : current) {
                if (!adj[u][i]) {
                    connected = false;
                    break;
                }
            }

            if (connected) {
                current.add(i);
                if (findClique(i + 1, current, best, targetK, adj, vertices)) {
                    return true;
                }
                current.remove(current.size() - 1);
            }
        }
        return false;
    }
}
