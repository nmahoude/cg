package acoiaf;

public class Pos {
	public static Pos VOID = new Pos(-1, -1);
	static Pos positions[] = new Pos[12*12];
	static {
		for (int y=0;y<12;y++) {
			for (int x=0;x<12;x++) {
				positions[x + 12 * y] = new Pos(x,y);
			}
		}
		
		for (int y=0;y<12;y++) {
			for (int x=0;x<12;x++) {
				Pos current = Pos.get(x, y);
				current.neighbors[0] = y > 0 ? Pos.get(x, y-1) : VOID; 
				current.neighbors[1] = x < 11 ? Pos.get(x+1, y) : VOID;
				current.neighbors[2] = y < 11 ? Pos.get(x, y+1) : VOID;
				current.neighbors[3] = x > 0 ? Pos.get(x-1, y) : VOID;
			}
		}
		
		
	}
	
	
	public final int x;
	public final int y;
	public final int offset;
	public final Pos neighbors[] = new Pos[4];
	
	private Pos(int x, int y) {
		this.x = x;
		this.y = y;
		this.offset = x + 12 * y;
	}
	
	public static Pos get(int x, int y) {
		return positions[x+12*y];
	}
	
	public static Pos from(int offset) {
		return positions[offset];
	}
}
