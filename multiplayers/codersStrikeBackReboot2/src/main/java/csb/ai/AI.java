package csb.ai;

import csb.GameState;

public interface AI {

  public AISolution getSolution();

  public void setState(GameState state1);

}
