package xmashrush2;

public enum Direction {
  UP(7*0, 0, -1),
  RIGHT(7*1, 1, 0),
  DOWN(7*2, 0, 1),
  LEFT(7*3, -1, 0);
  
  static {
    UP.inverse = DOWN;
    RIGHT.inverse = LEFT;
    DOWN.inverse = UP;
    LEFT.inverse = RIGHT;
  }

  Direction inverse;
  public final int dx, dy;
  public final int offset;
  
  private Direction(int offset, int dx, int dy) {
    this.offset = offset;
    this.dx = dx;
    this.dy = dy;
  }

	boolean isSameKind(Direction dir) {
		return (this.isColumn() && dir.isColumn()) || (this.isRow() && dir.isRow());
	}

	public boolean isRow() {
		return this == Direction.LEFT || this == Direction.RIGHT;
	}
	public boolean isColumn() {
		return this == Direction.LEFT || this == Direction.RIGHT;
	}
}
