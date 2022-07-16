package blockthespreadingfire;

public class Pos {
	private static final Pos poses[] = new Pos[51*51];
	static {
		for (int y=0;y<51;y++) {
			for (int x=0;x<51;x++) {
				poses[x + 51 * y ] = new Pos(x, y);
			}
		}
	}
	public final int x;
	public final int y;
	public final int offset;
	
	public Pos(int x, int y) {
		this.x = x;
		this.y = y;
		this.offset = x + 51 *y;
	}

	@Override
	public String toString() {
		return "("+x+","+y+")";
	}
	public static Pos from(int x, int y) {
		return poses[x + 51 * y ];
	}
}
