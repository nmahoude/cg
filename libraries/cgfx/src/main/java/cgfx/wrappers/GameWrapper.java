package cgfx.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public abstract class GameWrapper extends Observable {

	public static final String CURRENT = "Current";
	
	protected abstract void _copyFrom(GameWrapper gameWrapper);

	protected abstract void readGlobalInput(String in) ;
	protected abstract void readTurnInput(String in);
	protected abstract List<String> _calculateAIListOfActions();
	protected abstract void _think();
	protected abstract void _resetFromBase();
	protected abstract void _applyAction(String string);
	
	public void copyFrom(GameWrapper gameWrapper) {
		_copyFrom(gameWrapper);
		setChanged();
		notifyObservers();
	}

	public List<String> calculateAIListOfActions() {
		return _calculateAIListOfActions();
	}
	
	public void think() {
		_think();
		setChanged();
		notifyObservers();
		
	}
	public void resetFromBase() {
		_resetFromBase();
		setChanged();
		notifyObservers();
	}
	public void applyAction(String string) {
		_applyAction(string);
		setChanged();
		notifyObservers();
	}

	
	public void readGlobal(String input) {
		readGlobalInput(input);
		readTurnInput(input);
		
		setChanged();
		notifyObservers();
	}

	public void readTurn(String input) {
		readTurnInput(input);
		setChanged();
		notifyObservers();
	}

	public List<String> getActionsFromString(String pasteString) {
		List<String> actions = new ArrayList<>();
		String[] actionsStr = pasteString.split(";");
		
		actions.add(CURRENT);
		for (int i=0;i<actionsStr.length;i++) {
			actions.add(actionsStr[i]);
		}
		return actions;
	}

}
