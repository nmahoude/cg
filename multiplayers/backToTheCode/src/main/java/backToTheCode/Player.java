package backToTheCode;

import java.util.*;
import java.io.*;
import java.math.*;

class Player {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int opponentCount = in.nextInt(); // Opponent count

    // game loop
    while (true) {
      int gameRound = in.nextInt();
      int x = in.nextInt(); // Your x position
      int y = in.nextInt(); // Your y position
      int backInTimeLeft = in.nextInt(); // Remaining back in time

      for (int i = 0; i < opponentCount; i++) {
        int opponentX = in.nextInt(); // X position of the opponent
        int opponentY = in.nextInt(); // Y position of the opponent
        int opponentBackInTimeLeft = in.nextInt(); // Remaining back in time of
                                                   // the opponent
      }

      for (int i = 0; i < 20; i++) {
        String line = in.next(); // One line of the map ('.' = free, '0' = you,
                                 // otherwise the id of the opponent)
      }

      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");

      // action: "x y" to move or "BACK rounds" to go back in time
      System.out.println("17 10");
    }
  }
}