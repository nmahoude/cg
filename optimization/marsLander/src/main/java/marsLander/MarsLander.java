package marsLander;

import java.util.Scanner;

public class MarsLander {
  public double x,y;
  public double vx, vy;
  
  public int fuel;
  public int angle;
  public int thrust;
  
  public void readInput(Scanner in) {
    x = in.nextInt();
    y = in.nextInt();
    vx = in.nextInt();
    vy = in.nextInt();
    fuel = in.nextInt();
    angle = in.nextInt();
    thrust = in.nextInt();
    if (Player.maxFuel == 0) {
      Player.maxFuel = fuel;
    }
    if (Player.DEBUG_OUTPUT) {
      System.err.println(""+(int)x+" "+(int)y+" "+(int)vx+" "+(int)vy+" "+fuel+" "+angle+" "+thrust);
    }
  }
  
  public int getXAsInt() {
    return (int)x; //round(x);
  }
  public int getYAsInt() {
    return (int)y; //round(y);
  }
  public int getVyAsInt() {
    return (int)vy; //round(vy);
  }
  public int getVxAsInt() {
    return (int)vx;//round(vx);
  }

  
  private int round(double d) {
    if (d < 0) {
      return (int) (d - 0.5);
    } else {
      return (int) (d + 0.5);
    }
  }

  public void copyFrom(MarsLander originalLander) {
    this.x = originalLander.x;
    this.y = originalLander.y;
    this.vx = originalLander.vx;
    this.vy = originalLander.vy;
    this.angle = originalLander.angle;
    this.thrust = originalLander.thrust;
    this.fuel = originalLander.fuel;
  }
}
