package com.fleetflow.network;

import com.fleetflow.model.Driver;
import com.fleetflow.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * Maximum Bipartite Matching for assigning drivers to delivery vehicles and tasks.
 * Reduces bipartite matching to maximum network flow on a graph with source S and sink T.
 * Used in FleetFlow to match certified, available drivers to vehicle delivery shifts.
 */
public class BipartiteDriverMatcher {

    public static class Assignment {
        private final Driver driver;
        private final Vehicle vehicle;

        public Assignment(Driver driver, Vehicle vehicle) {
            this.driver = driver;
            this.vehicle = vehicle;
        }

        public Driver getDriver() { return driver; }
        public Vehicle getVehicle() { return vehicle; }

        @Override
        public String toString() {
            return String.format("%s (%s) ===> %s (%s, %.0fkg)",
                    driver.getName(), driver.getLicenseType(),
                    vehicle.getLicensePlate(), vehicle.getModel(), vehicle.getCapacityKg());
        }
    }

    public static class MatchingResult {
        private final List<Assignment> assignments;
        private final List<Driver> unassignedDrivers;
        private final List<Vehicle> unassignedVehicles;

        public MatchingResult(List<Assignment> assignments, List<Driver> unassignedDrivers, List<Vehicle> unassignedVehicles) {
            this.assignments = assignments;
            this.unassignedDrivers = unassignedDrivers;
            this.unassignedVehicles = unassignedVehicles;
        }

        public List<Assignment> getAssignments() { return assignments; }
        public List<Driver> getUnassignedDrivers() { return unassignedDrivers; }
        public List<Vehicle> getUnassignedVehicles() { return unassignedVehicles; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Total Matched Assignments: %d\n", assignments.size()));
            for (Assignment a : assignments) {
                sb.append("  * ").append(a.toString()).append("\n");
            }
            if (!unassignedDrivers.isEmpty()) {
                sb.append(String.format("Unassigned Drivers (%d): ", unassignedDrivers.size()));
                for (Driver d : unassignedDrivers) sb.append(d.getName()).append(", ");
                sb.setLength(sb.length() - 2);
                sb.append("\n");
            }
            if (!unassignedVehicles.isEmpty()) {
                sb.append(String.format("Unassigned Vehicles (%d): ", unassignedVehicles.size()));
                for (Vehicle v : unassignedVehicles) sb.append(v.getLicensePlate()).append(", ");
                sb.setLength(sb.length() - 2);
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Finds maximum cardinality bipartite matching between drivers and vehicles.
     * Compatibility rule:
     * - Driver must be AVAILABLE.
     * - CDL-CLASS-A can drive all vehicles (heavy trucks and vans).
     * - CDL-CLASS-B can drive vans and trucks <= 8,000 kg.
     */
    public static MatchingResult matchDriversToVehicles(List<Driver> drivers, List<Vehicle> vehicles) {
        int nDrivers = drivers.size();
        int nVehicles = vehicles.size();

        // Node indexing:
        // 0: Source S
        // 1 .. nDrivers: Driver nodes
        // nDrivers + 1 .. nDrivers + nVehicles: Vehicle nodes
        // nDrivers + nVehicles + 1: Sink T
        int source = 0;
        int sink = nDrivers + nVehicles + 1;
        FlowNetwork net = new FlowNetwork(sink + 1);

        net.setNodeName(source, "SOURCE");
        net.setNodeName(sink, "SINK");

        for (int i = 0; i < nDrivers; i++) {
            net.setNodeName(1 + i, "DRV_" + drivers.get(i).getId());
            net.addEdge(source, 1 + i, 1.0, "S->Driver");
        }

        for (int j = 0; j < nVehicles; j++) {
            net.setNodeName(1 + nDrivers + j, "VEH_" + vehicles.get(j).getId());
            net.addEdge(1 + nDrivers + j, sink, 1.0, "Vehicle->T");
        }

        // Add compatibility edges
        for (int i = 0; i < nDrivers; i++) {
            Driver d = drivers.get(i);
            if (!"AVAILABLE".equalsIgnoreCase(d.getShiftStatus())) {
                continue; // only available drivers can be dispatched
            }

            for (int j = 0; j < nVehicles; j++) {
                Vehicle v = vehicles.get(j);
                if (!"ACTIVE".equalsIgnoreCase(v.getStatus()) && !"IDLE".equalsIgnoreCase(v.getStatus())) {
                    continue; // vehicle undergoing maintenance
                }

                boolean compatible = false;
                if ("CDL-CLASS-A".equalsIgnoreCase(d.getLicenseType())) {
                    compatible = true;
                } else if ("CDL-CLASS-B".equalsIgnoreCase(d.getLicenseType()) && v.getCapacityKg() <= 8000) {
                    compatible = true;
                }

                if (compatible) {
                    net.addEdge(1 + i, 1 + nDrivers + j, 1.0, d.getName() + "->" + v.getModel());
                }
            }
        }

        // Run Dinic's Algorithm for max flow
        DinicsFlow.computeMaxFlow(net, source, sink);

        List<Assignment> matches = new ArrayList<>();
        boolean[] matchedDrivers = new boolean[nDrivers];
        boolean[] matchedVehicles = new boolean[nVehicles];

        for (int i = 0; i < nDrivers; i++) {
            int driverNode = 1 + i;
            for (FlowEdge edge : net.getAdj(driverNode)) {
                if (edge.getFlow() > 0.5 && edge.getTo() > nDrivers && edge.getTo() <= nDrivers + nVehicles) {
                    int vehicleIndex = edge.getTo() - (1 + nDrivers);
                    matches.add(new Assignment(drivers.get(i), vehicles.get(vehicleIndex)));
                    matchedDrivers[i] = true;
                    matchedVehicles[vehicleIndex] = true;
                }
            }
        }

        List<Driver> unassignedDrivers = new ArrayList<>();
        for (int i = 0; i < nDrivers; i++) {
            if (!matchedDrivers[i]) unassignedDrivers.add(drivers.get(i));
        }

        List<Vehicle> unassignedVehicles = new ArrayList<>();
        for (int j = 0; j < nVehicles; j++) {
            if (!matchedVehicles[j]) unassignedVehicles.add(vehicles.get(j));
        }

        return new MatchingResult(matches, unassignedDrivers, unassignedVehicles);
    }
}
