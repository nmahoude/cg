package ag;

import java.util.Scanner;

import stc2.BitBoard;
import stc2.Game;
import stc2.Simulation;

public class Player {
  static Game game = new Game();
  private static AGSolution bestSolution;
  private static int bestOppPoints1;
  private static int bestOppPoints2;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    // game loop
    while (true) {
        for (int i = 0; i < 8; i++) {
          game.nextBalls[i] = in.nextInt();
          game.nextBalls2[i] = in.nextInt();
        }
        game.nanoStart = System.nanoTime();
        game.myScore = in.nextInt();
        game.prepare();
        
        for (int i = 0; i < 12; i++) {
          game.myBoard.updateRow(i, in.next());
        }
        game.myBoard.buildCompleteLayerMask();
        int skullsCount = game.myBoard.layers[BitBoard.SKULL_LAYER].bitCount();

        game.otherScore = in.nextInt();
        for (int i = 0; i < 12; i++) {
          game.otherBoard.updateRow(i, in.next());
        }
        game.otherBoard.buildCompleteLayerMask();

        System.err.println(game.debugPairs());
        System.err.println(game.otherBoard.getDebugString());
        
        int maxDepth = 8;

        forseeOpponentDepth2();
        if (bestOppPoints1 > 70*6*2) {
          maxDepth  = 2;
        } else if (bestOppPoints1 > 70*6*4) {
          maxDepth  = 1;
        } else if (bestOppPoints2 > 70*6*4) {
          maxDepth = 2;
        }
        
        System.err.println("Forsee opponent : "+bestOppPoints1+" / "+bestOppPoints2+" -> depth="+maxDepth);
        AG ag = new AG();
        ag.simulate(game, game.myBoard, maxDepth, 90_000_000, bestSolution);
        
        game.lastScore = game.myScore;
        System.out.println(ag.output());
        bestSolution = ag.bestSolution;
    }
}

  private static void forseeOpponentDepth2() {
    Simulation sim = new Simulation();
    sim.board = new BitBoard();
    Simulation sim2 = new Simulation();
    sim2.board = new BitBoard();
    
    bestOppPoints1 = 0;
    bestOppPoints2 = 0;
    int[] bestKeys = new int[2];
    
    for (int key=0;key<24;key++) {
      if (key == 2 || key ==22) { continue; }
      sim.board.copyFrom(game.otherBoard);
      sim.putBalls(game.nextBalls[0], game.nextBalls2[0], AGSolution.keyToRotation(key), AGSolution.keyToColumn(key));
      if (sim.points > bestOppPoints1) { 
        bestOppPoints1 = sim.points; 
      }
      for (int key2 =0;key2<24;key2++) {
        if (key2 == 2 || key2 ==22) { continue; }
        sim2.board.copyFrom(sim.board);
        sim2.putBalls(game.nextBalls[1], game.nextBalls2[1], AGSolution.keyToRotation(key2), AGSolution.keyToColumn(key2));
        if (sim2.points > bestOppPoints2) { 
          bestOppPoints2 = sim2.points; 
          bestKeys[0] = key;
          bestKeys[1] = key2;
        }
      }
    }
    System.err.println("Best keys for opp : "+bestKeys[0]+" -> "+bestKeys[1]);
  }

}
