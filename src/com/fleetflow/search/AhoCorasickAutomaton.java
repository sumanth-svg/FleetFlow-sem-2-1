package com.fleetflow.search;

import java.util.*;

/**
 * Aho-Corasick Multi-Pattern Automaton.
 * Used in FleetFlow to detect multiple delivery-status patterns, critical alerts, and operational flags
 * simultaneously across streaming telemetry and continuous logistics logs in linear time O(TextLength + TotalMatches).
 */
public class AhoCorasickAutomaton {

    public static class MatchResult {
        private final String keyword;
        private final String category;
        private final int startIndex;
        private final int endIndex;

        public MatchResult(String keyword, String category, int startIndex, int endIndex) {
            this.keyword = keyword;
            this.category = category;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getKeyword() { return keyword; }
        public String getCategory() { return category; }
        public int getStartIndex() { return startIndex; }
        public int getEndIndex() { return endIndex; }

        @Override
        public String toString() {
            return String.format("'%s' [%s] at offset [%d..%d]", keyword, category, startIndex, endIndex);
        }
    }

    private static class Node {
        final Map<Character, Node> children = new HashMap<>();
        Node fail = null;
        final List<String> matchedKeywords = new ArrayList<>();
        final List<String> matchedCategories = new ArrayList<>();
    }

    private final Node root = new Node();
    private boolean built = false;

    public void addPattern(String keyword, String category) {
        if (built) {
            throw new IllegalStateException("Cannot add patterns after automaton is built");
        }
        if (keyword == null || keyword.isEmpty()) return;

        Node curr = root;
        for (char c : keyword.toCharArray()) {
            curr = curr.children.computeIfAbsent(c, k -> new Node());
        }
        curr.matchedKeywords.add(keyword);
        curr.matchedCategories.add(category);
    }

    public void buildAutomaton() {
        Queue<Node> queue = new ArrayDeque<>();

        // Root children fail links point to root
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }

        // BFS to build fail transitions and copy output dictionary links
        while (!queue.isEmpty()) {
            Node curr = queue.poll();

            for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();

                Node f = curr.fail;
                while (f != null && !f.children.containsKey(ch)) {
                    f = f.fail;
                }

                child.fail = (f == null) ? root : f.children.get(ch);

                // Inherit output matches from fail state
                if (child.fail != null) {
                    child.matchedKeywords.addAll(child.fail.matchedKeywords);
                    child.matchedCategories.addAll(child.fail.matchedCategories);
                }

                queue.add(child);
            }
        }
        built = true;
    }

    public List<MatchResult> search(String text) {
        if (!built) {
            buildAutomaton();
        }

        List<MatchResult> results = new ArrayList<>();
        if (text == null || text.isEmpty()) return results;

        Node curr = root;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            while (curr != null && !curr.children.containsKey(ch)) {
                curr = curr.fail;
            }

            if (curr == null) {
                curr = root;
                continue;
            }

            curr = curr.children.get(ch);

            if (!curr.matchedKeywords.isEmpty()) {
                for (int m = 0; m < curr.matchedKeywords.size(); m++) {
                    String kw = curr.matchedKeywords.get(m);
                    String cat = curr.matchedCategories.get(m);
                    results.add(new MatchResult(kw, cat, i - kw.length() + 1, i));
                }
            }
        }

        return results;
    }

    /**
     * Creates a pre-configured Aho-Corasick automaton initialized with standard logistics delivery keywords.
     */
    public static AhoCorasickAutomaton createLogisticsMonitor() {
        AhoCorasickAutomaton ac = new AhoCorasickAutomaton();
        ac.addPattern("OUT_FOR_DELIVERY", "DISPATCH_STATUS");
        ac.addPattern("CUSTOMS_HOLD", "REGULATORY_ALERT");
        ac.addPattern("TEMPERATURE_BREACH", "SAFETY_CRITICAL");
        ac.addPattern("HIGH_SPEED_WARNING", "TELEMETRY_ALERT");
        ac.addPattern("DELIVERY_EXCEPTION", "EXCEPTION");
        ac.addPattern("DELIVERED", "COMPLETION");
        ac.addPattern("IN_TRANSIT", "NORMAL_TRANSIT");
        ac.addPattern("HAZMAT", "CARGO_SPEC");
        ac.addPattern("REFRIGERATED", "CARGO_SPEC");
        ac.addPattern("DAMAGED", "CRITICAL_DAMAGE");
        ac.buildAutomaton();
        return ac;
    }
}
