package algorithms.suffix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Suffix Tree Data Structure for Fast Substring Queries.
 * 
 * Stores all suffixes of a text string in a compressed Trie structure.
 * 
 * Used in FleetFlow (Module 2 - CO2) to:
 * 1. Perform fast O(M) substring pattern matching across multi-document logistics archives.
 * 2. Search shipment keywords and multi-pattern indices.
 * 
 * Time Complexity:
 * - Query: O(M) where M is pattern length.
 * - Tree Construction: O(N) using Ukkonen's algorithm or O(N^2) suffix insertion.
 * Space Complexity: O(N) nodes.
 */
public class SuffixTree {

    public static class Node {
        private final Map<Character, Node> children = new HashMap<>();
        private final List<Integer> suffixIndices = new ArrayList<>();

        public Map<Character, Node> getChildren() {
            return children;
        }

        public List<Integer> getSuffixIndices() {
            return suffixIndices;
        }
    }

    private final Node root;
    private final String text;

    public SuffixTree(String text) {
        this.text = text == null ? "" : text;
        this.root = new Node();
        buildTree();
    }

    private void buildTree() {
        int n = text.length();
        for (int i = 0; i < n; i++) {
            insertSuffix(text.substring(i), i);
        }
    }

    private void insertSuffix(String suffix, int startIndex) {
        Node curr = root;
        curr.suffixIndices.add(startIndex);

        for (int i = 0; i < suffix.length(); i++) {
            char ch = suffix.charAt(i);
            curr.children.putIfAbsent(ch, new Node());
            curr = curr.children.get(ch);
            curr.suffixIndices.add(startIndex);
        }
    }

    /**
     * Searches for pattern in the Suffix Tree in O(M) time.
     *
     * @param pattern Query string to search
     * @return List of starting positions in text where pattern occurs
     */
    public List<Integer> search(String pattern) {
        List<Integer> results = new ArrayList<>();
        if (pattern == null || pattern.isEmpty()) return results;

        Node curr = root;
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (!curr.children.containsKey(ch)) {
                return results;
            }
            curr = curr.children.get(ch);
        }

        results.addAll(curr.suffixIndices);
        return results;
    }

    /**
     * Checks if pattern is a substring in O(M) time.
     */
    public boolean containsSubstring(String pattern) {
        return !search(pattern).isEmpty();
    }
}
