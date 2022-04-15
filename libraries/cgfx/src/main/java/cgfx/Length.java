package cgfx;

import java.util.Objects;

public class Length {
  public static final Length NO = Length.of(0, 0);
  public final int dx;
  public final int dy;
  
  public Length(int x, int y) {
    this.dx = x;
    this.dy = y;
  }
  
  public static Length of(int x, int y) {
    return new Length(x,y);
  }

  public static Length square(int both) {
    return new Length(both, both);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dx, dy);
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
    return dx == other.x && dy == other.y;
  }
}
