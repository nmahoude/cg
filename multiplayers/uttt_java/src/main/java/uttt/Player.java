package uttt;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import uttt.mcts.MCTS;
import uttt.mcts.NodeCache;
import uttt.state.State;

public class Player {
  static int turn = 0;
  public static long start;
  public static Random random = ThreadLocalRandom.current();
  public static boolean DEBUG = true;
  public static boolean DEBUG_GRID = false;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    MCTS ai = null;
    
    // game loop
    while (true) {
      int opponentRow = in.nextInt();
      Player.start = System.currentTimeMillis();
      if (turn == 0) {
        ai = new MCTS();
        NodeCache.init();
        Player.start += 900;
      }
      int opponentCol = in.nextInt();
      int validActionCount = in.nextInt();
      int row = 0;
      int col = 0;
      for (int i = 0; i < validActionCount; i++) {
        row = in.nextInt();
        col = in.nextInt();
      }
    
      if (opponentCol == -1) {
        ai.firstToPlay();
        ai.think();
        ai.doAction(true, ai.best.row, ai.best.col);
        ai.output();
      } else {
        ai.doAction(false, opponentRow, opponentCol);
        
        if (DEBUG_GRID) {
          ai.getCurrentState().debug();
        }

        ai.think();
        ai.doAction(true, ai.best.row, ai.best.col);
        ai.output();

      }
      turn++;
      
    }
  }
}