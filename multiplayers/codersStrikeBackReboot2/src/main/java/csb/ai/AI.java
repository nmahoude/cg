package csb.ai;

import csb.GameState;

public interface AI {

  public AISolution evolve();

  public void setState(GameState state1);

}
