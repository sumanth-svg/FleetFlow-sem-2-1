package util;

import java.util.ArrayList;
import java.util.List;

import model.Package;
import model.Shipment;

/**
 * SampleData utility for pre-loading realistic test data into FleetFlow.
 * Contains sample shipments, package codes, delivery keywords, and status log streams
 * specifically curated for testing String Algorithms (M1 - CO1).
 */
public class SampleData {

    /**
     * Generates a sample list of shipments with various ID patterns, package codes, and status keywords.
     */
    public static List<Shipment> getSampleShipments() {
        List<Shipment> shipments = new ArrayList<>();

        shipments.add(new Shipment("SHIP-2026-NY-101", "PKG-DELUXE-9901", "Acme Logistics", "John Doe", "New York", "Boston", 12.5));
        shipments.add(new Shipment("SHIP-2026-LA-202", "PKG-FRAGILE-8822", "Tech Supplies Inc", "Jane Smith", "Los Angeles", "San Francisco", 4.2));
        shipments.add(new Shipment("SHIP-2026-CHI-303", "PKG-STANDARD-1100", "Global Freight", "Robert Johnson", "Chicago", "Detroit", 45.0));
        shipments.add(new Shipment("EXPRESS-SHIP-NY-909", "PKG-HAZMAT-4455", "BioMed Corp", "Alice Brown", "New York", "Philadelphia", 8.8));
        shipments.add(new Shipment("URGENT-SHIP-BOS-505", "PKG-DELUXE-9902", "Priority Air", "Charlie Davis", "Boston", "Washington DC", 1.5));
        shipments.add(new Shipment("HUB1-HUB2-HUB1-HUB2", "PKG-REPEATING-777", "Loop Carriers", "Transit Warehouse", "Chicago", "Chicago", 22.0));
        shipments.add(new Shipment("SHIP-2026-NY-999", "PKG-FRAGILE-8823", "Glassware Ltd", "Eva Martinez", "New York", "Buffalo", 3.0));

        // Set realistic statuses
        shipments.get(0).setStatus("In Transit - DELIVERED TO HUB");
        shipments.get(1).setStatus("DELAYED - CUSTOMS_HOLD");
        shipments.get(2).setStatus("Out for Delivery");
        shipments.get(3).setStatus("FAILED_ATTEMPT - RETURN_TO_SENDER");
        shipments.get(4).setStatus("DELIVERED - SIGNED_BY_RECEIVER");
        shipments.get(5).setStatus("In Transit - ROUTE_LOOP_DETECTED");
        shipments.get(6).setStatus("DELAYED - WEATHER_HALT");

        return shipments;
    }

    /**
     * Generates sample packages for package repository and Rabin-Karp searches.
     */
    public static List<Package> getSamplePackages() {
        List<Package> packages = new ArrayList<>();
        packages.add(new Package("PKG-DELUXE-9901", "Electronics Component", 12.5, "In Transit"));
        packages.add(new Package("PKG-FRAGILE-8822", "Glassware Set", 4.2, "Delayed"));
        packages.add(new Package("PKG-STANDARD-1100", "Industrial Machinery Parts", 45.0, "Out for Delivery"));
        packages.add(new Package("PKG-HAZMAT-4455", "Chemical Samples", 8.8, "Failed Attempt"));
        packages.add(new Package("PKG-DELUXE-9902", "Medical Equipment", 1.5, "Delivered"));
        return packages;
    }

    /**
     * Generates a sample delivery status log stream for Aho-Corasick multi-pattern testing.
     */
    public static String getSampleStatusLogStream() {
        return "2026-09-27 08:00:00 [HUB-NY] Package PKG-DELUXE-9901 state updated to IN_TRANSIT.\n"
             + "2026-09-27 09:15:22 [HUB-LA] Package PKG-FRAGILE-8822 flagged as DELAYED due to CUSTOMS_HOLD.\n"
             + "2026-09-27 10:30:45 [HUB-BOS] Package PKG-DELUXE-9902 status: OUT_FOR_DELIVERY.\n"
             + "2026-09-27 11:45:10 [HUB-PHL] Package PKG-HAZMAT-4455 update: FAILED_ATTEMPT, marked as RETURN_TO_SENDER.\n"
             + "2026-09-27 12:00:00 [HUB-BOS] Package PKG-DELUXE-9902 confirmed DELIVERED.\n"
             + "2026-09-27 13:20:00 [HUB-CHI] Route anomaly: ROUTE_LOOP_DETECTED on SHIP-HUB1-HUB2-HUB1-HUB2.\n";
    }

    /**
     * Common delivery status keywords for Aho-Corasick automaton.
     */
    public static List<String> getDeliveryStatusKeywords() {
        List<String> keywords = new ArrayList<>();
        keywords.add("IN_TRANSIT");
        keywords.add("DELIVERED");
        keywords.add("DELAYED");
        keywords.add("CUSTOMS_HOLD");
        keywords.add("OUT_FOR_DELIVERY");
        keywords.add("FAILED_ATTEMPT");
        keywords.add("RETURN_TO_SENDER");
        keywords.add("ROUTE_LOOP_DETECTED");
        return keywords;
    }
}
