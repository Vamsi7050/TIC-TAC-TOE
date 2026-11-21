Tic-Tac-Toe (Java)

Console Tic-Tac-Toe with selectable board size and an AI opponent.

Features

• Board sizes: 3x3, 5x5, 6x6
• Modes: two players, or you (X) vs computer (O)
• Computer uses minimax with alpha-beta pruning
  ◦ 3x3: full search (plays perfectly, cannot be beaten)
  ◦ 5x5 / 6x6: depth-limited search with a heuristic evaluation
• Input validation, win/draw detection, replay

Run

javac TicTacToe.java
java TicTacToe

Rules

• 3 in a row to win on 3x3, 4 in a row on 5x5 and 6x6
(horizontal, vertical or diagonal).
• Enter a cell number to place your mark.
