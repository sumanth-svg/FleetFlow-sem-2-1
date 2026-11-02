package com.fleetflow.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) string search algorithm.
 * Used in FleetFlow to search shipment IDs, tracking numbers, and delivery keywords in O(N + M) time.
 */
public class KMPSearcher {

    /**
     * Computes the Longest Proper Prefix that is also a Suffix (LPS) array for the given pattern.
     */
    public static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Searches for occurrences of pattern in text using KMP.
     * @return List of 0-based starting indices where pattern matches.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int[] lps = computeLPS(pattern);
        int i = 0; // index for text
        int j = 0; // index for pattern
        int n = text.length();
        int m = pattern.length();

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == m) {
                matches.add(i - j);
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    /**
     * Case-insensitive variant of KMP search for logistics keywords.
     */
    public static List<Integer> searchIgnoreCase(String text, String pattern) {
        if (text == null || pattern == null) return new ArrayList<>();
        return search(text.toLowerCase(), pattern.toLowerCase());
    }
}
