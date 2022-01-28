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
			for (int y=0;y<10;y++) {
				for (int x=0;x<19;x++) {
					int need = neededDirOnCell[Pos.get(x, y).posOffset];
					System.err.print(need == -1 ? ' ' : ""+need);
				}
				System.err.println();
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
		
		final Pos initialPos = robot.pos;
		final int initialNeed = neededDirOnCell[initialPos.posOffset];
		
		bestScore = -1;
		posFE = 0;
		for (int dir=0;dir<4;dir++) {
			this.initState.copyFrom(state);
			robot.incrementSim();
			robot.pos = initialPos;
			
			if (!initState.isCellEmpty(robot.pos) && dir != robot.pos.dir) continue;
			
			neededDirOnCell[initialPos.posOffset] = dir;
			if (initialPos.dir != dir) {
				robot.pos = robot.pos.update(dir);
				initState.applyArrow(robot.pos);
				positions[posFE++] = robot.pos;
			} else {
			}
			
			iterate(initState, robot, 0);
	
			neededDirOnCell[initialPos.posOffset] = initialNeed;
			if (initialPos.dir != dir) {
				initState.removeArrow(initialPos);
				posFE--;
			}else {
			}
		}
		
		neededDirOnCell[initialPos.posOffset] = initialNeed;
		
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
	
	
	static int[] neededDirOnCell = new int[10*19];
	static {
		for (int i=0;i<neededDirOnCell.length;i++) {
			neededDirOnCell[i] = -1; // no dir needed
		}
	}
	private void iterate(State state, Robot robot, int currentScore) {
		boolean debug = false;
		if (robot.pos.posOffset == Pos.get(8, 4).posOffset
		 ||	robot.pos.posOffset == Pos.get(8, 4).posOffset
				
				) {
			//System.err.println("Robot is on "+robot.pos+" ... debugging");
			debug = false;
		}
		
		if (robot.pos == Pos.get(4, 4, 'L')) {
			System.err.println("debug ! ");
		}

		Pos initialPos = robot.pos;

		robot.move(state); // make the move in the current direction
		currentScore++;
		
		if (debug) {
			System.err.println("Robot pos after move is "+robot.pos);
		}
		
		if (robot.pos == Pos.VOID) {
			//System.err.println("End of line with score "+currentScore);
			
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
		final int initialNeedToStayEmptyCells = neededDirOnCell[movedPos.posOffset];
		if (debug) {
			System.err.println("Robot pos : "+robot.pos);
			System.err.println("is cell empty ? " + state.isCellEmpty(robot.pos));
			System.err.println("need to stay this way ? "+neededDirOnCell[movedPos.posOffset]);
		}
		if (state.isCellEmpty(robot.pos) && neededDirOnCell[movedPos.posOffset] != 6) {
			if (debug) System.err.println("I can put an arrow on "+robot.pos);
			for (int dir=0;dir<4;dir++) {
				
				if (neededDirOnCell[movedPos.posOffset] != -1 && 
						(neededDirOnCell[movedPos.posOffset] != dir && dir != movedPos.dir)) continue;
				
				robot.pos = movedPos;
				if (dir != movedPos.dir) {
					if (robot.hasVisited(robot.pos.update(dir))) {
						continue; // don't turn
					}
					boolean result = state.applyArrow(movedPos.update(dir));
					robot.pos = robot.pos.update(dir);
					positions[posFE++] = robot.pos;
					if (debug) System.err.println("  Putting the arrow was "+result+" and the updated robot pos is "+robot.pos);
				}
				
				if (neededDirOnCell[movedPos.posOffset] == -1) {
					neededDirOnCell[movedPos.posOffset] = dir; // force this dir
				} else if (neededDirOnCell[movedPos.posOffset] != dir) {
					neededDirOnCell[movedPos.posOffset] = 6; // multiple dirs on the same cell, 
				}
				
				iterate(state, robot, currentScore);
				
				neededDirOnCell[movedPos.posOffset] = initialNeedToStayEmptyCells;
				
				if (dir != movedPos.dir) { 
					state.removeArrow(movedPos);
					posFE--;
					if (debug) System.err.println("  Removing the arrow");
				}
				
			}
		} else {
			if (neededDirOnCell[movedPos.posOffset] == -1) {
				neededDirOnCell[movedPos.posOffset] = movedPos.dir;
			} else if (neededDirOnCell[movedPos.posOffset] != movedPos.dir) {
				neededDirOnCell[movedPos.posOffset] = 6;
			}
			
			iterate(state, robot, currentScore);

			neededDirOnCell[movedPos.posOffset] = initialNeedToStayEmptyCells;
		}

		robot.undoMoveTo(initialPos);
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
