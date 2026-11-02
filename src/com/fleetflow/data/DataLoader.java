package com.fleetflow.data;

import com.fleetflow.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and parsing FleetFlow CSV data files with support for quoted strings and commas.
 */
public class DataLoader {

    public static List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }

    public static List<Warehouse> loadWarehouses(String filePath) {
        List<Warehouse> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> t = parseCsvLine(line);
                if (t.size() >= 7) {
                    list.add(new Warehouse(
                            t.get(0), t.get(1), t.get(2), t.get(3),
                            Double.parseDouble(t.get(4)),
                            Double.parseDouble(t.get(5)),
                            Double.parseDouble(t.get(6))
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading warehouses CSV: " + e.getMessage());
        }
        return list;
    }

    public static List<Vehicle> loadVehicles(String filePath) {
        List<Vehicle> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> t = parseCsvLine(line);
                if (t.size() >= 7) {
                    list.add(new Vehicle(
                            t.get(0), t.get(1), t.get(2),
                            Double.parseDouble(t.get(3)),
                            t.get(4), t.get(5),
                            Double.parseDouble(t.get(6))
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading vehicles CSV: " + e.getMessage());
        }
        return list;
    }

    public static List<Driver> loadDrivers(String filePath) {
        List<Driver> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> t = parseCsvLine(line);
                if (t.size() >= 7) {
                    list.add(new Driver(
                            t.get(0), t.get(1), t.get(2),
                            Integer.parseInt(t.get(3)),
                            t.get(4), t.get(5),
                            Double.parseDouble(t.get(6))
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading drivers CSV: " + e.getMessage());
        }
        return list;
    }

    public static List<PackageItem> loadPackages(String filePath) {
        List<PackageItem> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> t = parseCsvLine(line);
                if (t.size() >= 11) {
                    list.add(new PackageItem(
                            t.get(0), t.get(1), t.get(2), t.get(3),
                            t.get(4), t.get(5), t.get(6),
                            Double.parseDouble(t.get(7)),
                            t.get(8), t.get(9),
                            Double.parseDouble(t.get(10))
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading packages CSV: " + e.getMessage());
        }
        return list;
    }

    public static List<ShipmentDocument> loadDocuments(String filePath) {
        List<ShipmentDocument> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> t = parseCsvLine(line);
                if (t.size() >= 4) {
                    list.add(new ShipmentDocument(
                            t.get(0), t.get(1), t.get(2), t.get(3)
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading documents CSV: " + e.getMessage());
        }
        return list;
    }

    public static List<TelemetryEvent> loadTelemetry(String filePath) {
        List<TelemetryEvent> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> t = parseCsvLine(line);
                if (t.size() >= 9) {
                    list.add(new TelemetryEvent(
                            t.get(0), t.get(1), t.get(2),
                            Double.parseDouble(t.get(3)),
                            Double.parseDouble(t.get(4)),
                            Double.parseDouble(t.get(5)),
                            Double.parseDouble(t.get(6)),
                            Double.parseDouble(t.get(7)),
                            t.get(8)
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading telemetry CSV: " + e.getMessage());
        }
        return list;
    }

    public static List<DeliveryConstraint> loadConstraints(String filePath) {
        List<DeliveryConstraint> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                List<String> t = parseCsvLine(line);
                if (t.size() >= 4) {
                    list.add(new DeliveryConstraint(
                            t.get(0), t.get(1), t.get(2), t.get(3)
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading constraints CSV: " + e.getMessage());
        }
        return list;
    }
}
