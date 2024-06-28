package fall2022.ai.poubelle;

import java.util.ArrayList;
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

public class RecyclerMatterUpgrader2 {
	TimeTraveler myTT = new TimeTraveler();
	private Set<Pos> frontier = new HashSet<>();

	private TimeTraveler originalTT;
	private List<Ilot> originalIlots;
	private int myOriginalPotentialCells;
	
	public List<Action> think(State work, List<Ilot> originalIlots, TimeTraveler tt) {
		this.originalIlots = originalIlots;
		myOriginalPotentialCells = Ilot.myPotentialCells(originalIlots); 
		
		this.originalTT = tt;
		if (work.myMatter < O.COST) return Collections.emptyList();
		
		List<Action> toBuild = new ArrayList<>();
		frontier.clear();
		
		int myUnitCount = 0;
		int oppUnitCount = 0;
		boolean hasDisputed = false;
		for (Ilot ilot : originalIlots) {
			if (ilot.isDisputed()) {
				hasDisputed = true;
				myUnitCount += ilot.myTroupsCount;
				oppUnitCount += ilot.oppTroupsCount;
				
				Territory t = new Territory();
				t.calculateTerritories(ilot, tt);
				for (Pos f : t.frontier) {
					frontier.add(f);
				}
				
			}
		}
		int myTotalUnitInTime = myUnitCount + (work.myMatter + tt.myTotalMatterBonus) / O.COST;
		int oppTotalUnitInTime = oppUnitCount + (work.oppMatter + tt.oppTotalMatterBonus) / O.COST;
		
		if (oppTotalUnitInTime == 0) return Collections.emptyList();
		
		if (hasDisputed && myTotalUnitInTime < oppTotalUnitInTime) {
			// hmm he will have more units than me !
			toBuild.addAll(buildSomeMatter(work));
			return toBuild;
		}

		return Collections.emptyList();
	}

	private List<Action> buildSomeMatter(State work) {
		List<Action> actions = new ArrayList<>();

		double bestScore = Double.NEGATIVE_INFINITY;
		Pos bestPos = null;
		for (Pos p : Pos.allMapPositions) {
			if (!work.canBuild(p)) continue;
			
			
			double score = eval(work, p);
			if (score > bestScore) {
				bestScore = score;
				bestPos = p;
			}
		}
		
		if (bestPos != null) {
			Action build = Action.build(bestPos, "RMU");
			actions.add(build);
			work.apply(build);
		}
		
		return actions;
	}

	Territory territory = new Territory();
	private double eval(State originalState, Pos placeToBuild) {
		double score = 0.0;

		State temp = new State();
		temp.copyFrom(originalState);
		temp.rec[placeToBuild.o] = 1;
		temp.myMatter-=10;
		myTT.init(temp);

		State stateAfter10 = myTT.sliceAt(10).state;
		State originalStateAfter10 = originalTT.sliceAt(10).state;
		
		Sim.oneTurn(temp);
		List<Ilot> ilots = Ilot.build(temp);
		int myPotentialCells = Ilot.myPotentialCells(ilots);
		
		score -= (myOriginalPotentialCells - myPotentialCells);
		
		int matterBonus = stateAfter10.myMatter - originalStateAfter10.myMatter; 
		score += 1.0 * (1.0 * matterBonus / O.COST);
		
		int killedBlueCells =0, killedRedCells = 0, killedNeutralCells = 0;
		for (Pos n : placeToBuild.meAndNeighbors4dirs) {
			if (stateAfter10.s[n.o] == 0 && originalStateAfter10.s[n.o] != 0) {
				if (stateAfter10.isMine(n)) {
					killedBlueCells++;
				} else if (stateAfter10.isOpp(n)) {
					killedRedCells++;
				} else {
					killedNeutralCells++;
				}
			}
		}
		score -= 4.0 * killedBlueCells;
		score += 4.0 * killedRedCells;
		score -= 2.0 * killedNeutralCells;
		
		if (frontier.contains(placeToBuild)) {
			score += 10.0;
		}
		
		
		return score;
	}
}
