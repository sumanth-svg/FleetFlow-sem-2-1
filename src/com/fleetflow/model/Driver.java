package com.fleetflow.model;

/**
 * Represents a logistics driver in FleetFlow.
 */
public class Driver {
    private final String id;
    private final String name;
    private final String licenseType;
    private final int yearsExperience;
    private final String assignedWarehouseId;
    private String shiftStatus;
    private final double rating;

    public Driver(String id, String name, String licenseType, int yearsExperience,
                  String assignedWarehouseId, String shiftStatus, double rating) {
        this.id = id;
        this.name = name;
        this.licenseType = licenseType;
        this.yearsExperience = yearsExperience;
        this.assignedWarehouseId = assignedWarehouseId;
        this.shiftStatus = shiftStatus;
        this.rating = rating;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLicenseType() { return licenseType; }
    public int getYearsExperience() { return yearsExperience; }
    public String getAssignedWarehouseId() { return assignedWarehouseId; }
    public String getShiftStatus() { return shiftStatus; }
    public void setShiftStatus(String shiftStatus) { this.shiftStatus = shiftStatus; }
    public double getRating() { return rating; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, %d yrs exp, Rating: %.2f) - Shift: %s",
                id, name, licenseType, yearsExperience, rating, shiftStatus);
    }
}
