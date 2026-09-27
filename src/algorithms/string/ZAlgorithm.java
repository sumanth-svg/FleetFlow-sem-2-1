package algorithms.string;

import java.util.ArrayList;
import java.util.List;
import model.Shipment;

/**
 * Z-Algorithm (Z-Function) Implementation.
 * 
 * Used in FleetFlow to:
 * 1. Detect repeated shipment patterns, periodic tracking histories, and route loops.
 * 2. Perform fast string pattern matching using Z-array concatenation.
 * 
 * Time Complexity:
 * - Z-Array Computation: O(N) where N is length of string.
 * - Pattern Search: O(N + M) where M is pattern length.
 * Space Complexity: O(N)
 */
public class ZAlgorithm {

    /**
     * Inner class representing the results of repeated pattern analysis.
     */
    public static class RepeatAnalysis {
        private String originalText;
        private String repeatingUnit;
        private int periodLength;
        private int repeatCount;
        private boolean fullyPeriodic;

        public RepeatAnalysis(String originalText, String repeatingUnit, int periodLength, int repeatCount, boolean fullyPeriodic) {
            this.originalText = originalText;
            this.repeatingUnit = repeatingUnit;
            this.periodLength = periodLength;
            this.repeatCount = repeatCount;
            this.fullyPeriodic = fullyPeriodic;
        }

        public String getOriginalText() {
            return originalText;
        }

        public String getRepeatingUnit() {
            return repeatingUnit;
        }

        public int getPeriodLength() {
            return periodLength;
        }

        public int getRepeatCount() {
            return repeatCount;
        }

        public boolean isFullyPeriodic() {
            return fullyPeriodic;
        }

        @Override
        public String toString() {
            if (fullyPeriodic) {
                return "Fully Periodic Pattern: '" + repeatingUnit + "' (Length " + periodLength + ") repeated " + repeatCount + " times.";
            } else if (repeatCount > 1) {
                return "Partial Repeated Pattern: '" + repeatingUnit + "' repeated " + repeatCount + " times in '" + originalText + "'";
            } else {
                return "No repeating pattern detected in: '" + originalText + "'";
            }
        }
    }

    /**
     * Calculates the Z-array for the given string.
     * Z[i] is the length of the longest common prefix between S and the suffix of S starting at index i.
     * Z[0] is set to 0 by convention.
     *
     * @param text The input string
     * @return Z-array of integers
     */
    public static int[] calculateZ(String text) {
        if (text == null || text.isEmpty()) {
            return new int[0];
        }

        int n = text.length();
        int[] z = new int[n];
        int l = 0;
        int r = 0;

        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }

            while (i + z[i] < n && text.charAt(z[i]) == text.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }

        return z;
    }

    /**
     * Pattern matching using Z-Algorithm.
     * Constructs string S = pattern + "$" + text and calculates Z-array.
     *
     * @param text    The text to search in
     * @param pattern The pattern to search for
     * @return List of matching start indices in text
     */
    public static List<Integer> searchPattern(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        String concat = pattern + "$" + text;
        int patternLength = pattern.length();
        int[] z = calculateZ(concat);

        for (int i = 0; i < z.length; i++) {
            if (z[i] == patternLength) {
                // Adjust index relative to original text
                matches.add(i - patternLength - 1);
            }
        }

        return matches;
    }

    /**
     * Analyzes a string (such as tracking logs or route sequences) to detect repeated patterns or string periodicity.
     * Uses Z-function property: If n % k == 0 and Z[k] == n - k, then prefix of length k is the fundamental period.
     * Also supports route token periodicity analysis.
     *
     * @param sequence String sequence to analyze
     * @return RepeatAnalysis object containing details of repetition
     */
    public static RepeatAnalysis analyzeRepetitions(String sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return new RepeatAnalysis(sequence, "", 0, 0, false);
        }

        int n = sequence.length();
        int[] z = calculateZ(sequence);

        // 1. Check for full character-level string periodicity first (smallest period k)
        for (int k = 1; k <= n / 2; k++) {
            if (n % k == 0 && z[k] == n - k) {
                String unit = sequence.substring(0, k);
                return new RepeatAnalysis(sequence, unit, k, n / k, true);
            }
        }

        // 2. Check token-based pattern repetition (e.g. "HUB1-HUB2-HUB1-HUB2-HUB1-HUB2")
        String[] tokens = sequence.split("(?<=[-,>])|(?=[-,>])");
        if (tokens.length > 1) {
            int numTokens = tokens.length;
            for (int k = 1; k <= numTokens / 2; k++) {
                boolean periodic = true;
                for (int i = k; i < numTokens; i++) {
                    if (!tokens[i].equals(tokens[i % k])) {
                        periodic = false;
                        break;
                    }
                }
                if (periodic) {
                    StringBuilder unitSb = new StringBuilder();
                    for (int i = 0; i < k; i++) {
                        unitSb.append(tokens[i]);
                    }
                    return new RepeatAnalysis(sequence, unitSb.toString(), unitSb.length(), numTokens / k, true);
                }
            }
        }

        // 3. Fallback to longest repeated prefix pattern via Z-array
        int maxRepeats = 1;
        int bestPeriod = 0;
        String bestUnit = "";

        for (int k = 1; k <= n / 2; k++) {
            int count = 1 + (z[k] / k);
            if (count > maxRepeats) {
                maxRepeats = count;
                bestPeriod = k;
                bestUnit = sequence.substring(0, k);
            }
        }

        if (maxRepeats > 1) {
            return new RepeatAnalysis(sequence, bestUnit, bestPeriod, maxRepeats, false);
        }

        return new RepeatAnalysis(sequence, sequence, n, 1, false);
    }

    /**
     * Searches a list of shipments and returns those whose Shipment ID or package code exhibits repeated sub-patterns.
     *
     * @param shipments List of shipments
     * @return List of shipments with repeated patterns detected
     */
    public static List<Shipment> detectShipmentsWithRepeatedPatterns(List<Shipment> shipments) {
        List<Shipment> repeatingShipments = new ArrayList<>();

        if (shipments == null) {
            return repeatingShipments;
        }

        for (Shipment shipment : shipments) {
            RepeatAnalysis idAnalysis = analyzeRepetitions(shipment.getShipmentId());
            RepeatAnalysis pkgAnalysis = analyzeRepetitions(shipment.getPackageCode());

            if (idAnalysis.getRepeatCount() > 1 || pkgAnalysis.getRepeatCount() > 1) {
                repeatingShipments.add(shipment);
            }
        }

        return repeatingShipments;
    }
}
