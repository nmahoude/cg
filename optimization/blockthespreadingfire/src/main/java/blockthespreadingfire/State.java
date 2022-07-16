package blockthespreadingfire;

import fast.read.FastReader;

public class State {

	static final char HOUSE = 'X';
	static final char TREE = '.';
	static final char SAFE = '#';
	
	public static int height;
	public static int width;
	public static int treeTreatmentDuration;
	public static int treeFireDuration;
	public static int treeValue;
	public static int houseTreatmentDuration;
	public static int houseFireDuration;
	public static int houseValue;
	public static int fireStartX;
	public static int fireStartY;
	public static Pos fireStart;

	public int[] grid;

	public int cooldown;
	private int totalValue;
	
	public void readGlobal(FastReader in) {
		treeTreatmentDuration = in.nextInt();
		treeFireDuration = in.nextInt();
		treeValue = in.nextInt();
		
		houseTreatmentDuration = in.nextInt();
		houseFireDuration = in.nextInt();
		houseValue = in.nextInt();
		
		width = in.nextInt();
		height = in.nextInt();
		
		fireStartX = in.nextInt();
		fireStartY = in.nextInt();
		fireStart = Pos.from(fireStartX, fireStartY);
		
		totalValue = 0;
		
		grid = new int[51 * 51];
		for (int y = 0; y < height; y++) {
			String gridLine = in.next();
			for (int x=0;x<width;x++) {
				Pos pos = Pos.from(x, y);
				grid[pos.offset] = gridLine.charAt(x);
				if ((x == fireStartX && y == fireStartY) || grid[pos.offset] == SAFE) totalValue+=0;
				else totalValue += (grid[pos.offset] == HOUSE ? houseValue : treeValue);
			}
		}
		System.err.println("Values : H="+houseValue+" T="+treeValue+" total value ="+totalValue);
	}

	public void readTurn(FastReader in) {
		cooldown = in.nextInt();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int fireProgress = in.nextInt(); // state of the fire in this cell (-2: safe, -1: no fire, 0<=.<fireDuration:
			}
		}
	}
}
