package hypersonic.utils;

public class P {
  static P[][] ps = new P[20][20]; // maximum board
  static {
    for (int x=0;x<20;x++) {
      for (int y=0;y<20;y++) {
        ps[x][y] = new P(x,y);
      }
    }
  }
  public static P get(final int x,final int y) {
    return ps[x][y];
  }
  
  
  final public int x;
  final public int y;

  public P(final int x, final int y) {
    super();
    this.x = x;
    this.y = y;
  }

  public int squareDistance(final P p) {
    return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
  }

  public int manhattanDistance(final P p) {
    return Math.abs(x - p.x) + Math.abs(y - p.y);
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final P other = (P) obj;
    return x == other.x && y == other.y;
  }
}

