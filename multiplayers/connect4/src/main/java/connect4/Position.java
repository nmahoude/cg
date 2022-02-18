package connect4;

public class Position {
	static Position positions[] = new Position[1_000_000];
	static int positionsFE = 0;

	static {
		for (int i=0;i<positions.length;i++) {
			positions[i] = new Position();
		}
	}
	
	public static void resetCache() {
		positionsFE = 0;
	}
	
	public static Position getFromCache() {
		return positions[positionsFE++];
	}
	
	
	
	public long mine;
	public long opp;
	public double score;
}
