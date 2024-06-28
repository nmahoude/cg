package fall2022.ai.poubelle;

import java.util.ArrayList;
import java.util.List;

import fall2022.Action;
import fall2022.DistanceMap;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;

//
// Si il y a des cases disputées dans mon camp, calculer les distances me versus opp et spawn pour proteger ses zones
// @author nmahoude
//
//
public class DisputedProtector {
	DistanceMap mine = new DistanceMap(O.ME);
	DistanceMap opp = new DistanceMap(O.OPP);
	
	public List<Action> think(State work, Ilot ilot) {
		List<Action> actions = new ArrayList<>();
		TimeTraveler tt = new TimeTraveler();
		tt.init(work);
		Territory t = new Territory();
		t.calculateTerritories(ilot, tt);
		
		mine.calculate(work);
		opp.calculate(work);
		
		for (Pos f : t.frontier) {
			for (Pos n : f.neighbors4dirs) {
				if (work.isNeutral(n) && t.disputed.contains(n) && !t.frontier.contains(n)) {

					// oups, check if we can still reach it before opp
					if (mine.grid[n.o] >= opp.grid[n.o]) {
						System.err.println("Need to protect "+n);
						for (Pos target : n.neighbors4dirs) {
							if (work.isMine(target)) {
								Action spawn = Action.spawn(1, target, n, "Protect disputed");
								actions.add(spawn);
								work.apply(spawn);
							}
						}
					}
				}
			}
		}
		return actions;
	}
	
}
