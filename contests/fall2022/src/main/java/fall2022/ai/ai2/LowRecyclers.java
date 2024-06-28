package fall2022.ai.ai2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fall2022.Action;
import fall2022.BFS;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.Territory;
import fall2022.sim.Sim;

//* Spawn recycler in early game
public class LowRecyclers {
	public static double ratio[] = new double[Pos.MAX_OFFSET];
	public static int diffInPotentialCells[] = new int[Pos.MAX_OFFSET];
	public static int diffInMatter[] = new int[Pos.MAX_OFFSET];
	public static int diffInRobots[] = new int[Pos.MAX_OFFSET];
	public static int diffInCells[] = new int[Pos.MAX_OFFSET];
	
	
	
	static public void prepareRatio(State work) {
		List<Pos> myPos = new ArrayList<>();
		List<Pos> posToCheck = new ArrayList<>();
		for (Pos p : Pos.allMapPositions) {
			if (work.o[p.o] == O.ME ) {
				if (work.canMove(p)) {
					myPos.add(p);
				}
				
				if (work.canBuild(p) ) {
					posToCheck.add(p);
				}
			}
		}
		
		State originalInTen = new State();
		originalInTen.copyFrom(work);
		Sim.tenTurn(originalInTen);
		List<Pos> quickReachableCells = BFS.quickReachableCells(originalInTen, myPos);
		int myOriginalPotentialCells = quickReachableCells.size(); 
		
		// reduce map to calculate
		for (Pos c : posToCheck) {
			updateRatioFor(work, c, originalInTen, myPos, myOriginalPotentialCells);
		}
	}
	
	
	public static void updateRatioFor(State work, Pos c, State originalInTen, List<Pos> myPos, int myOriginalPotentialCells) {
		
		ratio[c.o] = 0;
		diffInPotentialCells[c.o] = 0;
		diffInMatter[c.o] = 0;
		diffInRobots[c.o] = 0;
		diffInCells[c.o] = 0;
		
		if (work.s[c.o] == 0 || work.rec[c.o] != 0) return;


		State temp = new State();
		temp.copyFrom(work);
		temp.rec[c.o] = 1;
		temp.o[c.o] = O.ME;
		temp.myMatter -= O.COST;
		Sim.tenTurn(temp);

		int myPotentialCells = BFS.quickReachableCells(temp, myPos).size(); 

		int diffInRobots = temp.myMatter / O.COST - originalInTen.myMatter / O.COST;
		int diffInCells =0;
		for (Pos n : c.meAndNeighbors4dirs) {
			if (originalInTen.s[n.o] != 0 && temp.s[n.o]==0 && temp.o[n.o] != O.OPP) diffInCells++;
		}

		double ratio;
		if (diffInCells == 0) {
			// free
			if (diffInRobots <= 0) {
				ratio = 0.0;
			} else {
				ratio = diffInRobots;
			}
		} else {
			ratio = 1.0*diffInRobots / diffInCells;
		}

		LowRecyclers.diffInRobots[c.o] = diffInRobots;
		LowRecyclers.diffInCells[c.o] = diffInCells;
		LowRecyclers.diffInMatter[c.o] = temp.myMatter - originalInTen.myMatter;
		LowRecyclers.diffInPotentialCells[c.o] = (myPotentialCells +diffInCells) - myOriginalPotentialCells; 
		LowRecyclers.ratio[c.o] = ratio;		
	}


	public List<Action> think(State work) {
		List<Action> actions = new ArrayList<>();
		
		if (Territory.myTerritory < 0.95 * Territory.oppTerritory) return actions; 
		
		if (work.myMatter < O.COST) return Collections.emptyList();
		
		boolean hasBuild =true;
		while (hasBuild) {
			if (work.myMatter < O.COST) break;

			hasBuild = false;
			
			double bestRatio = Double.NEGATIVE_INFINITY;
			Pos bestPos = null;
			for (Pos c : Pos.allMapPositions) {
				
				if (!work.canBuild(c)) continue;
				if (diffInPotentialCells[c.o] < 0) continue; // don't build isolated ilots
	

				if (ratio[c.o] > bestRatio) {
					bestRatio = ratio[c.o];
					bestPos = c;
				}
			}
			
			if (bestRatio >= 1.0) {
				System.err.println("Found a recycler with ratio :"+bestRatio);
				Action build = Action.build(bestPos, "LowRecycler - high ratio : "+bestRatio);
				actions.add(build);
				work.apply(build);
				hasBuild = true;
			} else {
				System.err.println("Did'nt found any recycler, bestRatio was "+bestRatio);
			}
		}
		return actions;
	}
}
