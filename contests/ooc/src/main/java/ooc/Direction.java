package ooc;

public enum Direction {
	NORTH(0),
	EAST(1),
	SOUTH(2),
	WEST(3);
	
	public final int direction;

	Direction(int direction) {
		this.direction = direction;
	}
	
	public static Direction inverse(Direction dir) {
		switch (dir) {
		case EAST: return WEST;
		case NORTH: return SOUTH;
		case SOUTH: return NORTH;
		case WEST: return EAST;
		default: return null;
		}
	}
	
	public static Direction from(int value) {
		switch(value) {
		case 0 : return NORTH;
		case 1 : return EAST;
		case 2 : return SOUTH;
		case 3 : return WEST;
		default: return null;
		}
	}
}
