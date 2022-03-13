package breakthrough;

import fast.read.FastReader;

public class Player {

  public static int turn;
  public static boolean firstPlayer;
  

  public static void main(String args[]) {
    FastReader in = new FastReader(System.in);

    new Player().play(in);
  }

  private void play(FastReader in) {
    firstPlayer = true;
    turn = 0;

    // game loop
    while (true) {
      turn++;

      String opponentMove = in.next(); // last move played or "None"
      System.err.println(opponentMove);
      if (turn == 1 && !"None".equals(opponentMove)) {
        firstPlayer = false;
      }

      int legalMoves = in.nextInt(); // number of legal moves
      String bestMoveString = "";
      int bestMoveScore = 0;

      System.err.println("Legal moves : "+legalMoves);
      for (int i = 0; i < legalMoves; i++) {
        String moveString = "";
        moveString = in.next(); // a legal move
        int row = moveString.charAt(3) - '0';
        if (!firstPlayer)
          row = 8 - row;

        if (row > bestMoveScore) {
          bestMoveScore = row;
          bestMoveString = moveString;
        }
        System.err.println(moveString);
      }

      System.out.println(bestMoveString);
    }
  }
}
