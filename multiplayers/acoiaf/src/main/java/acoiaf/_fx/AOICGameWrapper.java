package acoiaf._fx;

import java.util.List;

import acoiaf.Action;
import acoiaf.Player;
import acoiaf.State;
import cgfx.frames.Frame;
import cgfx.wrappers.GameWrapper;
import fast.read.FastReader;

public class AOICGameWrapper extends GameWrapper {

	State state = new State();
  List<Action> actions;
	
	@Override
	protected void _copyFrom(GameWrapper gameWrapper) {
		throw new RuntimeException("Not Implemented");
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
    long start = System.currentTimeMillis();
    actions = Player.ai.think(this.state);
    long end = System.currentTimeMillis();
    System.err.println("Think in "+(end-start)+"ms");
    System.err.println("New Actions ");
    actions.forEach(Action::debug);
	}

	@Override
	protected void _resetFromBase() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void _applyAction(String string) {
		// TODO Auto-generated method stub
		
	}

	public void readFromInput(String cleanInput) {
		FastReader in = FastReader.fromString(cleanInput);
		state.readInit(in);
		state.readOptional(in);
		state.read(in);
		
	}

  public void readFromFrame(Frame f) {
    readFromInput(f.cleanStderr());
  }

}
