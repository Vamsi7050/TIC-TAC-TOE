import java.util.Scanner;

/**
 * Console Tic-Tac-Toe for two players with selectable board size (3x3, 5x5, 6x6).
 * Win length: 3 in a row on 3x3, 4 in a row on 5x5 and 6x6.
 */
public class TicTacToe {

    private static char[] board;
    private static int size;
    private static int winLen;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean playAgain;

        do {
            size = readSize(sc);
            winLen = Math.min(size, 4);
            board = new char[size * size];
            java.util.Arrays.fill(board, ' ');

            char player = 'X';
            int moves = 0;
            char winner = ' ';

            System.out.println("\n" + size + "x" + size + " board - get " + winLen + " in a row to win.");

            while (winner == ' ' && moves < board.length) {
                printBoard();
                int cell = readMove(sc, player);
                board[cell - 1] = player;
                moves++;
                winner = checkWinner();
                if (winner == ' ') {
                    player = (player == 'X') ? 'O' : 'X';
                }
            }

            printBoard();
            System.out.println(winner != ' ' ? "Player " + winner + " wins!" : "It's a draw!");

            System.out.print("Play again? (y/n): ");
            playAgain = sc.next().trim().equalsIgnoreCase("y");
        } while (playAgain);

        System.out.println("Thanks for playing!");
        sc.close();
    }

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
                          }
