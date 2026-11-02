package com.fleetflow.model;

/**
 * Represents a customer package / cargo parcel in the logistics system.
 */
public class PackageItem {
    private final String id;
    private final String trackingCode;
    private final String originWarehouse;
    private final String destAddress;
    private final String destCity;
    private final String destState;
    private final String destZip;
    private final double weightKg;
    private final String priority; // URGENT, HIGH, STANDARD, LOW
    private String status;
    private final double valueUsd;

    public PackageItem(String id, String trackingCode, String originWarehouse,
                       String destAddress, String destCity, String destState, String destZip,
                       double weightKg, String priority, String status, double valueUsd) {
        this.id = id;
        this.trackingCode = trackingCode;
        this.originWarehouse = originWarehouse;
        this.destAddress = destAddress;
        this.destCity = destCity;
        this.destState = destState;
        this.destZip = destZip;
        this.weightKg = weightKg;
        this.priority = priority;
        this.status = status;
        this.valueUsd = valueUsd;
    }

    public String getId() { return id; }
    public String getTrackingCode() { return trackingCode; }
    public String getOriginWarehouse() { return originWarehouse; }
    public String getDestAddress() { return destAddress; }
    public String getDestCity() { return destCity; }
    public String getDestState() { return destState; }
    public String getDestZip() { return destZip; }
    public double getWeightKg() { return weightKg; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getValueUsd() { return valueUsd; }

    public int getPriorityRank() {
        switch (priority.toUpperCase()) {
            case "URGENT": return 4;
            case "HIGH": return 3;
            case "STANDARD": return 2;
            case "LOW": default: return 1;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s | %s] To: %s, %s %s - %.1fkg | Priority: %s | Status: %s | $%.2f",
                id, trackingCode, destAddress, destCity, destZip, weightKg, priority, status, valueUsd);
    }
}
