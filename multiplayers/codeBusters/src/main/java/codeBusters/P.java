package codeBusters;

public class P {
  public static final P NOWHERE = new P(100_001, 100_002);

	public P(int x, int y) {
    this.x = x;
    this.y = y;
  }
  public final int x;
  public final int y;
  
  public int dist2(P other) {
  	if (this == NOWHERE || other == NOWHERE) return Integer.MAX_VALUE;
    return (x-other.x)*(x-other.x) + (y-other.y)*(y-other.y);
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
  @Override
  public String toString() {
    return " "+x+" "+y+" ";
  }

	public double dist(P position) {
		return Math.sqrt(dist2(position));
	}
}
