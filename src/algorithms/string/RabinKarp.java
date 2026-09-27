package algorithms.string;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Shipment;

/**
 * Rabin-Karp String Matching Algorithm.
 * 
 * Used in FleetFlow to:
 * 1. Search package codes (e.g., "PKG-88392") efficiently across shipment databases.
 * 2. Perform multi-pattern package code matching using rolling hashes.
 * 
 * Time Complexity:
 * - Average Case: O(N + M) where N is text length and M is pattern length.
 * - Worst Case: O(N * M) (when hash collisions occur frequently, mitigated by prime 10^9 + 7).
 * Space Complexity: O(1) auxiliary space (O(K) for result list).
 */
public class RabinKarp {

    // Alphabet size for ASCII/UTF-8 characters
    private static final int BASE = 256;
    // Large prime modulus to minimize hash collisions
    private static final long PRIME = 1000000007L;

    /**
     * Structure to store multi-pattern search results.
     */
    public static class PackageMatch {
        private String packageCode;
        private int matchIndex;

        public PackageMatch(String packageCode, int matchIndex) {
            this.packageCode = packageCode;
            this.matchIndex = matchIndex;
        }

        public String getPackageCode() {
            return packageCode;
        }

        public int getMatchIndex() {
            return matchIndex;
        }

        @Override
        public String toString() {
            return "Package '" + packageCode + "' found at index " + matchIndex;
        }
    }

    /**
     * Searches for occurrences of pattern in text using Rabin-Karp rolling hash (Case-Sensitive).
     *
     * @param text    Text string to search in
     * @param pattern Pattern string to search for
     * @return List of starting indices where pattern is found in text
     */
    public static List<Integer> search(String text, String pattern) {
        return search(text, pattern, true);
    }

    /**
     * Searches for occurrences of pattern in text using Rabin-Karp with case-sensitivity option.
     *
     * @param text          Text string to search in
     * @param pattern       Pattern string to search for
     * @param caseSensitive True for case-sensitive, false for case-insensitive
     * @return List of starting indices
     */
    public static List<Integer> search(String text, String pattern, boolean caseSensitive) {
        List<Integer> matches = new ArrayList<>();

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        String searchText = caseSensitive ? text : text.toLowerCase();
        String searchPattern = caseSensitive ? pattern : pattern.toLowerCase();

        int n = searchText.length();
        int m = searchPattern.length();

        // Calculate h = BASE^(m-1) % PRIME
        long h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % PRIME;
        }

        long patternHash = 0;
        long textWindowHash = 0;

        // Calculate initial hash values for pattern and first window of text
        for (int i = 0; i < m; i++) {
            patternHash = (BASE * patternHash + searchPattern.charAt(i)) % PRIME;
            textWindowHash = (BASE * textWindowHash + searchText.charAt(i)) % PRIME;
        }

        // Slide the pattern over text one character at a time
        for (int i = 0; i <= n - m; i++) {
            // If hash values match, check characters one by one to verify exact match
            if (patternHash == textWindowHash) {
                boolean exactMatch = true;
                for (int j = 0; j < m; j++) {
                    if (searchText.charAt(i + j) != searchPattern.charAt(j)) {
                        exactMatch = false;
                        break;
                    }
                }

                if (exactMatch) {
                    matches.add(i);
                }
            }

            // Calculate hash value for next window of text: Remove leading digit, add trailing digit
            if (i < n - m) {
                textWindowHash = (BASE * (textWindowHash - searchText.charAt(i) * h) + searchText.charAt(i + m)) % PRIME;

                // We might get negative value of textWindowHash, converting it to positive
                if (textWindowHash < 0) {
                    textWindowHash += PRIME;
                }
            }
        }

        return matches;
    }

    /**
     * Searches a list of shipments and returns shipments whose package code contains packageCodePattern.
     *
     * @param shipments          List of shipments to search
     * @param packageCodePattern Target package code or sub-code
     * @return List of matching Shipment objects
     */
    public static List<Shipment> searchPackageCodes(List<Shipment> shipments, String packageCodePattern) {
        List<Shipment> results = new ArrayList<>();

        if (shipments == null || packageCodePattern == null || packageCodePattern.isEmpty()) {
            return results;
        }

        for (Shipment shipment : shipments) {
            if (shipment.getPackageCode() != null) {
                List<Integer> matches = search(shipment.getPackageCode(), packageCodePattern, false);
                if (!matches.isEmpty()) {
                    results.add(shipment);
                }
            }
        }

        return results;
    }

    /**
     * Efficiently searches a large manifest or text block for multiple package codes using Rabin-Karp hashing.
     *
     * @param text         Manifest text containing multiple package codes
     * @param packageCodes List of package codes to look for (assumed equal or fixed length)
     * @return List of PackageMatch objects detailing which package codes were found where
     */
    public static List<PackageMatch> searchMultiplePackageCodes(String text, List<String> packageCodes) {
        List<PackageMatch> results = new ArrayList<>();

        if (text == null || packageCodes == null || packageCodes.isEmpty()) {
            return results;
        }

        // Filter package codes to group by length for optimal hashing
        for (String pattern : packageCodes) {
            if (pattern != null && !pattern.isEmpty()) {
                List<Integer> matchIndices = search(text, pattern, false);
                for (int idx : matchIndices) {
                    results.add(new PackageMatch(pattern, idx));
                }
            }
        }

        return results;
    }
}
