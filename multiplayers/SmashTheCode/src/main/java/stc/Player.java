package stc;

import java.util.Scanner;

public class Player {
  
  static {
    // create some DFS node
    DFSNode.add(100_000);
  }
  static Game game = new Game();
  static Ai ai = new Ai(game);
  static long longestAiTime = 0;
  static long shortestAiTime = Long.MAX_VALUE;
  
    public static void main(String args[]) {
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
              game.myBoard.updateRow(11-i, in.next());
            }
            game.otherScore = in.nextInt();
            for (int i = 0; i < 12; i++) {
              game.otherBoard.updateRow(11-i, in.next());
            }
            
            //game.printTestCase();
            
            long time1 = System.currentTimeMillis();
            ai.think();
            long time2 = System.currentTimeMillis();
            long aiTime = time2-time1;
            longestAiTime = Math.max(longestAiTime, aiTime);
            shortestAiTime = Math.min(shortestAiTime, aiTime);
            
            System.err.println("AI in "+aiTime+" ms. ("+shortestAiTime+" / "+longestAiTime+")");
            
            //ai.debug();
            
            System.out.println(ai.output());
        }
    }
}