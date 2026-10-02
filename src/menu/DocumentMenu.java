package menu;

import java.util.List;
import java.util.Scanner;

import algorithms.suffix.KasaiLCP;
import algorithms.suffix.SuffixArray;
import algorithms.suffix.SuffixTree;
import model.ShipmentDocument;
import service.DocumentService;

/**
 * Interactive menu for Suffix Structures & Document Similarity (Module 2 - CO2).
 */
public class DocumentMenu {

    private final Scanner scanner;
    private final DocumentService documentService;

    public DocumentMenu(Scanner scanner) {
        this.scanner = scanner;
        this.documentService = new DocumentService();
    }

    public DocumentMenu(Scanner scanner, DocumentService documentService) {
        this.scanner = scanner;
        this.documentService = documentService;
    }

    public void display() {
        System.out.println();
        System.out.println("====================================================");
        System.out.println("     SUFFIX STRUCTURES & DOCUMENT SIMILARITY MENU");
        System.out.println("                   (Module 2 - CO2)");
        System.out.println("====================================================");
        System.out.println("1. Suffix Array Document Indexing & Search");
        System.out.println("2. SA-IS Large-Scale Logistics Document Indexing");
        System.out.println("3. Kasai LCP Repeated Address & Content Detection");
        System.out.println("4. Suffix Tree Fast Substring Query");
        System.out.println("5. Run Full Module 2 Demonstration Suite");
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
                    runSuffixArraySearch();
                    break;
                case 2:
                    runSAISIndexing();
                    break;
                case 3:
                    runKasaiLCPDetection();
                    break;
                case 4:
                    runSuffixTreeQuery();
                    break;
                case 5:
                    documentService.runAllDocumentDemos();
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

    private void runSuffixArraySearch() {
        System.out.println("\n--- Suffix Array Document Search ---");
        List<ShipmentDocument> docs = documentService.getDocuments();
        if (docs.isEmpty()) {
            System.out.println("No documents available.");
            return;
        }

        System.out.println("Select document to search:");
        for (int i = 0; i < docs.size(); i++) {
            System.out.println((i + 1) + ". " + docs.get(i).getTitle());
        }
        System.out.print("Enter document number: ");
        int docIdx = getChoice() - 1;

        if (docIdx < 0 || docIdx >= docs.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        ShipmentDocument doc = docs.get(docIdx);
        System.out.print("Enter search keyword/phrase: ");
        String pattern = scanner.nextLine().trim();

        if (pattern.isEmpty()) {
            System.out.println("Pattern cannot be empty.");
            return;
        }

        List<Integer> occurrences = documentService.searchDocumentWithSuffixArray(doc, pattern);
        System.out.println("Found " + occurrences.size() + " occurrence(s) in document at indices: " + occurrences);
        pause();
    }

    private void runSAISIndexing() {
        System.out.println("\n--- SA-IS Linear-Time Suffix Array Indexing ---");
        System.out.print("Enter large document text block (or press ENTER for sample combined documents): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ShipmentDocument d : documentService.getDocuments()) {
                sb.append(d.getContent()).append(" ");
            }
            input = sb.toString();
        }

        int[] sa = documentService.performSAISIndexing(input);
        System.out.println("SA-IS Array constructed for text of length " + input.length());
        System.out.println("Sample Suffix Array entries (first 10):");
        for (int i = 0; i < Math.min(10, sa.length); i++) {
            System.out.println("  sa[" + i + "] = " + sa[i] + " -> \"" + input.substring(sa[i], Math.min(sa[i] + 30, input.length())) + "...\"");
        }
        pause();
    }

    private void runKasaiLCPDetection() {
        System.out.println("\n--- Kasai LCP Repeated Address & Content Detection ---");
        System.out.print("Enter text to analyze for repeated addresses / boilerplates (or press ENTER for sample): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ShipmentDocument d : documentService.getDocuments()) {
                sb.append(d.getContent()).append("\n");
            }
            input = sb.toString();
        }

        KasaiLCP.LCPResult result = documentService.detectRepeatedContentWithKasai(input);
        System.out.println("\nKasai Analysis Result:");
        System.out.println(result);
        pause();
    }

    private void runSuffixTreeQuery() {
        System.out.println("\n--- Suffix Tree Fast Substring Query ---");
        List<ShipmentDocument> docs = documentService.getDocuments();
        if (docs.isEmpty()) {
            System.out.println("No documents available.");
            return;
        }

        ShipmentDocument doc = docs.get(0);
        System.out.println("Target Document: " + doc.getTitle());
        System.out.print("Enter query string: ");
        String pattern = scanner.nextLine().trim();

        if (pattern.isEmpty()) {
            System.out.println("Query cannot be empty.");
            return;
        }

        List<Integer> matches = documentService.querySubstringWithSuffixTree(doc, pattern);
        System.out.println("Suffix Tree returned " + matches.size() + " match(es) at character indices: " + matches);
        pause();
    }

    private void pause() {
        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}
