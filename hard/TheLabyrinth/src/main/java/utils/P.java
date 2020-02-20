package utils;

public class P {
  private static final int MAX_H = 2000;
  private static final int MAX_W = 2000;
  static P[][] ps = new P[MAX_W][MAX_H]; // maximum board
  static {
    for (int x=0;x<MAX_W;x++) {
      for (int y=0;y<MAX_H;y++) {
        ps[x][y] = new P(x,y);
      }
    }
  }
  public static P get(int x,int y) {
    if (x<0 || y<0) {
      return ps[MAX_W-1][MAX_H-1];
    }
    return ps[x][y];
  }
  
  
  final public int x;
  final public int y;

  public P(int x, int y) {
    super();
    this.x = x;
    this.y = y;
  }

  public int squareDistance(P p) {
    return (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
  }

  int manhattanDistance(P p) {
    return Math.abs(x - p.x) + Math.abs(y - p.y);
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 113;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    P other = (P) obj;
    return x == other.x && y == other.y;
  }
}

