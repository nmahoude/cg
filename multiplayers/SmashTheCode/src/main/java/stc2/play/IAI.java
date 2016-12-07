package stc2.play;

import stc2.Game;
import stc2.MCTSOld.AjustementVariables;

public interface IAI {
 
  Move getMove();
  public void prepare(Game game, int player);
}
