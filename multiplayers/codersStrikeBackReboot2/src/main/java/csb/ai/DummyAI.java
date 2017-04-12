package csb.ai;

import csb.GameState;
import csb.entities.CheckPoint;

public class DummyAI implements AI {
  GameState state;
  
  public DummyAI() {
  }

  @Override
  public AISolution evolve() {
    DummySolution solution = new DummySolution();
    CheckPoint cp = state.checkPoints[state.pods[0].nextCheckPointId];
    solution.target1 = cp.position;
    solution.thrust1 = 100;
    
    cp = state.checkPoints[state.pods[1].nextCheckPointId];
    solution.target2 = cp.position;
    solution.thrust2 = 100;

    return solution;
  }

  @Override
  public void setState(GameState state) {
    this.state = state;
  }

}
