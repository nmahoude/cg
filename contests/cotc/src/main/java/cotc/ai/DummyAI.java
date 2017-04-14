package cotc.ai;

import cotc.GameState;

public class DummyAI implements AI {
  GameState state;
  
  public DummyAI() {
  }

  @Override
  public AISolution evolve() {
    DummySolution solution = new DummySolution(state);

    return solution;
  }

  @Override
  public void setState(GameState state) {
    this.state = state;
  }

}
