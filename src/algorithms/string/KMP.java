package algorithms.string;

import java.util.ArrayList;
import java.util.List;
import model.Shipment;

/**
 * Knuth-Morris-Pratt (KMP) String Matching Algorithm.
 * 
 * Used in FleetFlow to:
 * 1. Search Shipment IDs for specific sub-patterns or prefixes/suffixes.
 * 2. Search delivery keywords in shipment details, notes, and addresses.
 * 
 * Time Complexity:
 * - Preprocessing (LPS computation): O(M) where M is pattern length.
 * - Search: O(N) where N is text length.
 * Total Time Complexity: O(N + M)
 * Space Complexity: O(M) for the LPS array.
 */
public class KMP {

    /**
     * Computes the Longest Prefix Suffix (LPS) array for the given pattern.
     * lps[i] stores the length of the longest proper prefix of pattern[0..i]
     * that is also a suffix of pattern[0..i].
     *
     * @param pattern The pattern string to preprocess
     * @return Array of LPS values
     */
    public static int[] computeLPSArray(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return new int[0];
        }

        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        lps[0] = 0;

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
     * Searches for all occurrences of pattern in text using the KMP algorithm (Case-Sensitive).
     *
     * @param text    The text string to search within
     * @param pattern The pattern string to search for
     * @return List of starting indices (0-indexed) where pattern occurs in text
     */
    public static List<Integer> search(String text, String pattern) {
        return search(text, pattern, true);
    }

    /**
     * Searches for all occurrences of pattern in text using KMP, with case-sensitivity option.
     *
     * @param text          The text string to search within
     * @param pattern       The pattern string to search for
     * @param caseSensitive True for case-sensitive match, false for case-insensitive match
     * @return List of starting indices where pattern occurs in text
     */
    public static List<Integer> search(String text, String pattern, boolean caseSensitive) {
        List<Integer> occurrences = new ArrayList<>();

        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return occurrences;
        }

        String searchText = caseSensitive ? text : text.toLowerCase();
        String searchPattern = caseSensitive ? pattern : pattern.toLowerCase();

        int n = searchText.length();
        int m = searchPattern.length();
        int[] lps = computeLPSArray(searchPattern);

        int i = 0; // index for searchText
        int j = 0; // index for searchPattern

        while (i < n) {
            if (searchPattern.charAt(j) == searchText.charAt(i)) {
                i++;
                j++;
            }

            if (j == m) {
                occurrences.add(i - j);
                j = lps[j - 1];
            } else if (i < n && searchPattern.charAt(j) != searchText.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return occurrences;
    }

    /**
     * Checks if the pattern exists anywhere inside text using KMP.
     *
     * @param text    The target string
     * @param pattern The pattern string
     * @return true if pattern is found, false otherwise
     */
    public static boolean contains(String text, String pattern) {
        return !search(text, pattern, false).isEmpty();
    }

    /**
     * Searches a list of shipments and returns those whose Shipment ID matches the pattern.
     *
     * @param shipments List of shipments to search
     * @param idPattern Pattern to match in Shipment ID
     * @return List of matching Shipment objects
     */
    public static List<Shipment> searchShipmentIds(List<Shipment> shipments, String idPattern) {
        List<Shipment> results = new ArrayList<>();
        if (shipments == null || idPattern == null || idPattern.isEmpty()) {
            return results;
        }

        for (Shipment shipment : shipments) {
            if (shipment.getShipmentId() != null && contains(shipment.getShipmentId(), idPattern)) {
                results.add(shipment);
            }
        }

        return results;
    }

    /**
     * Searches a list of shipments for delivery keywords in any field (Sender, Receiver, Source, Destination, Status).
     *
     * @param shipments List of shipments
     * @param keyword   Delivery keyword to search for
     * @return List of matching Shipment objects
     */
    public static List<Shipment> searchKeywords(List<Shipment> shipments, String keyword) {
        List<Shipment> results = new ArrayList<>();
        if (shipments == null || keyword == null || keyword.isEmpty()) {
            return results;
        }

        for (Shipment shipment : shipments) {
            String combinedData = (shipment.getShipmentId() + " "
                    + shipment.getPackageCode() + " "
                    + shipment.getSenderName() + " "
                    + shipment.getReceiverName() + " "
                    + shipment.getSource() + " "
                    + shipment.getDestination() + " "
                    + shipment.getStatus()).toLowerCase();

            if (contains(combinedData, keyword)) {
                results.add(shipment);
            }
        }

        return results;
    }
}
