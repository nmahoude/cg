package spring2022._cgfx.components;

import java.util.Collections;
import java.util.List;

import cgfx.wrappers.GameWrapper;
import fast.read.FastReader;
import spring2022.Player;
import spring2022.State;
import spring2022.ag.LightState;

public class S22GameWrapper extends GameWrapper {

  
  public Player player = new Player();
  public LightState lightState;
  
  public S22GameWrapper() {
    State.DEBUG_INPUTS = false; // don't show input when reviewing a game !
    
  }
  
  
  @Override
  protected void _copyFrom(GameWrapper gameWrapper) {
    // TODO
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


  public void read(FastReader in) {
    player.state.read(in);
  }


  public void readGlobal(FastReader in) {
    player.state.readGlobal(in);
  }


  public void readFog(FastReader in) {
    player.state.readFog(in);
  }


  public void readFromInput(boolean inversed, String i) {
    State input = State.fromInput(i, null);
    player.state.copyFrom(input);
  }


  public State state() {
    return player.state;
  }

}
