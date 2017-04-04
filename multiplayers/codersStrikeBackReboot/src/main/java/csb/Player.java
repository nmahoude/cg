package csb;

import java.util.Scanner;

public class Player {
  static Map map = new Map();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int laps = in.nextInt();
    map.readCheckpoints(in);

    // game loop
    while (true) {
      for (int i = 0; i < 4; i++) {
        int x = in.nextInt(); // x position of your pod
        int y = in.nextInt(); // y position of your pod
        int vx = in.nextInt(); // x speed of your pod
        int vy = in.nextInt(); // y speed of your pod
        int angle = in.nextInt(); // angle of your pod
        int nextCheckPointId = in.nextInt(); // next check point id of your pod
        map.pods[i].readInput(x, y, vx, vy, angle, nextCheckPointId);
      }

      System.out.println("8000 4500 100");
      System.out.println("8000 4500 100");
    }
  }
}
