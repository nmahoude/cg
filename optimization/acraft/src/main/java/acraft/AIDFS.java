package acraft;

public class AIDFS {

	int grid[][][] = new int[19][10][4];
	int bestScore = -1;
	private State initState;

	Pos fullSolution[][] = new Pos[10][4*10*19];
	int fullSolutionFE[] = new int[10];
	
	public void think(State initState) {
		long start = System.currentTimeMillis();
		
		for (int i=0;i<initState.robotsFE;i++) {
			think(initState, initState.robots[i]);
			
			System.arraycopy(bestPos, 0, fullSolution[i], 0, bestPosFE);
			fullSolutionFE[i] = bestPosFE;
		}
		
		long end = System.currentTimeMillis();
		System.err.println("Think in "+(end - start)+ " ms");
	}

	private void think(State state, Robot robot) {
		bestScore = -1;
		
		this.initState = state;
		robot.restore();
		robot.incrementSim();
		
		Pos initialPos = robot.pos;
		needToStayEmptyCells[initialPos.posOffset] = 1;
		
		posFE = 0;
		for (int dir=0;dir<4;dir++) {
			robot.pos = initialPos;
			if (!state.isCellEmpty(robot.pos) && dir != robot.pos.dir) continue;
			
			if (initialPos.dir != dir) {
				robot.pos = robot.pos.update(dir);
				state.applyArrow(robot.pos);
				positions[posFE++] = robot.pos;
				System.err.println("Starting with an arrow : "+robot.pos);
			} else {
				System.err.println("Sarting on dir ...");
			}
			
			iterate(state, robot, 0);
	
			if (initialPos.dir != dir) {
				state.removeArrow(initialPos);
				posFE--;
				System.err.println("Ending arrow ...");
			}else {
				System.err.println("Ending on dir");
			}
		}
		
		
		
		System.err.println("Best score is "+bestPosFE);
		for (int i=0;i<bestPosFE;i++) {
			System.err.print(bestPos[i]);
			System.err.print(" ; ");
		}
		System.err.println();
		
	}

	static Pos[] positions = new Pos[4*10*19];
	static Pos[] bestPos = new Pos[4*10*19];
	static int posFE = 0;
	static int bestPosFE = 0;
	
	
	static int[] needToStayEmptyCells = new int[10*19];
	
	private void iterate(State state, Robot robot, int currentScore) {
		if (robot.pos.x == 1 && robot.pos.y == 7) {
			System.err.println("Robot is on "+robot.pos);
		}
		
		

		Pos initialPos = robot.pos;
		int initialNeedToStayEmptyCells = needToStayEmptyCells[initialPos.posOffset];

		robot.move(state); // make the move in the current direction
		if (initialPos == Pos.get(1, 8, Pos.UP)) {
			System.err.println("After "+initialPos +" been in "+robot.pos);
		}
		
		if (robot.pos == Pos.VOID) {
			//System.err.println("End of line with score "+currentScore);
			
			if (currentScore > bestScore) {
				bestScore = currentScore;
				System.err.println("New best score ! " + bestScore);
				for (int i=0;i<posFE;i++) {
					System.err.print(positions[i]);
					System.err.println(" ; ");
				}
				System.err.println();
				System.arraycopy(positions, 0, bestPos, 0, posFE);
				bestPosFE = posFE;
			}
			return;
		}
		
		Pos movedPos = robot.pos;
		if (state.isCellEmpty(robot.pos) && needToStayEmptyCells[robot.pos.posOffset] == 0) {
			for (int dir=0;dir<4;dir++) {
				robot.pos = movedPos;
				if (dir != movedPos.dir) { 
					state.applyArrow(movedPos.update(dir));
					robot.pos = robot.pos.update(dir);
					positions[posFE++] = robot.pos;
				}
				needToStayEmptyCells[robot.pos.posOffset] = 1;
				
				iterate(state, robot, currentScore+1);
				
				if (dir != movedPos.dir) { 
					state.removeArrow(movedPos);
					posFE--;
				}
				
			}
		} else {
			iterate(state, robot, currentScore+1);
		}

		robot.undoMoveTo(initialPos);
		needToStayEmptyCells[robot.pos.posOffset] = initialNeedToStayEmptyCells;
	}

	public void output() {
		for (int r=0;r<initState.robotsFE;r++) {
			for (int i=0;i<fullSolutionFE[r];i++) {
				
				System.out.print(""+fullSolution[r][i].x +" "+fullSolution[r][i].y+" "+fullSolution[r][i].dirToLetter()+" ");
			}
		}
		System.out.println();
	}
}
