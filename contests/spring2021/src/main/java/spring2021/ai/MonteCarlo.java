package spring2021.ai;

import java.util.Random;

import spring2021.Action;
import spring2021.Cell;
import spring2021.Player;
import spring2021.Simulator;
import spring2021.State;

public class MonteCarlo {
	private static Random random = new Random(0);
	
	public static final int MAX_DEPTH = 450;
	
	public Action[] bestActions = new Action[MAX_DEPTH];
	public int bestActionsFE = 0;
	
	
	Action[] actions = new Action[MAX_DEPTH];
	Action[] possibleActions = new Action[1000];
	int possibleActionsFE = 1;
	{
		possibleActions[0] = Action.WAIT;
	}

	// Les arbres qui peuvent agir
	private int[] initActionableTrees = new int[37];
	private int initActionablesTreesFE, initTotalTreesFE;
	
	// Les arbres qui peuvent agir sur un day donné
	int[] currentActionableTrees = new int[37];
	int currentActionablesFE;
	int currentTotalFE;	
	
	private State state = new State();
	private int actionsFE;

	public Action think(State model) {
		initActionableTrees(model);
		
		double bestScore = Double.NEGATIVE_INFINITY;
		int iter = 0;
		while(true) {
			iter++;
			if ((iter & (1024-1)) == 0) {
				if (System.currentTimeMillis()-Player.start > Player.MAX_TIME) {
					System.err.println("iterations : "+iter);
					break;
				}
			}
//			if (iter == 2000) break; // fix iterations

			actionsFE = 0;
			this.state.copyFrom(model);
			double score = 0.0;
			int waitCount = 0;

			restoreInitialTreesActions();
			
			while(true) {
				
				Action chosen = choseRandomAction();
				
				actions[actionsFE++] = chosen;
				
				if (chosen == Action.WAIT) {
					Simulator.endTurn(state);
					waitCount++;
					score = 1.05*score + Player.evaluator.evaluate(state);
					if (waitCount == Player.MAX_WAIT || state.day == 24) {
						break;
					}
					currentActionablesFE = currentTotalFE; // reset all dormant trees
				} else {
					Simulator.simulate(state, chosen, 0);
					swapActionableTreesFromAction(chosen);
				}
			}
			
			if (score > bestScore) {
				bestScore = score;
				copyBestActions();
			}
		}
		return bestActions[0];
	}

	private void copyBestActions() {
		bestActionsFE = actionsFE;
		for (int d=0;d<actionsFE;d++) {
			bestActions[d] = actions[d];
		}
	}

	private Action choseRandomAction() {
		Action chosen;
		if (currentActionablesFE == 0) {
			chosen = Action.WAIT;
		} else {
			if (state.day > 19 
				|| random.nextInt(100) > Player.PERCENT_ACTIONS_WITH_SEEDS 
				) {
				calculatePossibleActions(state, false); // No seeds
			} else {
				calculatePossibleActions(state, true);
			}
			chosen = possibleActions[random.nextInt(possibleActionsFE)];
		}
		return chosen;
	}

	private void restoreInitialTreesActions() {
		currentActionablesFE = initActionablesTreesFE;
		currentTotalFE = initTotalTreesFE;
		//System.arraycopy(initActionableTrees, 0, currentActionableTrees, 0, initTotalTreesFE);
		for (int c=0;c<initTotalTreesFE;c++) {
			currentActionableTrees[c] = initActionableTrees[c];
		}
	}

	protected void swapActionableTreesFromAction(Action chosen) {
		int offset = -1;
		// Find back where the tree is in the list
		// TODO optimize with a reverse index ?
		for (int i=0;i<currentActionablesFE;i++) {
			if (currentActionableTrees[i] == chosen.index0) {
				offset = i;
				break;
			}
		}
		
		if (chosen.type == Action.SEED) {
			currentActionableTrees[currentTotalFE++] = chosen.index1; // can do something next turns
			if (currentActionablesFE > 0) {
				currentActionableTrees[offset] = currentActionableTrees[currentActionablesFE-1];
				currentActionableTrees[currentActionablesFE-1] = chosen.index0;
			}
			currentActionablesFE--;
		} else if (chosen.type == Action.GROW) {
			// swap a movable with this one that can't move further
			if (currentActionablesFE > 0) {
				currentActionableTrees[offset] = currentActionableTrees[currentActionablesFE-1];
				currentActionableTrees[currentActionablesFE-1] = chosen.index0;
			}
			currentActionablesFE--;
		} else if (chosen.type == Action.COMPLETE) {
			// remove from the list
			if (currentActionablesFE > 0) {
				currentActionableTrees[offset] = currentActionableTrees[currentActionablesFE-1];
				currentActionableTrees[currentActionablesFE-1] = currentActionableTrees[currentTotalFE-1];
			}
			currentActionablesFE--;
			currentTotalFE--;
		}
	}
	
	private void initActionableTrees(State theState) {
		initActionablesTreesFE = 0;
		initTotalTreesFE = 0;
		
		for (int i=0;i<37;i++) {
			int size = theState.trees[i];
			if (size == -1) continue; // nothing here
			if (!theState.isMine(i)) continue;
			if (theState.isDormant(i)) continue;
			
			// tree can move
			initActionableTrees[initActionablesTreesFE++] = i;
		}
		// put the dormants last
		initTotalTreesFE = initActionablesTreesFE;
		for (int i=0;i<37;i++) {
			int size = theState.trees[i];
			if (size == -1) continue; // nothing here
			if (!theState.isMine(i)) continue;
			if (!theState.isDormant(i)) continue;
			
			// tree can move
			initActionableTrees[initTotalTreesFE++] = i;
		}
	}

	private void calculatePossibleActions(State state, boolean withSeeds) {
		possibleActionsFE = 1;
		
		for (int j = 0; j < currentActionablesFE; j++) {
			int i = currentActionableTrees[j];
			
			int size = state.trees[i];
			
			// seeds
			if (withSeeds) addSeedsPossibilities(state, i, size);
			
			// grow
			if (size < 3) {
				if (state.costToGrow(0, size) <= state.sun[0]) {
					possibleActions[possibleActionsFE++] = Action.grow(i);
				}
			} else {
				// complete
				if (state.costToComplete(0) <= state.sun[0]) {
					possibleActions[possibleActionsFE++] = Action.complete(i);
				}
			}
		}
	}

	private void addSeedsPossibilities(State state, int treeIndex, int size) {
		if (state.costToSeed(0) <= state.sun[0]) {
			for (int s = 0; s < Cell.distanceIndexesFE[treeIndex][size]; s++) {
				int index = Cell.distanceIndexes[treeIndex][s];
				if (state.trees[index] >= 0)	continue; // already something
				if (State.forbidenSeedCells[index]) continue; // not allowd to seed here
				if (State.richness[index] == 0) continue; // TODO remove is we are sure of cells we try
				
				possibleActions[possibleActionsFE++] = Action.seed(treeIndex, index);
			}
		}
	}
}
