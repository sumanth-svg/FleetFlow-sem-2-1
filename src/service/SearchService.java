package service;

import java.util.List;
import java.util.Map;

import algorithms.string.AhoCorasick;
import algorithms.string.KMP;
import algorithms.string.RabinKarp;
import algorithms.string.ZAlgorithm;
import model.Package;
import model.Shipment;
import repository.PackageRepository;
import repository.ShipmentRepository;
import util.SampleData;

/**
 * SearchService binds Module 1 String Algorithms to FleetFlow domain objects and operations.
 */
public class SearchService {

    private ShipmentRepository shipmentRepository;
    private PackageRepository packageRepository;

    public SearchService() {
        this.shipmentRepository = new ShipmentRepository();
        this.packageRepository = new PackageRepository();

        // Load sample data
        for (Shipment s : SampleData.getSampleShipments()) {
            shipmentRepository.addShipment(s);
        }
        for (Package p : SampleData.getSamplePackages()) {
            packageRepository.addPackage(p);
        }
    }

    public SearchService(ShipmentRepository shipmentRepository, PackageRepository packageRepository) {
        this.shipmentRepository = shipmentRepository;
        this.packageRepository = packageRepository;
    }

    /**
     * Requirement 1: KMP to search shipment IDs and delivery keywords.
     */
    public List<Shipment> searchByKMP(String pattern) {
        System.out.println("\n[KMP Search] Searching for keyword / pattern: '" + pattern + "'");
        List<Shipment> idMatches = KMP.searchShipmentIds(shipmentRepository.getAllShipments(), pattern);
        List<Shipment> keywordMatches = KMP.searchKeywords(shipmentRepository.getAllShipments(), pattern);

        // Combine results without duplicates
        for (Shipment s : keywordMatches) {
            if (!idMatches.contains(s)) {
                idMatches.add(s);
            }
        }

        return idMatches;
    }

    /**
     * Requirement 2: Z-Function to detect repeated shipment patterns.
     */
    public ZAlgorithm.RepeatAnalysis detectRepeatedPattern(String sequence) {
        System.out.println("\n[Z-Algorithm] Analyzing string sequence for repetitions: '" + sequence + "'");
        return ZAlgorithm.analyzeRepetitions(sequence);
    }

    /**
     * Detect shipments containing repeated tracking / route patterns.
     */
    public List<Shipment> findShipmentsWithRepeatedPatterns() {
        return ZAlgorithm.detectShipmentsWithRepeatedPatterns(shipmentRepository.getAllShipments());
    }

    /**
     * Requirement 3: Rabin-Karp to search package codes efficiently.
     */
    public List<Shipment> searchPackageCodesByRabinKarp(String packageCodePattern) {
        System.out.println("\n[Rabin-Karp Search] Searching for Package Code pattern: '" + packageCodePattern + "'");
        return RabinKarp.searchPackageCodes(shipmentRepository.getAllShipments(), packageCodePattern);
    }

    /**
     * Requirement 4: Aho-Corasick to detect multiple delivery-status patterns.
     */
    public List<AhoCorasick.StatusMatch> scanLogStreamForStatuses(String logStream, List<String> statusKeywords) {
        System.out.println("\n[Aho-Corasick Multi-Pattern Search] Scanning log stream for " + statusKeywords.size() + " status patterns...");
        AhoCorasick ac = new AhoCorasick(statusKeywords);
        return ac.search(logStream);
    }

    public Map<String, Integer> countStatusFrequencies(String logStream, List<String> statusKeywords) {
        AhoCorasick ac = new AhoCorasick(statusKeywords);
        return ac.countStatusFrequencies(logStream);
    }

    /**
     * Demonstrates all 4 String Algorithms in a structured format.
     */
    public void runAllAlgorithmDemos() {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println("            FLEETFLOW STRING ALGORITHMS DEMONSTRATION");
        System.out.println("                     (Module 1 - CO1)");
        System.out.println("==================================================================");

        // 1. KMP Demo
        System.out.println("\n--- 1. KMP Algorithm (Shipment ID & Delivery Keyword Search) ---");
        String kmpQuery = "NY";
        List<Shipment> kmpResults = searchByKMP(kmpQuery);
        System.out.println("Found " + kmpResults.size() + " matching shipments for KMP query '" + kmpQuery + "':");
        for (Shipment s : kmpResults) {
            System.out.println("  -> " + s);
        }

        // 2. Z-Algorithm Demo
        System.out.println("\n--- 2. Z-Function Algorithm (Repeated Pattern Detection) ---");
        String testSequence1 = "HUB1-HUB2-HUB1-HUB2-HUB1-HUB2";
        ZAlgorithm.RepeatAnalysis zAnalysis1 = detectRepeatedPattern(testSequence1);
        System.out.println("Test Sequence 1: " + testSequence1);
        System.out.println("Z-Analysis Result: " + zAnalysis1);

        String testSequence2 = "SHIP-2026-NY-101";
        ZAlgorithm.RepeatAnalysis zAnalysis2 = detectRepeatedPattern(testSequence2);
        System.out.println("Test Sequence 2: " + testSequence2);
        System.out.println("Z-Analysis Result: " + zAnalysis2);

        // 3. Rabin-Karp Demo
        System.out.println("\n--- 3. Rabin-Karp Algorithm (Package Code Rolling Hash Search) ---");
        String rkQuery = "DELUXE";
        List<Shipment> rkResults = searchPackageCodesByRabinKarp(rkQuery);
        System.out.println("Found " + rkResults.size() + " shipments with Package Code matching '" + rkQuery + "':");
        for (Shipment s : rkResults) {
            System.out.println("  -> Shipment ID: " + s.getShipmentId() + " | Package Code: " + s.getPackageCode());
        }

        // 4. Aho-Corasick Demo
        System.out.println("\n--- 4. Aho-Corasick Automaton (Multi-Pattern Delivery Status Search) ---");
        String logStream = SampleData.getSampleStatusLogStream();
        List<String> keywords = SampleData.getDeliveryStatusKeywords();
        System.out.println("Scanning Log Stream:");
        System.out.println("------------------------------------------------------------------");
        System.out.println(logStream.trim());
        System.out.println("------------------------------------------------------------------");

        List<AhoCorasick.StatusMatch> matches = scanLogStreamForStatuses(logStream, keywords);
        System.out.println("Total Delivery Status Matches Found: " + matches.size());
        for (AhoCorasick.StatusMatch match : matches) {
            System.out.println("  -> " + match);
        }

        Map<String, Integer> freqs = countStatusFrequencies(logStream, keywords);
        System.out.println("\nStatus Keyword Frequencies:");
        for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
            if (entry.getValue() > 0) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " time(s)");
            }
        }
        System.out.println("==================================================================\n");
    }
}
