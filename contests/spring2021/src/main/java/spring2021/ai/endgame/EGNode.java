package spring2021.ai.endgame;

import java.util.ArrayList;
import java.util.List;

import spring2021.Action;
import spring2021.State;

public class EGNode {
	State state = new State();
	Action action;
	List<Action> actions = new ArrayList<>(10);
	public EGNode parent;

	public List<Action> calculatePossibleMoves() {
		actions.clear();
		actions.add(Action.WAIT);
		for (int j=0;j<EndGameSearch.indexesOfMyTreesFE;j++) {
			int i = EndGameSearch.indexesOfMyTrees[j];
			
			int size = state.trees[i];
			if (size == -1) continue; // nothing here
			if (state.isDormant(i)) continue;
			
			if (size == 3) {
				if (state.costToComplete(0) <= state.sun[0]) actions.add(Action.complete(i));
			} else {
				if (state.day-20 <= size) {
					if (state.costToGrow(0, size) <= state.sun[0])	actions.add(Action.grow(i));
				}
			}
		}
		return actions;
	}
	
	
}
