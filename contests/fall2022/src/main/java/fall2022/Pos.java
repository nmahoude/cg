package fall2022;


import java.util.ArrayList;
import java.util.List;

public class Pos {
  public static int WIDTH = 24;
  public static int HEIGHT = 12;
  public static final int MAX_OFFSET  = WIDTH * HEIGHT;
  
  private static Pos[] positions = new Pos[MAX_OFFSET];

  public static Pos VOID = new Pos(-1, -1);
  public static List<Pos> allMapPositions = new ArrayList<>();
  
  public List<Pos> neighbors4dirs = new ArrayList<>();
	public List<Pos> meAndNeighbors4dirs = new ArrayList<>();
  
  static void init(int W, int H) {
  	WIDTH = W;
  	HEIGHT = H;
  	//MAX_OFFSET = W * H;
  	allMapPositions.clear();
  	
    for (int y=0;y<HEIGHT;y++) {
      for (int x=0;x<WIDTH;x++) {
        Pos pos = new Pos(x,y);
				positions[x+WIDTH*y] = pos;
        allMapPositions.add(pos);
      }
    }

    initCardinalNeighbors();
    init8Neighbors();
    
  }
  
  public final int x;
  public final int y;
  public final int o;
  
  private Pos(int x, int y) {
    this.x = x;
    this.y = y;
    this.o = x+WIDTH*y;
  }

  // 8 neighbors, in all direction
  public List<Pos> neighbors8dirs = new ArrayList<>();
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
              p.neighbors8dirs.add(o);
            }
          }
        }
      }
    }
  }

  
  /* Cardinal neighbors are 4 in the cardinal direction */
  private static void initCardinalNeighbors() {
  	
    for (int y=0;y<HEIGHT;y++) {
      for (int x=0;x<WIDTH;x++) {
        Pos p = positions[x+WIDTH*y];
        p.neighbors4dirs.clear();
        p.meAndNeighbors4dirs.clear();
        
        
        if (x>0) p.neighbors4dirs.add(Pos.from(x-1, y)); 
        if (y>0) p.neighbors4dirs.add(Pos.from(x, y-1)); 
        if (x<WIDTH-1) p.neighbors4dirs.add(Pos.from(x+1, y)); 
        if (y<HEIGHT-1) p.neighbors4dirs.add(Pos.from(x, y+1)); 
        
        
        p.meAndNeighbors4dirs.add(p);
        for (Pos pos : p.neighbors4dirs) {
        	p.meAndNeighbors4dirs.add(pos);
        }
        
      }
    }
    
  }
  /* */
  
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

	public int manhattan(Pos current) {
		return Math.abs(current.x - x) + Math.abs(current.y - y); 
	}

	public boolean isBorder() {
		return x == 0 || y ==0 || x == Pos.WIDTH-1 || y == Pos.HEIGHT-1;
	}

}
