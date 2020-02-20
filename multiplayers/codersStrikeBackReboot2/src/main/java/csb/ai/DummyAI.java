package csb.ai;

import csb.GameState;
import csb.entities.CheckPoint;
import trigonometry.Point;

public class DummyAI implements AI {
  GameState state;
  
  public DummyAI() {
  }

  @Override
  public AISolution evolve() {
    DummySolution solution = new DummySolution();
    CheckPoint cp = state.checkPoints[state.pods[0].nextCheckPointId];
    solution.target1 = new Point(cp.x, cp.y);
    solution.thrust1 = 100;
    
    cp = state.checkPoints[state.pods[1].nextCheckPointId];
    solution.target2 = new Point(cp.x, cp.y);
    solution.thrust2 = 100;

    return solution;
  }

  @Override
  public void setState(GameState state) {
    this.state = state;
  }

}
