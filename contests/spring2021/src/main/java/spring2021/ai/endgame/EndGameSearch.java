package spring2021.ai.endgame;

import java.util.ArrayList;
import java.util.List;

import spring2021.Action;
import spring2021.Simulator;
import spring2021.State;

public class EndGameSearch {
	static final int MAX_NODES = 300_000;
	static EGNode nodes[] = new EGNode[MAX_NODES];
	public static int currentNodesFE = 0;
	static {
		for (int i=0;i<MAX_NODES;i++) {
			nodes[i] = new EGNode();
		}
	}
	
	
	EGNode bestChild;
	int bestScore;
	
	static int indexesOfMyTrees[] = new int[37];
	static int indexesOfMyTreesFE = 0;
	
	private void initIndexesOfMyTrees(State state) {
		indexesOfMyTreesFE = 0;
		for (int i=0;i<37;i++) {
			int size = state.trees[i];
			if (size == -1) continue; // nothing here
			if (!state.isMine(i)) continue;
			indexesOfMyTrees[indexesOfMyTreesFE++] = i; // dormant or not
		}
	}
	
	public Action think(State model) {
		currentNodesFE = 0;
		
		initIndexesOfMyTrees(model);
		System.err.println("Trees to play : "+indexesOfMyTreesFE);
		bestScore = Integer.MIN_VALUE;
		
		EGNode root = popEGNode();
		root.state.copyFrom(model);
		root.action = null;
		
		List<EGNode> toVisit = new ArrayList<>();
		toVisit.add(root);
		
		while (!toVisit.isEmpty()) {
			EGNode current = toVisit.remove(0);

			List<Action> possibleMoves = current.calculatePossibleMoves();
			for (Action action : possibleMoves) {
				EGNode child = popEGNode();
				child.state.copyFrom(current.state);
				child.action = action;
				child.parent = current;
				Simulator.simulate(child.state, action, 0);
				if (child.state.day == 24) {
					if (child.state.score[0] > bestScore) {
						bestChild = child;
						bestScore = child.state.score[0];
					}
				} else {
					toVisit.add(child);
				}
			}
		}		
		
		// reconstruct from best(end)Child
		EGNode current = bestChild;
		while (current.parent != root) {
			current = current.parent;
		}
		return current.action;
	}

	private EGNode popEGNode() {
		return nodes[currentNodesFE++];
	}

	
}
