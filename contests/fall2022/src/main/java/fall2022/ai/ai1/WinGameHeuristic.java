package fall2022.ai.ai1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fall2022.Action;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;
import fall2022.sim.Sim;

public class WinGameHeuristic {

  //  Find if there is conditions when we win the game !
	public List<Action> think(State work, List<Ilot> ilots, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();
		
		
		List<Action> moreIlotsThanHim = moreIlotsThanHim(work, ilots);
		actions.addAll(moreIlotsThanHim);
		
		
		List<Action> frontierOfRecyclers = frontierOfRecyclers(work, ilots, tt);
		actions.addAll(frontierOfRecyclers);
		
		
		for (Ilot ilot : ilots) {
			if (ilot.ruler == O.ME && ilot.isFullCoverByMe()) {
				// do nothing!
			}
			if (ilot.ruler == O.OPP || ilot.ruler == O.NEUTRAL) {
				// nothing to do anymore :(
			} else {
				Territory territory = new Territory();
				territory.calculateTerritories(ilot, tt);
				
				List<Action> winByRecyclers = winByRecyclers(work, territory, ilots, tt);
				if (!winByRecyclers.isEmpty()) {
					actions.addAll(winByRecyclers);
				}
			}
		}
		
		// apply all actions to state
		for (Action a : actions) {
			work.apply(a);
		}
		
		
		return actions;
	}

	// if we can build recycler on the frontier and i win the game, do it ! 
	private List<Action> frontierOfRecyclers(State work, List<Ilot> ilots, TimeTraveler tt) {
		
		boolean canFrontierize = true;
		Set<Pos> toBuild = new HashSet<>();
		for (Ilot ilot : ilots) {
			if (!ilot.isDisputed()) continue;
			
			Territory t = new Territory();
			t.calculateTerritories(ilot, tt);
			
			int count = 0;
			for (Pos f : t.frontier) {
				if (work.canBuild(f)) {
					toBuild.add(f);
					count++;
				}
			}
			
			if (count != t.frontier.size()) {
				canFrontierize = false;
			}
		}
		
		if (canFrontierize && toBuild.size() * O.COST <= work.myMatter) {
			State temp = new State();
			temp.copyFrom(work);

			for (Pos b : toBuild) {
				temp.rec[b.o] = 1;
			}
			
			Sim.tenTurn(temp);
			
			List<Ilot> newIlots = Ilot.build(temp);
			if (Ilot.mySureCells(newIlots) > Ilot.oppPotentialCells(newIlots)) {
				List<Action> actions = new ArrayList<>();
				for (Pos b : toBuild) {
					Action build = Action.build(b, this.getClass().getSimpleName());
					work.apply(build);
					actions.add(build);
				}
				return actions;
			}
		}
		return Collections.emptyList();
	}

	// Force Spawn an unit
	private List<Action> moreIlotsThanHim(State work, List<Ilot> ilots) {
		if (work.myMatter < O.COST) return Collections.emptyList();
		
		int mySureCells = 0;
		int oppPotentialCells = 0;
		
		for (Ilot ilot : ilots) {
			if (ilot.ruler == O.ME) mySureCells += ilot.size();
			if (ilot.ruler == O.OPP || ilot.ruler == Ilot.DISPUTED) oppPotentialCells += (ilot.size()-ilot.nbRecyclers); 
		}
		
		if (mySureCells > oppPotentialCells) {
			// force spawn of an unit on my empyty ilots
			for (Ilot ilot : ilots) {
				if (ilot.ruler == O.ME && ilot.myTroupsCount == 0 && !ilot.isFullCoverByMe)  {
					for (int i = 0;i<ilot.pFE;i++) {
						Pos p = ilot.p[i];
						if (work.canSpawn(p)) {
							Action spawn = Action.spawn(1, p, "enough Cells when building @"+p+" => "+mySureCells+" vs "+oppPotentialCells); // spawn everything to prevents mistakes with recyclers :)
							work.apply(spawn);
							return Arrays.asList(spawn);
						}
					}
				}
			}
		}
		
		return Collections.emptyList();
	}

	private List<Action> winByRecyclers(State original, Territory territoryO, List<Ilot> originalIlots, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();
		
		if (original.myMatter < O.COST) return Collections.emptyList();
		
		Set<Pos> visited = new HashSet<>();
		for (Pos f : territoryO.frontier) {
			for (Pos p : f.meAndNeighbors4dirs) {
				if (visited.contains(p)) continue;
				visited.add(p);
				
				if (!original.canBuild(p)) continue;
				
				State state = new State();
				state.copyFrom(original);
				
				Action build = Action.build(p, this.getClass().getSimpleName());
				state.apply(build);
				Sim.oneTurn(state); // hope it won't change after one turn
				
				List<Ilot> ilots = Ilot.build(state);
				if (ilots.size() == originalIlots.size()) continue;

				// count cells by player, removing the dead ones
				Set<Pos> deadPositions = getDeadCellsInTimeFrom(state);
				
				
				int mines = 0;
				int opps = 0;
				for (Ilot ilot : ilots) {
					if (ilot.ruler == O.ME) mines += countAliveCells(ilot, deadPositions);
					if (ilot.ruler == O.OPP || ilot.isDisputed()) opps += countAliveCells(ilot, deadPositions);
				}
				
				
				
				// compter combien je kill de cells
				int deletedCells = 0;
				for (Pos n : p.neighbors4dirs) {
					if (original.o[n.o] == O.ME && original.s[n.o] <= original.s[p.o]) deletedCells++;
				}
				
				if (mines - deletedCells > opps /* nombre de case que je pourrais perdre :/ */) {
					System.err.println("Winning !");
					actions.add(build);
					return actions;
				}
			}
		}
		return  actions;
	}

	private int countAliveCells(Ilot ilot, Set<Pos> deadPositions) {
		int count = 0;
		for (int i=0;i<ilot.pFE;i++) {
			if (deadPositions.contains(ilot.p[i])) continue;
			count++;
		}
		
		return count;
	}

	private Set<Pos> getDeadCellsInTimeFrom(State state) {
		Set<Pos> deadCells = new HashSet<>();
		for (Pos pos : Pos.allMapPositions) {
			if (state.rec[pos.o] != 0) {
				deadCells.add(pos);
				for (Pos n : pos.neighbors4dirs) {
					if( state.s[n.o] <= 0) continue;
					if (state.s[n.o] <= state.s[pos.o]) {
						deadCells.add(n);
					}
				}
			}
		}
		
		return deadCells;
	}
}
