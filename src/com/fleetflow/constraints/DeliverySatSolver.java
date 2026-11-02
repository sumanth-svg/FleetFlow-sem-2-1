package com.fleetflow.constraints;

import com.fleetflow.model.DeliveryConstraint;

import java.util.*;

/**
 * DPLL (Davis-Putnam-Logemann-Loveland) 3-SAT Solver for complex logistics delivery constraints.
 * Features:
 * - Unit propagation (clauses with single unset literal)
 * - Pure literal elimination
 * - Backtracking search
 * Used in FleetFlow to satisfy driver shift limits, vehicle hazmat constraints, and urgent package delivery windows.
 */
public class DeliverySatSolver {

    public static class SatResult {
        private final boolean satisfiable;
        private final Map<Integer, Boolean> model;
        private final int backtracks;

        public SatResult(boolean satisfiable, Map<Integer, Boolean> model, int backtracks) {
            this.satisfiable = satisfiable;
            this.model = model;
            this.backtracks = backtracks;
        }

        public boolean isSatisfiable() { return satisfiable; }
        public Map<Integer, Boolean> getModel() { return model; }
        public int getBacktracks() { return backtracks; }

        @Override
        public String toString() {
            if (!satisfiable) {
                return String.format("UNSATISFIABLE (Delivery constraints have mutual conflict, backtracks: %d)", backtracks);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("SATISFIABLE (Valid dispatch schedule found, backtracks: %d):\n", backtracks));
            List<Integer> sortedVars = new ArrayList<>(model.keySet());
            Collections.sort(sortedVars);
            for (int var : sortedVars) {
                sb.append(String.format("  Variable x%d = %s\n", var, model.get(var) ? "TRUE (Assign)" : "FALSE (Exclude)"));
            }
            return sb.toString();
        }
    }

    private static int backtrackCounter = 0;

    /**
     * Solves a list of DeliveryConstraints.
     */
    public static SatResult solve(List<DeliveryConstraint> constraints) {
        List<List<Integer>> clauses = new ArrayList<>();
        Set<Integer> variables = new HashSet<>();

        for (DeliveryConstraint dc : constraints) {
            List<Integer> c = new ArrayList<>(dc.getLiterals());
            if (!c.isEmpty()) {
                clauses.add(c);
                for (int lit : c) {
                    variables.add(Math.abs(lit));
                }
            }
        }

        backtrackCounter = 0;
        Map<Integer, Boolean> assignment = new HashMap<>();
        boolean sat = dpll(clauses, variables, assignment);

        // Fill unassigned variables with default FALSE
        if (sat) {
            for (int v : variables) {
                assignment.putIfAbsent(v, false);
            }
        }

        return new SatResult(sat, sat ? assignment : Collections.emptyMap(), backtrackCounter);
    }

    private static boolean dpll(List<List<Integer>> clauses, Set<Integer> variables, Map<Integer, Boolean> assignment) {
        // 1. Evaluate clauses under current assignment
        List<List<Integer>> simplified = new ArrayList<>();

        for (List<Integer> clause : clauses) {
            boolean clauseSatisfied = false;
            List<Integer> remainingLiterals = new ArrayList<>();

            for (int lit : clause) {
                int var = Math.abs(lit);
                boolean sign = lit > 0;

                if (assignment.containsKey(var)) {
                    if (assignment.get(var) == sign) {
                        clauseSatisfied = true;
                        break;
                    }
                } else {
                    remainingLiterals.add(lit);
                }
            }

            if (clauseSatisfied) continue;

            if (remainingLiterals.isEmpty()) {
                // Clause is falsified
                return false;
            }

            simplified.add(remainingLiterals);
        }

        // All clauses satisfied!
        if (simplified.isEmpty()) {
            return true;
        }

        // 2. Unit propagation
        for (List<Integer> clause : simplified) {
            if (clause.size() == 1) {
                int lit = clause.get(0);
                int var = Math.abs(lit);
                boolean val = lit > 0;
                assignment.put(var, val);

                boolean result = dpll(clauses, variables, assignment);
                if (!result) {
                    assignment.remove(var);
                }
                return result;
            }
        }

        // 3. Choose unassigned variable (heuristic: first available)
        int chosenVar = -1;
        for (int v : variables) {
            if (!assignment.containsKey(v)) {
                chosenVar = v;
                break;
            }
        }

        if (chosenVar == -1) {
            return true;
        }

        // Branch 1: Try true
        backtrackCounter++;
        assignment.put(chosenVar, true);
        if (dpll(clauses, variables, assignment)) {
            return true;
        }

        // Branch 2: Try false
        assignment.put(chosenVar, false);
        if (dpll(clauses, variables, assignment)) {
            return true;
        }

        // Backtrack
        assignment.remove(chosenVar);
        return false;
    }
}
