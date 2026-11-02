package com.fleetflow.indexing;

import java.util.Arrays;

/**
 * Standard Suffix Array implementation with O(N log^2 N) construction and O(M log N) binary search queries.
 * Serves as a reference and baseline indexer for shipment descriptions and delivery documents.
 */
public class SuffixArray {

    private final String text;
    private final int n;
    private final int[] sa;

    private static class SuffixRank implements Comparable<SuffixRank> {
        int index;
        int rank1;
        int rank2;

        @Override
        public int compareTo(SuffixRank o) {
            if (this.rank1 != o.rank1) return Integer.compare(this.rank1, o.rank1);
            return Integer.compare(this.rank2, o.rank2);
        }
    }

    public SuffixArray(String text) {
        this.text = text;
        this.n = text.length();
        this.sa = buildSuffixArray(text);
    }

    private int[] buildSuffixArray(String s) {
        int len = s.length();
        SuffixRank[] suffixes = new SuffixRank[len];
        for (int i = 0; i < len; i++) {
            suffixes[i] = new SuffixRank();
            suffixes[i].index = i;
            suffixes[i].rank1 = s.charAt(i);
            suffixes[i].rank2 = (i + 1 < len) ? s.charAt(i + 1) : -1;
        }

        Arrays.sort(suffixes);
        int[] ind = new int[len];

        for (int k = 4; k < 2 * len; k *= 2) {
            int rank = 0;
            int prevRank1 = suffixes[0].rank1;
            suffixes[0].rank1 = rank;
            ind[suffixes[0].index] = 0;

            for (int i = 1; i < len; i++) {
                if (suffixes[i].rank1 == prevRank1 && suffixes[i].rank2 == suffixes[i - 1].rank2) {
                    prevRank1 = suffixes[i].rank1;
                    suffixes[i].rank1 = rank;
                } else {
                    prevRank1 = suffixes[i].rank1;
                    suffixes[i].rank1 = ++rank;
                }
                ind[suffixes[i].index] = i;
            }

            for (int i = 0; i < len; i++) {
                int nextIndex = suffixes[i].index + k / 2;
                suffixes[i].rank2 = (nextIndex < len) ? suffixes[ind[nextIndex]].rank1 : -1;
            }

            Arrays.sort(suffixes);
        }

        int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            result[i] = suffixes[i].index;
        }
        return result;
    }

    public int[] getArray() {
        return sa;
    }

    public String getText() {
        return text;
    }

    /**
     * Binary search to find if pattern exists in the indexed document.
     * @return 0-based start index of an occurrence in text, or -1 if not found.
     */
    public int search(String pattern) {
        int low = 0, high = n - 1;
        int m = pattern.length();

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int suffixStart = sa[mid];
            int cmp = comparePrefix(text, suffixStart, pattern);

            if (cmp == 0) {
                return suffixStart;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    /**
     * Counts all occurrences of pattern in text using binary search lower/upper bounds.
     */
    public int countOccurrences(String pattern) {
        if (pattern == null || pattern.isEmpty()) return 0;
        int m = pattern.length();

        // Find lower bound
        int low = 0, high = n - 1;
        int first = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = comparePrefix(text, sa[mid], pattern);
            if (cmp >= 0) {
                if (cmp == 0) first = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        if (first == -1) return 0;

        // Find upper bound
        low = 0; high = n - 1;
        int last = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int cmp = comparePrefix(text, sa[mid], pattern);
            if (cmp <= 0) {
                if (cmp == 0) last = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return (last - first + 1);
    }

    private static int comparePrefix(String text, int suffixStart, String pattern) {
        int n = text.length();
        int m = pattern.length();
        for (int i = 0; i < m; i++) {
            if (suffixStart + i >= n) return -1; // suffix is shorter than pattern
            char tc = text.charAt(suffixStart + i);
            char pc = pattern.charAt(i);
            if (tc != pc) return Character.compare(tc, pc);
        }
        return 0; // pattern matches prefix of suffix
    }
}
