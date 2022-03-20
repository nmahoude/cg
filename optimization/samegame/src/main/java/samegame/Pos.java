package samegame;

public class Pos {
  private static Pos positions[] = new Pos[15*15];
  static {
    for (int y=0;y<15;y++) {
      for (int x=0;x<15;x++) {
        positions[x+15*y] = new Pos(x,y);
      }
    }
  }
  
  public final int x;
  public final int y;
  public final int offset;
  
  private Pos(int x, int y) {
    this.x = x;
    this.y = y;
    this.offset = x+15*y;
  }
  
  public static Pos from(int x, int y) {
    return positions[x+15*y];
  }
  
}
