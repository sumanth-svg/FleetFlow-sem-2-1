package algorithms.editdistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wagner-Fischer Dynamic Programming Algorithm for Edit Distance & Variants.
 * 
 * Computes minimum edit operations (Insertions, Deletions, Substitutions, Transpositions)
 * required to transform source string s1 into target string s2.
 * 
 * Used in FleetFlow to:
 * 1. Correct misspelled destination addresses and warehouse names.
 * 2. Perform fuzzy search and string similarity ranking on shipment logs.
 * 
 * Time Complexity:
 * - Standard Wagner-Fischer: O(M * N) where M = len(s1), N = len(s2).
 * - Damerau-Levenshtein: O(M * N).
 * Space Complexity:
 * - O(M * N) DP Matrix for operation traceback.
 */
public class WagnerFischer {

    public enum EditOperation {
        MATCH,
        INSERT,
        DELETE,
        SUBSTITUTE,
        TRANSPOSITIONAL_SWAP
    }

    public static class OperationStep {
        private final EditOperation operation;
        private final char sourceChar;
        private final char targetChar;
        private final int position;

        public OperationStep(EditOperation operation, char sourceChar, char targetChar, int position) {
            this.operation = operation;
            this.sourceChar = sourceChar;
            this.targetChar = targetChar;
            this.position = position;
        }

        public EditOperation getOperation() {
            return operation;
        }

        public char getSourceChar() {
            return sourceChar;
        }

        public char getTargetChar() {
            return targetChar;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public String toString() {
            switch (operation) {
                case MATCH:
                    return String.format("MATCH '%c'", sourceChar);
                case INSERT:
                    return String.format("INSERT '%c' at position %d", targetChar, position);
                case DELETE:
                    return String.format("DELETE '%c' at position %d", sourceChar, position);
                case SUBSTITUTE:
                    return String.format("SUBSTITUTE '%c' -> '%c' at position %d", sourceChar, targetChar, position);
                case TRANSPOSITIONAL_SWAP:
                    return String.format("SWAP ADJACENT '%c%c'", sourceChar, targetChar);
                default:
                    return operation.name();
            }
        }
    }

    public static class EditDistanceResult {
        private final String source;
        private final String target;
        private final int distance;
        private final double similarityScore;
        private final int[][] dpMatrix;
        private final List<OperationStep> operations;

        public EditDistanceResult(String source, String target, int distance, double similarityScore,
                                  int[][] dpMatrix, List<OperationStep> operations) {
            this.source = source;
            this.target = target;
            this.distance = distance;
            this.similarityScore = similarityScore;
            this.dpMatrix = dpMatrix;
            this.operations = operations;
        }

        public String getSource() {
            return source;
        }

        public String getTarget() {
            return target;
        }

        public int getDistance() {
            return distance;
        }

        public double getSimilarityScore() {
            return similarityScore;
        }

        public int[][] getDpMatrix() {
            return dpMatrix;
        }

        public List<OperationStep> getOperations() {
            return operations;
        }

        @Override
        public String toString() {
            return String.format("Source: '%s' | Target: '%s' | Edit Distance: %d | Similarity: %.2f%%",
                    source, target, distance, similarityScore * 100.0);
        }
    }

    /**
     * Standard Wagner-Fischer Levenshtein Distance with operation traceback.
     *
     * @param source Source string
     * @param target Target string
     * @return EditDistanceResult object
     */
    public static EditDistanceResult computeLevenshteinDistance(String source, String target) {
        String s = source == null ? "" : source;
        String t = target == null ? "" : target;

        int m = s.length();
        int n = t.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s.charAt(i - 1) == t.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        dp[i - 1][j] + 1,      // Deletion
                        Math.min(
                                dp[i][j - 1] + 1,  // Insertion
                                dp[i - 1][j - 1] + cost // Substitution
                        )
                );
            }
        }

        int distance = dp[m][n];
        int maxLen = Math.max(m, n);
        double similarity = maxLen == 0 ? 1.0 : 1.0 - ((double) distance / maxLen);

        List<OperationStep> ops = traceBackOperations(s, t, dp);

        return new EditDistanceResult(s, t, distance, similarity, dp, ops);
    }

    /**
     * Damerau-Levenshtein Edit Distance Variant (Supports adjacent transposition swap).
     *
     * @param source Source string
     * @param target Target string
     * @return Minimum distance including transposition swaps
     */
    public static int computeDamerauLevenshteinDistance(String source, String target) {
        String s = source == null ? "" : source;
        String t = target == null ? "" : target;

        int m = s.length();
        int n = t.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s.charAt(i - 1) == t.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        dp[i - 1][j] + 1,
                        Math.min(dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
                );

                // Adjacent transposition check
                if (i > 1 && j > 1 && s.charAt(i - 1) == t.charAt(j - 2) && s.charAt(i - 2) == t.charAt(j - 1)) {
                    dp[i][j] = Math.min(dp[i][j], dp[i - 2][j - 2] + 1);
                }
            }
        }

        return dp[m][n];
    }

    /**
     * Weighted Edit Distance Variant with customized insertion, deletion, and substitution costs.
     *
     * @param source         Source string
     * @param target         Target string
     * @param insertCost     Cost of character insertion
     * @param deleteCost     Cost of character deletion
     * @param substituteCost Cost of character substitution
     * @return Total weighted edit cost
     */
    public static double computeWeightedEditDistance(String source, String target,
                                                     double insertCost, double deleteCost, double substituteCost) {
        String s = source == null ? "" : source;
        String t = target == null ? "" : target;

        int m = s.length();
        int n = t.length();
        double[][] dp = new double[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i * deleteCost;
        for (int j = 0; j <= n; j++) dp[0][j] = j * insertCost;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(
                            dp[i - 1][j] + deleteCost,
                            Math.min(dp[i][j - 1] + insertCost, dp[i - 1][j - 1] + substituteCost)
                    );
                }
            }
        }

        return dp[m][n];
    }

    private static List<OperationStep> traceBackOperations(String s, String t, int[][] dp) {
        List<OperationStep> ops = new ArrayList<>();
        int i = s.length();
        int j = t.length();

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && s.charAt(i - 1) == t.charAt(j - 1)) {
                ops.add(new OperationStep(EditOperation.MATCH, s.charAt(i - 1), t.charAt(j - 1), i - 1));
                i--;
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                ops.add(new OperationStep(EditOperation.SUBSTITUTE, s.charAt(i - 1), t.charAt(j - 1), i - 1));
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add(new OperationStep(EditOperation.DELETE, s.charAt(i - 1), ' ', i - 1));
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                ops.add(new OperationStep(EditOperation.INSERT, ' ', t.charAt(j - 1), j - 1));
                j--;
            } else {
                break;
            }
        }

        Collections.reverse(ops);
        return ops;
    }
}
