package com.fleetflow.analytics;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Blelloch Work-Efficient Parallel Prefix Sum (Scan) Algorithm.
 * Work Complexity: O(N) operations.
 * Depth (Span) Complexity: O(log N) parallel steps.
 *
 * Consists of two phases on a balanced binary tree:
 * 1. Up-Sweep (Reduction Phase): computes partial sums up the tree.
 * 2. Down-Sweep Phase: distributes prefix sums down the tree.
 *
 * Used in FleetFlow to compute cumulative cargo weights, running fuel burn,
 * and progressive delivery statistics across thousands of logistics shipments in parallel.
 */
public class BlellochParallelScan {

    private static final ForkJoinPool POOL = ForkJoinPool.commonPool();

    public static class ScanResult {
        private final double[] original;
        private final double[] exclusiveScan;
        private final double[] inclusiveScan;
        private final double totalSum;

        public ScanResult(double[] original, double[] exclusiveScan, double[] inclusiveScan, double totalSum) {
            this.original = original;
            this.exclusiveScan = exclusiveScan;
            this.inclusiveScan = inclusiveScan;
            this.totalSum = totalSum;
        }

        public double[] getOriginal() { return original; }
        public double[] getExclusiveScan() { return exclusiveScan; }
        public double[] getInclusiveScan() { return inclusiveScan; }
        public double getTotalSum() { return totalSum; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Blelloch Scan (Total Sum: %.2f, %d items):\n", totalSum, original.length));
            int limit = Math.min(original.length, 8);
            for (int i = 0; i < limit; i++) {
                sb.append(String.format("  Item %d: val=%.1f | Running Excl=%.1f | Running Incl=%.1f\n",
                        i, original[i], exclusiveScan[i], inclusiveScan[i]));
            }
            if (original.length > limit) {
                sb.append(String.format("  ... (%d more items)\n", original.length - limit));
            }
            return sb.toString();
        }
    }

    /**
     * Executes work-efficient parallel Blelloch scan on input array.
     */
    public static ScanResult scan(double[] input) {
        int originalLen = input.length;
        if (originalLen == 0) {
            return new ScanResult(new double[0], new double[0], new double[0], 0.0);
        }

        // Round length up to next power of two
        int n = 1;
        while (n < originalLen) {
            n <<= 1;
        }

        double[] a = new double[n];
        System.arraycopy(input, 0, a, 0, originalLen);

        final int totalN = n;

        // 1. Up-Sweep (Reduce Phase)
        for (int d = 0; (1 << d) < totalN; d++) {
            final int step = 1 << (d + 1);
            final int halfStep = 1 << d;

            POOL.invoke(new RecursiveAction() {
                @Override
                protected void compute() {
                    for (int k = 0; k < totalN; k += step) {
                        a[k + step - 1] += a[k + halfStep - 1];
                    }
                }
            });
        }

        double totalSum = a[totalN - 1];

        // 2. Down-Sweep Phase: set root to 0
        a[totalN - 1] = 0.0;

        for (int d = (int) (Math.log(totalN) / Math.log(2)) - 1; d >= 0; d--) {
            final int step = 1 << (d + 1);
            final int halfStep = 1 << d;

            POOL.invoke(new RecursiveAction() {
                @Override
                protected void compute() {
                    for (int k = 0; k < totalN; k += step) {
                        double t = a[k + halfStep - 1];
                        a[k + halfStep - 1] = a[k + step - 1];
                        a[k + step - 1] += t;
                    }
                }
            });
        }

        double[] exclusive = new double[originalLen];
        double[] inclusive = new double[originalLen];

        System.arraycopy(a, 0, exclusive, 0, originalLen);
        for (int i = 0; i < originalLen; i++) {
            inclusive[i] = exclusive[i] + input[i];
        }

        return new ScanResult(input, exclusive, inclusive, totalSum);
    }
}
