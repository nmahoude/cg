package bitrunner2048;

import java.util.Scanner;

/**
 * Made by Illedan, pb4 and Agade
 **/
public class Player {
  State state = new State();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    new Player().play(in);
  }

  private void play(Scanner in) {
    int mapRadius = in.nextInt();
    int centerRadius = in.nextInt();
    int minSwapImpulse = in.nextInt(); // Impulse needed to steal a prisoner from another car
    int carCount = in.nextInt(); // the number of cars you control

    // game loop
    while (true) {
      state.read(in);
      
      for (int i=0;i<2;i++) {
        int destX = 0;
        int destY = 0;
        int speed = 0;
        
        Entity car = state.entities[i];
        
        if (car.prisonerId != 0) {
          destX = (int) (0 - 3 * car.vx);
          destY = (int) (0 - 3 * car.vy);
          speed = 150;
        } else {
          int bestDist = Integer.MAX_VALUE;
          int bestId = 4;
          for (int j=4;j<state.ballsFE;j++) {
            Entity ball = state.entities[j];
            int dist = (int) ((ball.x - car.x)*(ball.x - car.x) + (ball.y - car.y)*(ball.y - car.y));
            if (dist < bestDist) {
              bestDist = dist;
              bestId = j;
            }
          }
          
          Entity ball = state.entities[bestId];
          
          destX = (int) (ball.x - 3 * car.vx);
          destY = (int) (ball.y - 3 * car.vy);
          speed = 100;
          
        }
        
        
        System.out.println("" + destX + " " + destY + " " + speed + " debug"); // X Y THRUST MESSAGE
      }
      
    }
  }
}
