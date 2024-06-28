package spring2021.ai;

import spring2021.Player;
import spring2021.State;

public class EvaluationNext {
	public double mySunScore;
	public double hisSunScore;
	public double currentSeedDeltaRichness;
	public double sunScore;
	public double potentialSunScore;
	public double potentialPoints;
	public double theScore;
	
	public double evaluate(State state) {
		double score = 0.0;

		if (state.day == 24) {
			score += 1_000_000.0 * state.score[0];
			//score -= 1000.0 * state.score[1];
			return score;
		}
		
		mySunScore = 1.0 * state.sun[0];
		score += mySunScore; // my sun point at start of day
		hisSunScore = 3.0 * state.sun[1];
		score -= hisSunScore; // dimish opp sun point
		
		
		// how many sun points expected
		// score -= 1.0 * state.treesCount[0][0];
		double coeff0, coeff1, coeff2, coeff3;
		if (state.day < Player.GROW_UNTIL_DAY) {
			coeff0 = 0.5;
      coeff1 = 1.0;
      coeff2 = 6.0;
      coeff3 = 12.0;
	  } else if (state.day < Player.GROW_UNTIL_DAY + 3) {
	  	coeff0 = 0.5;
	      coeff1 = 1.0;
	      coeff2 = 3.0;
	      coeff3 = 6.0;
	  } else {
		  	coeff0 = 0.5;
	      coeff1 = 1.0;
	      coeff2 = 2.0;
	      coeff3 = 3.0;
	  }
		
		
		
		double sunProviderCoeff = 0.7; //0.5 + 1.0 * Math.pow(state.nutrients/20.0, 1); // on sera pas eclairé tout le temps
		
		sunScore = 0.0;
		sunScore += sunProviderCoeff * coeff0 * state.treesCount[5*0+0] * (24 - state.day);
		sunScore += sunProviderCoeff * coeff1 * state.treesCount[5*0+1] * (24 - state.day);
		sunScore += sunProviderCoeff * coeff2 * state.treesCount[5*0+2] * (24 - state.day);
		sunScore += sunProviderCoeff * coeff3 * state.treesCount[5*0+3] * (24 - state.day);
		score +=sunScore;
		
		
//		potentialSunScore = 0.0;
//		potentialSunScore += 1 * state.treesCount[5*0+1] * (24 - state.day);
//		potentialSunScore += 2 * state.treesCount[5*0+2] * (24 - state.day);
//		potentialSunScore += 3 * state.treesCount[5*0+3] * (24 - state.day);
//		potentialSunScore *= 1.0;
//		score += 1.0 * potentialSunScore;
//		
//		potentialPoints = 1.2*calculatePotentialPoints(state);
//		score += 1.0 * potentialPoints;
		
		
		currentSeedDeltaRichness = 0.01 * state.currentSeedDeltaRichness;
		score += currentSeedDeltaRichness; // try to get trees on best cells
		
		theScore = 2.5 * state.score[0];
		if (state.day > 20) {
	      // 2 4 6 8 
	      theScore *= (2 * (state.day - 20));
		}
		score += theScore;
		
		return score;
	}

	private int calculatePotentialPoints(State state) {
		int points = 0;
		int totalTrees = 0;
		
		// potential to gain points
//		if (state.day < 20) totalTrees += state.treesCount[0*5+0];
//		if (state.day < 21) totalTrees += state.treesCount[0*5+1];
//		if (state.day < 22) totalTrees += state.treesCount[0*5+2];
		if (state.day < 23) totalTrees += state.treesCount[0*5+3];
		
		if (totalTrees > state.nutrients) totalTrees = state.nutrients;
		int remainingNutrients = state.nutrients - totalTrees;
				
		points = (state.nutrients +1) * state.nutrients / 2;
		points-=  (remainingNutrients +1 ) * remainingNutrients / 2;
		
			// richness of the soil give more points
		points += 1.0 * state.currentTotalRichness;
		return points;
	}

	public void debug(State state) {
		evaluate(state);

		System.err.println("The score:" +theScore);
		System.err.println("mySunScore :" +mySunScore);
		System.err.println("hisSunScore :" +hisSunScore);
		System.err.println("currentSeedDeltaRichness :" +currentSeedDeltaRichness);
		System.err.println("sunScore :" +sunScore);

		
	}
}
