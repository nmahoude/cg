package fall2022.ai.ai2;

import java.util.ArrayList;
import java.util.List;

import fall2022.Action;
import fall2022.DiffusionMap;
import fall2022.Logger;
import fall2022.O;
import fall2022.Player;
import fall2022.Pos;
import fall2022.State;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;
import fall2022.ai.Search;
import fall2022.sim.Sim;

// defend frontier with the less move & spawns
public class Defense {
	private static int calculateDeficitInUnit(List<Ilot> ilots) {
		int deficit = 0;
		for (Ilot ilot : ilots) {
			// ilots can't transfert units, so if i'm up, don't take it into account
			if (ilot.isDisputed()) {
				
				deficit += Math.max(0, ilot.oppTroupsCount-ilot.myTroupsCount);
				
				//deficit += ilot.oppTroupsCount-ilot.myTroupsCount;
			}
		}
		return deficit;
	}

	public static int unitsNeededInTime(State work, List<Ilot> ilots) {
		int deficitInUnit = calculateDeficitInUnit(ilots);

		int deficitInUnitMatter = deficitInUnitInTime(work);
		int deficitInTime = deficitInUnit - deficitInUnitMatter;
		
		return deficitInTime;
	}

	public static int deficitInUnitInTime(State work) {
		State lastOriginal = new State();
		lastOriginal.copyFrom(work);
		Sim.tenTurn(lastOriginal);

		int deficitInUnitMatter = lastOriginal.myMatter / O.COST - lastOriginal.oppMatter / O.COST;
		return deficitInUnitMatter;
	}

	public List<Action> think(State work, TimeTraveler tt, List<Ilot> ilots, Ilot ilot, DiffusionMap myMap, DiffusionMap oppMap) {

		Territory territory = new Territory();
		territory.calculateTerritories(ilot, tt);
		
		List<Action> actions = new ArrayList<>();
		

		Search search = new Search();
		setGoalsNeeded(work, tt, territory, search);
		search.search(work, tt, ilots, territory.frontier, territory.blueDangers);
		
		if (search.FOUND > 0) {
			Player.frontierIsProtected++;
			actions.addAll(search.actions);
			for (Action a :actions) {
				work.apply(a);
			}

			actions.addAll(protection(work, territory.frontier, search));
			
			
			return actions;
		} else {
			Logger.error("Found not defense and I don't know what to do ...");
			Player.frontierIsProtected = 0;
			actions.addAll(protection(work, territory.frontier, search));
		}
		
		// Oops found nothing ...
		return actions;
	}

	private List<Action> protection(State work, List<Pos> frontier, Search search) {
		List<Action> actions = new ArrayList<>();
		
		for (Pos f : frontier) {
			if (work.units(f) < search.goalNeeded[f.o]) {
				// we can't protect the cell, check if we can retreat to a neutral one to gain it
				for (Pos n : f.neighbors4dirs) {
					if (work.isNeutral(n) && work.countRedAround(n) == 0 && work.movableUnits(f) > 0) {
						Action retreat = Action.move(1, f, n, "Retreat");
						work.apply(retreat);
						actions.add(retreat);
					}
				}
			}
		}

		return actions;
	}

	public static void setGoalsNeeded(State work, TimeTraveler tt, Territory territory, Search search) {
		for (Pos cell : territory.frontier) {
			search.goalNeeded[cell.o] = 0;
			
			if (!tt.sliceAt(1).state.canMove(cell))	continue;
			
			if (work.isOpp(cell)) {
				if (work.units(cell) == 0) {
					search.goalNeeded[cell.o] = 0;
				}
			} else {
				//int maxOppUnits = work.countRedAround(cell);

				boolean hasBlueAround = false;
				for (Pos n : cell.neighbors4dirs) {
					if (work.isMine(n)) {
						hasBlueAround = true;
						break;
					}
				}
				
				int maxOppUnits = neededToDefend(work, cell);
				
				search.goalNeeded[cell.o] = maxOppUnits;

				if (maxOppUnits == 0 && work.isNeutral(cell) && hasBlueAround) {
					search.goalNeeded[cell.o] = 1;
				}
			}
		}
	}
	
	// how many units needed to defend this cell 
	public static int neededToDefend(State state, Pos cell) {
		
		int maxOppUnits = 0;
		int mineOrNeutralAround = 0;
		for (Pos n : cell.neighbors4dirs) {
			if ((state.isMine(n) || state.isNeutral(n)) && state.units(n) == 0) mineOrNeutralAround ++;
			if (!state.isOpp(n)) continue;
			
			int locked = 0;
			for (Pos nn : n.neighbors4dirs) {
				
				if (nn == cell) continue;
				if (state.o[nn.o] == O.ME && state.ou[nn.o] > 0) {
					locked += Math.max(0, (state.ou[nn.o]-state.lm[nn.o]));
				}
			}
			maxOppUnits += Math.max(0, state.units(n) - locked);
		}
		
		if (mineOrNeutralAround > 1) {
			maxOppUnits = state.countRedAround(cell);
		}
		
		
		return maxOppUnits;
	}
	
}
