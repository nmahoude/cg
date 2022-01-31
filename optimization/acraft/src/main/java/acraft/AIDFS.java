package acraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AIDFS {

	int grid[][][] = new int[19][10][4];
	int bestScore = -1;
	private State initState = new State();

	Pos fullSolution[][] = new Pos[10][4*10*19];
	int fullSolutionFE[] = new int[10];

	Pos bestSolution[][] = new Pos[10][4*10*19];
	int bestSolutionFE[] = new int[10];
	int bestSolutionScore = -1;
	
	public void think(State fullState) {
		long start = System.currentTimeMillis();
		
		List<Integer> order = new ArrayList<>();
		for (int i=0;i<fullState.robotsFE;i++) {
			order.add(i);
		}
		do {
	
			think(fullState, order);
			
			if (bestScore > bestSolutionScore) {
				bestSolutionScore = bestScore;
				
				for (int r=0;r<fullState.robotsFE;r++) {
					System.arraycopy(fullSolution[r], 0, bestSolution[r], 0, fullSolutionFE[r]);
					bestSolutionFE[r] = fullSolutionFE[r];
				}
			}
			Collections.shuffle(order);
			
		} while(System.currentTimeMillis() - start < 500);
	}
	
	public void think(State fullState, List<Integer> order) {
		boolean debug;
		bestScore = -1;
		long start = System.currentTimeMillis();
		
		initState.copyFrom(fullState);
		
		
		
		//for (int r=0;r<fullState.robotsFE;r++) {
		for (int o=0;o<fullState.robotsFE;o++) {
			int r = order.get(o);
			think(this.initState, this.initState.robots[r]);
			
			System.arraycopy(bestPos, 0, fullSolution[r], 0, bestPosFE);
			fullSolutionFE[r] = bestPosFE;
			

			initState.copyFrom(fullState);
			// reapply former robots arrows & paths
			for (int o2=0;o2<=o;o2++) {
				int r2 = order.get(o2);
				for (int i=0;i<fullSolutionFE[r2];i++) {
					this.initState.applyArrow(fullSolution[r2][i]);
				}
			}

			debug = r==4 || r == 3;
			
			if (debug) {
				System.err.println("After applying solution's arrows for robots 0 to "+r);
				initState.print();
			}

			resetNeedDirOnCell();
			for (int o2=0;o2<=o;o2++) {
				int r2 = order.get(o2);
				// reapply needed direction of the former robots
				//System.err.println("Robot "+r2+" path : ");
				Robot robot = initState.robots[r2];
				robot.calculatePath(initState);
				for (int i=0;i<robot.pathFE;i++) {
					//System.err.print(robot.path[i]+ "->");
					if (neededDirOnCell[robot.path[i].posOffset] == -1) {
						neededDirOnCell[robot.path[i].posOffset] = robot.path[i].dir;
					} else if (neededDirOnCell[robot.path[i].posOffset] != robot.path[i].dir) {
						neededDirOnCell[robot.path[i].posOffset] = 6; // two or more dirs, no arrows
					}
				}
				//System.err.println();
			}
	
			if (debug) {
				System.err.println("Need map: ");
				System.err.println(" ------------------- ");
				for (int y=0;y<10;y++) {
					System.err.print("|");
					for (int x=0;x<19;x++) {
						int need = neededDirOnCell[Pos.get(x, y).posOffset];
						System.err.print(need == -1 ? ' ' : ""+need);
					}
					System.err.println("|");
				}
				System.err.println(" ------------------- ");
			
			}
//			System.err.println("______________________");
//			System.err.println("Robot : "+r);
//			System.err.println("______________________");
			
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

			robot.pos = initialPos;
			robot.incrementSim();
			
			if (!initState.isCellEmpty(robot.pos) && dir != robot.pos.dir) continue;
			
			neededDirOnCell[initialPos.posOffset] = dir;
			if (initialPos.dir != dir) {
				robot.pos = robot.pos.update(dir);
				initState.applyArrow(robot.pos);
				positionedArrows[posFE++] = robot.pos;
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
		
		
//		System.err.println("Best score is "+bestScore);
//		for (int i=0;i<bestPosFE;i++) {
//			System.err.print(bestPos[i]);
//			System.err.print(" ; ");
//		}
//		System.err.println();
		
	}

	static Pos[] positionedArrows = new Pos[4*10*19];
	static Pos[] bestPos = new Pos[4*10*19];
	static int posFE = 0;
	static int bestPosFE = 0;
	
	
	static int[] neededDirOnCell = new int[10*19];
	static {
		resetNeedDirOnCell();
	}

	private static void resetNeedDirOnCell() {
		for (int i=0;i<neededDirOnCell.length;i++) {
			neededDirOnCell[i] = -1; // no dir needed
		}
	}
	
	static int iter = 0;
	private void iterate(State state, Robot robot, int currentScore) {
        Pos initialPos = robot.pos;

        // make the move in the current direction
        robot.move(state);
        currentScore++;

        if (robot.pos == Pos.VOID) {
            updateBestScore(currentScore);
            robot.undoMoveTo(initialPos);
            return;
        }
        final Pos movedPos = robot.pos;
        final int initialNeedToStayEmptyCells = neededDirOnCell[movedPos.posOffset];

        if (state.isCellEmpty(robot.pos) && neededDirOnCell[movedPos.posOffset] != 6) {
            for (int dir = 0; dir < 4; dir++) {
                if (neededDirOnCell[movedPos.posOffset] != -1 && (neededDirOnCell[movedPos.posOffset] != dir && dir != movedPos.dir))
                    continue;
                robot.pos = movedPos;
                if (dir != movedPos.dir) {
                    if (robot.hasVisited(robot.pos.update(dir))) {
                        updateBestScore(currentScore);
                        continue;
                    }
                    boolean result = state.applyArrow(movedPos.update(dir));
                    robot.pos = robot.pos.update(dir);
                    positionedArrows[posFE++] = robot.pos;
                }
                if (neededDirOnCell[movedPos.posOffset] == -1) {
                    // force this dir
                    neededDirOnCell[movedPos.posOffset] = dir;
                } else if (neededDirOnCell[movedPos.posOffset] != dir) {
                    // multiple dirs on the same cell, 
                    neededDirOnCell[movedPos.posOffset] = 6;
                }
                iterate(state, robot, currentScore);
                neededDirOnCell[movedPos.posOffset] = initialNeedToStayEmptyCells;
                if (dir != movedPos.dir) {
                    state.removeArrow(movedPos);
                    posFE--;
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

	private void updateBestScore(int currentScore) {
//		if (currentScore > 26) {
//			if (posFE == 1) {
//				System.err.println("debug!");
//			}
//		System.err.println("Final score = "+currentScore);
//		for (int i=0;i<posFE;i++) {
//			System.err.print(positionedArrows[i]+" ; ");
//		}
//		System.err.println();
//		}
		
		
		int score = currentScore; // * 1000 + 100 - posFE; // the less arrow the better
		if (score > bestScore) {
			bestScore = score;
			//System.err.println("New best score ! " + bestScore);
			/*
			for (int i=0;i<posFE;i++) {
				System.err.print(positions[i]);
				System.err.print(" ; ");
			}
			System.err.println();
			*/
			System.arraycopy(positionedArrows, 0, bestPos, 0, posFE);
			bestPosFE = posFE;
		}
	}

	public void output() {
		for (int r=0;r<initState.robotsFE;r++) {
			for (int i=0;i<bestSolutionFE[r];i++) {
				
				System.out.print(""+bestSolution[r][i].x +" "+bestSolution[r][i].y+" "+bestSolution[r][i].dirToLetter()+" ");
			}
		}
		System.out.println();
	}
}
