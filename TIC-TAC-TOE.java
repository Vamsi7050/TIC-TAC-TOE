import java.util.Scanner;

/**
 * Console Tic-Tac-Toe with selectable board size (3x3, 5x5, 6x6).
 * Modes: two players, or you (X) vs computer (O).
 * The computer uses minimax with alpha-beta pruning
 * (full search on 3x3, depth-limited with a heuristic on bigger boards).
 * Win length: 3 in a row on 3x3, 4 in a row on 5x5 and 6x6.
 */
public class TicTacToe {

    private static final char HUMAN = 'X';
    private static final char COMPUTER = 'O';
    private static final int WIN_SCORE = 100000;

    private static char[] board;
    private static int size;
    private static int winLen;
    private static int maxDepth;

  public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean playAgain;

        do {
            size = readSize(sc);
            boolean vsComputer = readMode(sc);
            winLen = Math.min(size, 4);
            maxDepth = size == 3 ? 9 : (size == 5 ? 4 : 3);
            board = new char[size * size];
            java.util.Arrays.fill(board, ' ');

            char player = 'X';
            int moves = 0;
            char winner = ' ';

            System.out.println("\n" + size + "x" + size + " board - get " + winLen + " in a row to win.");

            while (winner == ' ' && moves < board.length) {
                printBoard();
                int idx;
                if (vsComputer && player == COMPUTER) {
                    idx = bestMove();
                    System.out.println("Computer plays " + (idx + 1));
                } else {
                    idx = readMove(sc, player) - 1;
                }
                board[idx] = player;
                moves++;
                winner = checkWinner();
                if (winner == ' ') {
                    player = (player == 'X') ? 'O' : 'X';
                }
            }

            printBoard();
            if (winner == ' ') {
                System.out.println("It's a draw!");
            } else if (vsComputer) {
                System.out.println(winner == HUMAN ? "You win!" : "Computer wins!");
            } else {
                System.out.println("Player " + winner + " wins!");
            }

            System.out.print("Play again? (y/n): ");
            playAgain = sc.next().trim().equalsIgnoreCase("y");
        } while (playAgain);

        System.out.println("Thanks for playing!");
        sc.close();
    }
    // ---------- Setup ----------

    private static int readSize(Scanner sc) {
        while (true) {
            System.out.print("Choose board size (3, 5 or 6): ");
            if (!sc.hasNextInt()) {
                System.out.println("Please enter a number.");
                sc.next();
                continue;
            }
            int n = sc.nextInt();
            if (n == 3 || n == 5 || n == 6) return n;
            System.out.println("Only 3, 5 or 6 are allowed.");
        }
    }

    private static boolean readMode(Scanner sc) {
        while (true) {
            System.out.print("Mode: 1 = vs Computer (you are X), 2 = Two players: ");
            if (!sc.hasNextInt()) {
                System.out.println("Please enter 1 or 2.");
                sc.next();
                continue;
            }
            int n = sc.nextInt();
            if (n == 1) return true;
            if (n == 2) return false;
            System.out.println("Enter 1 or 2.");
        }
    }

 // ---------- Display and input ----------

    private static void printBoard() {
        int w = String.valueOf(board.length).length();
        StringBuilder sep = new StringBuilder();
        for (int c = 0; c < size; c++) {
            sep.append("-".repeat(w + 2));
            if (c < size - 1) sep.append("+");
        }
        System.out.println();
        for (int r = 0; r < size; r++) {
            StringBuilder row = new StringBuilder();
            for (int c = 0; c < size; c++) {
                int i = r * size + c;
                String s = board[i] == ' ' ? String.valueOf(i + 1) : String.valueOf(board[i]);
                row.append(" ").append(" ".repeat(w - s.length())).append(s).append(" ");
                if (c < size - 1) row.append("|");
            }
            System.out.println(row);
            if (r < size - 1) System.out.println(sep);
        }
        System.out.println();
    }

    private static int readMove(Scanner sc, char player) {
        int max = board.length;
        while (true) {
            System.out.print("Player " + player + ", choose a cell (1-" + max + "): ");
            if (!sc.hasNextInt()) {
                System.out.println("Please enter a number.");
                sc.next();
                continue;
            }
            int n = sc.nextInt();
            if (n < 1 || n > max) {
                System.out.println("Number must be between 1 and " + max + ".");
            } else if (board[n - 1] != ' ') {
                System.out.println("That cell is already taken.");
            } else {
                return n;
            }
        }
    }

    // ---------- Win detection ----------

    // Checks every cell in 4 directions (right, down, diag-down-right, diag-down-left)
    private static char checkWinner() {
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                char p = board[r * size + c];
                if (p == ' ') continue;
                for (int[] d : dirs) {
                    int k = 1;
                    while (k < winLen) {
                        int nr = r + d[0] * k, nc = c + d[1] * k;
                        if (nr < 0 || nr >= size || nc < 0 || nc >= size
                                || board[nr * size + nc] != p) break;
                        k++;
                    }
                    if (k == winLen) return p;
                }
            }
        }
        return ' ';
    }

    // ---------- Computer AI (minimax + alpha-beta) ----------

    private static int bestMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestIdx = -1;
        for (int i : orderedMoves()) {
            board[i] = COMPUTER;
            int score = minimax(maxDepth - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            board[i] = ' ';
            if (score > bestScore) {
                bestScore = score;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    private static int minimax(int depth, boolean computerTurn, int alpha, int beta) {
        char w = checkWinner();
        if (w == COMPUTER) return WIN_SCORE + depth;   // faster wins score higher
        if (w == HUMAN) return -WIN_SCORE - depth;     // slower losses score higher
        if (isFull()) return 0;
        if (depth == 0) return evaluate();

        if (computerTurn) {
            int best = Integer.MIN_VALUE;
            for (int i : orderedMoves()) {
                board[i] = COMPUTER;
                best = Math.max(best, minimax(depth - 1, false, alpha, beta));
                board[i] = ' ';
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break;
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i : orderedMoves()) {
                board[i] = HUMAN;
                best = Math.min(best, minimax(depth - 1, true, alpha, beta));
                board[i] = ' ';
                beta = Math.min(beta, best);
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    private static boolean isFull() {
        for (char c : board) if (c == ' ') return false;
        return true;
    }

    // Empty cells sorted by closeness to the centre (better pruning, better play)
    private static int[] orderedMoves() {
        int count = 0;
        for (char c : board) if (c == ' ') count++;
        Integer[] moves = new Integer[count];
        int k = 0;
        for (int i = 0; i < board.length; i++) if (board[i] == ' ') moves[k++] = i;
        double mid = (size - 1) / 2.0;
        java.util.Arrays.sort(moves, (a, b) -> Double.compare(dist(a, mid), dist(b, mid)));
        int[] result = new int[count];
        for (int i = 0; i < count; i++) result[i] = moves[i];
        return result;
    }

    private static double dist(int idx, double mid) {
        double dr = idx / size - mid, dc = idx % size - mid;
        return dr * dr + dc * dc;
    }

    // Heuristic for non-terminal positions: score every window of winLen cells
    // that contains marks of only one player.
    private static int evaluate() {
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        int score = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                for (int[] d : dirs) {
                    int er = r + d[0] * (winLen - 1), ec = c + d[1] * (winLen - 1);
                    if (er < 0 || er >= size || ec < 0 || ec >= size) continue;
                    int comp = 0, hum = 0;
                    for (int k = 0; k < winLen; k++) {
                        char p = board[(r + d[0] * k) * size + (c + d[1] * k)];
                        if (p == COMPUTER) comp++;
                        else if (p == HUMAN) hum++;
                    }
                    if (hum == 0 && comp > 0) score += comp * comp * comp;
                    else if (comp == 0 && hum > 0) score -= hum * hum * hum;
                }
            }
        }
        return score;
    }
}
