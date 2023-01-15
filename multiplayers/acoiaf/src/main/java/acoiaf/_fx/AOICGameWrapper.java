package acoiaf._fx;

import java.util.List;

import acoiaf.State;
import cgfx.wrappers.GameWrapper;
import fast.read.FastReader;

public class AOICGameWrapper extends GameWrapper {

	State state = new State();
	
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

	public void readFromInput(String cleanInput) {
		FastReader in = FastReader.fromString(cleanInput);
		state.readInit(in);
		state.readOptional(in);
		state.read(in);
		
	}

}
