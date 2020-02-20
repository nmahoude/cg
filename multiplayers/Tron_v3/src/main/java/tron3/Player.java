package tron3;

import java.util.Scanner;

import tron.common.Grid;

public class Player {
  static Grid grid = new Grid();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    
    // game loop
    while (true) {
        int N = in.nextInt(); // total number of players (2 to 4).
        int myId = in.nextInt(); // your player number (0 to 3).
        for (int i = 0; i < N; i++) {
            int x0 = in.nextInt(); // starting X coordinate of lightcycle (or -1)
            int y0 = in.nextInt(); // starting Y coordinate of lightcycle (or -1)
            int x1 = in.nextInt(); // starting X coordinate of lightcycle (can be the same as X0 if you play before this player)
            int y1 = in.nextInt(); // starting Y coordinate of lightcycle (can be the same as Y0 if you play before this player)
            if (x0 != -1) {
              grid.markOwner(x0, y0, i);
            }
            if (x1 != -1) {
              grid.markOwner(x1, y1, i);
            } else {
              grid.resetDeadOwner(i);
            }
        }

        AI ai = new AI();
        ai.think(this);
//        grid.toTDD();
        System.out.println("LEFT"); // A single line with UP, DOWN, LEFT or RIGHT
    }
}
}
