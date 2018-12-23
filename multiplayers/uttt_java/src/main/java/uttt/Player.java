package uttt;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import uttt.mcts.MCTS;
import uttt.mcts.NodeCache;

public class Player {
  static int turn = 0;
  public static long start;
  public static Random random = new Random(0); //ThreadLocalRandom.current();
  public static boolean DEBUG = true;
  public static boolean DEBUG_GRID = false;
  public static boolean DEBUG_MCTS = false;

  public static void main(String args[]) {
    NodeCache.init();

    Scanner in = new Scanner(System.in);
    MCTS ai = null;
    
    // game loop
    while (true) {
      int opponentRow = in.nextInt();
      Player.start = System.currentTimeMillis();
      if (turn == 0) {
        ai = new MCTS();
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
        // we are the first player, so we will choose the best spot
        ai.firstToPlay();
        
        ai.think();
        ai.doAction(true, ai.best.gDecal, ai.best.lDecal);
        ai.output();
      } else {
        //System.err.println("Opponent: "+ opponentRow +" , " + opponentCol );
        int gDecal = 3  * (opponentRow / 3) + opponentCol / 3;
        int lDecal = 1 << (3 * (opponentRow % 3) + opponentCol % 3);
        //System.err.println("Decal system : "+gDecal+" , " + Integer.toBinaryString(lDecal));
        ai.doAction(false, gDecal, lDecal);
        
        if (DEBUG_GRID) {
          ai.getCurrentState().debug();
        }

        ai.think();
        ai.doAction(true, ai.best.gDecal, ai.best.lDecal);
        ai.output();

      }
      turn++;
      
    }
  }
}