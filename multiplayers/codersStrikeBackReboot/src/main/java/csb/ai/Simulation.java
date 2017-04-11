package csb.ai;

import csb.entities.Pod;
import csb.game.PhysicsEngine;
import csb.simulation.Action;
import trigonometry.Vector;

public class Simulation {
  public PhysicsEngine engine;
  public AGScorer scorer1;
  public AGScorer scorer2;
  
  public Simulation(PhysicsEngine engine) {
    this.engine = engine;
  }
  
  public double simulate(Action[] actions0, Action[] actions1,Action[] actions2,Action[] actions3) {
    for (int i=0;i<AGSolution.ACTION_SIZE;i++) {
      applyMoves(0, actions0[i]);
      applyMoves(1, actions1[i]);
      applyMoves(2, actions2[i]);
      applyMoves(3, actions3[i]);
      engine.simulate();
      
    }
    double score = scorer1.score(this, engine.pods[0])
                  +scorer2.score(this, engine.pods[1]);
    return score;
  }

  private void applyMoves(int i, Action action) {
    Pod pod = engine.pods[i];
    Vector direction = pod.direction.rotate(action.angle);
    pod.apply(direction, action.thrust);
  }

  public String actionOutput(AGSolution solution, int i) {
    Pod pod;
    Action action;
    if (i == 0) {
      pod = engine.pods[0];
      action = solution.actions0[0];
    } else {
      pod = engine.pods[1];
      action = solution.actions1[0];
    }
    
    Vector direction = pod.direction.rotate(action.angle);
    return ""+(int)(pod.position.x+3000*direction.vx)+" "+(int)(pod.position.y+ 3000*direction.vy)+" "+(int)action.thrust;
  }


}
