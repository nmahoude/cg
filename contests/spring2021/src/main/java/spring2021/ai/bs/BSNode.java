package spring2021.ai.bs;

import spring2021.Action;
import spring2021.Cell;
import spring2021.Player;
import spring2021.Simulator;
import spring2021.State;

public class BSNode{
	public BSNode parent;
	int turn;
	public Action fromAction;
	public State state = new State();
	double score;
	double lastWaitScore;
	
	@Override
	public String toString() {
		String actions = "";
		BSNode current = this;
		while(current.fromAction != null) {
			actions = current.fromAction+";"+actions;
			current = current.parent;
		}
		actions = "(R)->"+actions;
		
		return "T"+turn+" "+actions;
	}
	
	public int expand(int oldTail) {
		/**
		 * expand from this node by adding child nodes into the nodes, return the newTail from oldTail
		 * 
		 * for each node, the new state must be computed and the fitness calculated
		 * 
		 * /!\ update the best Fitnesse from here  
		 */
		
		int index = oldTail;
		
		BSNode doWait = BeamSearch.cacheNodes[BeamSearch.currentCacheIndex++];
		createChild(doWait, Action.WAIT);
		BeamSearch.waitingNodes[BeamSearch.waitingNodesFE++] = doWait; // add to the waiting line
		if (doWait.score > BeamSearch.bestFitnesse) {
			BeamSearch.bestFitnesse = doWait.score;
			BeamSearch.bestNode = doWait;
		}
		
		for (int i=0;i<37;i++) { // TODO use array of movable trees precalculated in BeamSearch ?
			int size = state.trees[i];
			if (size == -1) continue; // nothing here
			if (!state.isMine(i)) continue;
			if (state.isDormant(i)) continue;
			
			if (size == 3) {
				if (state.day > 9 && !state.hasGrow2to3 && state.costToComplete(i) <= state.sun[0]) {
					BSNode next = BeamSearch.cacheNodes[BeamSearch.currentCacheIndex++];
					BeamSearch.nodes[index++] = next;
					createChild(next, Action.complete(i));
				}
			} else {
				boolean canDoIt = true;
				if (size == 1 && state.hasGrow0to1) canDoIt = false; // si j'ai déjà fait pousser un 0 en 1, je chercher plus à faire pousser des 1 (car l'action d'avant etait plus chere)
				if (size == 2 && state.hasGrow1to2) canDoIt = false; // idem pour 1 vers 2 et pousser un 2 en 3
				
				if (canDoIt && state.costToGrow(0, size) <= state.sun[0]) {
					BSNode next = BeamSearch.cacheNodes[BeamSearch.currentCacheIndex++];
					BeamSearch.nodes[index++] = next;
					createChild(next, Action.grow(i));
				}
			}
			if (size > 0) {
				// seeds
				if (state.costToSeed(0) == 0) {
					for (int s = 0; s < Cell.distanceIndexesFE[i][size]; s++) {
						int index2 = Cell.distanceIndexes[i][s];
						if (state.trees[index2] >= 0)	continue; // already something
						if (State.forbidenSeedCells[index2]) continue; // not allowd to seed here
						if (State.richness[index2] == 0) continue; // TODO remove is we are sure of cells we try

						BSNode next = BeamSearch.cacheNodes[BeamSearch.currentCacheIndex++];
						BeamSearch.nodes[index++] = next;
						createChild(next, Action.seed(i, index2));
					}
				}
			}
		}
		return index;
	}

	private void createChild(BSNode next, Action action) {
		next.parent = this;
		next.fromAction = action;
		next.state.copyFrom(state);
		Simulator.simulate(next.state, next.fromAction, 0);

		if (action == Action.WAIT) {
			next.score = this.lastWaitScore * 1.5 + Player.evaluator.evaluate(next.state);
			next.lastWaitScore = next.score;
		} else {
			next.lastWaitScore = this.lastWaitScore;
			if (this.fromAction != Action.WAIT) {
				next.score = this.score;	
			} else {
				next.score = 0;
			}
			next.score = 1.05 * next.score +  evaluate(next.state, action);

			// dummy ... will complete a tree after each move if it can
			if (next.state.day > 20 
					&& !next.state.oppIsWaiting
					&& next.state.treesCount[1*5+3] > 0 
					&& next.state.sun[1] > 4) {
				next.state.treesCount[1*5+3]--;
				next.state.score[1]+= next.state.nutrients;
				next.state.nutrients--;
				next.state.sun[1] -= 8; // costly not to overfit
			}
		}
	}
	

	public static double evaluate(State state, Action action) {
		return Player.evaluator.evaluate(state);
		
//		if (action.type == Action.GROW || action.type == Action.COMPLETE) {
//			return  5;
//		} else {
//			return State.richness[action.index1];
//		}
	}

}
