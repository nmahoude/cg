package cotc.utils;

public class Coord {
  
  private static final int MAP_WIDTH = 23;
  private static final int MAP_HEIGHT = 21;

  private final static int[][] DIRECTIONS_EVEN = new int[][] { { 1, 0 }, { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 } };
  private final static int[][] DIRECTIONS_ODD = new int[][] { { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, 0 }, { 0, 1 }, { 1, 1 } };
  public final int x;
  public final int y;

  public Coord(int x, int y) {
      this.x = x;
      this.y = y;
  }

  public Coord(Coord other) {
      this.x = other.x;
      this.y = other.y;
  }

  public double angle(Coord targetPosition) {
      double dy = (targetPosition.y - this.y) * Math.sqrt(3) / 2;
      double dx = targetPosition.x - this.x + ((this.y - targetPosition.y) & 1) * 0.5;
      double angle = -Math.atan2(dy, dx) * 3 / Math.PI;
      if (angle < 0) {
          angle += 6;
      } else if (angle >= 6) {
          angle -= 6;
      }
      return angle;
  }

  public CubeCoordinate toCubeCoordinate() {
      int xp = x - (y - (y & 1)) / 2;
      int zp = y;
      int yp = -(xp + zp);
      return new CubeCoordinate(xp, yp, zp);
  }

  public Coord neighbor(int orientation) {
      int newY, newX;
      if (this.y % 2 == 1) {
          newY = this.y + DIRECTIONS_ODD[orientation][1];
          newX = this.x + DIRECTIONS_ODD[orientation][0];
      } else {
          newY = this.y + DIRECTIONS_EVEN[orientation][1];
          newX = this.x + DIRECTIONS_EVEN[orientation][0];
      }

      return new Coord(newX, newY);
  }

  public boolean isInsideMap() {
      return x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT;
  }

  public int distanceTo(Coord dst) {
      return this.toCubeCoordinate().distanceTo(dst.toCubeCoordinate());
  }

  @Override
  public boolean equals(Object obj) {
    // SPeed hack
    //      if (obj == null || getClass() != obj.getClass()) {
    //          return false;
    //      }
      Coord other = (Coord) obj;
      return y == other.y && x == other.x;
  }

  @Override
  public String toString() {
      return Util.join(x, y);
  }

}
