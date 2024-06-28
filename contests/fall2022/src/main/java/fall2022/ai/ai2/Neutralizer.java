package fall2022.ai.ai2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fall2022.Action;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.TimeTraveler;

 // Spawn a unit to prepare battle on cells
// Quand on anticipe un match nul (autour d'une neutre par exemple)
// et qu'on a encore de la matter, spawn tout de suite une nouvelle unité
public class Neutralizer {
	
	public Collection<? extends Action> think(State work, TimeTraveler tt, List<Action> doneActions) {
		
		List<Action> actions = new ArrayList<>();
		
		// pour toutes mes actions, chercher des move sur des cases neutres ou opp qui ont un opp autour
		// spawn une unité sur la case origine si c'est possible
		Set<Pos> neutralized = new HashSet<>();
		
		for (Action a : doneActions) {
			if (work.myMatter < O.COST) break;
			
			Pos target = a.to();
			if (neutralized.contains(target)) continue;
			if (work.isMine(target)) continue;
			
			int redAround = work.countRedAround(target);
			if (redAround == 0) continue; // no way he can save it

			// don't neutralize multiple times
			neutralized.add(target);
			
			// spawn one unit to prepare
			Action spawn = Action.spawn(1, a.from(), "Neutralizer - prepare");
			actions.add(spawn);
			work.apply(spawn);
		}
		
		return actions;
	}
}
