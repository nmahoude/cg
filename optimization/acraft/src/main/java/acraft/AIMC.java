package acraft;

import cgutils.random.FastRandom;

public class AIMC {
	int bestScore = -1;

	Pos positionedArrows[] = new Pos[10*19];
	int positionedArrowsFE = 0;
	
	
	Pos bestPositionedArrows[] = new Pos[10*19];
	int bestPositionedArrowsFE = 0;
	private State initState = new State();
	
	FastRandom random = new FastRandom(System.currentTimeMillis());
	
	public void think(State fullState) {
		
		int iter = 0;
		while(true) {
			initState.copyFrom(fullState);
			
			
			
			iter++;
			if ((iter & 255) == 0) {

				if (System.currentTimeMillis() - Player.start > 900) {
					break;
				}
			}

			
			if ((iter & 0b1111111111111111) == 0 || bestPositionedArrowsFE == 0) {
				generateRandomSolution();
			} else {
				// copy best positions
				System.arraycopy(bestPositionedArrows, 0, positionedArrows, 0, bestPositionedArrowsFE);
				positionedArrowsFE = bestPositionedArrowsFE;
			
				
				int percent = random.nextInt(100);
				if (percent > 80) {
					// random delete
					int randomDelete = random.nextInt(positionedArrowsFE);
					positionedArrows[randomDelete] = positionedArrows[positionedArrowsFE-1];
					positionedArrowsFE--;
				} else if (percent > 40) {
					// random rotate
					int randomRotate = random.nextInt(positionedArrowsFE);
					int randomDir = random.nextInt(4);
					positionedArrows[randomRotate] = positionedArrows[randomRotate].update(randomDir);
				} else {
					int tentative = 5;
					while (tentative > 0) {
						if (randomAdd()) break;
						tentative--;
					}
					if (tentative == 0) {
						generateRandomSolution();
					}
				}
			}
			
			
			
			for (int i=0;i<positionedArrowsFE;i++) {
				initState.applyArrow(positionedArrows[i]);
			}
			int score = initState.calculateScore();
			if (score > bestScore) {
				bestScore = score;
				System.arraycopy(positionedArrows, 0, bestPositionedArrows, 0, positionedArrowsFE);
				bestPositionedArrowsFE = positionedArrowsFE;
			}
		}
		
		
	}

	private void generateRandomSolution() {
		positionedArrowsFE = 0;
		
		int arrowsCount = random.nextInt(10*19);
		for (int i=0;i<arrowsCount;i++) {
			randomAdd();
		}
	}

	private boolean randomAdd() {
		int x = random.nextInt(State.WIDTH);
		int y = random.nextInt(State.HEIGHT);
		int dir = random.nextInt(4);
		
		if (initState.cells[x][y] != State.EMPTY)
			return false;

		initState.applyArrow(Pos.get(x, y, dir));
		positionedArrows[positionedArrowsFE++] = Pos.get(x, y, dir);
		return true;
	}

	public void output() {
		for (int r=0;r<initState.robotsFE;r++) {
			for (int i=0;i<bestPositionedArrowsFE;i++) {
				
				System.out.print(""+bestPositionedArrows[i].x +" "+bestPositionedArrows[i].y+" "+bestPositionedArrows[i].dirToLetter()+" ");
			}
		}
		System.out.println();
	}
}
