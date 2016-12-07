package h;

import java.util.Scanner;

class Player {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int width = in.nextInt();
    int height = in.nextInt();
    int myId = in.nextInt();

    // game loop
    while (true) {
      for (int i = 0; i < height; i++) {
        String row = in.next();
      }
      int entities = in.nextInt();
      for (int i = 0; i < entities; i++) {
        int entityType = in.nextInt();
        int owner = in.nextInt();
        int x = in.nextInt();
        int y = in.nextInt();
        int param1 = in.nextInt();
        int param2 = in.nextInt();
      }

      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");

      System.out.println("BOMB 6 5");
    }
  }
}
