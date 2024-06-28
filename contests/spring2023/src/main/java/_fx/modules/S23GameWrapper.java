package _fx.modules;

import java.util.ArrayList;
import java.util.List;

import cgfx.wrappers.GameWrapper;
import fast.read.FastReader;
import spring2023.Player;
import spring2023.State;
import spring2023.map.Map;
import spring2023.old.OldPlayer;

public class S23GameWrapper extends GameWrapper {

  protected OldPlayer player = new OldPlayer();
  public State state = new State();
  int[] beacons = new int[Map.MAX_CELLS];
  
  
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
    State.readInit(FastReader.fromString(input));
	}

	@Override
	protected void readTurnInput(String input) {
		FastReader in = FastReader.fromString(input);
    State.readInit(in);
    
    
    Player.state = state;
		state.readOptional(in);
		state.readPacked(in);
	}
	
	public void readFromInput(String input) {
    FastReader in = FastReader.fromString(input);
    State.readInit(in);

    state = new State();
    Player.state = state;
    state.readOptional(in);
    state.readPacked(in);
  }

  public void setBeacons(int[] beacons) {
    this.beacons = beacons;
  }

  public int[] getBeacons() {
    return beacons;
  }
}
