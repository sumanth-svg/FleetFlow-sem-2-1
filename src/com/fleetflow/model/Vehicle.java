package com.fleetflow.model;

/**
 * Represents a delivery vehicle in the fleet (vans, semi-trucks, etc.).
 */
public class Vehicle {
    private final String id;
    private final String licensePlate;
    private final String model;
    private final double capacityKg;
    private final String currentWarehouseId;
    private String status;
    private double fuelLevelPct;

    public Vehicle(String id, String licensePlate, String model, double capacityKg,
                   String currentWarehouseId, String status, double fuelLevelPct) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.model = model;
        this.capacityKg = capacityKg;
        this.currentWarehouseId = currentWarehouseId;
        this.status = status;
        this.fuelLevelPct = fuelLevelPct;
    }

    public String getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public String getModel() { return model; }
    public double getCapacityKg() { return capacityKg; }
    public String getCurrentWarehouseId() { return currentWarehouseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getFuelLevelPct() { return fuelLevelPct; }
    public void setFuelLevelPct(double fuelLevelPct) { this.fuelLevelPct = fuelLevelPct; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Cap: %.0fkg, Status: %s, Fuel: %.1f%%",
                id, licensePlate, model, capacityKg, status, fuelLevelPct);
    }
}
