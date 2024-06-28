package cultistwars;

import java.util.ArrayList;
import java.util.List;

public class Pos {
	public static Pos WALL = new Pos(-1, -1);
	
  private static Pos[] positions = new Pos[13*7];

  static {
    for (int y=0;y<7;y++) {
      for (int x=0;x<13;x++) {
        positions[x + 13 * y] = new Pos(x, y);
      }
    }
    
    calculateNears();
    
  }
  public final int x;
  public final int y;
  public final int offset;
  private List<Pos> near = new ArrayList<>();
  
  
  private Pos(int x, int y) {
    this.x = x;
    this.y = y;
    this.offset = x + 13*y;
  }
  
  private static void calculateNears() {
  	System.err.println("Calculate nears");
    for (int y=0;y<7;y++) {
      for (int x=0;x<13;x++) {
      	Pos pos = Pos.get(x, y);
      	
      	int[] dx = { 1, -1, 0, 0};
    		int[] dy = { 0, 0, 1, -1};
    		for (int i=0;i<4;i++) {
    			Pos near = pos.delta(dx[i], dy[i]);
    			if (near != Pos.WALL) pos.near.add(near);
    		}
      	
      }
    }
	}

	public static Pos get(int x, int y) {
    return positions[x + 13 * y];
  }
  public static Pos get(int offset) {
    return positions[offset];
  }
  @Override
  public String toString() {
    return String.format("(%d,%d)", x, y);
  }
  
  public String output() {
    return String.format("%d %d", x, y);
  }

	public Pos delta(int dx, int dy) {
		if (x+dx<0) return WALL;
		if (x+dx>=13) return WALL;
		if (y+dy<0) return WALL;
		if (y+dy>=7) return WALL;
		return get(x+dx, y+dy);
	}

	public int manhattan(Pos pos) {
		return Math.abs(x - pos.x) + Math.abs(y - pos.y);
	}
	
	public static Pos[] allPositions() {
		return positions;
	}

	public List<Pos> near() {
		return this.near;
	}
}
