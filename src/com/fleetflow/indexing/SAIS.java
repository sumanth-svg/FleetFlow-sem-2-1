package com.fleetflow.indexing;

import java.util.Arrays;

/**
 * Suffix Array Induced Sorting (SA-IS) algorithm.
 * Constructs the Suffix Array in strictly linear O(N) time.
 * Used in FleetFlow for large-scale logistics document and manifest indexing.
 */
public class SAIS {

    private static final boolean S_TYPE = true;
    private static final boolean L_TYPE = false;

    /**
     * Builds Suffix Array for a string s using the SA-IS algorithm.
     */
    public static int[] buildSuffixArray(String s) {
        int n = s.length();
        int[] sInt = new int[n + 1];
        int maxChar = 0;
        for (int i = 0; i < n; i++) {
            sInt[i] = s.charAt(i) + 1; // shift so 0 is reserved for sentinel
            maxChar = Math.max(maxChar, sInt[i]);
        }
        sInt[n] = 0; // sentinel smaller than all other characters

        int[] sa = new int[n + 1];
        sais(sInt, sa, n + 1, maxChar + 1);

        // Remove sentinel suffix (which is at sa[0] = n)
        int[] result = new int[n];
        System.arraycopy(sa, 1, result, 0, n);
        return result;
    }

    private static void sais(int[] s, int[] sa, int n, int alphabetSize) {
        boolean[] type = new boolean[n];
        type[n - 1] = S_TYPE; // sentinel is S-type

        // 1. Classify suffixes as S-type or L-type
        for (int i = n - 2; i >= 0; i--) {
            if (s[i] < s[i + 1]) {
                type[i] = S_TYPE;
            } else if (s[i] > s[i + 1]) {
                type[i] = L_TYPE;
            } else {
                type[i] = type[i + 1];
            }
        }

        // Find LMS (Leftmost S-type) characters: type[i] is S and type[i-1] is L
        int[] lms = new int[n];
        int lmsCount = 0;
        for (int i = 1; i < n; i++) {
            if (type[i] == S_TYPE && type[i - 1] == L_TYPE) {
                lms[lmsCount++] = i;
            }
        }

        // 2. Bucket sort LMS suffixes and induce sort
        int[] bucket = new int[alphabetSize];
        getBuckets(s, bucket, n, alphabetSize);

        Arrays.fill(sa, -1);
        int[] bucketEnd = Arrays.copyOf(bucket, alphabetSize);
        for (int i = lmsCount - 1; i >= 0; i--) {
            int pos = lms[i];
            sa[--bucketEnd[s[pos]]] = pos;
        }

        induceSort(s, sa, type, n, alphabetSize, bucket);

        // 3. Name LMS substrings
        int[] lmsNames = new int[lmsCount];
        int name = 0;
        int prevLms = -1;

        // Position of LMS in sa
        int lmsIndex = 0;
        for (int i = 0; i < n; i++) {
            int pos = sa[i];
            if (pos > 0 && type[pos] == S_TYPE && type[pos - 1] == L_TYPE) {
                if (prevLms != -1 && !isLmsEqual(s, type, prevLms, pos, n)) {
                    name++;
                }
                prevLms = pos;
                // find index of pos in lms array
                for (int j = 0; j < lmsCount; j++) {
                    if (lms[j] == pos) {
                        lmsNames[j] = name;
                        break;
                    }
                }
            }
        }

        int[] lmsOrder = new int[lmsCount];
        if (name + 1 < lmsCount) {
            // Recurse on reduced string
            sais(lmsNames, lmsOrder, lmsCount, name + 1);
        } else {
            // Each LMS substring is unique
            for (int i = 0; i < lmsCount; i++) {
                lmsOrder[lmsNames[i]] = i;
            }
        }

        // 4. Place LMS suffixes in exact sorted order
        Arrays.fill(sa, -1);
        bucketEnd = Arrays.copyOf(bucket, alphabetSize);
        for (int i = lmsCount - 1; i >= 0; i--) {
            int pos = lms[lmsOrder[i]];
            sa[--bucketEnd[s[pos]]] = pos;
        }

        induceSort(s, sa, type, n, alphabetSize, bucket);
    }

    private static void induceSort(int[] s, int[] sa, boolean[] type, int n, int alphabetSize, int[] bucket) {
        // Induce L-type from left to right
        int[] bucketHead = new int[alphabetSize];
        bucketHead[0] = 0;
        for (int i = 1; i < alphabetSize; i++) {
            bucketHead[i] = bucket[i - 1];
        }

        for (int i = 0; i < n; i++) {
            int j = sa[i] - 1;
            if (j >= 0 && type[j] == L_TYPE) {
                sa[bucketHead[s[j]]++] = j;
            }
        }

        // Induce S-type from right to left
        int[] bucketEnd = Arrays.copyOf(bucket, alphabetSize);
        for (int i = n - 1; i >= 0; i--) {
            int j = sa[i] - 1;
            if (j >= 0 && type[j] == S_TYPE) {
                sa[--bucketEnd[s[j]]] = j;
            }
        }
    }

    private static void getBuckets(int[] s, int[] bucket, int n, int alphabetSize) {
        int[] count = new int[alphabetSize];
        for (int i = 0; i < n; i++) {
            count[s[i]]++;
        }
        int sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            sum += count[i];
            bucket[i] = sum;
        }
    }

    private static boolean isLmsEqual(int[] s, boolean[] type, int p1, int p2, int n) {
        int i = 0;
        while (true) {
            int c1 = s[p1 + i];
            int c2 = s[p2 + i];
            if (c1 != c2) return false;
            boolean t1 = type[p1 + i];
            boolean t2 = type[p2 + i];
            if (t1 != t2) return false;

            if (i > 0) {
                boolean lms1 = (type[p1 + i] == S_TYPE && type[p1 + i - 1] == L_TYPE);
                boolean lms2 = (type[p2 + i] == S_TYPE && type[p2 + i - 1] == L_TYPE);
                if (lms1 && lms2) return true;
                if (lms1 || lms2) return false;
            }
            i++;
            if (p1 + i >= n || p2 + i >= n) return false;
        }
    }
}
