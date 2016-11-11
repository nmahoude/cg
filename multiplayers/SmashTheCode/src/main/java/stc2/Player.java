package stc2;

import java.util.Scanner;

public class Player {
  static Game game = new Game();
  static MCTS mcts = new MCTS();
  
  public static void main(String args[]) {
    mcts.attachGame(game);
    Scanner in = new Scanner(System.in);
    // game loop
    while (true) {
        for (int i = 0; i < 8; i++) {
          game.nextBalls[i] = in.nextInt();
          game.nextBalls2[i] = in.nextInt();
        }
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
        
//        System.err.println(game.debugPairs());
//        System.err.println(game.myBoard.getJunitString());
        
        long time1 = System.currentTimeMillis();
        System.err.println("Skulls b4/af: "+game.lastSkullsCount+" / "+skullsCount);
        mcts.simulate(skullsCount == game.lastSkullsCount);
        long time2 = System.currentTimeMillis();
        long aiTime = time2-time1;
        System.err.println("AI Time : "+aiTime);
        
        game.lastSkullsCount = mcts.getSkullCountAfterMove();
        System.out.println(mcts.output());
    }
}

}
