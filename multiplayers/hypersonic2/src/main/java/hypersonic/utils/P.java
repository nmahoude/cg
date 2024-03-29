package hypersonic.utils;

import hypersonic.Board;
import hypersonic.Move;

public class P {
  static P[][] ps = new P[20][20]; // maximum board
  static {
    for (int x=0;x<20;x++) {
      for (int y=0;y<20;y++) {
        ps[x][y] = new P(x,y);
      }
    }
  }
  public static P get(int x,int y) {
    return ps[x][y];
  }
  
  
  final public int x;
  final public int y;
  final public int offset;
  
  private P(final int x, final int y) {
    super();
    this.x = x;
    this.y = y;
    if ((x & 0b1) == 0b1 && (y &0b1) == 0b1) {
      offset = Board.WALL_OFFSET;
    } else {
      if (y % 2 == 0) {
        offset = 20*(y/2) + x; 
      } else {
        offset = 20*(y/2) + 13 + x / 2;
      }
    }
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

  public P move(Move move) {
    return ps[x+move.dx][y+move.dy];
  }
}

