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
  
  
}
