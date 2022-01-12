package xmashrush2.ai;

public class Cell {
	public final static int UP = 0;
	public final static int RIGHT = 1;
	public final static int DOWN = 2;
	public final static int LEFT = 3;
	
	public static final int DIR_UP = 0b0001;
	public static final int DIR_RIGHT = 0b0010;
	public static final int DIR_DOWN = 0b0100;
	public static final int DIR_LEFT = 0b1000;
	
	public static int exits(int mask) {
		return Integer.bitCount(mask);
	}

	public static int tileToInt(char[] tile) {
		int directions = 0;
		if (tile[UP] == '1')
			directions |= DIR_UP;
		if (tile[RIGHT] == '1')
			directions |= DIR_RIGHT;
		if (tile[DOWN] == '1')
			directions |= DIR_DOWN;
		if (tile[LEFT] == '1')
			directions |= DIR_LEFT;

		return directions;
	}
}
