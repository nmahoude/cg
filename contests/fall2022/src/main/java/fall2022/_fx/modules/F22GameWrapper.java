package fall2022._fx.modules;

import java.util.ArrayList;
import java.util.List;

import cgfx.wrappers.GameWrapper;
import fall2022.Player;
import fall2022.State;
import fast.read.FastReader;

public class F22GameWrapper extends GameWrapper {

  protected Player player = new Player();
  public State state = player.state;
  
  
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
		for (String a : actions ) {
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
    state.readGlobal(FastReader.fromString(input));
	}

	@Override
	protected void readTurnInput(String input) {
		FastReader in = FastReader.fromString(input);
    state.readGlobal(in);
		state.readOptionalState(in);
		state.readPacked(in);
	}
	
	public void readFromInput(String input) {
    FastReader in = FastReader.fromString(input);
    state.readGlobal(in);
    state.readOptionalState(in);
    state.readPacked(in);
  }

	public void readInversedFromInput(String input) {
    FastReader in = FastReader.fromString(input);
    state.readGlobal(in);
    state.readOptionalState(in);
    state.readPackedInversed(in);
	}


}
