package util;

import java.util.ArrayList;
import java.util.List;

import model.Package;
import model.Shipment;
import model.ShipmentDocument;

/**
 * SampleData utility for pre-loading realistic test data into FleetFlow.
 * Contains sample shipments, package codes, delivery keywords, status log streams,
 * and logistics documents for Modules M1, M2, and M3 testing.
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
     * Generates sample shipment documents for Module 2 Suffix Structures testing.
     */
    public static List<ShipmentDocument> getSampleDocuments() {
        List<ShipmentDocument> docs = new ArrayList<>();

        docs.add(new ShipmentDocument(
                "DOC-BOL-101",
                "Bill of Lading - NY to Boston",
                "Bill of Lading",
                "Consignment Bill of Lading. Origin: 100 Port Street, New York, NY 10001. Destination: 500 Freight Way, Boston, MA 02110. Contents: High priority electronic components. Special Instructions: Handle with care, store at room temperature.",
                "SHIP-2026-NY-101"
        ));

        docs.add(new ShipmentDocument(
                "DOC-REC-102",
                "Delivery Receipt - LA Distribution Hub",
                "Delivery Receipt",
                "Delivery Verification Receipt. Origin: 200 Harbor Drive, Los Angeles, CA 90001. Destination: 500 Freight Way, Boston, MA 02110. Recipient confirmed package received in good condition. Special Instructions: Handle with care, store at room temperature.",
                "SHIP-2026-LA-202"
        ));

        docs.add(new ShipmentDocument(
                "DOC-CUST-103",
                "Customs Declaration - Chicago Central Hub",
                "Customs Declaration",
                "Customs Clearance Manifesto. Entry Hub: 300 Logistics Blvd, Chicago, IL 60601. Destination: 500 Freight Way, Boston, MA 02110. Declared Cargo Value: $45,000 USD. Clearance Status: Approved after inspection.",
                "SHIP-2026-CHI-303"
        ));

        return docs;
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
