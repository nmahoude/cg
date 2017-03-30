package bttc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player {
  static Board board = new Board();
  
  private static int gameRound;
  private static int backInTimeLeft;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int opponentCount = in.nextInt(); // Opponent count

    // game loop
    while (true) {
      gameRound = in.nextInt();
      int x = in.nextInt(); // Your x position
      int y = in.nextInt(); // Your y position
      backInTimeLeft = in.nextInt();
      for (int i = 0; i < opponentCount; i++) {
        int opponentX = in.nextInt(); // X position of the opponent
        int opponentY = in.nextInt(); // Y position of the opponent
        int opponentBackInTimeLeft = in.nextInt(); // Remaining back in time of the opponent
      }

      board.reinit();
      for (int i = 0; i < 20; i++) {
        board.addRow(i, in.next());
      }
      
      P position = new P(x,y);
      List<P> pointsToCheck = new ArrayList<>();
      pointsToCheck.add(position);
      
      P p = board.findClosestFreeCell(position, pointsToCheck, new ArrayList<>());
      
      // action: "x y" to move or "BACK rounds" to go back in time
      System.out.println(""+p.x+" "+p.y);
    }
  }
}