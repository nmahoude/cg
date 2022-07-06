package csb._cgfx;

import java.util.List;

import cgfx.wrappers.GameWrapper;
import csb.Player;
import csb.State;
import fast.read.FastReader;

public class CSBGameWrapper extends GameWrapper {

  protected Player player = new Player();
  protected State state = player.state;

  @Override
  protected void _copyFrom(GameWrapper gameWrapper) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void readGlobalInput(String in) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void readTurnInput(String in) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected List<String> _calculateAIListOfActions() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void _think() {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void _resetFromBase() {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void _applyAction(String string) {
    // TODO Auto-generated method stub
    
  }

  public void readFromInput(boolean b, String input) {
    FastReader in = FastReader.fromString(input.replace("^", ""));
    player.readGlobal(in);
    player.readTurn(in);
  }

}
