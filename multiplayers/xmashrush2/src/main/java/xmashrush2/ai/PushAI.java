package xmashrush2.ai;

import java.util.List;

import xmashrush2.Agent;
import xmashrush2.BFS;
import xmashrush2.Direction;
import xmashrush2.Item;
import xmashrush2.Player;
import xmashrush2.Pos;
import xmashrush2.PushAction;
import xmashrush2.State;

public class PushAI {

	private static final int MAX_MOVES = 20;
	private State tmpState = new State();
	public PushAction action;
	private BFS bfs = new BFS();
	
	public void output() {
		// System.out.println("PUSH 3 RIGHT"); // PUSH <id> <direction> | MOVE <direction> | PASS
		System.out.println(action.toString());
	}

	public void think(State currentState) {
		boolean imWinning = (currentState.agents[0].score >= currentState.agents[1].score); 
		
		
		
		double maxScore = Double.NEGATIVE_INFINITY;
		PushAction best = null;
		boolean pushWithSolution = false;
		
		for (PushAction action0 : PushAction.actions) {
			if (action0 == Player.forbiddenAction) continue; 
			
			
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
				
				if (action1 == PushAction.actions(1, Direction.RIGHT)) {
					System.err.println("*************************************");
					System.err.println(" me:"+action0+" opp:"+action1+"  => "+myScore+" "+hisScore);
					System.err.println("his score: "+tmpState.agents[1].score);
					tmpState.debugGrid();
					System.err.println("*************************************");
				}
				double score = (imWinning ? 1.00 : 1.00) *(myScore - hisScore);
				if (score < minScore) {
					minScore = score;
					minBest = action1;
				}
				totalMinScore += score;
			}
			
			tmpState.copyFrom(currentState);
			tmpState.apply(action0, null);
			
			double myScore = evalScore(tmpState, tmpState.agents[0], action0, false && action0 == PushAction.actions(3, Direction.UP));
			//System.err.println("My action : "+action0+" -> avgMinScore is "+avgMinScore+ " his best actions would be "+minBest);
			//System.err.println("My score alone would be "+myScore);
			
			if (tmpState.reachableItems(MAX_MOVES) != 0) {
				pushWithSolution = true;
			}
			
			double avgMinScore = totalMinScore / counterActionsCount;
			if (minScore > maxScore) {
				maxScore = minScore;
				best = action0;
			}
		}
		
		this.action = best;
		
//		if (pushWithSolution) {
//			System.err.println("Setting the push action when there is a solution du catch an item...");
//			this.action = best;
//		} else {
//			System.err.println("Can't find a direct solution with one push, trying a second push");
//			PushTreeAI multipush = new PushTreeAI(MAX_MOVES);
//			List<PushTreeNode> solutions = multipush.findSolution(currentState);
//			if (solutions.isEmpty()) {
//				System.err.println("No solution with multi push, rollback to best solution");
//				this.action = best;
//			} else {
//				// TODO DO NOT take random solution ...
//				PushTreeNode solution = null;
//				for (PushTreeNode s : solutions) {
//					if (s.reachableQuestItems.size() > 0 && s.getInitialPushAction() != Player.forbiddenAction) {
//						solution = s;
//						break;
//					}
//				}
//				if (solution != null) {
//					System.err.println("Found some solutions with multi push, random picking ...");
//					// no move, so do the striaght parent push action
//					System.err.println("Actions : "+solution.parent.actionFromParent + " -> "+solution.actionFromParent );
//					solution.state.debugGrid();
//					this.action = solution.getInitialPushAction();
//				} else {
//					System.err.println("Can't find solution in multiple pushes, rollback to best");
//					this.action = best;
//				}
//			}
//			
//		}
		
	}

	
	private double evalScore(State state, Agent agent, PushAction action, boolean debug) {
		bfs.process(state, agent.pos, MAX_MOVES);
		
		double score = 200 * agent.score; // sometimes a push is enough to get an object
		
		// won
		if (agent.score == 12) {
			score += 100_000;
		}
		
		if (agent.needs(agent.item)) {
			score += 10; // getting our own item in out hands is good
		}
		
		if (action.dir.isRow()) score += 0.1; // row is better than column ...
		
		for (int i = 0;i<49;i++) {
			if (bfs.gScore[i] == Integer.MAX_VALUE) continue; // not reachable

			if (debug) System.err.println("     can reach "+Pos.from(i) + " item : "+Item.name(state.items[i]));
			
			score +=1.5; // reachable cells is a good thing
			
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
