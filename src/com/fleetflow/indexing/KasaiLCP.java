package com.fleetflow.indexing;

import java.util.ArrayList;
import java.util.List;

/**
 * Kasai's algorithm for linear time O(N) Longest Common Prefix (LCP) array construction.
 * Used in FleetFlow to detect repeated delivery addresses, duplicate shipment clauses,
 * and common manifest descriptions across documents.
 */
public class KasaiLCP {

    public static class RepeatedSubstring {
        private final String text;
        private final int length;
        private final int position1;
        private final int position2;

        public RepeatedSubstring(String text, int length, int position1, int position2) {
            this.text = text;
            this.length = length;
            this.position1 = position1;
            this.position2 = position2;
        }

        public String getText() { return text; }
        public int getLength() { return length; }
        public int getPosition1() { return position1; }
        public int getPosition2() { return position2; }

        @Override
        public String toString() {
            return String.format("Repeated (len=%d) at [%d, %d]: \"%s\"", length, position1, position2, text);
        }
    }

    /**
     * Computes the LCP array in O(N) using Kasai's algorithm.
     * lcp[i] is the length of the longest common prefix between suffixes starting at sa[i] and sa[i-1].
     * By definition, lcp[0] = 0.
     */
    public static int[] computeLCP(String s, int[] sa) {
        int n = s.length();
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[sa[i]] = i;
        }

        int[] lcp = new int[n];
        int h = 0;

        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = sa[rank[i] - 1];
                while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--;
                }
            }
        }
        return lcp;
    }

    /**
     * Finds the longest repeated substring in a document using SA and LCP array.
     */
    public static RepeatedSubstring findLongestRepeatedSubstring(String s, int[] sa, int[] lcp) {
        int maxLen = 0;
        int bestIndex = -1;

        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > maxLen) {
                maxLen = lcp[i];
                bestIndex = i;
            }
        }

        if (bestIndex == -1 || maxLen == 0) {
            return new RepeatedSubstring("", 0, -1, -1);
        }

        int pos1 = sa[bestIndex - 1];
        int pos2 = sa[bestIndex];
        String rep = s.substring(pos2, pos2 + maxLen);
        return new RepeatedSubstring(rep, maxLen, pos1, pos2);
    }

    /**
     * Finds all significant repeated text blocks (e.g. repeated address patterns, boilerplates)
     * with length greater than minLength.
     */
    public static List<RepeatedSubstring> findRepeatedBlocks(String s, int[] sa, int[] lcp, int minLength) {
        List<RepeatedSubstring> blocks = new ArrayList<>();
        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] >= minLength) {
                int pos1 = sa[i - 1];
                int pos2 = sa[i];
                String text = s.substring(pos2, pos2 + lcp[i]);
                blocks.add(new RepeatedSubstring(text, lcp[i], pos1, pos2));
            }
        }
        return blocks;
    }
}
