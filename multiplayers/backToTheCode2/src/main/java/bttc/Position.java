package bttc;

public class Position {

  static final Position WALL = new Position(-1,-1);
  static final Position ps[] = new Position[35*20];
  static {
    for (int y=0;y<20;y++) {
      for (int x=0;x<35;x++) {
        ps[x+35*y] = new Position(x,y);
      }
    }
  }
  private Position(int x_, int y_) {
    x = x_;
    y = y_;
  }

  public final int x;
  public final int y;

  int distance(Position other) {
    return Math.abs(other.x - x) + Math.abs(other.y - y);
  }

  /**
   * moves to the Position to the given direction (if possible) return true if
   * the Position stays on the board
   */
  Position move(final Direction dir) {
    switch (dir) {
    case Up:
    case UpLeft:
    case UpRight:
      if (y > 0) {
        return ps[x + (y-1)*35];
      } else {
        return WALL;
      }
    case Down:
    case DownLeft:
    case DownRight:
      if (y < 19) {
        return ps[x + (y+1)*35];
      } else {
        return WALL;
      }
    default:
      break;
    }

    switch (dir) {
    case Left:
    case UpLeft:
    case DownLeft:
      if (x > 0) {
        return ps[x-1 + 35 *y];
      } else {
        return WALL;
      }
    case Right:
    case UpRight:
    case DownRight:
      if (x < 34) {
        return ps[x+1 + 35 * y];
      } else {
        return WALL;
      }
    default:
      break;
    }
    return WALL;
  }

  boolean isValid() {
    return y >= 0 && y < 20 && x >= 0 && x < 35;
  }

  Direction direction() {
    if (x == 0) {
      if (y == 0) {
        return Direction.None;
      } else if (y > 0) {
        return Direction.Down;
      } else {
        return Direction.Up;
      }
    } else if (x > 0) {
      if (y == 0) {
        return Direction.Right;
      } else if (y > 0) {
        return Direction.DownRight;
      } else {
        return Direction.UpRight;
      }
    } else {
      if (y == 0) {
        return Direction.Left;
      } else if (y > 0) {
        return Direction.DownLeft;
      } else {
        return Direction.UpLeft;
      }
    }
  }

  Position stepTowards(Position target) {
    if (x == target.x) {
      // same column
      if (y < target.y) {
        return this.move(Direction.Down);
      } else if (y > target.y) {
        return this.move(Direction.Up);
      } else {
        return target;
      }
    } else if (x < target.x) {
      return this.move(Direction.Right);
    } else {
      return this.move(Direction.Left);
    }
  }

  public Position add(Direction direction) {
    return this.move(direction);
  }

  public Direction directionTo(Position pos) {
    if (pos.y == this.y) {
      if (pos.x == this.x)
        return Direction.None;
      else if (pos.x < this.x)
        return Direction.Left;
      else
        return Direction.Right;
    } else {
      if (pos.y < this.y)
        return Direction.Up;
      else
        return Direction.Down;
    }
  }

  public static Position get(int x, int y) {
    return ps[x+35*y];
  }

  static Position topLeft(Position a, Position b) {
    return Position.get(Math.min(a.x, b.x), Math.min(a.y, b.y));
  }

  static Position bottomRight(Position a, Position b) {
    return Position.get(Math.max(a.x, b.x), Math.max(a.y, b.y));
  }

  
  @Override
  public String toString() {
    return "("+x+","+y+")";
  }
}
