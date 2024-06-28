package fall2022.ai.poubelle;

import java.util.ArrayList;
import java.util.List;

import fall2022.Action;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;

public class FrontierSpread {

	//	move units on new frontier 
	public List<Action> think(State work, Ilot ilot, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();

		Territory territory = new Territory();
		territory.calculateTerritories(ilot, tt);
		
		for (Pos f : territory.frontier) {
			
			if (!work.isNeutral(f)) continue; // TODO more than NEUTRAL ?
			
			for (Pos n : f.neighbors4dirs) {
				if (work.isMine(n) && work.movableUnits(n) > 0) {
					int locked = work.lm[n.o];
					int movables = work.movableUnits(n);
					if (locked < movables) {
						// send units
						Action move = Action.move(movables - locked, n, f, "Frontier spread, locked is ok");
						work.apply(move);
						actions.add(move);
						break;
					} else if (work.myMatter >= O.COST) {
						Action move = Action.move(1, n, f, "Frontier spread, locked is ko, will replace");
						work.apply(move);
						actions.add(move);
						
						Action spawn = Action.spawn(1, n, "Frontier spread - spawn to compensate");
						work.apply(spawn);
						actions.add(spawn);
						
						
					}
				}
			}
		}
		
		
		
		
		return actions;
	}
}
