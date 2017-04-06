package csb;

import java.util.Scanner;

import csb.entities.CheckPoint;
import csb.entities.Pod;

public class Player {
  static Map map = new Map();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int laps = in.nextInt();
    map.readCheckpoints(in);
    int round = 0;
    // game loop
    while (true) {
      round++;
      for (int i = 0; i < 4; i++) {
        int x = in.nextInt(); // x position of your pod
        int y = in.nextInt(); // y position of your pod
        int vx = in.nextInt(); // x speed of your pod
        int vy = in.nextInt(); // y speed of your pod
        int angle = in.nextInt(); // angle of your pod
        int nextCheckPointId = in.nextInt(); // next check point id of your pod
        map.pods[i].readInput(x, y, vx, vy, angle, nextCheckPointId);
      }

      System.out.println("8000 4500 0");

      Pod pod = map.pods[1];
      CheckPoint cp =map.checkPoints[pod.nextCheckPointId];
      String target = ""+(int)(cp.position.x)+" "+(int)(cp.position.y);
      if (round > 20) {
        System.out.println(""+target+" 100");
      } else {
        System.out.println(""+target+" 0");
      }

    }
  }
}
