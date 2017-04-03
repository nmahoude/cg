package csb;

import java.util.Scanner;

public class Player {

      public static void main(String args[]) {
          Scanner in = new Scanner(System.in);
          int laps = in.nextInt();
          int checkpointCount = in.nextInt();
          for (int i = 0; i < checkpointCount; i++) {
              int checkpointX = in.nextInt();
              int checkpointY = in.nextInt();
          }

          // game loop
          while (true) {
              for (int i = 0; i < 2; i++) {
                  int x = in.nextInt(); // x position of your pod
                  int y = in.nextInt(); // y position of your pod
                  int vx = in.nextInt(); // x speed of your pod
                  int vy = in.nextInt(); // y speed of your pod
                  int angle = in.nextInt(); // angle of your pod
                  int nextCheckPointId = in.nextInt(); // next check point id of your pod
              }
              for (int i = 0; i < 2; i++) {
                  int x2 = in.nextInt(); // x position of the opponent's pod
                  int y2 = in.nextInt(); // y position of the opponent's pod
                  int vx2 = in.nextInt(); // x speed of the opponent's pod
                  int vy2 = in.nextInt(); // y speed of the opponent's pod
                  int angle2 = in.nextInt(); // angle of the opponent's pod
                  int nextCheckPointId2 = in.nextInt(); // next check point id of the opponent's pod
              }

              // Write an action using System.out.println()
              // To debug: System.err.println("Debug messages...");


              // You have to output the target position
              // followed by the power (0 <= thrust <= 100)
              // i.e.: "x y thrust"
              System.out.println("8000 4500 100");
              System.out.println("8000 4500 100");
          }
      }
  }
}
