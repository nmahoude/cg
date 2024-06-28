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
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;

public class Finisher {

	public static Pos getCoveringUnit(State state, Ilot ilot, Territory territory) {
		// return the pos of a covering unit (used when stall)
		
		Set<Pos> augmentedFrontier = new HashSet<>();
		for (Pos f : territory.frontier) {
			for (Pos n : f.meAndNeighbors4dirs) {
				augmentedFrontier.add(n);
			}
		}
		
		
		for (int i=0;i<ilot.pFE;i++) {
			Pos current = ilot.p[i];
			if (augmentedFrontier.contains(current)) continue;
			
			if (state.isMine(current) && state.u[current.o] > 0) {
				return current;
			}
		}
		
		return null;
	}
	
	
	public static Collection<? extends Action> cover(State state, List<Ilot> ilots, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();
		if (State.isStall()) {
			System.err.println("Moving to cover .... ");
			
			for (Ilot ilot : ilots) {
				if (ilot.ruler != Ilot.DISPUTED) continue;
				Territory t = new Territory();
				t.calculateTerritories(ilot, tt);
				
				Pos coverRobot = getCoveringUnit(state, ilot, t);
				if (coverRobot != null) {
					System.err.println("Cover robot is "+coverRobot);
					return Spreader.spreadQuickly(state, ilot, tt, coverRobot, false);
				} else {
					System.err.println("Can't find spare robot, spawning one");
				}
				
				
				Pos bestForNeutral = null;
				for (Pos p : t.blueTerritory) {
					if (state.isNeutral(p)) {
						
						for (Pos n : p.neighbors4dirs) {
							if (state.isMine(n) && state.canMove(n)) {
								bestForNeutral = n;
								break;
							}
						}
						if( bestForNeutral != null) {
							break;
						}
					}
				}
				// ici on a une cell bleu pour spawner

				if(bestForNeutral != null) {
					Action spawn = Action.spawn(1, bestForNeutral, "Finisher - spawn to cover");
					state.apply(spawn);
					actions.add(spawn);
					return actions;
				}
			}
		}
		return actions;
	}
	
	
	public static Collection<? extends Action> think(State state, List<Ilot> ilots, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();
		
		boolean allIlotsDecided = true;
		for (Ilot ilot : ilots) {
			if (ilot.isDisputed()) allIlotsDecided = false;
		}		
		boolean filledIn = State.isStall() || allIlotsDecided;
		
		// check if we need to spawn one unit to finish cover of ilot !
		for (Ilot ilot : ilots) {
			if (ilot.ruler == O.ME) {
				if (ilot.isFullCoverByMe()) {
					// do nothing!
				} else {
					Territory t = new Territory();
					t.calculateTerritories(ilot, tt);
					if (t.blueRobots.isEmpty()) {
						if (state.myMatter >= O.COST && filledIn) {
							for (Pos p : t.blueTerritory) {
								if (state.canSpawn(p)) {
									Action spawn = Action.spawn(1, p, "SPAWN TO SPREAD");
									state.apply(spawn);
									actions.add(spawn);
									break;
								}
							}
						}
					}
				}
			} else if (ilot.ruler == O.OPP || ilot.ruler == O.NEUTRAL) {
				// nothing to do
			} else {
				actions.addAll(Spreader.spreadQuickly(state, ilot, tt));
			}
		}

		
		return actions;
	}

}
