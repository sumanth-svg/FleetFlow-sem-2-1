package com.fleetflow.constraints;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 2-Approximation Algorithm for Minimum Vertex Cover using Maximal Edge Matching.
 * Time Complexity: O(V + E).
 *
 * Approximation Ratio Proof:
 * Let M be the set of disjoint edges picked during matching.
 * Any valid vertex cover must contain at least one endpoint from each edge in M, so OPT >= |M|.
 * The algorithm includes both endpoints of each edge in M, so |C| = 2 * |M|.
 * Thus, |C| <= 2 * OPT.
 *
 * Logistics Application:
 * Used to identify the minimal set of critical conflict locations, inspection waypoints,
 * and security checkpoints needed to oversee all conflicting delivery routes.
 */
public class VertexCoverApproximator {

    public static class ApproxResult {
        private final List<String> coverVertices;
        private final List<String> matchedEdges;
        private final double approxRatioUpper;

        public ApproxResult(List<String> coverVertices, List<String> matchedEdges) {
            this.coverVertices = coverVertices;
            this.matchedEdges = matchedEdges;
            this.approxRatioUpper = 2.0;
        }

        public List<String> getCoverVertices() { return coverVertices; }
        public List<String> getMatchedEdges() { return matchedEdges; }
        public double getApproxRatioUpper() { return approxRatioUpper; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Vertex-Cover 2-Approximation Result:\n");
            sb.append(String.format("  * Selected Maximal Matching Edges (|M| = %d):\n", matchedEdges.size()));
            for (String e : matchedEdges) sb.append("      - ").append(e).append("\n");
            sb.append(String.format("  * Approximate Vertex Cover Size: |C| = %d (<= 2 * OPT)\n", coverVertices.size()));
            sb.append("  * Critical Monitoring Locations: ").append(coverVertices).append("\n");
            sb.append("  * Theoretical Quality Guarantee: |C| / OPT <= 2.0 (Strictly bounded)\n");
            return sb.toString();
        }
    }

    public static ApproxResult approximateCover(List<String> nodeNames, boolean[][] adjMatrix) {
        int n = nodeNames.size();
        boolean[] inCover = new boolean[n];
        List<String> matchedEdges = new ArrayList<>();

        for (int u = 0; u < n; u++) {
            if (inCover[u]) continue;

            for (int v = u + 1; v < n; v++) {
                if (!inCover[v] && adjMatrix[u][v]) {
                    // Include both endpoints in cover
                    inCover[u] = true;
                    inCover[v] = true;
                    matchedEdges.add(nodeNames.get(u) + " <---> " + nodeNames.get(v));
                    break;
                }
            }
        }

        List<String> coverVertices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (inCover[i]) {
                coverVertices.add(nodeNames.get(i));
            }
        }

        return new ApproxResult(coverVertices, matchedEdges);
    }
}
