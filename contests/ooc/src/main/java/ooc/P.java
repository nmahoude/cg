package ooc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class P {

  public static final P I = new P(-5, -5, 0);
  public static int _d[][] = new int[][] { {0, -1}, {1, 0}, {0, 1}, {-1, 0}};
  public static String dirs[] = new String[] { "N", "E", "S", "W" };
  
  private static P pos[] = new P[15*15];
  static {
    // init points
    initPositions();
    
    buildNeighbors();
    
  }

	private static void initPositions() {
		for (int y=0;y<15;y++) {
      for (int x=0;x<15;x++) {
        pos[x + y * 15] = new P(x,y);
      }
    }
	}

	private static void buildNeighbors() {
		for (int y=0;y<15;y++) {
      for (int x=0;x<15;x++) {
      	P currentPos = pos[x + y * 15];
      	
      	for (int dy=-1;dy<=1;dy++) {
	      	for (int dx=-1;dx<=1;dx++) {
	      		if (dy == 0 && dx == 0) continue; // center
	      		int newX = x + dx;
	          int newY = y + dy;
	          if (newX < 0 || newX>=15) continue;
	          if (newY < 0 || newY>=15) continue;
	          currentPos.squaredNeighbors.add(pos[newX + newY * 15]);
	      	}
      	}
      	
        for (int d=0;d<4;d++) {
          int newX = x + _d[d][0];
          int newY = y + _d[d][1];
          
					currentPos.neighbors[d] = P.I;
          if (newX < 0 || newX>=15) continue;
          if (newY < 0 || newY>=15) continue;
          currentPos.neighbors[d] = pos[newX + newY * 15];
          
        }
      }
    }
	}
  
  public final int x, y, sector, o;
  public P neighbors[] = new P[4];
	public List<P> squaredNeighbors = new ArrayList<>();
  
  private P(int x, int y, int offset) {
    this.x = x;
    this.y = y;
    this.sector = offset;
    this.o = x + 15 * y;
  }
  
  private P(int x, int y) {
    this.x = x;
    this.y = y;
    this.sector = x / 5 + 3 * (y / 5) + 1;
    o = x + 15 * y;
  }
  
  @Override
  public String toString() {
    return String.format("(%d, %d)", x, y);
  }
  public static P get(int x, int y) {
  	if (x <0 || x>=15 || y<0 || y>=15) return P.I;
    return pos[x + y * 15];
  }

  public int manhattan(P pos) {
    return Math.abs(this.x - pos.x) + Math.abs(this.y - pos.y);
  }

  public int blastDistance(P pos) {
    return Player.map.blastDistance(this, pos );
  }

	public static List<P> getSector(int searchSector) {
		return Arrays.asList(pos).stream().filter(p -> p.sector == searchSector).collect(Collectors.toList());
	}

	public static P getFromOffset(int offset) {
		return pos[offset];
	}
}
