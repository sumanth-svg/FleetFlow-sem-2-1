package algorithms.sequence;

/**
 * Smith-Waterman Local Sequence Alignment Algorithm.
 * 
 * Computes optimal local alignment between two sequences (e.g. finding matching sub-routes or
 * log pattern segments inside longer telemetry streams).
 * 
 * Time Complexity: O(M * N) where M = len(seq1), N = len(seq2).
 * Space Complexity: O(M * N) for DP score matrix.
 */
public class SmithWaterman {

    public static class LocalAlignmentResult {
        private final String seq1;
        private final String seq2;
        private final String alignedSub1;
        private final String alignedSub2;
        private final String matchVisual;
        private final int maxScore;
        private final int maxRow;
        private final int maxCol;
        private final int[][] scoreMatrix;

        public LocalAlignmentResult(String seq1, String seq2, String alignedSub1, String alignedSub2,
                                    String matchVisual, int maxScore, int maxRow, int maxCol, int[][] scoreMatrix) {
            this.seq1 = seq1;
            this.seq2 = seq2;
            this.alignedSub1 = alignedSub1;
            this.alignedSub2 = alignedSub2;
            this.matchVisual = matchVisual;
            this.maxScore = maxScore;
            this.maxRow = maxRow;
            this.maxCol = maxCol;
            this.scoreMatrix = scoreMatrix;
        }

        public String getSeq1() {
            return seq1;
        }

        public String getSeq2() {
            return seq2;
        }

        public String getAlignedSub1() {
            return alignedSub1;
        }

        public String getAlignedSub2() {
            return alignedSub2;
        }

        public String getMatchVisual() {
            return matchVisual;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public int getMaxRow() {
            return maxRow;
        }

        public int getMaxCol() {
            return maxCol;
        }

        public int[][] getScoreMatrix() {
            return scoreMatrix;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Local Alignment Score Peak: ").append(maxScore).append("\n");
            sb.append("Local Subsegment 1: ").append(alignedSub1).append("\n");
            sb.append("                    ").append(matchVisual).append("\n");
            sb.append("Local Subsegment 2: ").append(alignedSub2);
            return sb.toString();
        }
    }

    /**
     * Performs Smith-Waterman local sequence alignment with default parameters (Match=+3, Mismatch=-2, Gap=-2).
     */
    public static LocalAlignmentResult alignLocal(String seq1, String seq2) {
        return alignLocal(seq1, seq2, 3, -2, -2);
    }

    /**
     * Performs Smith-Waterman local sequence alignment with custom scoring system.
     *
     * @param seq1            First sequence (e.g. Master log pattern)
     * @param seq2            Second sequence (e.g. Telemetry event stream)
     * @param matchScore      Positive reward for matching character
     * @param mismatchPenalty Negative penalty for mismatched character
     * @param gapPenalty      Negative penalty for inserting gap
     * @return LocalAlignmentResult object
     */
    public static LocalAlignmentResult alignLocal(String seq1, String seq2, int matchScore, int mismatchPenalty, int gapPenalty) {
        String s1 = seq1 == null ? "" : seq1;
        String s2 = seq2 == null ? "" : seq2;

        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        int maxScore = 0;
        int maxR = 0;
        int maxC = 0;

        // Fill matrix with local threshold at 0
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int sim = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? matchScore : mismatchPenalty;
                int diag = dp[i - 1][j - 1] + sim;
                int up = dp[i - 1][j] + gapPenalty;
                int left = dp[i][j - 1] + gapPenalty;

                dp[i][j] = Math.max(0, Math.max(diag, Math.max(up, left)));

                if (dp[i][j] > maxScore) {
                    maxScore = dp[i][j];
                    maxR = i;
                    maxC = j;
                }
            }
        }

        // Traceback from maximum scoring cell until cell value == 0
        StringBuilder align1 = new StringBuilder();
        StringBuilder align2 = new StringBuilder();
        StringBuilder visual = new StringBuilder();

        int i = maxR;
        int j = maxC;

        while (i > 0 && j > 0 && dp[i][j] > 0) {
            int sim = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? matchScore : mismatchPenalty;

            if (dp[i][j] == dp[i - 1][j - 1] + sim) {
                align1.append(s1.charAt(i - 1));
                align2.append(s2.charAt(j - 1));
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    visual.append("|");
                } else {
                    visual.append(".");
                }
                i--;
                j--;
            } else if (dp[i][j] == dp[i - 1][j] + gapPenalty) {
                align1.append(s1.charAt(i - 1));
                align2.append("-");
                visual.append(" ");
                i--;
            } else if (dp[i][j] == dp[i][j - 1] + gapPenalty) {
                align1.append("-");
                align2.append(s2.charAt(j - 1));
                visual.append(" ");
                j--;
            } else {
                break;
            }
        }

        String res1 = align1.reverse().toString();
        String res2 = align2.reverse().toString();
        String resVis = visual.reverse().toString();

        return new LocalAlignmentResult(s1, s2, res1, res2, resVis, maxScore, maxR, maxC, dp);
    }
}
