package pac.map;

public class Pos {

  public static final Pos INVALID = new Pos(-5, -5, 0);
  public static final int MAX_WIDTH = 35;
  public static final int MAX_HEIGHT = 17;
  public static final int SURFACE = MAX_WIDTH * MAX_HEIGHT;
  
  public static int _d[][] = new int[][] { {0, -1}, {1, 0}, {0, 1}, {-1, 0}};
  public static String dirs[] = new String[] { "N", "E", "S", "W" };
  
  private static Pos pos[] = new Pos[MAX_WIDTH * MAX_HEIGHT];
  static int distances[] = new int[Pos.SURFACE * Pos.SURFACE];

  static {
    // init points
    initPositions();
  }

	private static void initPositions() {
		for (int y=0;y<MAX_HEIGHT;y++) {
      for (int x=0;x<MAX_WIDTH;x++) {
        pos[x + y * MAX_WIDTH] = new Pos(x,y);
      }
    }
	}

  public final int x, y, offset;
  public Pos neighbors[] = new Pos[4];
  
	public int neighborsListFE = 0;
	public Pos[] neighborsList = new Pos[4];
	public boolean deadEnd;
	public long marker; // already visited, mutable for algorithms
  public boolean blocked; // blocked by an ennemy, mutable for algorithms 
  
  private Pos(int x, int y, int offset) {
    this.x = x;
    this.y = y;
    this.offset = offset;
    for (int i=0;i<4;i++) {
      neighbors[i] = Pos.INVALID;
    }
  }
  
  private Pos(int x, int y) {
    this(x, y, x + MAX_WIDTH * y);
  }
  
  @Override
  public String toString() {
    return String.format("(%d,%d)", x, y);
  }
  
  public static Pos get(int x, int y) {
  	if (x <0 || x>=MAX_WIDTH || y<0 || y>=MAX_HEIGHT) return Pos.INVALID;
    return pos[x + y * MAX_WIDTH];
  }

  public int manhattan(Pos pos) {
    return Math.abs(this.x - pos.x) + Math.abs(this.y - pos.y);
  }

	public static Pos getFromOffset(int offset) {
		return pos[offset];
	}

  public int distance(Pos p) {
    return distances[SURFACE * this.offset + p.offset];
  }
  
  public Pos getClosestCellToDist(Pos target) {
    if (this == target) return this;
    
    int bestDist = Integer.MAX_VALUE;
    Pos bestPos = Pos.INVALID;
    
    for (int d=0;d<this.neighborsListFE;d++) {
      Pos next = this.neighborsList[d];
      
      if (next == target) return next;
      if (next.distance(target) < bestDist) {
        bestDist = next.distance(target);
        bestPos = next;
      }
    }
    return bestPos;
  }

}
