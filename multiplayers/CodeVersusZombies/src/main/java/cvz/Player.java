package cvz;

import java.util.Scanner;

import trigonometry.Point;

public class Player {
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    // game loop
    while (true) {
      int x = in.nextInt();
      int y = in.nextInt();
      Point myPos = new Point(x, y);
      
      int humanCount = in.nextInt();
      double minDist = Integer.MAX_VALUE;
      Point closer = null;
      
      for (int i = 0; i < humanCount; i++) {
        int humanId = in.nextInt();
        int humanX = in.nextInt();
        int humanY = in.nextInt();
        Point human = new Point(humanX, humanY);
        if (human.squareDistance(myPos) < minDist) {
          minDist = human.squareDistance(myPos);
          closer = human;
        }
      }
      int zombieCount = in.nextInt();
      for (int i = 0; i < zombieCount; i++) {
        int zombieId = in.nextInt();
        int zombieX = in.nextInt();
        int zombieY = in.nextInt();
        int zombieXNext = in.nextInt();
        int zombieYNext = in.nextInt();
      }

      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");

      System.out.println((int)(closer.x)+ " "+(int)(closer.y)); // Your destination coordinates
    }
  }
}
