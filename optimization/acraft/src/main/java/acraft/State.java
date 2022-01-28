package acraft;

import fast.read.FastReader;

public class State {
	
	static final int WIDTH = 19;
	static final int HEIGHT = 10;
	
	public static final char VOID = '#';
	public static final char EMPTY = '.';
	
	char cells[][] = new char[WIDTH][HEIGHT];
	Robot[] robots = new Robot[10];
	int robotsFE = 0;
	
	public void read(FastReader in) {
		for (int y = 0; y < 10; y++) {
            char[] line = in.nextChars();
            for (int x=0;x<WIDTH;x++) {
            	cells[x][y] = line[x];
            }
        }
		
        int robotCount = in.nextInt();
        for (int i = 0; i < robotCount; i++) {
        	addRobot(Robot.read(in));
        }
	}

	public void resetRobots() {
		for (int i=0;i<robotsFE;i++) {
			robots[i].restore();
		}
	}

	public void print() {
		System.err.println("State : ");
		for (int y=0;y<HEIGHT;y++) {
			for (int x=0;x<WIDTH;x++) {
				System.err.print(cells[x][y]);
			}
			System.err.println();
		}
	}

	public void copyFrom(State model) {
		this.robotsFE = model.robotsFE;
		for (int i=0;i<10;i++) {
			this.robots[i] = model.robots[i];
		}
		for (int y=0;y<HEIGHT;y++) {
			for (int x=0;x<WIDTH;x++) {
				this.cells[x][y] = model.cells[x][y];
			}
		}
	}
	
	public int calculateScore() {
		int score = 0;
		for (int i=0;i<robotsFE;i++) {
			robots[i].calculatePath(this);
			score += robots[i].score();
		}
		return score;
	}

	public void addRobot(Robot robot) {
    	robots[robotsFE++] = robot;
	}

	public boolean applyArrow(Pos pos) {
		if (this.cells[pos.x][pos.y] != EMPTY) return false;
		
		this.cells[pos.x][pos.y] = pos.dir == 4 ? ' ' : dirToLetter(pos.dir);
		return true;
		
	}
	
	private char dirToLetter(int dir) {
		switch(dir) {
		case 0: return 'U';
		case 1: return 'R';
		case 2: return 'D';
		case 3: return 'L';
		}
		return 0;
	}

	public void applySolution(Pos[] candidate, int count) {
		for (int i=0;i<count;i++) {
			this.applyArrow(candidate[i]);
		}
	}

	/*
	 * warning, this method doesn't check if the arrow is removable
	 */
	public void removeArrow(Pos pos) {
		this.cells[pos.x][pos.y] = EMPTY; 
	}

	public boolean isCellEmpty(Pos pos) {
		return cells[pos.x][pos.y] == EMPTY;
	}

	public void debugRobotsPath() {
		for (int r=0;r<robotsFE;r++) {
			System.err.println("Robot "+r);
			robots[r].debugPath();
		}
	}

}
