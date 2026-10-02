package algorithms.dp;

import java.util.Arrays;

/**
 * Interval Dynamic Programming Algorithm (e.g. Matrix Chain Multiplication / Optimal Segment Batching).
 * 
 * Computes minimum cost sequence for merging contiguous segments/batches of logistics hubs or shipments.
 * 
 * Time Complexity: O(N^3) where N is number of hub segments.
 * Space Complexity: O(N^2) for DP cost matrix and optimal split point matrix.
 */
public class IntervalDP {

    public static class IntervalDPResult {
        private final int minCost;
        private final String optimalMergeOrder;
        private final int[][] dpMatrix;
        private final int[][] splitMatrix;

        public IntervalDPResult(int minCost, String optimalMergeOrder, int[][] dpMatrix, int[][] splitMatrix) {
            this.minCost = minCost;
            this.optimalMergeOrder = optimalMergeOrder;
            this.dpMatrix = dpMatrix;
            this.splitMatrix = splitMatrix;
        }

        public int getMinCost() {
            return minCost;
        }

        public String getOptimalMergeOrder() {
            return optimalMergeOrder;
        }

        public int[][] getDpMatrix() {
            return dpMatrix;
        }

        public int[][] getSplitMatrix() {
            return splitMatrix;
        }

        @Override
        public String toString() {
            return String.format("Minimum Interval Merge Cost: %d units | Optimal Merge Structure: %s", minCost, optimalMergeOrder);
        }
    }

    /**
     * Solves Interval DP for Matrix Chain / Hub Route Batch Consolidation.
     *
     * @param dimensions Dimensions or cost weights of contiguous hub segments (length N+1 for N hubs)
     * @param hubNames   Optional descriptive names for the hub nodes
     * @return IntervalDPResult object
     */
    public static IntervalDPResult computeOptimalHubMerge(int[] dimensions, String[] hubNames) {
        if (dimensions == null || dimensions.length < 2) {
            return new IntervalDPResult(0, "", new int[0][0], new int[0][0]);
        }

        int n = dimensions.length - 1; // Number of hub segments
        int[][] dp = new int[n + 1][n + 1];
        int[][] split = new int[n + 1][n + 1];

        // Cost is zero for merging single hub segment (interval length 1)
        for (int i = 1; i <= n; i++) {
            dp[i][i] = 0;
        }

        // len is interval length (2 to n)
        for (int len = 2; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                dp[i][j] = Integer.MAX_VALUE;

                for (int k = i; k < j; k++) {
                    int cost = dp[i][k] + dp[k + 1][j] + (dimensions[i - 1] * dimensions[k] * dimensions[j]);
                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                        split[i][j] = k;
                    }
                }
            }
        }

        String mergeOrder = buildMergeString(1, n, split, hubNames);

        return new IntervalDPResult(dp[1][n], mergeOrder, dp, split);
    }

    private static String buildMergeString(int i, int j, int[][] split, String[] hubNames) {
        if (i == j) {
            if (hubNames != null && i - 1 < hubNames.length) {
                return hubNames[i - 1];
            }
            return "H" + i;
        }
        int k = split[i][j];
        String left = buildMergeString(i, k, split, hubNames);
        String right = buildMergeString(k + 1, j, split, hubNames);
        return "(" + left + " -> " + right + ")";
    }
}
