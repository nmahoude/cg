package cotc.ai;

import cotc.GameState;

public interface AI {

  public AISolution evolve();

  public void setState(GameState state1);

}
