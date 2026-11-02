package com.fleetflow.indexing;

import java.util.*;

/**
 * Suffix Tree for fast substring queries, longest common substring, and manifest similarity.
 * Used in FleetFlow to index multiple delivery documents and find shared clauses or repeated addresses.
 */
public class SuffixTree {

    public static class Node {
        final Map<Character, Node> children = new TreeMap<>();
        int start;
        int end;
        int suffixIndex = -1; // -1 for internal nodes; >= 0 for leaf nodes

        Node(int start, int end) {
            this.start = start;
            this.end = end;
        }

        int edgeLength() {
            return end - start;
        }
    }

    private final String text;
    private final Node root;

    public SuffixTree(String text) {
        // Append unique sentinel character if not present
        if (!text.endsWith("$")) {
            this.text = text + "$";
        } else {
            this.text = text;
        }
        this.root = new Node(-1, -1);
        buildTree();
    }

    private void buildTree() {
        int n = text.length();
        for (int i = 0; i < n; i++) {
            insertSuffix(i);
        }
    }

    private void insertSuffix(int suffixStart) {
        Node curr = root;
        int i = suffixStart;
        int n = text.length();

        while (i < n) {
            char ch = text.charAt(i);
            Node child = curr.children.get(ch);

            if (child == null) {
                // Create new leaf node
                Node leaf = new Node(i, n);
                leaf.suffixIndex = suffixStart;
                curr.children.put(ch, leaf);
                return;
            }

            // Traverse the edge
            int edgeLen = child.edgeLength();
            int matchLen = 0;
            while (matchLen < edgeLen && i + matchLen < n &&
                    text.charAt(child.start + matchLen) == text.charAt(i + matchLen)) {
                matchLen++;
            }

            if (matchLen == edgeLen) {
                // Full edge matched, advance to child
                curr = child;
                i += edgeLen;
            } else {
                // Split the edge
                Node split = new Node(child.start, child.start + matchLen);
                curr.children.put(ch, split);

                child.start += matchLen;
                split.children.put(text.charAt(child.start), child);

                Node leaf = new Node(i + matchLen, n);
                leaf.suffixIndex = suffixStart;
                split.children.put(text.charAt(i + matchLen), leaf);
                return;
            }
        }
    }

    /**
     * Checks if a pattern exists as a substring in the indexed documents.
     */
    public boolean contains(String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        Node curr = root;
        int i = 0;
        int m = pattern.length();

        while (i < m) {
            char ch = pattern.charAt(i);
            Node child = curr.children.get(ch);
            if (child == null) return false;

            int edgeLen = child.edgeLength();
            int matchLen = 0;
            while (matchLen < edgeLen && i + matchLen < m &&
                    text.charAt(child.start + matchLen) == pattern.charAt(i + matchLen)) {
                matchLen++;
            }

            if (i + matchLen == m) {
                return true;
            }
            if (matchLen < edgeLen) {
                return false;
            }
            i += edgeLen;
            curr = child;
        }
        return true;
    }

    /**
     * Collects all starting indices where pattern occurs in the text.
     */
    public List<Integer> search(String pattern) {
        List<Integer> occurrences = new ArrayList<>();
        if (pattern == null || pattern.isEmpty()) return occurrences;

        Node curr = root;
        int i = 0;
        int m = pattern.length();

        while (i < m) {
            char ch = pattern.charAt(i);
            Node child = curr.children.get(ch);
            if (child == null) return occurrences;

            int edgeLen = child.edgeLength();
            int matchLen = 0;
            while (matchLen < edgeLen && i + matchLen < m &&
                    text.charAt(child.start + matchLen) == pattern.charAt(i + matchLen)) {
                matchLen++;
            }

            if (i + matchLen == m) {
                collectLeaves(child, occurrences);
                Collections.sort(occurrences);
                return occurrences;
            }
            if (matchLen < edgeLen) {
                return occurrences;
            }
            i += edgeLen;
            curr = child;
        }
        return occurrences;
    }

    private void collectLeaves(Node node, List<Integer> list) {
        if (node.suffixIndex != -1) {
            list.add(node.suffixIndex);
        }
        for (Node child : node.children.values()) {
            collectLeaves(child, list);
        }
    }

    /**
     * Computes the Longest Common Substring (LCS) between two logistics documents/strings.
     */
    public static String longestCommonSubstring(String s1, String s2) {
        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty()) return "";
        String combined = s1 + "#" + s2 + "$";
        int len1 = s1.length();

        int[] sa = SAIS.buildSuffixArray(combined);
        int[] lcp = KasaiLCP.computeLCP(combined, sa);

        int maxLen = 0;
        int bestIdx = -1;

        for (int i = 1; i < combined.length(); i++) {
            int p1 = sa[i - 1];
            int p2 = sa[i];

            // One suffix belongs to s1 and the other belongs to s2
            boolean inS1A = p1 < len1;
            boolean inS1B = p2 < len1;

            if (inS1A != inS1B) {
                if (lcp[i] > maxLen) {
                    maxLen = lcp[i];
                    bestIdx = p2;
                }
            }
        }

        if (bestIdx == -1 || maxLen == 0) return "";
        return combined.substring(bestIdx, bestIdx + maxLen);
    }
}
