package com.fleetflow.analytics;

import com.fleetflow.model.PackageItem;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Parallel Tree Reduction using ForkJoin framework.
 * Work Complexity: O(N).
 * Span (Depth): O(log N).
 * Used in FleetFlow for high-throughput aggregation of logistics metrics across large package inventories
 * and vehicle fleets (total cargo weight, total parcel value, critical delays).
 */
public class ParallelLogisticsReducer {

    private static final int THRESHOLD = 1000;
    private static final ForkJoinPool POOL = ForkJoinPool.commonPool();

    public static class AggregateStats {
        private final int count;
        private final double totalWeightKg;
        private final double totalValueUsd;
        private final int urgentCount;
        private final long executionTimeNanos;

        public AggregateStats(int count, double totalWeightKg, double totalValueUsd, int urgentCount, long executionTimeNanos) {
            this.count = count;
            this.totalWeightKg = totalWeightKg;
            this.totalValueUsd = totalValueUsd;
            this.urgentCount = urgentCount;
            this.executionTimeNanos = executionTimeNanos;
        }

        public int getCount() { return count; }
        public double getTotalWeightKg() { return totalWeightKg; }
        public double getTotalValueUsd() { return totalValueUsd; }
        public int getUrgentCount() { return urgentCount; }
        public long getExecutionTimeNanos() { return executionTimeNanos; }

        @Override
        public String toString() {
            return String.format("Logistics Aggregation Summary:\n" +
                            "  * Processed Shipments: %,d\n" +
                            "  * Total Cargo Weight: %,.2f kg (%,.2f tons)\n" +
                            "  * Total Consignment Value: $%,.2f USD\n" +
                            "  * Urgent / Priority Shipments: %,d\n" +
                            "  * Parallel Compute Time: %.3f ms (using %d ForkJoin workers)",
                    count, totalWeightKg, totalWeightKg / 1000.0, totalValueUsd, urgentCount,
                    executionTimeNanos / 1e6, POOL.getParallelism());
        }
    }

    private static class PartialStats {
        int count;
        double weight;
        double value;
        int urgent;

        PartialStats(int count, double weight, double value, int urgent) {
            this.count = count;
            this.weight = weight;
            this.value = value;
            this.urgent = urgent;
        }

        static PartialStats combine(PartialStats a, PartialStats b) {
            return new PartialStats(
                    a.count + b.count,
                    a.weight + b.weight,
                    a.value + b.value,
                    a.urgent + b.urgent
            );
        }
    }

    private static class ReduceTask extends RecursiveTask<PartialStats> {
        private final List<PackageItem> list;
        private final int start;
        private final int end;

        ReduceTask(List<PackageItem> list, int start, int end) {
            this.list = list;
            this.start = start;
            this.end = end;
        }

        @Override
        protected PartialStats compute() {
            if (end - start <= THRESHOLD) {
                // Sequential base case
                int count = 0;
                double weight = 0.0;
                double value = 0.0;
                int urgent = 0;

                for (int i = start; i < end; i++) {
                    PackageItem p = list.get(i);
                    count++;
                    weight += p.getWeightKg();
                    value += p.getValueUsd();
                    if ("URGENT".equalsIgnoreCase(p.getPriority()) || "HIGH".equalsIgnoreCase(p.getPriority())) {
                        urgent++;
                    }
                }
                return new PartialStats(count, weight, value, urgent);
            }

            int mid = (start + end) >>> 1;
            ReduceTask left = new ReduceTask(list, start, mid);
            ReduceTask right = new ReduceTask(list, mid, end);

            left.fork();
            PartialStats rightRes = right.compute();
            PartialStats leftRes = left.join();

            return PartialStats.combine(leftRes, rightRes);
        }
    }

    /**
     * Executes parallel reduction over package list.
     */
    public static AggregateStats aggregate(List<PackageItem> packages) {
        if (packages == null || packages.isEmpty()) {
            return new AggregateStats(0, 0, 0, 0, 0);
        }

        long start = System.nanoTime();
        PartialStats res = POOL.invoke(new ReduceTask(packages, 0, packages.size()));
        long duration = System.nanoTime() - start;

        return new AggregateStats(res.count, res.weight, res.value, res.urgent, duration);
    }
}
