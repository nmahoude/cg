package acraft;

import fast.read.FastReader;

public class Robot {
	Pos pos = Pos.VOID;
	private Pos _pos = Pos.VOID;
	
	Pos[] path = new Pos[4*10*19];
	int pathFE;
	
	public Robot(Pos pos) {
		this.pos = pos;
		this._pos = pos;
		this.pathFE = 0;
	}

	public static Robot read(FastReader in) {

		int x = in.nextInt();
        int y = in.nextInt();
        char[] direction = in.nextChars();

        Robot r = new Robot(Pos.get(x, y, direction[0]));
        r._pos = r.pos;
		return r;
	}

	public void restore() {
		this.pos = _pos;
		this.pathFE = 0;
	}

	static int grid[] = new int[19*10*4]; // to check if we already saw this position
	static int simNumber = 0;
	
	public void calculatePath(State state) {
		this.restore();
		this.incrementSim();
		
		do {
			path[pathFE++] = this.pos; // remember the path
			move(state);
		} while	(pos != Pos.VOID );
		
	}
	
	
	public void move(State state) {
		this.pos = this.pos.move();
		this.pos = pos.update(state.cells[pos.x][pos.y]);
		
		if (grid[pos.offset] == simNumber) { 
			pos = Pos.VOID;
		} else { 
			grid[pos.offset] = simNumber; // remember robot get there with the direction
			if (state.cells[pos.x][pos.y]== State.VOID ) this.pos = Pos.VOID;
		}
	}

	public void incrementSim() {
		simNumber++;
	}
	
	public int score() {
		return pathFE; // score is the length of the path
	}

	public void undoMoveTo(Pos initialPos) {
		if (pos != Pos.VOID) {
			grid[pos.offset] = 0;
		}
		this.pos = initialPos;
	}

}