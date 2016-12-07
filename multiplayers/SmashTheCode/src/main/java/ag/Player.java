package ag;

import java.util.Scanner;

import stc2.BitBoard;
import stc2.Game;

public class Player {
  static Game game = new Game();
  private static AGSolution bestSolution;
  
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

        AG ag = new AG();
        ag.simulate(game, bestSolution);
        
        game.lastScore = game.myScore;
        System.out.println(ag.output());
        bestSolution = ag.bestSolution;
    }
}

}
