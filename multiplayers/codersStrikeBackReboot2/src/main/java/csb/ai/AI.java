package csb.ai;

import csb.GameState;

public interface AI {

  public void setState(GameState state1);

  AISolution evolve(long stopTime);

}
