package algorithms.dp;

/**
 * Optimal Binary Search Tree (OBST) Dynamic Programming Algorithm.
 * 
 * Computes the binary search tree with minimum expected search cost for frequently accessed shipment tracking keys
 * given key access probabilities/frequencies.
 * 
 * Used in FleetFlow (Module 3 - CO3) to:
 * 1. Optimize lookup time for high-frequency shipment keys and status queries.
 * 2. Structure search index trees based on real-time request distributions.
 * 
 * Time Complexity: O(N^3) standard DP formulation.
 * Space Complexity: O(N^2) for DP cost table and root table.
 */
public class OptimalBST {

    public static class OBSTResult {
        private final double minExpectedCost;
        private final double[][] costTable;
        private final int[][] rootTable;
        private final String[] keys;

        public OBSTResult(double minExpectedCost, double[][] costTable, int[][] rootTable, String[] keys) {
            this.minExpectedCost = minExpectedCost;
            this.costTable = costTable;
            this.rootTable = rootTable;
            this.keys = keys;
        }

        public double getMinExpectedCost() {
            return minExpectedCost;
        }

        public double[][] getCostTable() {
            return costTable;
        }

        public int[][] getRootTable() {
            return rootTable;
        }

        public String[] getKeys() {
            return keys;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Minimum Expected Search Cost: %.4f\n", minExpectedCost));
            sb.append("Optimal Root Selection Matrix Built for ").append(keys.length).append(" key(s).");
            return sb.toString();
        }
    }

    /**
     * Computes the Optimal Binary Search Tree for a set of keys and their search probabilities.
     *
     * @param keys  Sorted array of shipment keys / IDs
     * @param p     Access probabilities for keys (length N)
     * @param q     Access probabilities for unsuccessful searches (length N + 1)
     * @return OBSTResult object
     */
    public static OBSTResult computeOptimalBST(String[] keys, double[] p, double[] q) {
        if (keys == null || p == null || q == null || keys.length == 0) {
            return new OBSTResult(0.0, new double[0][0], new int[0][0], new String[0]);
        }

        int n = keys.length;
        double[][] e = new double[n + 2][n + 1];
        double[][] w = new double[n + 2][n + 1];
        int[][] root = new int[n + 1][n + 1];

        for (int i = 1; i <= n + 1; i++) {
            e[i][i - 1] = q[i - 1];
            w[i][i - 1] = q[i - 1];
        }

        for (int l = 1; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                e[i][j] = Double.MAX_VALUE;
                w[i][j] = w[i][j - 1] + p[j - 1] + q[j];

                for (int r = i; r <= j; r++) {
                    double t = e[i][r - 1] + e[r + 1][j] + w[i][j];
                    if (t < e[i][j]) {
                        e[i][j] = t;
                        root[i][j] = r;
                    }
                }
            }
        }

        return new OBSTResult(e[1][n], e, root, keys);
    }
}
