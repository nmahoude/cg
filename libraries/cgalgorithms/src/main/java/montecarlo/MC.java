package montecarlo;

import montecarlo.support.Action;
import montecarlo.support.Scorer;
import montecarlo.support.Simulator;
import montecarlo.support.State;
import support.Player;

public class MC {
	
	
	private static final int DEPTH = 0;
	private Action[] actions = new Action[DEPTH];
	private Action[] bestActions = new Action[DEPTH];

	public Action[] think(State initialState) {
		State workState = new State();
		
		double bestScore = Double.NEGATIVE_INFINITY;
		while (Player.start - System.currentTimeMillis() < Player.MAX_THINK_TIME) {
			
			workState.copyFrom(initialState);
			for (int i=0;i<DEPTH;i++) {
				workState = Simulator.simulate(workState, actions[i]);
			}
			double score = Scorer.score(workState);
			if (score > bestScore) {
				bestScore = score;
				// swap actions to keep best
				Action[] tmp = actions;
				actions = bestActions;
				bestActions = tmp;
			}
		}
		return bestActions;
	}
}
