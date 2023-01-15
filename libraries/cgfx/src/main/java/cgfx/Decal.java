package cgfx;

import java.util.Objects;

public class Decal {
  public static final Decal NO = Decal.of(0, 0);
  public final int dx;
  public final int dy;
  
  public Decal(int x, int y) {
    this.dx = x;
    this.dy = y;
  }
  
  public static Decal of(int x, int y) {
    return new Decal(x,y);
  }

  public static Decal square(int both) {
    return new Decal(both, both);
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

	public Decal add(Decal decal) {
		return new Decal(this.dx + decal.dx, this.dy + decal.dy);
	}
}
