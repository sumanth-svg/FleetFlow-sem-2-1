package algorithms.suffix;

/**
 * Kasai's Algorithm for Longest Common Prefix (LCP) Array Construction.
 * 
 * Computes lcp[i] = length of the longest common prefix between suffix sa[i] and suffix sa[i-1].
 * 
 * Used in FleetFlow (Module 2 - CO2) to:
 * 1. Detect repeated delivery addresses and duplicate shipment descriptions.
 * 2. Analyze document similarity and extract common boilerplate terms across logistics contracts.
 * 
 * Time Complexity: O(N) linear time for text length N.
 * Space Complexity: O(N) for rank and LCP arrays.
 */
public class KasaiLCP {

    public static class LCPResult {
        private final int[] lcp;
        private final int maxLcpLength;
        private final String longestRepeatedSubstring;

        public LCPResult(int[] lcp, int maxLcpLength, String longestRepeatedSubstring) {
            this.lcp = lcp;
            this.maxLcpLength = maxLcpLength;
            this.longestRepeatedSubstring = longestRepeatedSubstring;
        }

        public int[] getLcp() {
            return lcp;
        }

        public int getMaxLcpLength() {
            return maxLcpLength;
        }

        public String getLongestRepeatedSubstring() {
            return longestRepeatedSubstring;
        }

        @Override
        public String toString() {
            return String.format("Longest Repeated Substring Length: %d | Repeated Content: '%s'",
                    maxLcpLength, longestRepeatedSubstring);
        }
    }

    /**
     * Computes LCP array using Kasai's Algorithm in O(N) time.
     *
     * @param text Original text string
     * @param sa   Suffix Array of text
     * @return LCPResult containing LCP array, maximum match length, and longest repeated string
     */
    public static LCPResult computeLCP(String text, int[] sa) {
        if (text == null || sa == null || text.length() == 0 || sa.length == 0) {
            return new LCPResult(new int[0], 0, "");
        }

        int n = text.length();
        int[] rank = new int[n];
        int[] lcp = new int[n];

        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int h = 0;
        int maxLcp = 0;
        int maxLcpIdx = -1;

        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];

                while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) {
                    h++;
                }

                lcp[rank[i]] = h;

                if (h > maxLcp) {
                    maxLcp = h;
                    maxLcpIdx = i;
                }

                if (h > 0) {
                    h--;
                }
            } else {
                lcp[0] = 0;
            }
        }

        String longestRepeated = maxLcpIdx != -1 ? text.substring(maxLcpIdx, maxLcpIdx + maxLcp) : "";

        return new LCPResult(lcp, maxLcp, longestRepeated);
    }
}
