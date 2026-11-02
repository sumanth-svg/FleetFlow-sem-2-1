package com.fleetflow.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rabin-Karp polynomial rolling hash string search.
 * Used in FleetFlow to efficiently search single and multiple package codes (e.g. barcodes, tracking tokens)
 * across massive shipment manifests and warehouse inventories.
 */
public class RabinKarpSearcher {

    private static final long PRIME_MOD = 1_000_000_007L;
    private static final int BASE = 256;

    /**
     * Searches for a single pattern in text using Rabin-Karp rolling hash.
     */
    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int n = text.length();
        int m = pattern.length();

        // Compute BASE^(m-1) % PRIME_MOD
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % PRIME_MOD;
        }

        long pHash = 0;
        long tHash = 0;

        // Calculate initial hash value of pattern and first window of text
        for (int i = 0; i < m; i++) {
            pHash = (BASE * pHash + pattern.charAt(i)) % PRIME_MOD;
            tHash = (BASE * tHash + text.charAt(i)) % PRIME_MOD;
        }

        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                // Verify characters on hash match to prevent collision false positives
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }

            // Rolling hash for next window
            if (i < n - m) {
                tHash = (BASE * (tHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME_MOD;
                if (tHash < 0) {
                    tHash += PRIME_MOD;
                }
            }
        }

        return matches;
    }

    /**
     * Multi-pattern search for package codes of uniform or varying lengths.
     * Returns a map of pattern -> list of start indices in text.
     */
    public static Map<String, List<Integer>> searchMultiple(String text, List<String> patterns) {
        Map<String, List<Integer>> results = new HashMap<>();
        for (String p : patterns) {
            results.put(p, search(text, p));
        }
        return results;
    }
}
