package numbershifting;

public class Pos {
  public static final Pos WALL = new Pos(-1000, -1000);
  private static int width;
  private static int heigth;
  public static void setDimension(int width, int heigth) {
    Pos.width = width;
    Pos.heigth = heigth;
  }
  private static Pos positions[] = new Pos[1000*1000];
  static {
    for (int y=0;y<1000;y++) {
      for (int x=0;x<1000;x++) {
        positions[x+1000*y] = new Pos(x,y);
      }
    }
  }
  public final int x;
  public final int y;
  public final int offset;
  
  public Pos(int x, int y) {
    this.x = x;
    this.y = y;
    this.offset = x + 1000 * y;
  }
  
  public static Pos from(int x, int y) {
    if (x < 0 || x>=width) return WALL;
    if (y<0 || y>=heigth) return WALL;
    
    return positions[x+1000*y];
  }
  public static Pos from (int offset) {
    return positions[offset];
  }
}
