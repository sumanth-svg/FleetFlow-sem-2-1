package com.fleetflow.optimization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Damerau-Levenshtein distance algorithm (with adjacent transpositions).
 * Used in FleetFlow for intelligent delivery address correction, street name typo correction,
 * and fuzzy matching of customer shipping destinations.
 */
public class DamerauLevenshtein {

    public static class AddressMatch {
        private final String candidate;
        private final int distance;
        private final double similarity;

        public AddressMatch(String candidate, int distance, double similarity) {
            this.candidate = candidate;
            this.distance = distance;
            this.similarity = similarity;
        }

        public String getCandidate() { return candidate; }
        public int getDistance() { return distance; }
        public double getSimilarity() { return similarity; }

        @Override
        public String toString() {
            return String.format("\"%s\" (Distance: %d, Match: %.1f%%)", candidate, distance, similarity * 100);
        }
    }

    /**
     * Computes the Damerau-Levenshtein distance between two strings s1 and s2,
     * supporting insertion, deletion, substitution, and transposition of adjacent characters.
     */
    public static int computeDistance(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        int len1 = s1.length();
        int len2 = s2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;

                int deletion = dp[i - 1][j] + 1;
                int insertion = dp[i][j - 1] + 1;
                int substitution = dp[i - 1][j - 1] + cost;

                dp[i][j] = Math.min(deletion, Math.min(insertion, substitution));

                // Adjacent transposition check (Damerau condition)
                if (i > 1 && j > 1 &&
                    s1.charAt(i - 1) == s2.charAt(j - 2) &&
                    s1.charAt(i - 2) == s2.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }
        return dp[len1][len2];
    }

    /**
     * Calculates normalized similarity score in range [0.0, 1.0].
     */
    public static double computeSimilarity(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        int dist = computeDistance(s1.toLowerCase(), s2.toLowerCase());
        return 1.0 - ((double) dist / maxLen);
    }

    /**
     * Corrects a potentially misspelled address by finding the top k closest matches
     * from a master catalog of verified logistics delivery addresses.
     */
    public static List<AddressMatch> correctAddress(String inputAddress, List<String> verifiedAddresses, int topK) {
        List<AddressMatch> matches = new ArrayList<>();
        String normalizedInput = inputAddress.trim().toLowerCase();

        for (String addr : verifiedAddresses) {
            String normAddr = addr.trim().toLowerCase();
            int dist = computeDistance(normalizedInput, normAddr);
            int maxLen = Math.max(normalizedInput.length(), normAddr.length());
            double sim = (maxLen == 0) ? 1.0 : (1.0 - ((double) dist / maxLen));
            matches.add(new AddressMatch(addr, dist, sim));
        }

        matches.sort(Comparator.comparingInt(AddressMatch::getDistance)
                .thenComparing((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity())));

        if (matches.size() > topK) {
            return matches.subList(0, topK);
        }
        return matches;
    }
}
