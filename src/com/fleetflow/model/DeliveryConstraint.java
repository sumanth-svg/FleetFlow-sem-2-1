package com.fleetflow.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a logistics delivery constraint formatted for 3-SAT solving.
 */
public class DeliveryConstraint {
    private final String constraintId;
    private final String category;
    private final String description;
    private final List<Integer> literals;

    public DeliveryConstraint(String constraintId, String category, String description, String cnfClause) {
        this.constraintId = constraintId;
        this.category = category;
        this.description = description;
        this.literals = new ArrayList<>();
        if (cnfClause != null && !cnfClause.trim().isEmpty()) {
            String[] tokens = cnfClause.trim().split("\\s+");
            for (String t : tokens) {
                if (!t.isEmpty()) {
                    literals.add(Integer.parseInt(t));
                }
            }
        }
    }

    public String getConstraintId() { return constraintId; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public List<Integer> getLiterals() { return literals; }

    @Override
    public String toString() {
        return String.format("[%s | %s] %s -> CNF: %s", constraintId, category, description, literals);
    }
}
