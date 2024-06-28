package spring2021;

public class Simulator {
	
	public static void simulate(State state, Action action, int index) {
		// TODO remove if other player need to move ...
		if (action == Action.WAIT) {
			endTurn(state);
			return;
		}
		
		
		// GROW trees
		if (action.type == Action.GROW) {
			int currentSize = state.trees[action.index0];

			if (currentSize == 3) {
				// a complete in fact
				state.sun[index]-=state.costToComplete(index);
				state.treesCount[5*index+3]--;
				state.trees[action.index0] = -1;
				state.score[index]+=state.nutrients + state.richnessBonus(action.index0);
				state.nutrients--;
				state.currentTotalRichness-=State.richness[action.index0];
				state.unsetMine(action.index0);
				
			} else {
				if (currentSize == 0) state.hasGrow0to1 = true;
				if (currentSize == 1) state.hasGrow1to2 = true;
				if (currentSize == 2) state.hasGrow2to3 = true;
				state.sun[index]-=state.costToGrow(index, currentSize); 
				state.treesCount[5*index+currentSize]--;
				state.treesCount[5*index+currentSize+1]++;
	
				state.trees[action.index0]++;
				state.setDormant(action.index0);
			}
		} else if (action.type == Action.COMPLETE) {
			// COMPLETE
			state.sun[index]-=state.costToComplete(index);
			state.treesCount[5*index+3]--;
			state.trees[action.index0] = -1;
			state.score[index]+=state.nutrients + state.richnessBonus(action.index0);
			state.nutrients--;
			state.currentTotalRichness-=State.richness[action.index0];
			state.unsetMine(action.index0);
		} else if (action.type == Action.SEED) {
			// SEED
			state.hasSeed = true;
			
			state.sun[index]-=state.costToSeed(index);
			state.treesCount[5*index+0]++;
			state.trees[action.index1] = 0;
			state.setDormant(action.index0);
			state.setDormant(action.index1);
			state.setMine(action.index1);
			state.currentTotalRichness += State.richness[action.index1];
			state.currentSeedDeltaRichness += State.richness[action.index1];
			
			state.newSeeds[state.newSeedsFE++] = action.index1;
		}
	}

	
	public static void endTurn(State state) {
		state.resetGrows();
		state.oppIsWaiting = false;
		
		state.day++;
		int invShadow = (state.day + 3) % 6;
		// move sun
		if (state.day == 24) {
			// add remaining sun points
			state.score[0] += state.sun[0] / 3;
			state.score[1] += state.sun[1] / 3;
		} else {
			// calculate sun points
			for (int i=0;i<37;i++) {
				int size = state.trees[i];
				if (size <= 0) continue;
				
				// check shadow
				boolean shadow = false;
				for (int s=0;s<3;s++) {
					int index = Cell.shadowIndexes[i][invShadow][s];
					int treeSize = state.trees[index];
					
					
					if (treeSize > s && treeSize >= size) {
						shadow = true;
						break;
					}
				}
				
				if (!shadow) {
					if (state.isMine(i)) {
						state.sun[0]+=size;
					} else {
						state.sun[1]+=size;
					}
				}
			}
			
			state.resetDormants();
		}
	}

	public static void fastRollout(State state) {
		// TODO don't do full circle multiple times, the results will be the same
		while(state.day == 24) {
			Simulator.endTurn(state);
		}
	}
}
