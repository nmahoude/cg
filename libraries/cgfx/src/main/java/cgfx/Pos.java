package cgfx;

import java.util.Objects;

public class Pos {
  public final int x;
  public final int y;
  
  public Pos(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public static Pos from(int x, int y) {
    return new Pos(x,y);
  }

  public static Pos from(double x, double y) {
    return from((int)x,(int)y);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Pos other = (Pos) obj;
    return x == other.x && y == other.y;
  }

	public Pos add(int dx, int dy) {
		return from(x+dx, y+dy);
	}
  
  
}
