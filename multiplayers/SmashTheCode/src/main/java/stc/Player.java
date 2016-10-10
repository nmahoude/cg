package stc;

import java.util.*;
import java.io.*;
import java.math.*;

public class Player {
  static Game game = new Game();
  static Ai ai = new Ai(game);
  
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
              game.myBoard.updateRow(i, in.next());
            }
            game.otherScore = in.nextInt();
            for (int i = 0; i < 12; i++) {
              game.otherBoard.updateRow(i, in.next());
            }
            
            ai.think();
            System.out.println(ai.output());
        }
    }
}