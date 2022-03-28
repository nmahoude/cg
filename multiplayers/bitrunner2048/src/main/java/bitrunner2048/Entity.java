package bitrunner2048;

import java.util.Scanner;

public class Entity {
  int type;
  double x,y;
  double vx, vy;
  int prisonerId;
  
  public void read(Scanner in) {
    type = in.nextInt();
    x = in.nextInt(); // position x relative to center 0
    y = in.nextInt(); // position y relative to center 0
    vx = in.nextInt(); // horizontal speed. Positive is right
    vy = in.nextInt(); // vertical speed. Positive is downwards
    int angle = in.nextInt(); // facing angle of this car
    prisonerId = in.nextInt(); // id of carried prisoner, -1 if none
  }
  
  
}
