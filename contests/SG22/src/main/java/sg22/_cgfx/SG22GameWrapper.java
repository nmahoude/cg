package sg22._cgfx;

import java.util.List;

import cgfx.wrappers.GameWrapper;
import fast.read.FastReader;
import sg22.Player;

public class SG22GameWrapper extends GameWrapper {
  Player player = new Player();
  
  @Override
  protected void _copyFrom(GameWrapper gameWrapper) {
  }

  @Override
  protected void readGlobalInput(String in) {
    player = new Player();
    player.readGlobal(FastReader.fromString(in));
  }

  @Override
  protected void readTurnInput(String in) {
    player.readTurn(FastReader.fromString(in));
  }

  @Override
  protected List<String> _calculateAIListOfActions() {
    return null;
  }

  @Override
  protected void _think() {
    
  }

  @Override
  protected void _resetFromBase() {
    
  }

  @Override
  protected void _applyAction(String string) {
  }

}
