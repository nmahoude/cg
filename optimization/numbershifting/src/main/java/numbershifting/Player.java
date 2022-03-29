package numbershifting;

import java.util.Scanner;

public class Player {
  static int turn = 0;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    System.out.println("first_level");

    // game loop
    while (true) {
      turn ++;
        int width = in.nextInt();
        int height = in.nextInt();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int cell = in.nextInt();
            }
        }

        switch(turn) {
        case 1: 
          System.out.println("7 4 L +");
          System.out.println("3 0 D -");
          System.out.println("6 4 L -");
          break;
        case 2:
          System.out.println("4 2 D -");
          System.out.println("4 4 R +");
          System.out.println("7 0 L -");
          break;
        }
    }
}
}
