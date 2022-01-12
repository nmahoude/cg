package xmashrush2;

public class Pos {
	public static final Pos WALL = new Pos(-1, -1);
	public static final Pos CENTER ;
	static Pos[] positions = new Pos[49];
	public final int x;
	public final int y;
	public final int offset;
	
	static {
		for (int y=0;y<7;y++) {
			for (int x=0;x<7;x++) {
				Pos pos = new Pos(x,y);
				positions[pos.offset] = pos;
			}
		}
		CENTER = Pos.from(3,3);
	}
	
	private Pos(int x, int y) {
		this.x = x;
		this.y = y;
		this.offset = offsetOf(x, y);
	}
	
	@Override
	public String toString() {
		return "("+x+","+y+")";
	}
	
	public static Pos from(int x, int y) {
		return positions[x +7*y];
	}

	public static Pos from(int offset) {
		return positions[offset];
	}

	public Pos getVisitableNeighbor(int dir) {
		switch(dir) {
		case 0: return y > 0 ? Pos.from(x, y-1) : WALL; 
		case 1: return x < 6 ? Pos.from(x+1, y) : WALL; 
		case 2: return y < 6 ? Pos.from(x, y+1) : WALL; 
		case 3: return x > 0 ? Pos.from(x-1, y) : WALL; 
		}
		return null;
	}

	public static int offsetOf(int x, int y) {
		return x + 7*y;
	}
	
	public int manhattan(Pos pos) {
		return Math.abs(x-pos.x) + Math.abs(y-pos.y);
	}

	public static Pos getCircular(Pos pos, Direction dir) {
	    int x = pos.x + dir.dx;
	    int y = pos.y + dir.dy;
	    if (x == -1) x +=7;
	    if (y == -1) y +=7;
	    if (x == 7) x -=7;
	    if (y == 7) y -=7;
	    
	    return Pos.from(x, y);
	  }

	  public Pos applyPushOnPos(PushAction action) {
	    switch(action.dir ) {
	    case UP:
	      if (action.offset == x) {
	        return getCircular(this, action.dir);
	      }
	      break;
	    case RIGHT:
	      if (action.offset == y) {
	        return getCircular(this, action.dir);
	      }
	      break;
	    case DOWN:
	      if (action.offset == x) {
	        return getCircular(this, action.dir);
	      }

	      break;
	    case LEFT:
	      if (action.offset == y) {
	        return getCircular(this, action.dir);
	      }

	      break;
	    default:
	      break;
	    }
	    return this;
	  }

	public String dirFrom(Pos from) {
		if (this.x == from.x) {
			return this.y > from.y ? "DOWN" : "UP";
		} else {
			return this.x > from.x ? "RIGHT" : "LEFT";
		}
	}

	public Pos applyPushOnTile(PushAction action) {
	    switch(action.dir ) {
	    case UP:
	      if (action.offset == x) {
	    	  if (this.y == 0) return WALL; else return Pos.from(x, y-1);
	      }
	      break;
	    case RIGHT:
	      if (action.offset == y) {
	    	  if (this.x == 6) return WALL; else return Pos.from(x+1, y);
	      }
	      break;
	    case DOWN:
	      if (action.offset == x) {
	    	  if (this.y == 6) return WALL; else return Pos.from(x, y+1);
	      }

	      break;
	    case LEFT:
	      if (action.offset == y) {
	    	  if (this.x == 0) return WALL; else return Pos.from(x-1, y);
	      }

	      break;
	    default:
	      break;
	    }
	    return this;
	}

}
