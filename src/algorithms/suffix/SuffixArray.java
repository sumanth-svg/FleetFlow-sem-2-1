package algorithms.suffix;

import java.util.Arrays;

/**
 * Suffix Array and SA-IS (Suffix Array Induced Sorting) Algorithm.
 * 
 * Used in FleetFlow (Module 2 - CO2) to:
 * 1. Index shipment descriptions and delivery documents for fast pattern search.
 * 2. Perform SA-IS linear-time (O(N)) large-scale logistics document indexing.
 * 3. Search document keywords in O(M log N) time via Binary Search on Suffix Array.
 * 
 * Time Complexity:
 * - SA-IS Construction: O(N) linear time for document length N.
 * - Pattern Search: O(M log N) via Binary Search.
 * Space Complexity: O(N) for suffix array representation.
 */
public class SuffixArray {

    private final String text;
    private final int n;
    private final int[] sa; // Suffix Array: sa[i] is starting index of i-th lexicographically smallest suffix

    public SuffixArray(String text) {
        this.text = text == null ? "" : text;
        this.n = this.text.length();
        this.sa = buildSAIS(this.text);
    }

    public String getText() {
        return text;
    }

    public int[] getSuffixArray() {
        return sa;
    }

    public int length() {
        return n;
    }

    /**
     * Binary Search pattern matching on Suffix Array in O(M log N) time.
     *
     * @param pattern Query string to find
     * @return First index in suffix array where pattern is found, or -1 if not found
     */
    public int searchPattern(String pattern) {
        if (pattern == null || pattern.isEmpty() || n == 0) return -1;
        int m = pattern.length();
        int low = 0, high = n - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int suffixStart = sa[mid];
            int cmp = text.substring(suffixStart, Math.min(suffixStart + m, n)).compareTo(pattern);

            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    /**
     * Returns all starting positions of pattern occurrences in text.
     */
    public int[] searchAllOccurrences(String pattern) {
        if (pattern == null || pattern.isEmpty() || n == 0) return new int[0];
        int m = pattern.length();
        int idx = searchPattern(pattern);
        if (idx == -1) return new int[0];

        // Find lower bound
        int left = idx;
        while (left > 0 && text.substring(sa[left - 1], Math.min(sa[left - 1] + m, n)).equals(pattern)) {
            left--;
        }

        // Find upper bound
        int right = idx;
        while (right < n - 1 && text.substring(sa[right + 1], Math.min(sa[right + 1] + m, n)).equals(pattern)) {
            right++;
        }

        int count = right - left + 1;
        int[] occurrences = new int[count];
        for (int i = 0; i < count; i++) {
            occurrences[i] = sa[left + i];
        }
        Arrays.sort(occurrences);
        return occurrences;
    }

    /**
     * Entry method to build Suffix Array using SA-IS (Suffix Array Induced Sorting) O(N) algorithm.
     */
    public static int[] buildSAIS(String str) {
        if (str == null || str.isEmpty()) return new int[0];

        int[] s = new int[str.length() + 1];
        int maxVal = 0;
        for (int i = 0; i < str.length(); i++) {
            s[i] = (int) str.charAt(i) + 1;
            maxVal = Math.max(maxVal, s[i]);
        }
        s[str.length()] = 0; // Sentinel $

        int[] saWithSentinel = sais(s, maxVal + 1);

        int[] sa = new int[str.length()];
        System.arraycopy(saWithSentinel, 1, sa, 0, str.length());
        return sa;
    }

    /**
     * Core SA-IS Algorithm (Nong, Zhang, Chan). Linear time O(N) suffix array construction.
     */
    private static int[] sais(int[] s, int K) {
        int n = s.length;
        if (n == 1) return new int[]{0};

        boolean[] isS = new boolean[n];
        isS[n - 1] = true;

        for (int i = n - 2; i >= 0; i--) {
            if (s[i] < s[i + 1]) isS[i] = true;
            else if (s[i] > s[i + 1]) isS[i] = false;
            else isS[i] = isS[i + 1];
        }

        int[] lms = new int[n];
        int lmsCount = 0;
        for (int i = 1; i < n; i++) {
            if (isS[i] && !isS[i - 1]) {
                lms[lmsCount++] = i;
            }
        }

        int[] sa = new int[n];
        induceSort(s, sa, isS, lms, lmsCount, K);

        int[] sortedLms = new int[lmsCount];
        int j = 0;
        for (int i = 0; i < n; i++) {
            if (sa[i] > 0 && isS[sa[i]] && !isS[sa[i] - 1]) {
                sortedLms[j++] = sa[i];
            }
        }

        int[] name = new int[n];
        Arrays.fill(name, -1);
        int nameCount = 0;
        name[sortedLms[0]] = nameCount;

        for (int i = 1; i < lmsCount; i++) {
            int pos1 = sortedLms[i - 1];
            int pos2 = sortedLms[i];
            boolean same = true;

            for (int d = 0; ; d++) {
                if (s[pos1 + d] != s[pos2 + d] || isS[pos1 + d] != isS[pos2 + d]) {
                    same = false;
                    break;
                }
                if (d > 0 && (isS[pos1 + d] && !isS[pos1 + d - 1]) && (isS[pos2 + d] && !isS[pos2 + d - 1])) {
                    break;
                }
            }

            if (!same) nameCount++;
            name[pos2] = nameCount;
        }

        int[] summaryS = new int[lmsCount];
        int[] summaryLmsPos = new int[lmsCount];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            if (name[i] != -1) {
                summaryS[idx] = name[i];
                summaryLmsPos[idx] = i;
                idx++;
            }
        }

        int[] summarySA;
        if (nameCount + 1 < lmsCount) {
            summarySA = sais(summaryS, nameCount + 1);
        } else {
            summarySA = new int[lmsCount];
            for (int i = 0; i < lmsCount; i++) {
                summarySA[summaryS[i]] = i;
            }
        }

        int[] orderedLms = new int[lmsCount];
        for (int i = 0; i < lmsCount; i++) {
            orderedLms[i] = summaryLmsPos[summarySA[i]];
        }

        induceSort(s, sa, isS, orderedLms, lmsCount, K);
        return sa;
    }

    private static void induceSort(int[] s, int[] sa, boolean[] isS, int[] lms, int lmsCount, int K) {
        int n = s.length;
        int[] bucketHead = new int[K];
        int[] bucketTail = new int[K];
        getBuckets(s, bucketHead, bucketTail, K);

        Arrays.fill(sa, -1);

        for (int i = lmsCount - 1; i >= 0; i--) {
            int pos = lms[i];
            int b = s[pos];
            sa[bucketTail[b]--] = pos;
        }

        getBuckets(s, bucketHead, bucketTail, K);

        for (int i = 0; i < n; i++) {
            if (sa[i] > 0) {
                int j = sa[i] - 1;
                if (!isS[j]) {
                    int b = s[j];
                    sa[bucketHead[b]++] = j;
                }
            }
        }

        getBuckets(s, bucketHead, bucketTail, K);

        for (int i = n - 1; i >= 0; i--) {
            if (sa[i] > 0) {
                int j = sa[i] - 1;
                if (isS[j]) {
                    int b = s[j];
                    sa[bucketTail[b]--] = j;
                }
            }
        }
    }

    private static void getBuckets(int[] s, int[] bucketHead, int[] bucketTail, int K) {
        int[] count = new int[K];
        for (int val : s) {
            count[val]++;
        }
        int sum = 0;
        for (int i = 0; i < K; i++) {
            bucketHead[i] = sum;
            sum += count[i];
            bucketTail[i] = sum - 1;
        }
    }
}
