package acraft;

public class Pos {
	public static final Pos VOID = new Pos(-1,-1,-1) {
		@Override
		public String toString() {
			return "VOID";
		}
	};
	
	private static final Pos positions[] = new Pos[State.WIDTH * State.HEIGHT * 5];
	static {
		for (int dir = 0;dir<5;dir++) {
			for (int y=0;y<State.HEIGHT;y++) {
				for (int x=0;x<State.WIDTH;x++) {
					positions[x + y * State.WIDTH + dir * State.WIDTH*State.HEIGHT] = new Pos(x,y,dir);
				}
			}
		}
		
		
	}
	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	
	public final int x;
	public final int y;
	public final int dir;
	public final int offset;
	public final int posOffset;
	
	private Pos(int x, int y, int dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.offset = x + y * State.WIDTH + dir * State.WIDTH*State.HEIGHT;
		this.posOffset = x + y * State.WIDTH;
	}

	@Override
	public String toString() {
		return String.format("%d %d %s", x, y, dirToLetter());
	}

	public static Pos get(int x, int y) {
		return positions[x + y * State.WIDTH + 4 * State.WIDTH*State.HEIGHT]; // hack to get the correct direction quickly
	}

	public static Pos get(int x, int y, char dir) {
		return positions[x + y * State.WIDTH + 4 * State.WIDTH*State.HEIGHT].update(dir); // hack to get the correct direction quickly
	}

	public static Pos get(int x, int y, int dir) {
		return positions[x + y * State.WIDTH + dir * State.WIDTH*State.HEIGHT];
	}
	
	public Pos update(int newDir) {
		return positions[x + y * State.WIDTH + newDir * State.WIDTH*State.HEIGHT];
	}

	public Pos update(char newDir) {
		switch(newDir) {
		case 'U': return update(UP);
		case 'R': return update(RIGHT);
		case 'D': return update(DOWN);
		case 'L': return update(LEFT);
		default : return this;// nothing
		}
	}
	
	public Pos move() {
		int newX = x;
		int newY = y;
		switch(dir) {
		case UP: if (y == 0) newY = State.HEIGHT-1; else newY--; break;
		case RIGHT: if (x == State.WIDTH-1) newX = 0; else newX++; break; 
		case DOWN: if (x == State.HEIGHT-1) newY = 0; else newY++; break;
		case LEFT: if (x == 0) newX = State.WIDTH-1; else newX--; break;
		default : throw new IllegalArgumentException("not a correct direction  :"+dir);
		}
		
		return positions[newX + newY * State.WIDTH + dir * State.WIDTH*State.HEIGHT];
	}

	public String dirToLetter() {
		switch(dir) {
		case UP: return "U";
		case RIGHT: return "R"; 
		case DOWN: return "D";
		case LEFT: return "L";
		default : throw new IllegalArgumentException("not a correct direction  :"+dir);
		}
	}
}
