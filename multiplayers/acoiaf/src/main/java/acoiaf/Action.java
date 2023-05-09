package acoiaf;

public class Action {
	private static final Action[] cache = new Action[1000];
	private static int cacheFE = 0;
	static {
		for (int i=0;i<cache.length;i++) {
			cache[i] = new Action();
		}
	}
	
	public static final int WAIT = 0;
	public static final int MOVE = 1;
	public static final int TRAIN = 2;
	public static final int BUILD = 3;
	
	
	int type;
	int info;
	Pos pos;
	Pos from;
	
	private Action() {
	}
	
	
	public int type() { return type; }
	public int info() { return info; }
	public Pos pos() { return pos; }
	
	public static void resetCache() {
		cacheFE = 0;
	}
	
	
	public static Action move(int id, Pos from, Pos to) {
		Action a = cache[cacheFE++];
		a.type = MOVE;
		a.info = id;
		a.from = from;
		a.pos = to;
		return a;
	}
	
	public static Action train(int level, Pos to) {
		Action a = cache[cacheFE++];
		a.type = TRAIN;
		a.info = level;
		a.pos = to;
		return a;
	}
	
	public static Action build(int building, Pos to) {
		Action a = cache[cacheFE++];
		a.type = BUILD;
		a.info = building;
		a.pos = to;
		return a;
	}
	
	public static Action doWait() {
		Action a = cache[cacheFE++];
		a.type = WAIT;
		return a;
	}	
	
	public void debug() {
		System.err.println(debugString());
	}
	
	public String debugString() {
		switch(type) {
			case MOVE: return "MOVE "+info+" "+pos.x+" "+pos.y;
			case TRAIN: return "TRAIN "+info+" "+pos.x+" "+pos.y;
			case BUILD: return "BUILD "+info+" "+pos.x+" "+pos.y;
			default : return "WAIT";
		}
	}
	
	@Override
	public String toString() {
		return debugString();
	}
	
}
