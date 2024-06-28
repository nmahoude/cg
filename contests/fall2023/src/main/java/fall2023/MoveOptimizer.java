package fall2023;

public class MoveOptimizer {
  private final static Simulator SIM = new Simulator();
  private Action[] actions = new Action[] { new Action(), new Action(), new Action(), new Action() };
  private Action[] actions2 = new Action[] { new Action(), new Action(), new Action(), new Action() };
  private State tentative = new State();
  private State tentative2 = new State();
  
  
  public static interface ActionToScore {
    double eval(State tentativeState, Drone tentativeDrone, Action a);
  }
  
  public Vec optimize(State state, Drone drone, int[] speeds, ActionToScore ds) {
    double bestScore = Double.NEGATIVE_INFINITY;
    Vec bestSpeed = new Vec(0,-600);

    Action a = actions[drone.id];
    
    double angleDecal = 0;
    
    for (int s=0;s<speeds.length;s++) {
      for (int angle= 0;angle<360;angle+=2) {
        a.dx = (int)(Math.cos(angleDecal + angle * Math.PI / 180) * speeds[s]);
        a.dy = - (int)(Math.sin(angleDecal + angle * Math.PI / 180) * speeds[s]);
        
        tentative.copyFrom(state);
        SIM.applyJustMe(tentative, actions);
        
        if (tentative.dronesById[drone.id].emergency) continue; // no way
        
        // check if a solution next turn ...
        boolean wayOut = false;
        Action a2 = actions2[drone.id];
        for (int angle2= 0;angle2<360;angle2+=10) {
          a2.dx = (int)(Math.cos(angleDecal + angle2 * Math.PI / 180) * 600);
          a2.dy = - (int)(Math.sin(angleDecal + angle2 * Math.PI / 180) * 600);
          
          tentative2.copyFrom(tentative);
          SIM.applyJustMe(tentative2, actions2);
          
          if (!tentative2.dronesById[drone.id].emergency) {
            wayOut = true;
            break;
            
          }
        }      
        if (!wayOut) {
          continue; 
        }
        
        double score = ds.eval(tentative, tentative.dronesById[drone.id], a);
        if (score > bestScore) {
          bestScore = score;
          bestSpeed.vx = a.dx;
          bestSpeed.vy = a.dy;
        }
      }
    }
    return new Vec(bestSpeed);
  }

}
