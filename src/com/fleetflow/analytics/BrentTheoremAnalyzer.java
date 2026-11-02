package com.fleetflow.analytics;

import java.util.ArrayList;
import java.util.List;

/**
 * Analytical model based on Brent's Theorem for evaluating parallel logistics processing performance.
 *
 * Mathematical Formulation:
 * - Work (W = T_1): Total number of computational operations performed by 1 processor.
 * - Span / Depth (D = T_inf): Length of the critical dependency path (time with infinite processors).
 * - Brent's Inequality: On a machine with p processors, the parallel execution time T_p satisfies:
 *       T_p <= floor((W - D) / p) + D <= W / p + D
 * - Theoretical Speedup: S_p = T_1 / T_p
 * - Parallel Efficiency: E_p = S_p / p
 * - Theoretical Parallelism Limit: W / D
 */
public class BrentTheoremAnalyzer {

    public static class ProcessorEvaluation {
        private final int processorCount;
        private final double theoreticalTimeTp;
        private final double theoreticalSpeedup;
        private final double efficiencyPct;

        public ProcessorEvaluation(int processorCount, double theoreticalTimeTp, double theoreticalSpeedup, double efficiencyPct) {
            this.processorCount = processorCount;
            this.theoreticalTimeTp = theoreticalTimeTp;
            this.theoreticalSpeedup = theoreticalSpeedup;
            this.efficiencyPct = efficiencyPct;
        }

        public int getProcessorCount() { return processorCount; }
        public double getTheoreticalTimeTp() { return theoreticalTimeTp; }
        public double getTheoreticalSpeedup() { return theoreticalSpeedup; }
        public double getEfficiencyPct() { return efficiencyPct; }

        @Override
        public String toString() {
            return String.format("  p = %2d cores | Time T_p: %8.2f us | Speedup: %5.2fx | Efficiency: %5.1f%%",
                    processorCount, theoreticalTimeTp, theoreticalSpeedup, efficiencyPct);
        }
    }

    public static class BrentAnalysisReport {
        private final String taskName;
        private final double workT1;
        private final double spanTinf;
        private final double theoreticalMaxSpeedup;
        private final List<ProcessorEvaluation> evaluations;

        public BrentAnalysisReport(String taskName, double workT1, double spanTinf,
                                   double theoreticalMaxSpeedup, List<ProcessorEvaluation> evaluations) {
            this.taskName = taskName;
            this.workT1 = workT1;
            this.spanTinf = spanTinf;
            this.theoreticalMaxSpeedup = theoreticalMaxSpeedup;
            this.evaluations = evaluations;
        }

        public String getTaskName() { return taskName; }
        public double getWorkT1() { return workT1; }
        public double getSpanTinf() { return spanTinf; }
        public double getTheoreticalMaxSpeedup() { return theoreticalMaxSpeedup; }
        public List<ProcessorEvaluation> getEvaluations() { return evaluations; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Brent's Theorem Parallel Scaling Analysis: [%s]\n", taskName));
            sb.append(String.format("  * Total Work (T_1): %,.0f ops\n", workT1));
            sb.append(String.format("  * Critical Span / Depth (T_inf): %,.0f steps\n", spanTinf));
            sb.append(String.format("  * Theoretical Parallelism Ceiling (T_1 / T_inf): %.2fx max speedup\n", theoreticalMaxSpeedup));
            sb.append("  * Predicted Scaling Curve across Core Configurations:\n");
            for (ProcessorEvaluation pe : evaluations) {
                sb.append(pe.toString()).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Evaluates Brent's theorem bounds for a given parallel algorithm.
     * @param taskName Description of the logistics algorithm (e.g., "Blelloch Parallel Scan", "Parallel MapReduce")
     * @param workT1 Total computational operations (Work)
     * @param spanTinf Critical path depth (Span)
     * @param processorCounts Array of processor counts to evaluate (e.g., {1, 2, 4, 8, 16, 32, 64})
     */
    public static BrentAnalysisReport evaluate(String taskName, double workT1, double spanTinf, int[] processorCounts) {
        double maxSpeedup = (spanTinf > 0) ? (workT1 / spanTinf) : 1.0;
        List<ProcessorEvaluation> evals = new ArrayList<>();

        for (int p : processorCounts) {
            if (p <= 0) continue;
            // Brent's upper bound: T_p <= (W - D) / p + D
            double tp = ((workT1 - spanTinf) / p) + spanTinf;
            if (p == 1) tp = workT1;

            double speedup = workT1 / tp;
            double efficiency = (speedup / p) * 100.0;

            evals.add(new ProcessorEvaluation(p, tp, speedup, efficiency));
        }

        return new BrentAnalysisReport(taskName, workT1, spanTinf, maxSpeedup, evals);
    }
}
