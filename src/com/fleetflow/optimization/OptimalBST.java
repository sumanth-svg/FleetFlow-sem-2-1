package com.fleetflow.optimization;

import java.util.List;

/**
 * Optimal Binary Search Tree (OBST) Dynamic Programming algorithm.
 * Time Complexity: O(N^3).
 * Space Complexity: O(N^2).
 * Used in FleetFlow to construct an optimal search tree for frequently accessed shipment tracking keys,
 * minimizing expected cache and database lookup latency based on access frequencies.
 */
public class OptimalBST {

    public static class OBSTResult {
        private final double expectedCost;
        private final double uniformTreeCost;
        private final double costReductionPct;
        private final String treeStructure;

        public OBSTResult(double expectedCost, double uniformTreeCost, String treeStructure) {
            this.expectedCost = expectedCost;
            this.uniformTreeCost = uniformTreeCost;
            this.costReductionPct = (uniformTreeCost == 0) ? 0.0 : ((uniformTreeCost - expectedCost) / uniformTreeCost) * 100.0;
            this.treeStructure = treeStructure;
        }

        public double getExpectedCost() { return expectedCost; }
        public double getUniformTreeCost() { return uniformTreeCost; }
        public double getCostReductionPct() { return costReductionPct; }
        public String getTreeStructure() { return treeStructure; }

        @Override
        public String toString() {
            return String.format("Optimal Expected Search Depth/Cost: %.4f\nBalanced Uniform Tree Expected Cost: %.4f\nEfficiency Improvement: %.2f%%\nTree Hierarchy:\n%s",
                    expectedCost, uniformTreeCost, costReductionPct, treeStructure);
        }
    }

    /**
     * Builds the Optimal BST for given sorted shipment keys and their access probabilities.
     * @param keys Sorted list of shipment tracking keys
     * @param p Probabilities p[1..n] of successful search for each key
     * @param q Probabilities q[0..n] of unsuccessful search between keys
     */
    public static OBSTResult buildOptimalBST(List<String> keys, double[] p, double[] q) {
        int n = keys.size();
        double[][] e = new double[n + 2][n + 2];
        double[][] w = new double[n + 2][n + 2];
        int[][] root = new int[n + 2][n + 2];

        // Base cases: empty subtrees
        for (int i = 1; i <= n + 1; i++) {
            e[i][i - 1] = q[i - 1];
            w[i][i - 1] = q[i - 1];
        }

        // L is subtree size
        for (int L = 1; L <= n; L++) {
            for (int i = 1; i <= n - L + 1; i++) {
                int j = i + L - 1;
                e[i][j] = Double.MAX_VALUE;
                w[i][j] = w[i][j - 1] + p[j] + q[j];

                for (int r = i; r <= j; r++) {
                    double t = e[i][r - 1] + e[r + 1][j] + w[i][j];
                    if (t < e[i][j]) {
                        e[i][j] = t;
                        root[i][j] = r;
                    }
                }
            }
        }

        // Compute baseline uniform tree cost (depth of key * p[i])
        double uniformCost = 0.0;
        int depthLimit = (int) Math.ceil(Math.log(n + 1) / Math.log(2));
        for (int i = 1; i <= n; i++) {
            uniformCost += depthLimit * p[i];
        }
        for (int i = 0; i <= n; i++) {
            uniformCost += depthLimit * q[i];
        }

        StringBuilder sb = new StringBuilder();
        reconstructTree(root, 1, n, keys, 0, "ROOT", sb);

        return new OBSTResult(e[1][n], uniformCost, sb.toString());
    }

    private static void reconstructTree(int[][] root, int i, int j, List<String> keys, int depth, String side, StringBuilder sb) {
        if (i > j) return;
        int r = root[i][j];
        for (int d = 0; d < depth; d++) sb.append("  ");
        sb.append(side).append(" -> ").append(keys.get(r - 1)).append("\n");

        reconstructTree(root, i, r - 1, keys, depth + 1, "LEFT", sb);
        reconstructTree(root, r + 1, j, keys, depth + 1, "RIGHT", sb);
    }
}
