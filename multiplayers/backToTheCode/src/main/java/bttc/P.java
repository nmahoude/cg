package bttc;

public class P {
  public P(int i, int j) {
    x = i;
    y = j;
  }
  public int x;
  public int y;
  
  
  public int manhattanDistance(P p) {
    return Math.abs(p.x-x)+Math.abs(p.y-y);
  }

  public int manhattanDistance(int x2, int y2) {
    return Math.abs(x2-x)+Math.abs(y2-y);
  }

  @Override
  public String toString() {
    return "("+x+","+y+")";
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
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    P other = (P) obj;
    if (x != other.x)
      return false;
    if (y != other.y)
      return false;
    return true;
  }
  
}
