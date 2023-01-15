package acoiaf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AI {
	private State work = new State();

	public List<Action> think(State stateReadOnly) {
		work.copyFrom(stateReadOnly);
		
		List<Action> actions = new ArrayList<>();
		
		List<Pos> myUnits = extractMyUnitPositions();
		
		
		actions.addAll(moveUnits(myUnits));
		actions.addAll(trainLevel1Units());
		
		return actions;
	}

	private List<Pos> extractMyUnitPositions() {
		List<Pos> myUnits = new ArrayList<>();
		for (Pos pos : Pos.allPositions) {
			if (work.owner[pos.offset] == O.ME && work.unitId[pos.offset] >= 0) {
				myUnits.add(pos);
			}
		}
		return myUnits;
	}

	private Collection<? extends Action> moveUnits(List<Pos> myUnits) {
		List<Action> actions = new ArrayList<>();

		for (Pos unit : myUnits) {
			int unitId = work.unitId[unit.offset];

			
			Pos bestMove = null;
			double bestScore = Double.NEGATIVE_INFINITY;
			
			for (Pos n : unit.neighbors4dirs) {
				if (work.unitId[n.offset] > 0) continue;
				double score = work.owner[n.offset] == O.NEUTRAL ? 1000.0 : 0.0;
				if (score > bestScore) {
					bestScore = score;
					bestMove = n;
				}
			}
			
			if (bestMove != null) {
				actions.add(Action.move(unitId, bestMove));
				work.unitId[unit.offset] = -1;
				work.unitId[bestMove.offset] = unitId;
			}
			
		}
		
		return actions;
	}

	private Collection<? extends Action> trainLevel1Units() {
		
		
		if (work.gold[O.ME] < 10) return Collections.emptyList();

		
		List<Action> actions = new ArrayList<>();
		Pos best= null;
		double bestScore = Double.NEGATIVE_INFINITY;
		
		for (Pos pos : Pos.allPositions) {
			if (work.owner[pos.offset] == O.ME) {
				for (Pos n : pos.neighbors4dirs) {
					if (work.unitId[n.offset] >= 0) continue; // something here
					double score = 0.0;
					
					if (work.owner[n.offset] == O.OPP) {
						score += 1000;
					} else if (work.owner[n.offset] == O.NEUTRAL) {
						score += 500;
					} else {
						score += 0.0;
					}
					
					if (score > bestScore) {
						bestScore = score;
						best = n;
					}
				}
			}
		}
		
		if (best != null) {
			actions.add(Action.train(1, best));
		}

		
		
		return actions;
	}
}
