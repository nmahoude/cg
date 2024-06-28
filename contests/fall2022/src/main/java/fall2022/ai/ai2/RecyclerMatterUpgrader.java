package fall2022.ai.ai2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fall2022.Action;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;

public class RecyclerMatterUpgrader {

	public List<Action> think(State work, List<Ilot> originalIlots, TimeTraveler tt, List<Action> alreadyDoneActions) {
		
		List<Action> toBuild = new ArrayList<>();
		
		int myUnitCount = 0;
		int oppUnitCount = 0;
		boolean hasDisputed = false;
		for (Ilot ilot : originalIlots) {
			if (ilot.isDisputed()) {
				hasDisputed = true;
				myUnitCount += ilot.myTroupsCount;
				oppUnitCount += ilot.oppTroupsCount;
			}
		}
		int myTotalUnitInTime = myUnitCount + (work.myMatter + tt.myTotalMatterBonus) / O.COST;
		int oppTotalUnitInTime = oppUnitCount + (work.oppMatter + tt.oppTotalMatterBonus) / O.COST;
		
		if (oppTotalUnitInTime == 0) return Collections.emptyList();
		
		if ((hasDisputed 
				&& (myTotalUnitInTime < 0.95 * oppTotalUnitInTime)) // xx % 
				&& AI2.fullFrontier.size() > 1) {
			// hmm he will have more units than me !
			toBuild.addAll(buildSomeMatter(work, tt, alreadyDoneActions, oppTotalUnitInTime - myTotalUnitInTime));
			return toBuild;
		}

		return Collections.emptyList();
	}

	public double scores[] = new double[Pos.MAX_OFFSET];
	public List<Action> buildSomeMatter(State work, TimeTraveler tt, List<Action> alreadyDoneActions, int neededRobots) {
		
		List<Pos> posToCheck = new ArrayList<>();
		for (Pos p : Pos.allMapPositions) {
			if (work.o[p.o] == O.ME ) {
				if (work.ou[p.o] == 0 ) {
					posToCheck.add(p);
				}
			}
		}
		
		
		List<Action> toBuild = new ArrayList<>();
		
		double bestScore = Double.NEGATIVE_INFINITY;
		Pos bestPos = null;

		double bestScoreCanBuild = Double.NEGATIVE_INFINITY;
		Pos bestPosCanBuild = null;
		// TODO build more than one recycler ? 
		for(int i=0;i<1;i++) {
				if (neededRobots <= 0) break;
				if (work.myMatter < O.COST) break;
				
			for (Pos c : posToCheck) {
				scores[c.o] = 0;
				
				if (work.ou[c.o] != 0 || work.oo[c.o] != O.ME) continue;
				if (work.rec[c.o] != 0) continue;
				// don't check for actual units, we will take care of it later :)
				
				boolean shortenCircuitToFrontier = false;
				for (Pos n : c.neighbors4dirs) {
					
					if (AI2.fullFrontier.contains(n) && work.canMove(n) && !work.isMine(n)) {
						boolean canStillAcessAsQuickly = false;
						for (Pos nn : n.neighbors4dirs) {
							if ( nn == c) continue;
							if (!work.canMove(nn)) continue;
							
							if (AI2.myDistances.grid[nn.o] <= 1) {
								canStillAcessAsQuickly = true;
								break;
							}
						}
						if (!canStillAcessAsQuickly) {
							shortenCircuitToFrontier = true;
							break;
						}
					}
				}
				if (shortenCircuitToFrontier) {
					continue; // shortnening our path to some frontier cell
				}
				
				double score = 0.0;
				if (LowRecyclers.diffInPotentialCells[c.o] < 0) {
					score = Double.NEGATIVE_INFINITY;
				}
				
				int redCells = 0;
				for (Pos n : c.neighbors4dirs) {
					if (work.isOpp(n)) redCells++;
				}
				
				
				double ratio = 1.0 * LowRecyclers.ratio[c.o];
				if (ratio <= 0) {
					score = Double.NEGATIVE_INFINITY;
				}
				score += 1.0 * ratio; 
	
				score += 0.4 * redCells;
				
				int killReds = 0;
				int killBlues = 0;
				for (Pos n : c.neighbors4dirs) {
					if (work.s[n.o] == 1) {
						if (work.o[n.o] == O.OPP) killReds+=work.u[n.o];
						if (work.o[n.o] == O.ME) killBlues+=work.u[n.o];
					}
				}
				
				score -= 1.0  * Math.max(0, neededRobots -LowRecyclers.diffInRobots[c.o] );
				score += 1.0 * LowRecyclers.diffInRobots[c.o];
				score -= 1.1 * LowRecyclers.diffInCells[c.o];
				score += 0.1 * killReds;
				score -= 0.1 * killBlues;
				
				scores[c.o] = score;
	
				if (work.canBuild(c)) {
					if (score > bestScoreCanBuild) {
						bestScoreCanBuild = score;
						bestPosCanBuild = c;
					}
				}
				
				if (score > bestScore) {
					bestScore = score;
					bestPos = c;
				}
			}
			if (bestPos != null || bestPosCanBuild != null) {
				if (bestPos != null && work.u[bestPos.o] != 0 && work.ou[bestPos.o] == 0) {
					// remove actions leading to bestPos !
					List<Action> toRemove = new ArrayList<>();
					for (Action a : alreadyDoneActions) {
						if (a.to() == bestPos) toRemove.add(a);
					}
					
					for (Action a : toRemove) {
						if (a.type() == Action.SPAWN) work.myMatter+= a.amount() * O.COST;
						
						System.err.println("Removing "+a+" to put a recycler !");
						alreadyDoneActions.remove(a);
					}
					work.u[bestPos.o] = 0;
					work.mu[bestPos.o] = 0;
				} else {
					bestPos = bestPosCanBuild;
				}
				
				if (work.myMatter >= O.COST) {
					Action action = Action.build(bestPos, this.getClass().getSimpleName());
					toBuild.add(action);
					work.apply(action);
					neededRobots -= LowRecyclers.diffInRobots[bestPos.o];
				}
			} else {
				break;
			}
		}
		return toBuild;
	}
}
