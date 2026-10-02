package menu;

import java.util.Scanner;

import algorithms.dp.BitmaskTSP;
import algorithms.dp.IntervalDP;
import algorithms.dp.KnapsackDP;
import algorithms.dp.OptimalBST;
import algorithms.editdistance.WagnerFischer;
import algorithms.sequence.NeedlemanWunsch;
import algorithms.sequence.SmithWaterman;
import service.DPService;

/**
 * Interactive menu for Dynamic Programming & Optimization Algorithms (Module 3 - CO3).
 */
public class DPMenu {

    private final Scanner scanner;
    private final DPService dpService;

    public DPMenu(Scanner scanner) {
        this.scanner = scanner;
        this.dpService = new DPService();
    }

    public DPMenu(Scanner scanner, DPService dpService) {
        this.scanner = scanner;
        this.dpService = dpService;
    }

    public void display() {
        System.out.println();
        System.out.println("====================================================");
        System.out.println("     DYNAMIC PROGRAMMING & OPTIMIZATION MENU");
        System.out.println("                    (Module 3 - CO3)");
        System.out.println("====================================================");
        System.out.println("1. 0/1 Knapsack Payload Optimization (DP Framework)");
        System.out.println("2. Wagner-Fischer Edit Distance & Fuzzy Address Match");
        System.out.println("3. Needleman-Wunsch Global Sequence Alignment");
        System.out.println("4. Smith-Waterman Local Sequence Alignment");
        System.out.println("5. Interval DP Hub Batch Consolidation");
        System.out.println("6. Bitmask DP Travelling Salesperson Problem (TSP)");
        System.out.println("7. Optimal Binary Search Tree (OBST Key Lookup Optimization)");
        System.out.println("8. Run Full DP Algorithm Demo Suite");
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
                    runKnapsackPayloadOptimization();
                    break;
                case 2:
                    runEditDistanceFuzzyMatch();
                    break;
                case 3:
                    runNeedlemanWunschAlignment();
                    break;
                case 4:
                    runSmithWatermanAlignment();
                    break;
                case 5:
                    runIntervalDPConsolidation();
                    break;
                case 6:
                    runBitmaskTSPRouting();
                    break;
                case 7:
                    runOptimalBSTLookup();
                    break;
                case 8:
                    dpService.runAllDPDemos();
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

    private void runKnapsackPayloadOptimization() {
        System.out.println("\n--- 0/1 Knapsack Vehicle Payload Optimization ---");
        System.out.print("Enter maximum payload weight capacity (in kg, e.g. 30): ");
        int capacity = getChoice();
        if (capacity <= 0) {
            System.out.println("Capacity must be positive.");
            return;
        }

        KnapsackDP.KnapsackResult result = dpService.optimizeVehiclePayload(capacity);
        System.out.println("\nOptimization Result:");
        System.out.println(result);
        pause();
    }

    private void runEditDistanceFuzzyMatch() {
        System.out.println("\n--- Wagner-Fischer Edit Distance & Address Matching ---");
        System.out.print("Enter source address / string: ");
        String src = scanner.nextLine().trim();
        System.out.print("Enter target address / string: ");
        String tgt = scanner.nextLine().trim();

        if (src.isEmpty() || tgt.isEmpty()) {
            System.out.println("Inputs cannot be empty.");
            return;
        }

        WagnerFischer.EditDistanceResult result = dpService.correctAddressTypo(src, tgt);
        System.out.println("\nEdit Distance Result:");
        System.out.println(result);
        System.out.println("\nStep-by-Step Edit Operations:");
        for (WagnerFischer.OperationStep op : result.getOperations()) {
            System.out.println("  -> " + op);
        }

        int dlDist = dpService.computeDamerauLevenshtein(src, tgt);
        System.out.println("Damerau-Levenshtein (Transposition aware): " + dlDist);
        pause();
    }

    private void runNeedlemanWunschAlignment() {
        System.out.println("\n--- Needleman-Wunsch Global Sequence Alignment ---");
        System.out.print("Enter planned route string (e.g. HUB_NY->HUB_PHL->HUB_BAL->HUB_WDC): ");
        String s1 = scanner.nextLine().trim();
        System.out.print("Enter actual route string  (e.g. HUB_NY->HUB_PHL->HUB_WDC): ");
        String s2 = scanner.nextLine().trim();

        if (s1.isEmpty() || s2.isEmpty()) {
            System.out.println("Inputs cannot be empty.");
            return;
        }

        NeedlemanWunsch.AlignmentResult result = dpService.alignRouteSequences(s1, s2);
        System.out.println("\nGlobal Alignment Result:");
        System.out.println(result);
        pause();
    }

    private void runSmithWatermanAlignment() {
        System.out.println("\n--- Smith-Waterman Local Sequence Alignment ---");
        System.out.print("Enter telemetry/log stream text: ");
        String logText = scanner.nextLine().trim();
        System.out.print("Enter target pattern to locate: ");
        String pattern = scanner.nextLine().trim();

        if (logText.isEmpty() || pattern.isEmpty()) {
            System.out.println("Inputs cannot be empty.");
            return;
        }

        SmithWaterman.LocalAlignmentResult result = dpService.detectLocalRouteSubsegment(logText, pattern);
        System.out.println("\nLocal Subsegment Alignment Result:");
        System.out.println(result);
        pause();
    }

    private void runIntervalDPConsolidation() {
        System.out.println("\n--- Interval DP Hub Stage Consolidation ---");
        IntervalDP.IntervalDPResult result = dpService.optimizeHubConsolidation();
        System.out.println("\nInterval DP Result:");
        System.out.println(result);
        pause();
    }

    private void runBitmaskTSPRouting() {
        System.out.println("\n--- Bitmask DP & TSP Multi-Stop Delivery Routing ---");
        BitmaskTSP.TSPResult result = dpService.optimizeFleetDeliveryTour();
        System.out.println("\nOptimal Closed-Loop Tour:");
        System.out.println(result);
        pause();
    }

    private void runOptimalBSTLookup() {
        System.out.println("\n--- Optimal Binary Search Tree (OBST) Key Lookup ---");
        OptimalBST.OBSTResult result = dpService.optimizeShipmentKeyLookup();
        System.out.println("\nOBST Result:");
        System.out.println(result);
        pause();
    }

    private void pause() {
        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}
