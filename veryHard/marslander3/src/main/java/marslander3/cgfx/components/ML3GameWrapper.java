package marslander3.cgfx.components;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import cgfx.wrappers.GameWrapper;
import marslander3.Player;

public class ML3GameWrapper extends GameWrapper {

  
  Player player = new Player();
  
  public ML3GameWrapper() {
    Player.inputLogger.disable(); // don't show input when reviewing a game !
    
  }
  
  
  @Override
  protected void _copyFrom(GameWrapper gameWrapper) {
    // TODO
  }

  @Override
  protected void readGlobalInput(String in) {
    //player = new Player();
    player.readGlobal(new Scanner(in));
    //baseState.copyFrom(player.state);    
  }

  @Override
  protected void readTurnInput(String in) {
    player.readTurn(new Scanner(in));
  }

  @Override
  protected List<String> _calculateAIListOfActions() {
    return Collections.emptyList();
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

}
