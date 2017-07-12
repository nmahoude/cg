package ww;

public enum Dir {
  N(0, 0, -1), NE(1, 1, -1), E(2, 1, 0), SE(3, 1, 1), S(4, 0, 1), SW(5, -1, 1), W(6, -1, 0), NW(7 ,-1, -1);

  public static final int LENGTH = 8;
  static final Dir values[] = new Dir[] {N, NE, E, SE, S, SW, W, NW};
  
  
  public final int index;
  public final int dx;
  public final int dy;
  
  public final static Dir[] getValues() {
    return values;
  }
  
  Dir(int index, int dx, int dy) {
    this.index = index;
    this.dx = dx;
    this.dy = dy;
  }

  public Dir[] pushDirections() {
    switch (this) {
    case E:
      return new Dir[] { NE, E, SE };
    case N:
      return new Dir[] { NW, N, NE };
    case NE:
      return new Dir[] { N, NE, E };
    case NW:
      return new Dir[] { N, NW, W };
    case S:
      return new Dir[] { SE, S, SW };
    case SE:
      return new Dir[] { S, SE, E };
    case SW:
      return new Dir[] { W, SW, S };
    case W:
      return new Dir[] { NW, W, SW };
    default:
      return new Dir[] {};
    }
  }

  public Dir[] inversePushDirections() {
    switch (this) {
    case W:
      return new Dir[] { NE, E, SE };
    case S:
      return new Dir[] { NW, N, NE };
    case SW:
      return new Dir[] { N, NE, E };
    case SE:
      return new Dir[] { N, NW, W };
    case N:
      return new Dir[] { SE, S, SW };
    case NW:
      return new Dir[] { S, SE, E };
    case NE:
      return new Dir[] { W, SW, S };
    case E:
      return new Dir[] { NW, W, SW };
    default:
      return new Dir[] {};
    }
  }

  public Dir inverse() {
    return values[(index+4) % 8];
  }
}
