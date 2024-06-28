package exolegend._fx.modules;

import java.util.ArrayList;
import java.util.List;

import cgfx.frames.Frame;
import cgfx.wrappers.GameWrapper;
import exolegend.Player;
import exolegend.State;
import fast.read.FastReader;

public class XLGameWrapper extends GameWrapper {

  public State state = new State();
  
  public XLGameWrapper() {
  }
  
  
  @Override
  protected void _applyAction(String arg0) {
  }

  @Override
  protected List<String> _calculateAIListOfActions() {
    return null;
  }

  @Override
  public List<String> getActionsFromString(String pasteString) {
    String[] actions = pasteString.split(";");
    List<String> actionsList = new ArrayList<>();
    for (String a : actions) {
      actionsList.add(a);
    }
    return actionsList;
  }

  @Override
  protected void _copyFrom(GameWrapper arg0) {
  }

  @Override
  protected void _resetFromBase() {
  }

  @Override
  protected void _think() {
  }

  @Override
  protected void readGlobalInput(String input) {
    State.readInit(FastReader.fromString(cleanInput(input)));
  }

  @Override
  protected void readTurnInput(String input) {
    FastReader in = FastReader.fromString(input);
    State.readPackedInit(in);

    Player.state = state;
    state.readOptional(in);
    state.readPacked(in);
  }

  public void readFull(String input) {
    FastReader in = FastReader.fromString(cleanInput(input));
    State.readPackedInit(in);

    Player.state = state;
    state.readOptional(in);
    state.readPacked(in);
  }
  
  @Override
  public void readFrame(Frame frame) {
    super.readFrame(frame);
    
  }
  
}
