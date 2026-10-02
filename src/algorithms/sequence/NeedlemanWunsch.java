package algorithms.sequence;

/**
 * Needleman-Wunsch Global Sequence Alignment Algorithm.
 * 
 * Computes optimal global alignment between two sequences (e.g. planned vs actual vehicle route checkpoints).
 * 
 * Time Complexity: O(M * N) where M = len(seq1), N = len(seq2).
 * Space Complexity: O(M * N) for DP score matrix and traceback matrix.
 */
public class NeedlemanWunsch {

    public static class AlignmentResult {
        private final String seq1;
        private final String seq2;
        private final String alignedSeq1;
        private final String alignedSeq2;
        private final String matchVisual;
        private final int score;
        private final int[][] scoreMatrix;

        public AlignmentResult(String seq1, String seq2, String alignedSeq1, String alignedSeq2,
                               String matchVisual, int score, int[][] scoreMatrix) {
            this.seq1 = seq1;
            this.seq2 = seq2;
            this.alignedSeq1 = alignedSeq1;
            this.alignedSeq2 = alignedSeq2;
            this.matchVisual = matchVisual;
            this.score = score;
            this.scoreMatrix = scoreMatrix;
        }

        public String getSeq1() {
            return seq1;
        }

        public String getSeq2() {
            return seq2;
        }

        public String getAlignedSeq1() {
            return alignedSeq1;
        }

        public String getAlignedSeq2() {
            return alignedSeq2;
        }

        public String getMatchVisual() {
            return matchVisual;
        }

        public int getScore() {
            return score;
        }

        public int[][] getScoreMatrix() {
            return scoreMatrix;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Global Alignment Score: ").append(score).append("\n");
            sb.append("Seq 1 (Planned): ").append(alignedSeq1).append("\n");
            sb.append("                 ").append(matchVisual).append("\n");
            sb.append("Seq 2 (Actual) : ").append(alignedSeq2);
            return sb.toString();
        }
    }

    /**
     * Performs Needleman-Wunsch global sequence alignment using default scoring (Match=+2, Mismatch=-1, Gap=-1).
     */
    public static AlignmentResult align(String seq1, String seq2) {
        return align(seq1, seq2, 2, -1, -1);
    }

    /**
     * Performs Needleman-Wunsch global sequence alignment with custom scoring system.
     *
     * @param seq1            First sequence (e.g. Planned route string)
     * @param seq2            Second sequence (e.g. Actual route string)
     * @param matchScore      Score added for matching characters
     * @param mismatchPenalty Penalty (negative) subtracted for mismatched characters
     * @param gapPenalty      Penalty (negative) subtracted for inserting gaps
     * @return AlignmentResult object
     */
    public static AlignmentResult align(String seq1, String seq2, int matchScore, int mismatchPenalty, int gapPenalty) {
        String s1 = seq1 == null ? "" : seq1;
        String s2 = seq2 == null ? "" : seq2;

        int m = s1.length();
        int n = s2.length();
        int[][] scoreMatrix = new int[m + 1][n + 1];

        // Initialize boundary condition with gap penalties
        for (int i = 0; i <= m; i++) scoreMatrix[i][0] = i * gapPenalty;
        for (int j = 0; j <= n; j++) scoreMatrix[0][j] = j * gapPenalty;

        // Fill dynamic programming matrix
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int sim = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? matchScore : mismatchPenalty;
                int matchVal = scoreMatrix[i - 1][j - 1] + sim;
                int deleteVal = scoreMatrix[i - 1][j] + gapPenalty;
                int insertVal = scoreMatrix[i][j - 1] + gapPenalty;

                scoreMatrix[i][j] = Math.max(matchVal, Math.max(deleteVal, insertVal));
            }
        }

        // Traceback from (m, n) to (0, 0)
        StringBuilder align1 = new StringBuilder();
        StringBuilder align2 = new StringBuilder();
        StringBuilder visual = new StringBuilder();

        int i = m;
        int j = n;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0) {
                int sim = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? matchScore : mismatchPenalty;
                if (scoreMatrix[i][j] == scoreMatrix[i - 1][j - 1] + sim) {
                    align1.append(s1.charAt(i - 1));
                    align2.append(s2.charAt(j - 1));
                    if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                        visual.append("|");
                    } else {
                        visual.append(".");
                    }
                    i--;
                    j--;
                    continue;
                }
            }

            if (i > 0 && scoreMatrix[i][j] == scoreMatrix[i - 1][j] + gapPenalty) {
                align1.append(s1.charAt(i - 1));
                align2.append("-");
                visual.append(" ");
                i--;
            } else if (j > 0 && scoreMatrix[i][j] == scoreMatrix[i][j - 1] + gapPenalty) {
                align1.append("-");
                align2.append(s2.charAt(j - 1));
                visual.append(" ");
                j--;
            } else {
                // Fallback boundary shift
                if (i > 0) {
                    align1.append(s1.charAt(i - 1));
                    align2.append("-");
                    visual.append(" ");
                    i--;
                } else {
                    align1.append("-");
                    align2.append(s2.charAt(j - 1));
                    visual.append(" ");
                    j--;
                }
            }
        }

        String res1 = align1.reverse().toString();
        String res2 = align2.reverse().toString();
        String resVis = visual.reverse().toString();

        return new AlignmentResult(s1, s2, res1, res2, resVis, scoreMatrix[m][n], scoreMatrix);
    }
}
