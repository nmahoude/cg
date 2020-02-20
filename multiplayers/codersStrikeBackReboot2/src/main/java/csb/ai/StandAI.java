package csb.ai;

import csb.GameState;
import trigonometry.Point;

public class StandAI implements AI{

  private GameState state;

  @Override
  public AISolution evolve() {
    DummySolution solution= new DummySolution();
    solution.target1 = new Point(0,0);
    solution.thrust1 = 0;
    solution.target2 = new Point(0,0);
    solution.thrust2 = 0;
    
    return solution;
  }

  @Override
  public void setState(GameState state) {
    this.state = state;
  }

}
