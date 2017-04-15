package cotc.ai.ag;

import cotc.GameState;
import cotc.ai.AI;
import cotc.ai.AISolution;

public class AG implements AI {

  private GameState state;

  @Override
  public AISolution evolve() {
    return null;
  }

  @Override
  public void setState(GameState state) {
    this.state = state;
  }

}
