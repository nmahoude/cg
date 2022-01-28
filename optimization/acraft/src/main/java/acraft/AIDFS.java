package acraft;

public class AIDFS {

	int grid[][][] = new int[19][10][4];
	int bestScore = -1;
	private State initState = new State();

	Pos fullSolution[][] = new Pos[10][4*10*19];
	int fullSolutionFE[] = new int[10];
	
	public void think(State fullState) {
		bestScore = -1;
		long start = System.currentTimeMillis();
		
		
		for (int r=0;r<fullState.robotsFE;r++) {
			initState.copyFrom(fullState);
			
			// reapply former arrows
			for (int r2=0;r2<r;r2++) {
				for (int i=0;i<fullSolutionFE[r2];i++) {
					this.initState.applyArrow(fullSolution[r2][i]);
				}
			}
			
			
			System.err.println("______________________");
			System.err.println("Robot : "+r);
			System.err.println("______________________");
			think(this.initState, this.initState.robots[r]);
			
			System.arraycopy(bestPos, 0, fullSolution[r], 0, bestPosFE);
			fullSolutionFE[r] = bestPosFE;
		}
		
		long end = System.currentTimeMillis();
		System.err.println("Think in "+(end - start)+ " ms");
		
		
		System.err.println("replaying ...");
		this.initState.copyFrom(initState);
		System.err.println("Applying arrows : ");
		for (int r=0;r<initState.robotsFE;r++) {
			for (int i=0;i<fullSolutionFE[r];i++) {
				this.initState.applyArrow(fullSolution[r][i]);
				System.err.print(fullSolution[r][i]+" ; ");
			}
			System.err.println();
		}
		this.initState.calculateScore();
		this.initState.debugRobotsPath();
		
	}

	private void think(State state, Robot robot) {
		robot.restore();
		
		Pos initialPos = robot.pos;
		needToStayEmptyCells[initialPos.posOffset] = 1;
		
		bestScore = -1;
		posFE = 0;
		for (int dir=0;dir<4;dir++) {
			this.initState.copyFrom(state);
			robot.incrementSim();
			robot.pos = initialPos;
			
			if (!initState.isCellEmpty(robot.pos) && dir != robot.pos.dir) continue;
			
			if (initialPos.dir != dir) {
				robot.pos = robot.pos.update(dir);
				initState.applyArrow(robot.pos);
				positions[posFE++] = robot.pos;
			} else {
			}
			
			iterate(initState, robot, 0);
	
			if (initialPos.dir != dir) {
				initState.removeArrow(initialPos);
				posFE--;
			}else {
			}
		}
		
		
		
		System.err.println("Best score is "+bestScore);
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
		boolean debug = false;
		if (robot.pos.posOffset == Pos.get(8, 4).posOffset
		 ||	robot.pos.posOffset == Pos.get(8, 4).posOffset
				
				) {
			//System.err.println("Robot is on "+robot.pos+" ... debugging");
			debug = false;
		}
		
		

		Pos initialPos = robot.pos;

		robot.move(state); // make the move in the current direction
		if (debug) {
			System.err.println("Robot pos after move is "+robot.pos);
		}
		
		if (robot.pos == Pos.VOID) {
			//System.err.println("End of line with score "+currentScore);
			currentScore++;
			
			if (currentScore > bestScore) {
				bestScore = currentScore;
				System.err.println("New best score ! " + bestScore);
				for (int i=0;i<posFE;i++) {
					System.err.print(positions[i]);
					System.err.print(" ; ");
				}
				System.err.println();
				System.arraycopy(positions, 0, bestPos, 0, posFE);
				bestPosFE = posFE;
			}
			return;
		}

		
		final Pos movedPos = robot.pos;
		final int initialNeedToStayEmptyCells = needToStayEmptyCells[movedPos.posOffset];
		if (debug) {
			System.err.println("Robot pos : "+robot.pos);
			System.err.println("is cell empty ? " + state.isCellEmpty(robot.pos));
			System.err.println("need to stay this way ? "+needToStayEmptyCells[robot.pos.posOffset]);
		}
		if (state.isCellEmpty(robot.pos) && needToStayEmptyCells[robot.pos.posOffset] == 0) {
			if (debug) System.err.println("I can put an arrow on "+robot.pos);
			for (int dir=0;dir<4;dir++) {
				robot.pos = movedPos;
				if (dir != movedPos.dir) { 
					boolean result = state.applyArrow(movedPos.update(dir));
					robot.pos = robot.pos.update(dir);
					positions[posFE++] = robot.pos;
					if (debug) System.err.println("  Putting the arrow was "+result+" and the updated robot pos is "+robot.pos);
				}
				needToStayEmptyCells[movedPos.posOffset] = 1;
				
				iterate(state, robot, currentScore+1);
				
				if (dir != movedPos.dir) { 
					state.removeArrow(movedPos);
					posFE--;
					if (debug) System.err.println("  Removing the arrow");
				}
				
			}
		} else {
			iterate(state, robot, currentScore+1);
		}

		robot.undoMoveTo(initialPos);
		needToStayEmptyCells[movedPos.posOffset] = initialNeedToStayEmptyCells;
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
