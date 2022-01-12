package xmashrush2.ai;

import xmashrush2.Agent;
import xmashrush2.BFS;
import xmashrush2.Direction;
import xmashrush2.Item;
import xmashrush2.Pos;
import xmashrush2.PushAction;
import xmashrush2.State;

public class PushAI {

	private static final int MAX_MOVES = 20;
	private State tmpState = new State();
	private PushAction action;
	private BFS bfs = new BFS();
	
	public void output() {
		// System.out.println("PUSH 3 RIGHT"); // PUSH <id> <direction> | MOVE <direction> | PASS
		System.out.println(action.toString());
	}

	public void think(State currentState) {
		double maxScore = Double.NEGATIVE_INFINITY;
		PushAction best = null;
		for (PushAction action0 : PushAction.actions) {
			
			double minScore = Double.POSITIVE_INFINITY;
			PushAction minBest = null;
			
			int counterActionsCount = 0;
			double totalMinScore = 0;
			for (PushAction action1 : PushAction.actions) {
				if (!action0.isCompatibleWith(action1)) continue; // don't even copy the state
				
				tmpState.copyFrom(currentState);
				tmpState.apply(action0, action1);
				counterActionsCount++;
				
				double myScore = evalScore(tmpState, tmpState.agents[0], action0, false);
				double hisScore = evalScore(tmpState, tmpState.agents[1], action1, false);
				
				double score = 1.1*myScore - hisScore;
				if (score < minScore) {
					minScore = score;
					minBest = action1;
				}
				totalMinScore += score;
			}
			
			tmpState.copyFrom(currentState);
			tmpState.apply(action0, null);
			
			double myScore = evalScore(tmpState, tmpState.agents[0], action0, false && action0 == PushAction.actions(3, Direction.UP));
			
			double avgMinScore = totalMinScore / counterActionsCount;
			System.err.println("My action : "+action0+" -> avgMinScore is "+avgMinScore+ " his best actions would be "+minBest);
			System.err.println("My score alone would be "+myScore);
			if (avgMinScore > maxScore) {
				maxScore = avgMinScore;
				best = action0;
			}
		}
		
		System.err.println("Setting the push action ...");
		this.action = best;
		
	}

	
	private double evalScore(State state, Agent agent, PushAction action, boolean debug) {
		bfs.process(state, agent.pos, MAX_MOVES);
		
		double score = 100 * agent.score; // sometimes a push is enough to get an object
		
		if (agent.needs(agent.item)) {
			score += 25; // getting our own item is good
		}
		
		if (action.dir.isRow()) score += 0.1; // row is better than column ...
		
		for (int i = 0;i<49;i++) {
			if (bfs.gScore[i] == Integer.MAX_VALUE) continue; // not reachable

			if (debug) System.err.println("     can reach "+Pos.from(i) + " item : "+Item.name(state.items[i]));
			
			score +=1; // reachable cells is a good thing
			
			if (agent.needs(state.items[i]) ) {
				score+=100; // reachable cell items is a very good thing

				// TODO can we reach other items for extra points ?
			} else {
				if (debug) System.err.println("But I dont need this item");
			}
		}
		
		return score;
	}

}
