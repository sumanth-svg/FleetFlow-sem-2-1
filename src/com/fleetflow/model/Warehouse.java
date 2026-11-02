package com.fleetflow.model;

/**
 * Represents a warehouse or distribution center in the FleetFlow logistics network.
 */
public class Warehouse {
    private final String id;
    private final String name;
    private final String city;
    private final String state;
    private final double capacityTons;
    private final double latitude;
    private final double longitude;

    public Warehouse(String id, String name, String city, String state, double capacityTons, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.capacityTons = capacityTons;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public double getCapacityTons() { return capacityTons; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, %s) - Cap: %.1f tons", id, name, city, state, capacityTons);
    }
}
