package calm;

import java.util.ArrayList;
import java.util.List;

public class P {
  public static final P INVALID = new P(-1, -1);
  final public int x;
  final public int y;
  final public int offset;
  
  public List<P> squaredNeighbors = new ArrayList<>();
  
  static P ps[] = new P[11*7];
  static {
    for (int y=0;y<7;y++) {
      for (int x=0;x<11;x++) {
        ps[x+y*11] = new P(x,y);
      }
    }
    
    buildNeighbors();
    
  }
  
  private P (int x, int y) {
    this.x = x;
    this.y = y;
    this.offset = x+y*11;
  }
  
  private static void buildNeighbors() {
    int _d[][] = new int[][] { {0, -1}, {1, 0}, {0, 1}, {-1, 0}};
    for (int y=0;y<7;y++) {
      for (int x=0;x<11;x++) {
        P currentPos = ps[x + y * 11];
        
        for (int dy=-1;dy<=1;dy++) {
          for (int dx=-1;dx<=1;dx++) {
            if (dy == 0 && dx == 0) continue; // center
            int newX = x + dx;
            int newY = y + dy;
            if (newX < 0 || newX>=11) continue;
            if (newY < 0 || newY>=7) continue;
            currentPos.squaredNeighbors.add(ps[newX + newY * 11]);
          }
        }
      }
    }
  }

  public static P get(int x, int y) {
    if (x < 0 || x >= 11 || y < 0 || y >= 7) return INVALID;
    return ps[x+y*11];
  }
  
  public static P fromIndex(int index) {
    return ps[index];
  }

  public String output() {
    return x+" "+y;
  }

  public int neighbor(P pos) {
    return Math.max(Math.abs(this.x - pos.x), Math.abs(this.y - pos.y));
  }
  public int manhattan(P pos) {
    return Math.abs(this.x - pos.x)+Math.abs(this.y - pos.y);
  }

  @Override
  public String toString() {
    return "("+x+","+y+")";
  }
}
