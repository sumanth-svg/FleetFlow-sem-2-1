package com.fleetflow.data;

import com.fleetflow.model.*;

import java.io.File;
import java.util.*;

/**
 * In-memory repository for FleetFlow logistics entities loaded from dataset CSV files.
 */
public class FleetRepository {
    private final List<Warehouse> warehouses = new ArrayList<>();
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Driver> drivers = new ArrayList<>();
    private final List<PackageItem> packages = new ArrayList<>();
    private final List<ShipmentDocument> documents = new ArrayList<>();
    private final List<TelemetryEvent> telemetryEvents = new ArrayList<>();
    private final List<DeliveryConstraint> constraints = new ArrayList<>();

    private final Map<String, Warehouse> warehouseMap = new HashMap<>();
    private final Map<String, Vehicle> vehicleMap = new HashMap<>();
    private final Map<String, Driver> driverMap = new HashMap<>();
    private final Map<String, PackageItem> packageMap = new HashMap<>();

    public FleetRepository(String datasetDirPath) {
        loadData(datasetDirPath);
    }

    public void loadData(String datasetDirPath) {
        File dir = new File(datasetDirPath);
        if (!dir.exists()) {
            // try relative path fallback
            dir = new File("dataset");
            if (!dir.exists()) {
                dir = new File("../dataset");
            }
        }

        String basePath = dir.getAbsolutePath() + File.separator;

        warehouses.addAll(DataLoader.loadWarehouses(basePath + "warehouses.csv"));
        for (Warehouse w : warehouses) warehouseMap.put(w.getId(), w);

        vehicles.addAll(DataLoader.loadVehicles(basePath + "vehicles.csv"));
        for (Vehicle v : vehicles) vehicleMap.put(v.getId(), v);

        drivers.addAll(DataLoader.loadDrivers(basePath + "drivers.csv"));
        for (Driver d : drivers) driverMap.put(d.getId(), d);

        packages.addAll(DataLoader.loadPackages(basePath + "packages.csv"));
        for (PackageItem p : packages) packageMap.put(p.getId(), p);

        documents.addAll(DataLoader.loadDocuments(basePath + "shipment_documents.csv"));
        telemetryEvents.addAll(DataLoader.loadTelemetry(basePath + "telemetry_events.csv"));
        constraints.addAll(DataLoader.loadConstraints(basePath + "delivery_constraints.csv"));
    }

    public List<Warehouse> getWarehouses() { return Collections.unmodifiableList(warehouses); }
    public List<Vehicle> getVehicles() { return Collections.unmodifiableList(vehicles); }
    public List<Driver> getDrivers() { return Collections.unmodifiableList(drivers); }
    public List<PackageItem> getPackages() { return Collections.unmodifiableList(packages); }
    public List<ShipmentDocument> getDocuments() { return Collections.unmodifiableList(documents); }
    public List<TelemetryEvent> getTelemetryEvents() { return Collections.unmodifiableList(telemetryEvents); }
    public List<DeliveryConstraint> getConstraints() { return Collections.unmodifiableList(constraints); }

    public Warehouse getWarehouseById(String id) { return warehouseMap.get(id); }
    public Vehicle getVehicleById(String id) { return vehicleMap.get(id); }
    public Driver getDriverById(String id) { return driverMap.get(id); }
    public PackageItem getPackageById(String id) { return packageMap.get(id); }

    public void addPackage(PackageItem pkg) {
        packages.add(pkg);
        packageMap.put(pkg.getId(), pkg);
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        vehicleMap.put(vehicle.getId(), vehicle);
    }

    public void addDocument(ShipmentDocument doc) {
        documents.add(doc);
    }
}

