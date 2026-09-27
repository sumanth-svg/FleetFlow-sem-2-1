package menu;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import algorithms.string.AhoCorasick;
import algorithms.string.ZAlgorithm;
import model.Shipment;
import service.SearchService;
import util.SampleData;

/**
 * Interactive menu for String Algorithms (Module 1 - CO1).
 */
public class SearchMenu {

    private Scanner scanner;
    private SearchService searchService;

    public SearchMenu(Scanner scanner) {
        this.scanner = scanner;
        this.searchService = new SearchService();
    }

    public SearchMenu(Scanner scanner, SearchService searchService) {
        this.scanner = scanner;
        this.searchService = searchService;
    }

    public void display() {
        System.out.println();
        System.out.println("====================================================");
        System.out.println("          STRING ALGORITHMS SEARCH MENU");
        System.out.println("                 (Module 1 - CO1)");
        System.out.println("====================================================");
        System.out.println("1. KMP Search (Shipment IDs & Keywords)");
        System.out.println("2. Z-Algorithm Search (Repeated Patterns)");
        System.out.println("3. Rabin-Karp Search (Package Codes)");
        System.out.println("4. Aho-Corasick Search (Multi Delivery-Statuses)");
        System.out.println("5. Run Full String Algorithm Demo Suite");
        System.out.println("0. Return to Main Menu");
        System.out.println("----------------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    public void executeMenu() {
        boolean back = false;
        while (!back) {
            display();
            int choice = getChoice();
            switch (choice) {
                case 1:
                    runKMPSearch();
                    break;
                case 2:
                    runZAlgorithmSearch();
                    break;
                case 3:
                    runRabinKarpSearch();
                    break;
                case 4:
                    runAhoCorasickSearch();
                    break;
                case 5:
                    searchService.runAllAlgorithmDemos();
                    pause();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private int getChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Enter a number:");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return val;
    }

    private void runKMPSearch() {
        System.out.println("\n--- KMP String Matching ---");
        System.out.print("Enter Shipment ID or Keyword pattern to search: ");
        String pattern = scanner.nextLine().trim();

        if (pattern.isEmpty()) {
            System.out.println("Pattern cannot be empty.");
            return;
        }

        List<Shipment> matches = searchService.searchByKMP(pattern);
        System.out.println("Found " + matches.size() + " matching shipment(s):");
        for (Shipment s : matches) {
            s.displayShipment();
        }
        pause();
    }

    private void runZAlgorithmSearch() {
        System.out.println("\n--- Z-Algorithm Repeated Pattern Detection ---");
        System.out.println("1. Test a custom tracking/route sequence");
        System.out.println("2. Scan all existing shipments for repeating patterns");
        System.out.print("Enter option: ");
        int opt = getChoice();

        if (opt == 1) {
            System.out.print("Enter string sequence (e.g., HUB1-HUB2-HUB1-HUB2): ");
            String seq = scanner.nextLine().trim();
            ZAlgorithm.RepeatAnalysis result = searchService.detectRepeatedPattern(seq);
            System.out.println("\nAnalysis Result:");
            System.out.println(result);
        } else if (opt == 2) {
            List<Shipment> repeats = searchService.findShipmentsWithRepeatedPatterns();
            System.out.println("Found " + repeats.size() + " shipment(s) with repeating patterns:");
            for (Shipment s : repeats) {
                System.out.println("  -> " + s.getShipmentId() + " (" + s.getPackageCode() + ")");
            }
        } else {
            System.out.println("Invalid option.");
        }
        pause();
    }

    private void runRabinKarpSearch() {
        System.out.println("\n--- Rabin-Karp Package Code Search ---");
        System.out.print("Enter Package Code pattern to search: ");
        String pattern = scanner.nextLine().trim();

        if (pattern.isEmpty()) {
            System.out.println("Pattern cannot be empty.");
            return;
        }

        List<Shipment> matches = searchService.searchPackageCodesByRabinKarp(pattern);
        System.out.println("Found " + matches.size() + " matching shipment(s):");
        for (Shipment s : matches) {
            System.out.println("  -> Shipment ID: " + s.getShipmentId() + " | Package Code: " + s.getPackageCode());
        }
        pause();
    }

    private void runAhoCorasickSearch() {
        System.out.println("\n--- Aho-Corasick Multi-Pattern Delivery Status Search ---");
        System.out.println("Scanning sample log stream for status patterns...");
        String logStream = SampleData.getSampleStatusLogStream();
        List<String> keywords = SampleData.getDeliveryStatusKeywords();

        List<AhoCorasick.StatusMatch> matches = searchService.scanLogStreamForStatuses(logStream, keywords);
        System.out.println("\nFound " + matches.size() + " status match(es) in log stream:");
        for (AhoCorasick.StatusMatch m : matches) {
            System.out.println("  -> " + m);
        }

        Map<String, Integer> freqs = searchService.countStatusFrequencies(logStream, keywords);
        System.out.println("\nStatus Summary Counts:");
        for (Map.Entry<String, Integer> e : freqs.entrySet()) {
            System.out.println("  " + e.getKey() + " : " + e.getValue());
        }
        pause();
    }

    private void pause() {
        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}
