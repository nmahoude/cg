package ww;

public enum Dir {

  N(0, -1), NE(1, -1), E(1, 0), SE(1, 1), S(0, 1), SW(-1, 1), W(-1, 0), NW(-1, -1);

  public final int dx;
  public final int dy;

  Dir(int dx, int dy) {
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
}
