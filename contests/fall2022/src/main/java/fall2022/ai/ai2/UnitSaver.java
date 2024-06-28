package fall2022.ai.ai2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fall2022.Action;
import fall2022.Logger;
import fall2022.Pos;
import fall2022.State;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;

// remove the units from forbiden cells
public class UnitSaver {

	public List<Action> think(State work, Ilot ilot, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();
		
		Set<Pos> forbidenCells = tt.forbidenCells();
		
		for (int i=0;i<ilot.pFE;i++) {
			Pos current = ilot.p[i];
			if (work.movableUnits(current) <= 0 || !work.isMine(current)) continue;
			
			if (!forbidenCells.contains(current)) continue;
			
			double bestScore = Double.NEGATIVE_INFINITY;
			Pos bestPos = null;
			for (Pos n : current.neighbors4dirs) {
				if (forbidenCells.contains(n)) continue;
				if (!work.canMove(n)) continue;
				
				double score;
				if (work.isMine(n)) {
					score = 0.0;
				} else if (work.isNeutral(n)) {
					score = 100.0;
				} else {
					score = 100 - work.u[n.o];
				}
				
				if (score > bestScore) {
					bestScore = score;
					bestPos = n;
				}
			}
			
			if (bestPos != null) {
				Logger.warning("La logique pour sauver les unités est un peu basique ... mais on sauve "+current);
				Action move = Action.move(work.movableUnits(current), current, bestPos, "Save units from death");
				actions.add(move);
				work.apply(move);
			}
		}
		
		return actions;
	}
}
