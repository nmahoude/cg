package fall2022.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fall2022.Logger;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.Territory;
import fall2022.search.goals.AtLeast;
import fall2022.search.goals.AtLeastAround;
import fall2022.search.goals.Empty;
import fall2022.search.goals.Recycler;

/**
 * 
 * least of goals per cell
 * 
 *
 */
public class Auction {

	Map<Pos, List<Goal>> goalsPerCell = new HashMap<>();
	
	public Auction() {
		for (Pos p : Pos.allMapPositions) {
			goalsPerCell.put(p, new ArrayList<>());
		}
	}
	
	public void think(State state, Territory territory) {
		for (Pos p : Pos.allMapPositions) {
				goalsPerCell.get(p).clear(); // clear list of goals
		}
		
		List<Goal> goals;
		for (Pos p : territory.frontier) {
			goals = goalsPerCell.get(p);
			
			int unitsOnCell = state.u[p.o];
			int ownerOfCell = state.o[p.o];
			
			int blueAround = countUnitsARound(O.ME, state, p);
			int redAround = countUnitsARound(O.OPP, state, p);
			
			if (ownerOfCell == O.NEUTRAL) {
				if (blueAround > 0 && redAround == 0) {
					// move at least one unit
					goals.add(new AtLeast(1, 100.0, 0.0));
				}
				if (blueAround == 0 && redAround == 0) {
					// get units on our neighbors !
					for (Pos n : p.neighbors4dirs) {
						if (state.canMove(n)) goalsPerCell.get(n).add(new AtLeast(1, 10.0, 0.0)); // small score, no danger
					}
				}
				
				if (blueAround == 0 && redAround > 0) {
					// je vais perdre la case ...
					for (Pos n : p.neighbors4dirs) {
						if (state.canMove(n) && state.o[n.o] == O.ME) {
							// si on peut, on pose déjà un recycler
							if (state.u[n.o] == 0) goalsPerCell.get(n).add(new Recycler(10_000.0, -100.0));
							
							// sinon il faut que la case soit vide
							goalsPerCell.get(n).add(new Empty(1000.0, -100.0));
							
							// sinon il faut au moins le nombre d'unité qui vont arriver
							// TODO prendre en compte le spawn!
							goalsPerCell.get(n).add(new AtLeast(redAround, 1.0, 0.0));
						}
					}
				}
				
				if (blueAround > 0 && redAround > 0) {
					if (blueAround > redAround + 1) {
						goals.add(new AtLeast(blueAround+1, 10_000, 0.0));
					}
					if (blueAround == redAround) {
						goals.add(new AtLeast(blueAround, 1_000, 0.0));
					}
					if (blueAround < redAround) {
						// vider notre case 
						goals.add(new Empty(100.0, -1000.0));
					}
				}
			}
			
			if (ownerOfCell == O.OPP) {
				goals.add(new AtLeast(unitsOnCell + redAround + state.oppMatter / O.COST, 1000.0, 0.0));
			}
			
			if (ownerOfCell == O.ME) {
				if (unitsOnCell == 0 && redAround == 0) {
					// get units around, pas besoin de plus qu'une, ca devrait le forcer à mettre un recycler 
					// TODO mais si il le fait pas ?
					goals.add(new AtLeast(1, 10.0, 0.0)); // small score, no danger
					

					// TODO autre stratégie : le laisser à zéro, mais construire autour pour attaquer quand il mettra des unités !
					goals.add(new AtLeastAround(1, 100.0, 0.0)); // TODO faudrait calculer combien il faut qu'on amene ici ...
				}
				
				if (unitsOnCell > 0 && redAround == 0) {
					Logger.error("Ce cas ne devrait pas arriver ! ");
				}
				if (unitsOnCell == 0 && redAround > 0) {
					goals.add(new Recycler(1_000, -1000.0));
					goals.add(new AtLeast(redAround, 10_000, -100));
				}

				if (unitsOnCell > 0 && redAround > 0) {
					// TODO ou alors attaqué la case la plus comprométante pour lui ? => mais dans ce cas, il faut préparer la défense de la case que je vais perdre
					
					if (unitsOnCell < redAround) {
						// il ne faut pas le perdre ! 
						// TODO ou alors fuire ?
						goals.add(new AtLeast(redAround, 10_000, -10_000));
					} 
					if (unitsOnCell >= redAround) {
						goals.add(new AtLeast(redAround, 1_000, -10_000));
					}
				}
			}
		}
		
		debug();
	}

	private void debug() {
		System.out.println("Debug of Auction & Goals");
		for (Pos p : Pos.allMapPositions) {
			if (goalsPerCell.get(p).isEmpty()) continue;
			System.out.println(""+p+" => ");
			for (Goal g : goalsPerCell.get(p)) {
				System.out.println("    "+g);
			}
			System.out.println();
		}
		
		
	}

	private int countUnitsARound(int owner, State state, Pos p) {
		int count = 0;
		for (Pos n : p.neighbors4dirs) {
			if (state.o[n.o] == owner) count += state.u[n.o];
		}
		return count;
	}
	
	
}
