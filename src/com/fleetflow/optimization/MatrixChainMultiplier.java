package com.fleetflow.optimization;

import java.util.List;

/**
 * Matrix-Chain Multiplication (MCM) Dynamic Programming algorithm.
 * Time Complexity: O(N^3).
 * Space Complexity: O(N^2).
 * Used in FleetFlow to optimize the sequencing and parenthesization of logistics processing operations,
 * multi-stage parcel routing matrices, and cargo consolidation transformations.
 */
public class MatrixChainMultiplier {

    public static class MCMResult {
        private final long minCost;
        private final String optimalParenthesization;
        private final long naiveCost;
        private final double costReductionPct;

        public MCMResult(long minCost, String optimalParenthesization, long naiveCost) {
            this.minCost = minCost;
            this.optimalParenthesization = optimalParenthesization;
            this.naiveCost = naiveCost;
            this.costReductionPct = (naiveCost == 0) ? 0.0 : ((double) (naiveCost - minCost) / naiveCost) * 100.0;
        }

        public long getMinCost() { return minCost; }
        public String getOptimalParenthesization() { return optimalParenthesization; }
        public long getNaiveCost() { return naiveCost; }
        public double getCostReductionPct() { return costReductionPct; }

        @Override
        public String toString() {
            return String.format("Optimal Staging: %s\nOptimal Computational Operations: %,d\nNaive Left-to-Right Operations: %,d\nEfficiency Gain: %.1f%% reduction",
                    optimalParenthesization, minCost, naiveCost, costReductionPct);
        }
    }

    /**
     * Solves MCM for matrix dimensions p where matrix i has dimensions p[i-1] x p[i].
     * @param p Dimensions array of size n + 1 for n matrices.
     * @param operationNames Names of the logistics operations (e.g., "OrderParse", "SpatialRoute", etc.)
     */
    public static MCMResult optimizeSequence(int[] p, List<String> operationNames) {
        int n = p.length - 1; // number of matrices/operations
        long[][] m = new long[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        // Cost is 0 when multiplying one matrix
        for (int i = 1; i <= n; i++) {
            m[i][i] = 0;
        }

        // L is chain length
        for (int L = 2; L <= n; L++) {
            for (int i = 1; i <= n - L + 1; i++) {
                int j = i + L - 1;
                m[i][j] = Long.MAX_VALUE;

                for (int k = i; k <= j - 1; k++) {
                    long q = m[i][k] + m[k + 1][j] + (long) p[i - 1] * p[k] * p[j];
                    if (q < m[i][j]) {
                        m[i][j] = q;
                        s[i][j] = k;
                    }
                }
            }
        }

        // Compute naive left-to-right cost for comparison
        long naiveCost = 0;
        long currentRows = p[0];
        for (int i = 1; i < n; i++) {
            naiveCost += currentRows * (long) p[i] * p[i + 1];
        }

        StringBuilder sb = new StringBuilder();
        reconstructParenthesization(s, 1, n, operationNames, sb);

        return new MCMResult(m[1][n], sb.toString(), naiveCost);
    }

    private static void reconstructParenthesization(int[][] s, int i, int j, List<String> names, StringBuilder sb) {
        if (i == j) {
            String opName = (names != null && i - 1 < names.size()) ? names.get(i - 1) : ("Op" + i);
            sb.append(opName);
        } else {
            sb.append("(");
            reconstructParenthesization(s, i, s[i][j], names, sb);
            sb.append(" x ");
            reconstructParenthesization(s, s[i][j] + 1, j, names, sb);
            sb.append(")");
        }
    }
}
