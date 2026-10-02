package service;

import java.util.ArrayList;
import java.util.List;

import algorithms.dp.BitmaskTSP;
import algorithms.dp.IntervalDP;
import algorithms.dp.KnapsackDP;
import algorithms.dp.OptimalBST;
import algorithms.editdistance.WagnerFischer;
import algorithms.sequence.NeedlemanWunsch;
import algorithms.sequence.SmithWaterman;
import model.Package;
import util.SampleData;

/**
 * DPService binds Module 3 Dynamic Programming & Optimization Algorithms
 * to FleetFlow domain models and operational workflows.
 */
public class DPService {

    public DPService() {
    }

    /**
     * Requirement 1: Payload Optimization via 0/1 Knapsack DP.
     */
    public KnapsackDP.KnapsackResult optimizeVehiclePayload(int capacityKg) {
        List<Package> packages = SampleData.getSamplePackages();
        int n = packages.size();

        int[] weights = new int[n];
        int[] values = new int[n];
        String[] names = new String[n];

        for (int i = 0; i < n; i++) {
            Package p = packages.get(i);
            weights[i] = (int) Math.round(p.getWeight());
            values[i] = (int) Math.round(p.getWeight() * 10 + (p.getPackageCode().contains("DELUXE") ? 50 : 20));
            names[i] = p.getPackageCode() + " (" + p.getDescription() + ")";
        }

        System.out.println("\n[DP Payload Optimization] Running 0/1 Knapsack for max capacity " + capacityKg + " kg...");
        return KnapsackDP.solve01KnapsackTabulation(weights, values, names, capacityKg);
    }

    /**
     * Requirement 2: Address Fuzzy Correction & Edit Distance via Wagner-Fischer / Levenshtein.
     */
    public WagnerFischer.EditDistanceResult correctAddressTypo(String inputAddress, String targetAddress) {
        System.out.println("\n[Wagner-Fischer Edit Distance] Comparing input '" + inputAddress + "' with target '" + targetAddress + "'");
        return WagnerFischer.computeLevenshteinDistance(inputAddress, targetAddress);
    }

    public int computeDamerauLevenshtein(String s1, String s2) {
        return WagnerFischer.computeDamerauLevenshteinDistance(s1, s2);
    }

    /**
     * Requirement 3: Global Sequence Alignment via Needleman-Wunsch.
     */
    public NeedlemanWunsch.AlignmentResult alignRouteSequences(String plannedRoute, String actualRoute) {
        System.out.println("\n[Needleman-Wunsch Global Alignment] Aligning Planned vs Actual Hub Routes...");
        return NeedlemanWunsch.align(plannedRoute, actualRoute);
    }

    /**
     * Requirement 4: Local Sequence Alignment via Smith-Waterman.
     */
    public SmithWaterman.LocalAlignmentResult detectLocalRouteSubsegment(String logStream, String pattern) {
        System.out.println("\n[Smith-Waterman Local Alignment] Locating pattern segment in log stream...");
        return SmithWaterman.alignLocal(logStream, pattern);
    }

    /**
     * Requirement 5: Hub Consolidation via Interval DP / Matrix-Chain.
     */
    public IntervalDP.IntervalDPResult optimizeHubConsolidation() {
        System.out.println("\n[Interval DP] Calculating optimal consolidation hierarchy for contiguous hub stages...");
        int[] dimensions = {10, 20, 30, 40, 30};
        String[] hubs = {"Hub-Alpha", "Hub-Beta", "Hub-Gamma", "Hub-Delta"};
        return IntervalDP.computeOptimalHubMerge(dimensions, hubs);
    }

    /**
     * Requirement 6: Optimal Multi-Stop Fleet Routing via Bitmask TSP.
     */
    public BitmaskTSP.TSPResult optimizeFleetDeliveryTour() {
        System.out.println("\n[Bitmask DP & TSP] Computing optimal closed-loop delivery route (Held-Karp algorithm)...");
        String[] hubs = {"Depot Chicago", "New York Hub", "Boston Hub", "Philadelphia Hub", "Washington DC Hub"};
        double[][] distanceMatrix = {
                {0, 790, 980, 750, 700},
                {790, 0, 215, 95, 225},
                {980, 215, 0, 305, 435},
                {750, 95, 305, 0, 140},
                {700, 225, 435, 140, 0}
        };

        return BitmaskTSP.solveTSP(distanceMatrix, hubs);
    }

    /**
     * Requirement 7: Optimal Binary Search Tree (OBST) for frequently accessed shipment keys.
     */
    public OptimalBST.OBSTResult optimizeShipmentKeyLookup() {
        System.out.println("\n[Optimal BST DP] Building Optimal Binary Search Tree for frequently accessed shipment keys...");
        String[] keys = {"SHIP-101", "SHIP-202", "SHIP-303", "SHIP-404", "SHIP-505"};
        double[] p = {0.15, 0.25, 0.05, 0.30, 0.20};
        double[] q = {0.01, 0.01, 0.01, 0.01, 0.00, 0.01};

        return OptimalBST.computeOptimalBST(keys, p, q);
    }

    /**
     * Demonstrates all Dynamic Programming, Edit Distance, Alignment, OBST, and Bitmask TSP algorithms.
     */
    public void runAllDPDemos() {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println("         FLEETFLOW DYNAMIC PROGRAMMING & OPTIMIZATION DEMO");
        System.out.println("==================================================================");

        // 1. Dynamic Programming Framework & 0/1 Knapsack
        System.out.println("\n--- 1. DP Framework & 0/1 Knapsack Payload Optimization ---");
        KnapsackDP.KnapsackResult knapsackRes = optimizeVehiclePayload(30);
        System.out.println(knapsackRes);

        // 2. Edit Distance & Wagner-Fischer
        System.out.println("\n--- 2. Edit Distance & Wagner-Fischer Algorithm Variants ---");
        String inputAddress = "New Yrok Cty";
        String targetAddress = "New York City";
        WagnerFischer.EditDistanceResult editRes = correctAddressTypo(inputAddress, targetAddress);
        System.out.println(editRes);

        String typo1 = "BOS-NY";
        String typo2 = "BSO-NY";
        int dlDist = computeDamerauLevenshtein(typo1, typo2);
        System.out.println("Damerau-Levenshtein Transposition Distance ('" + typo1 + "' vs '" + typo2 + "'): " + dlDist);

        // 3. Sequence Alignment
        System.out.println("\n--- 3. Sequence Alignment (Needleman-Wunsch & Smith-Waterman) ---");
        String plannedRoute = "HUB_NY->HUB_PHL->HUB_BAL->HUB_WDC";
        String actualRoute = "HUB_NY->HUB_PHL->HUB_WDC";
        NeedlemanWunsch.AlignmentResult nwRes = alignRouteSequences(plannedRoute, actualRoute);
        System.out.println("Needleman-Wunsch Global Route Alignment:");
        System.out.println(nwRes);

        // 4. Interval DP & Bitmask TSP
        System.out.println("\n--- 4. Interval DP & Bitmask TSP Route Optimization ---");
        IntervalDP.IntervalDPResult intervalRes = optimizeHubConsolidation();
        System.out.println(intervalRes);

        BitmaskTSP.TSPResult tspRes = optimizeFleetDeliveryTour();
        System.out.println("\nHeld-Karp Bitmask DP TSP Result:");
        System.out.println(tspRes);

        // 5. Optimal Binary Search Tree (OBST)
        System.out.println("\n--- 5. Optimal Binary Search Tree (OBST Key Lookup) ---");
        OptimalBST.OBSTResult obstRes = optimizeShipmentKeyLookup();
        System.out.println(obstRes);
        System.out.println("==================================================================\n");
    }
}
