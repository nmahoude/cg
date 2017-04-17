package cotc.utils;

public class Coord {
  private static final int MAP_WIDTH = 23;
  private static final int MAP_HEIGHT = 21;

  private final static int[][] DIRECTIONS_EVEN = new int[][] { { 1, 0 }, { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 } };
  private final static int[][] DIRECTIONS_ODD = new int[][] { { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, 0 }, { 0, 1 }, { 1, 1 } };
  
  private static Coord[] cache;
  private static int distanceCache[][];
  
  static {
    cache = new Coord[50*50];
    distanceCache = new int[50*50][50*50];
    
    for (int x=-10;x<40;x++) {
      for (int y=-10;y<40;y++) {
        cache[x+10 + (y+10)*50] = new Coord(x,y);
        cache[x+10 + (y+10)*50].cubeCoordinateCache = preCalculateCubeCoordinate(x,y);
      }
    }

    for (int x=-10;x<40;x++) {
      for (int y=-10;y<40;y++) {
        for (int x2=-10;x2<40;x2++) {
          for (int y2=-10;y2<40;y2++) {
            int dist = cache[x+10 + (y+10)*50].toCubeCoordinate().distanceTo(cache[x2+10 + (y2+10)*50].toCubeCoordinate());
            distanceCache[x+10 + (y+10)*50][x2+10 + (y2+10)*50] =dist;  
          }
        }
      }
    }
    for (int x=-5;x<MAP_WIDTH+5;x++) {
      for (int y=-5;y<MAP_HEIGHT+5;y++) {
        Coord coord = get(x,y);
        for (int i=0;i<6;i++) {
          coord.neighborsCache[i] = precalculateNeighbor(x, y, i);
        }
      }
    }
  }
  
  public static Coord get(int x, int y) {
    return cache[x+10 + (y+10)*50];
  }
  
  private static CubeCoordinate preCalculateCubeCoordinate(int x, int y) {
    int xp = x - (y - (y & 1)) / 2;
    int zp = y;
    int yp = -(xp + zp);
    return new CubeCoordinate(xp, yp, zp);
  }

  public final int x;
  public final int y;
  public Coord[] neighborsCache = new Coord[6];
  private CubeCoordinate cubeCoordinateCache;
  
  private Coord(int x, int y) {
      this.x = x;
      this.y = y;
  }

  private Coord(Coord other) {
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
    return cubeCoordinateCache;
  }

  private static Coord precalculateNeighbor(int x, int y, int orientation) {
      int newY, newX;
      if (y % 2 == 1) {
          newY = y + DIRECTIONS_ODD[orientation][1];
          newX = x + DIRECTIONS_ODD[orientation][0];
      } else {
          newY = y + DIRECTIONS_EVEN[orientation][1];
          newX = x + DIRECTIONS_EVEN[orientation][0];
      }

      return Coord.get(newX, newY);
  }

  public Coord neighbor(int orientation) {
    return neighborsCache[orientation];
  }
  public boolean isInsideMap() {
      return x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT;
  }

  public int distanceTo(Coord dst) {
    return distanceCache[x+10 + (y+10)*50][dst.x+10 + (dst.y+10)*50];
  }

  public boolean equals(Object obj) {
    // Speed hack
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
