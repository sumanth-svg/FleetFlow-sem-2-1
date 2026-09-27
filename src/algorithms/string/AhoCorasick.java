package algorithms.string;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import model.Shipment;

/**
 * Aho-Corasick Multi-Pattern String Matching Automaton.
 * 
 * Used in FleetFlow to:
 * 1. Detect multiple delivery-status patterns (e.g. "DELIVERED", "IN_TRANSIT", "DELAYED",
 *    "FAILED_ATTEMPT", "CUSTOMS_HOLD") simultaneously in tracking logs and status updates.
 * 
 * Time Complexity:
 * - Construction: O(M) where M is the total sum of lengths of all patterns.
 * - Search: O(N + Z) where N is text length and Z is number of matches found.
 * Space Complexity: O(M * Sigma) for Trie structure.
 */
public class AhoCorasick {

    /**
     * Inner class representing a Trie node in the Aho-Corasick automaton.
     */
    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        Node fail = null;
        List<String> outputs = new ArrayList<>();
    }

    /**
     * Class representing a matched pattern result.
     */
    public static class StatusMatch {
        private String keyword;
        private int startIndex;
        private int endIndex;

        public StatusMatch(String keyword, int startIndex, int endIndex) {
            this.keyword = keyword;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public String getKeyword() {
            return keyword;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        @Override
        public String toString() {
            return "Status '" + keyword + "' detected at [" + startIndex + ".." + endIndex + "]";
        }
    }

    private Node root;
    private boolean automatonBuilt;
    private List<String> patterns;

    /**
     * Constructs an empty Aho-Corasick automaton.
     */
    public AhoCorasick() {
        this.root = new Node();
        this.automatonBuilt = false;
        this.patterns = new ArrayList<>();
    }

    /**
     * Constructs an Aho-Corasick automaton with pre-populated pattern list.
     *
     * @param keywords List of delivery status keywords
     */
    public AhoCorasick(List<String> keywords) {
        this();
        if (keywords != null) {
            for (String kw : keywords) {
                addPattern(kw);
            }
            buildAutomaton();
        }
    }

    /**
     * Adds a status keyword pattern to the automaton trie.
     *
     * @param pattern Keyword pattern to add
     */
    public void addPattern(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            return;
        }

        String searchPattern = pattern.trim().toUpperCase();
        Node current = root;

        for (char ch : searchPattern.toCharArray()) {
            current.children.putIfAbsent(ch, new Node());
            current = current.children.get(ch);
        }

        current.outputs.add(searchPattern);
        patterns.add(searchPattern);
        automatonBuilt = false; // Re-building required after adding new pattern
    }

    /**
     * Builds the failure links and output links using Breadth-First Search (BFS).
     */
    public void buildAutomaton() {
        Queue<Node> queue = new LinkedList<>();

        // Root's immediate children fail to root
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
                char ch = entry.getKey();
                Node child = entry.getValue();

                // Find failure link for child
                Node failTarget = current.fail;
                while (failTarget != null && !failTarget.children.containsKey(ch)) {
                    failTarget = failTarget.fail;
                }

                if (failTarget == null) {
                    child.fail = root;
                } else {
                    child.fail = failTarget.children.get(ch);
                    // Merge outputs from fail node
                    child.outputs.addAll(child.fail.outputs);
                }

                queue.add(child);
            }
        }

        automatonBuilt = true;
    }

    /**
     * Searches for all occurrences of registered delivery status keywords in the given text log.
     *
     * @param text Log text to scan
     * @return List of StatusMatch objects detailing every match
     */
    public List<StatusMatch> search(String text) {
        List<StatusMatch> matches = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return matches;
        }

        if (!automatonBuilt) {
            buildAutomaton();
        }

        String searchText = text.toUpperCase();
        Node current = root;

        for (int i = 0; i < searchText.length(); i++) {
            char ch = searchText.charAt(i);

            // Follow fail links if character is not a child of current node
            while (current != null && !current.children.containsKey(ch)) {
                current = current.fail;
            }

            if (current == null) {
                current = root;
            } else {
                current = current.children.get(ch);

                // Collect matches from node outputs
                for (String matchedPattern : current.outputs) {
                    int startIdx = i - matchedPattern.length() + 1;
                    matches.add(new StatusMatch(matchedPattern, startIdx, i));
                }
            }
        }

        return matches;
    }

    /**
     * Counts the occurrence frequency of each registered delivery status pattern in the text log.
     *
     * @param logText Log text to analyze
     * @return Map of Status Keyword -> Count of occurrences
     */
    public Map<String, Integer> countStatusFrequencies(String logText) {
        Map<String, Integer> counts = new HashMap<>();

        for (String pattern : patterns) {
            counts.put(pattern, 0);
        }

        List<StatusMatch> matches = search(logText);
        for (StatusMatch match : matches) {
            counts.put(match.getKeyword(), counts.getOrDefault(match.getKeyword(), 0) + 1);
        }

        return counts;
    }

    /**
     * Scans a list of shipments and returns all status matches across their status histories and current statuses.
     *
     * @param shipments      List of shipments
     * @param statusKeywords List of status patterns to search for simultaneously
     * @return Map of Shipment ID -> List of StatusMatch results
     */
    public static Map<String, List<StatusMatch>> searchShipmentStatuses(List<Shipment> shipments, List<String> statusKeywords) {
        Map<String, List<StatusMatch>> resultMap = new HashMap<>();

        if (shipments == null || statusKeywords == null || statusKeywords.isEmpty()) {
            return resultMap;
        }

        AhoCorasick ac = new AhoCorasick(statusKeywords);

        for (Shipment shipment : shipments) {
            String fullText = shipment.getShipmentId() + " " + shipment.getStatus();
            List<StatusMatch> matches = ac.search(fullText);
            if (!matches.isEmpty()) {
                resultMap.put(shipment.getShipmentId(), matches);
            }
        }

        return resultMap;
    }
}
