package meanmax.simulation;

import java.util.Scanner;

import meanmax.entities.Entity;
import trigo.Position;

public class Action {
  public final Position target = new Position();
  public int thrust;

  public void copyFrom(Action action) {
    this.target.x = action.target.x;
    this.target.y = action.target.y;
    this.thrust = action.thrust;
  }

  public static Action parse(Entity entity, String str) {
    Action action = new Action();
    
    try {
      if (str.startsWith("WAIT")) {
        action.thrust = 0;
      } else if (str.startsWith("SKILL")) {
        Scanner in = new Scanner(str);
        in.next(); // SKILL
        action.target.x = in.nextInt();
        action.target.y = in.nextInt();
        action.thrust = -1;
        in.close();
      } else {
        Scanner in = new Scanner(str);
        action.target.x = in.nextInt();
        action.target.y = in.nextInt();
        
        // make sure distance is 1.0 !
        double dx = action.target.x - entity.position.x;
        double dy = action.target.y - entity.position.y;
        double distance = Math.sqrt(dx*dx + dy*dy);
        action.target.x = entity.position.x + dx / distance;
        action.target.y = entity.position.y + dy / distance;
        
        action.thrust = in.nextInt();
        in.close();
      }
    } catch(Exception e) {
      System.out.println(str);
      throw e;
    }
    return action;
  }

  public void output() {
    if (thrust == -1) {
      System.out.println("SKILL "+target.toOutput());
    } else if (thrust == 0) {
      System.out.println("WAIT");
    } else {
      System.out.println(""+target.toOutput()+ " "+thrust);
    }
  }
  
  @Override
  public String toString() {
    if (thrust == -1) {
      return "SKILL "+target.toOutput();
    } else if (thrust == 0) {
      return "WAIT";
    } else {
      return ""+target.toOutput()+ " "+thrust;
    }
  }

  public void debug() {
    if (thrust == -1) {
      System.err.println("SKILL "+target.toOutput());
    } else if (thrust == 0) {
      System.err.println("WAIT");
    } else {
      System.err.println(""+target.toOutput()+ " "+thrust);
    }
  }
}
