package othello;


import java.util.ArrayList;
import java.util.List;

public class Pos {
  public static final int WIDTH = 8;
  public static final int HEIGHT = 8;
  
  public static Pos VOID = new Pos(-1, -1);
  private static Pos[] positions = new Pos[WIDTH * HEIGHT];

  static {
    for (int y=0;y<HEIGHT;y++) {
      for (int x=0;x<WIDTH;x++) {
        positions[x+WIDTH*y] = new Pos(x,y);
      }
    }

    //initCardinalNeighbors();
    init8Neighbors();
    
  }
  
  public final int x;
  public final int y;
  public final int offset;
  
  private Pos(int x, int y) {
    this.x = x;
    this.y = y;
    this.offset = x+WIDTH*y;
  }

  /* Cardinal neighbors are 4 in the cardinal direction */
  public List<Pos> neighbors = new ArrayList<>();
  private static void initCardinalNeighbors() {
    for (int y=0;y<HEIGHT;y++) {
      for (int x=0;x<WIDTH;x++) {
        Pos p = positions[x+WIDTH*y];
        if (x>0) p.neighbors.add(Pos.from(x-1, y)); 
        if (y>0) p.neighbors.add(Pos.from(x, y-1)); 
        if (x<WIDTH-1) p.neighbors.add(Pos.from(x+1, y)); 
        if (y<HEIGHT-1) p.neighbors.add(Pos.from(x, y+1)); 
      }
    }
  }
  /* */
  
  /* 8 neighbors, in all direction */
  public List<Pos> neighbors8 = new ArrayList<>();
  public static void init8Neighbors() {
    for (int sy=0;sy<HEIGHT;sy++) {
      for (int sx=0;sx<WIDTH;sx++) {
        Pos p = positions[sx+WIDTH*sy];
        
        for (int dy=-1;dy<=1;dy++) {
          for (int dx=-1;dx<=1;dx++) {
            if (dx == 0 && dy == 0) continue;
            int x = sx + dx;
            int y = sy + dy;
            Pos o;
            if ( (o = Pos.secureFrom(x, y)) != VOID) {
              p.neighbors8.add(o);
            }
          }
        }
      }
    }
  }
  /**/
  
  public static Pos secureFrom(int x, int y) {
    if (x < 0 || x>=WIDTH) return VOID;
    if (y < 0 || y>=HEIGHT) return VOID;
    return from(x,y);
  }

  public static Pos from(int x, int y) {
    return positions[x+WIDTH*y];
  }
  
  public static Pos from(int offset) {
    return positions[offset];
  }
  
  @Override
  public String toString() {
    return String.format("(%d, %d)", x, y);
  }

}
