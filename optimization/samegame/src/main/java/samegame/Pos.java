package samegame;

import java.util.ArrayList;
import java.util.List;

public class Pos {
  private static Pos positions[] = new Pos[15*15];
  static {
    for (int y=0;y<15;y++) {
      for (int x=0;x<15;x++) {
        positions[x+15*y] = new Pos(x,y);
      }
    }
    
    for (int y=0;y<15;y++) {
      for (int x=0;x<15;x++) {
        Pos p = positions[x+15*y];
        if (x>0) p.neighbors.add(Pos.from(x-1, y)); 
        if (y>0) p.neighbors.add(Pos.from(x, y-1)); 
        if (x<14) p.neighbors.add(Pos.from(x+1, y)); 
        if (y<14) p.neighbors.add(Pos.from(x, y+1)); 
      }
    }
    
    
    
  }
  
  public final int x;
  public final int y;
  public final int offset;
  public List<Pos> neighbors = new ArrayList<>();
  
  private Pos(int x, int y) {
    this.x = x;
    this.y = y;
    this.offset = x+15*y;
  }
  
  public static Pos from(int x, int y) {
    return positions[x+15*y];
  }
  
}
