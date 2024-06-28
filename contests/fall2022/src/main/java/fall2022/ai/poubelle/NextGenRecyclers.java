package fall2022.ai.poubelle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fall2022.Action;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.Ilot.Ilot;
import fall2022.ai.ai2.Defense;
import fall2022.sim.Sim;

public class NextGenRecyclers {

	private static final int NEEDED_MATTER_BONUS = 10;



	
	
	public List<Action> think(State work, List<Ilot> ilots) {
		if (work.myMatter < O.COST) return Collections.emptyList();
		List<Action> actions = new ArrayList<>();
		

		
		int deficit = Defense.unitsNeededInTime(work, ilots);
		if (deficit <= 0) return Collections.emptyList(); 

		// calculate levels based on deficit
		int urgent_CellDestructionMaxLevel;
		if (deficit < 2) {
			urgent_CellDestructionMaxLevel = 1; 
		} else if (deficit < 5) {
			urgent_CellDestructionMaxLevel = 2; 
		} else {
			urgent_CellDestructionMaxLevel = 5; // all cells destructed, i don't care 
		}

		
		// simulate to know initial conditions
		State lastOriginal = new State();
		lastOriginal.copyFrom(work);
		Sim.tenTurn(lastOriginal);

		
		double bestScore = Double.NEGATIVE_INFINITY;
		Pos bestPos = null;
		for (Pos pos : Pos.allMapPositions) {
			if (work.myMatter < O.COST) break;
			if (!work.canBuild(pos)) continue;
			
			
			State temp = new State();
			temp.copyFrom(work);
			Action build = Action.build(pos, "NextGen recycler");
			temp.apply(build);
			Sim.tenTurn(temp);

			int matterBonus = temp.myMatter - lastOriginal.myMatter;
			if (matterBonus < NEEDED_MATTER_BONUS) continue; // not enough bonus to take it into account
			
			int cellDestructedCount = 1;
			for (Pos n : pos.neighbors4dirs) {
				if (lastOriginal.s[n.o] == 0) continue;
				if (lastOriginal.s[n.o] <= work.s[pos.o]) cellDestructedCount++;
			}
			
			
			double score = 0.0;

			score += 0.01 * matterBonus;
			if (cellDestructedCount > urgent_CellDestructionMaxLevel) {
				score = Double.NEGATIVE_INFINITY; // a LOT of reason not to chose it :)
			}
			
			if (score > bestScore) {
				bestScore =score;
				bestPos = pos;
			}
		}
		
		if (bestPos != null) {
			Action build = Action.build(bestPos, "NextGen recycler");
			actions.add(build);
			work.apply(build);
		} else {
		}
		
		return actions;
	}
}
