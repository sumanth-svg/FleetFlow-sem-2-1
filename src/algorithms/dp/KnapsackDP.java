package algorithms.dp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dynamic Programming Framework - 0/1 Knapsack and Unbounded Knapsack Algorithms.
 * 
 * Demonstrates core DP principles:
 * 1. Optimal Substructure: The optimal solution to a payload capacity W contains optimal subproblems
 *    for capacities w < W.
 * 2. Overlapping Subproblems: Recursive evaluation produces identical sub-states (capacity w, item i)
 *    which are memoized / tabulated to achieve polynomial time complexity.
 * 
 * Time Complexity:
 * - 0/1 Knapsack: O(N * W) where N is number of packages and W is max vehicle weight capacity.
 * - Unbounded Knapsack: O(N * W).
 * Space Complexity:
 * - 0/1 Knapsack: O(N * W) for 2D DP matrix or O(W) for 1D space-optimized DP array.
 */
public class KnapsackDP {

    /**
     * Container holding the result of a Knapsack optimization.
     */
    public static class KnapsackResult {
        private final int maxValue;
        private final int totalWeight;
        private final List<Integer> selectedIndices;
        private final List<String> selectedItemNames;
        private final int[][] dpTable;

        public KnapsackResult(int maxValue, int totalWeight, List<Integer> selectedIndices,
                              List<String> selectedItemNames, int[][] dpTable) {
            this.maxValue = maxValue;
            this.totalWeight = totalWeight;
            this.selectedIndices = selectedIndices;
            this.selectedItemNames = selectedItemNames;
            this.dpTable = dpTable;
        }

        public int getMaxValue() {
            return maxValue;
        }

        public int getTotalWeight() {
            return totalWeight;
        }

        public List<Integer> getSelectedIndices() {
            return selectedIndices;
        }

        public List<String> getSelectedItemNames() {
            return selectedItemNames;
        }

        public int[][] getDpTable() {
            return dpTable;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Optimized Total Cargo Value: $").append(maxValue).append("\n");
            sb.append("Total Payload Weight Used: ").append(totalWeight).append(" kg\n");
            sb.append("Selected Freight Items: ").append(selectedItemNames);
            return sb.toString();
        }
    }

    /**
     * Solves 0/1 Knapsack using Bottom-Up Tabulation with step-by-step item traceback.
     *
     * @param weights   Weights of packages
     * @param values    Values/Priorities of packages
     * @param itemNames Descriptive names of packages
     * @param capacity  Maximum vehicle payload weight capacity
     * @return KnapsackResult containing maximum value, chosen packages, and DP state matrix
     */
    public static KnapsackResult solve01KnapsackTabulation(int[] weights, int[] values, String[] itemNames, int capacity) {
        if (weights == null || values == null || capacity <= 0 || weights.length == 0) {
            return new KnapsackResult(0, 0, new ArrayList<>(), new ArrayList<>(), new int[0][0]);
        }

        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];

        // Build DP table in bottom-up manner
        for (int i = 1; i <= n; i++) {
            int w = weights[i - 1];
            int v = values[i - 1];
            for (int cap = 0; cap <= capacity; cap++) {
                if (w <= cap) {
                    dp[i][cap] = Math.max(dp[i - 1][cap], v + dp[i - 1][cap - w]);
                } else {
                    dp[i][cap] = dp[i - 1][cap];
                }
            }
        }

        // Trace back selected items
        List<Integer> selectedIndices = new ArrayList<>();
        List<String> selectedNames = new ArrayList<>();
        int res = dp[n][capacity];
        int wCap = capacity;
        int totalWeightUsed = 0;

        for (int i = n; i > 0 && res > 0; i--) {
            if (res != dp[i - 1][wCap]) {
                // Item (i - 1) was included
                int idx = i - 1;
                selectedIndices.add(idx);
                selectedNames.add(itemNames != null && idx < itemNames.length ? itemNames[idx] : "Item-" + idx);
                res -= values[idx];
                wCap -= weights[idx];
                totalWeightUsed += weights[idx];
            }
        }

        return new KnapsackResult(dp[n][capacity], totalWeightUsed, selectedIndices, selectedNames, dp);
    }

    /**
     * Solves 0/1 Knapsack using Top-Down Recursion with Memoization.
     * Demonstrates how overlapping subproblems are cached to avoid exponential runtime.
     *
     * @param weights  Weights array
     * @param values   Values array
     * @param capacity Maximum weight capacity
     * @return Maximum value achievable
     */
    public static int solve01KnapsackMemoization(int[] weights, int[] values, int capacity) {
        if (weights == null || values == null || capacity <= 0) return 0;
        int n = weights.length;
        int[][] memo = new int[n + 1][capacity + 1];
        for (int[] row : memo) {
            Arrays.fill(row, -1);
        }
        return knapsackMemoRecursive(weights, values, capacity, n, memo);
    }

    private static int knapsackMemoRecursive(int[] weights, int[] values, int cap, int i, int[][] memo) {
        if (i == 0 || cap == 0) return 0;
        if (memo[i][cap] != -1) return memo[i][cap];

        if (weights[i - 1] <= cap) {
            int include = values[i - 1] + knapsackMemoRecursive(weights, values, cap - weights[i - 1], i - 1, memo);
            int exclude = knapsackMemoRecursive(weights, values, cap, i - 1, memo);
            memo[i][cap] = Math.max(include, exclude);
        } else {
            memo[i][cap] = knapsackMemoRecursive(weights, values, cap, i - 1, memo);
        }
        return memo[i][cap];
    }

    /**
     * Solves Unbounded Knapsack where each package type can be chosen multiple times.
     *
     * @param weights  Weights of package types
     * @param values   Values/priorities of package types
     * @param capacity Maximum vehicle payload capacity
     * @return Maximum achievable payload value
     */
    public static int solveUnboundedKnapsack(int[] weights, int[] values, int capacity) {
        if (weights == null || values == null || capacity <= 0) return 0;
        int[] dp = new int[capacity + 1];

        for (int w = 1; w <= capacity; w++) {
            for (int i = 0; i < weights.length; i++) {
                if (weights[i] <= w) {
                    dp[w] = Math.max(dp[w], values[i] + dp[w - weights[i]]);
                }
            }
        }
        return dp[capacity];
    }
}
