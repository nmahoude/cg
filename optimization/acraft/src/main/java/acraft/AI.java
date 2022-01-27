package acraft;

import java.util.Arrays;

import cgutils.random.FastRandom;

public class AI {
	RobotSolution solution = new RobotSolution();

	
	public void think(State initState) {
		solution.clear();

		RobotSolution candidate = new RobotSolution();
		
		// init the robot paths
		initState.copyFrom(initState);
		
		int bestScore = initState.calculateScore();
		System.err.println("AI - init score is "+bestScore);
		
		FastRandom random = new FastRandom(System.currentTimeMillis());
		
		long start = System.currentTimeMillis();
		int iter = 0;
		while(true) {
			iter++;
			if ((iter % 255) == 0 && System.currentTimeMillis() - start > 900) {
				break;
			}
			
			candidate.copyFrom(initState);
			for (int arrow=0;arrow<50;arrow++) {
				
				int x = random.nextInt(State.WIDTH);
				int y = random.nextInt(State.HEIGHT);
				int dir = random.nextInt(4);
				
				candidate.apply(Pos.get(x, y, dir));
			}

			//System.err.println("First solution string : ");
			//candidate.debug();
			//System.err.println("now exploring alternatives");
			exploreAlternatives(candidate);
			
			
			if (bestAlternativeScore > bestScore) {
//				System.err.println("AI - new best score :"+score);
				bestScore = bestAlternativeScore;
				solution.backupSolution(alternative);
//				System.err.println("Solution is ("+solutionFE+") : ");
//				for (int i=0;i<solutionFE;i++) {
//					System.err.print(solution[i]);
//					System.err.print(",  ");
//				}
//				System.err.println();
			}
			
		}
		
		long end = System.currentTimeMillis();
		System.err.println("Think: "+iter+" iter in "+(end-start)+" ms");
	}

	
	private RobotSolution exploreAlternatives(RobotSolution candidate) {
		bestAlternativeScore = -1;
		exploreAlternatives(candidate, 0);
		
		return alternative;
	}

	RobotSolution alternative = new RobotSolution();
	int bestAlternativeScore = 0;
	
	private void exploreAlternatives(RobotSolution candidate, int index) {
		if (index >= candidate.state.robotsFE) {
			// time to redo simulation !
			int score = candidate.calculateScore();
			if (score > bestAlternativeScore) {
				   //System.err.println("New best score:  "+score);
	               // System.err.println("For solution : ");
	               // candidate.debug();
	                bestAlternativeScore = score;
	                alternative.backupSolution(candidate);
			}
			return;
		}
		
		Robot current = candidate.state.robots[index];
		Pos lastValidPos = current.path[current.pathFE-1]; 
		
		exploreAlternatives(candidate, index+1);

		if (candidate.state.cells[lastValidPos.x][lastValidPos.y] == State.EMPTY) {
			// try to 
			for (int dir=0;dir<4;dir++) {
				if (dir == lastValidPos.dir) continue; 
				candidate.apply(Pos.get(lastValidPos.x, lastValidPos.y, dir));
				exploreAlternatives(candidate, index+1);
				candidate.unapply(Pos.get(lastValidPos.x, lastValidPos.y, dir));
			}
		}
	}


	public void output() {
		solution.print();
	}
}
