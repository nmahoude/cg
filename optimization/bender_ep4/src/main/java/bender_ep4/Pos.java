package bender_ep4;

public class Pos {
	private static final int MAX_WIDTH = 21;
	private static final int MAX_HEIGHT = 21;
	
	public static final Pos WALL = new Pos(-1, -1);
	
	private static final Pos[] positions = new Pos[MAX_WIDTH * MAX_HEIGHT];
	static {
		for (int y=0;y<MAX_HEIGHT;y++) {
			for (int x=0;x<MAX_WIDTH;x++) {
				positions[x + MAX_WIDTH * y ] = new Pos(x, y);
			}
		}
	}
	public final int x;
	public final int y;
	public final int offset;
	
	public Pos(int x, int y) {
		this.x = x;
		this.y = y;
		this.offset = x + MAX_WIDTH *y;
	}

	@Override
	public String toString() {
		return "("+x+","+y+")";
	}
	
	public static Pos from(int x, int y) {
		return positions[x + MAX_WIDTH * y ];
	}

	public int manhattanDist(Pos target) {
		return Math.abs(target.x - this.x) + Math.abs(target.y + this.y); // TODO cache for perf ?
	}

	public static Pos fromOffset(int offset) {
		return positions[offset];
	}

	/*
	 * you better be sure it remains in the acceptable positions
	 */
	public Pos decal(int dx, int dy) {
		return positions[ (x+dx) + MAX_WIDTH * (y+dy) ];
	}
}
