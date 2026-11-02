package com.fleetflow.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Z-Algorithm for linear-time pattern matching and periodicity/repeated cycle detection.
 * Used in FleetFlow to detect repeated shipment patterns, circular route loops, and recurring sensor anomalies.
 */
public class ZAlgorithm {

    /**
     * Computes the Z-array for string s in O(N) time.
     * Z[i] is the length of the longest substring starting at s[i] that is also a prefix of s.
     */
    public static int[] computeZArray(String s) {
        int n = s.length();
        int[] z = new int[n];
        if (n == 0) return z;

        int l = 0, r = 0;
        z[0] = n;

        for (int i = 1; i < n; i++) {
            if (i > r) {
                l = r = i;
                while (r < n && s.charAt(r - l) == s.charAt(r)) {
                    r++;
                }
                z[i] = r - l;
                r--;
            } else {
                int k = i - l;
                if (z[k] < r - i + 1) {
                    z[i] = z[k];
                } else {
                    l = i;
                    while (r < n && s.charAt(r - l) == s.charAt(r)) {
                        r++;
                    }
                    z[i] = r - l;
                    r--;
                }
            }
        }
        return z;
    }

    /**
     * Searches for pattern in text using Z-algorithm with concatenation: pattern + "$" + text.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        String concat = pattern + "$" + text;
        int[] z = computeZArray(concat);
        int m = pattern.length();

        for (int i = m + 1; i < concat.length(); i++) {
            if (z[i] == m) {
                matches.add(i - (m + 1));
            }
        }
        return matches;
    }

    /**
     * Detects fundamental periodic patterns in a shipment status/route checkpoint sequence.
     * Returns the smallest period length T such that the sequence is periodic with period T.
     * Example: "WH1->WH2->WH1->WH2->WH1->WH2" -> returns period length.
     */
    public static int detectSmallestPeriod(String s) {
        int n = s.length();
        if (n == 0) return 0;
        int[] z = computeZArray(s);
        for (int p = 1; p < n; p++) {
            if (n % p == 0 && z[p] == n - p) {
                return p;
            }
        }
        return n; // no non-trivial period found
    }

    /**
     * Scans for repeated sub-patterns of minLength occurring more than minOccurrences times.
     */
    public static List<String> detectRepeatedShipmentPatterns(String sequence, int minLen) {
        List<String> repeated = new ArrayList<>();
        int n = sequence.length();
        for (int len = minLen; len <= n / 2; len++) {
            for (int i = 0; i <= n - 2 * len; i++) {
                String sub = sequence.substring(i, i + len);
                String rest = sequence.substring(i + len);
                List<Integer> occurrences = search(rest, sub);
                if (!occurrences.isEmpty() && !repeated.contains(sub)) {
                    repeated.add(sub);
                }
            }
        }
        return repeated;
    }
}
