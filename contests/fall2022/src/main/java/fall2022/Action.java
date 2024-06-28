package fall2022;

import java.util.Objects;

public class Action {
	private static Action[] cache = new Action[1000];
	static {
		for (int i=0;i<cache.length;i++) {
			cache[i] = new Action();
		}
	}
	private static int cacheFE = 0;
	
	public static final int WAIT = 0;
	public static final int MOVE = 1;
	public static final int BUILD = 2;
	public static final int SPAWN = 3;
	
	int type;
	int amount;
	Pos from;
	Pos to;
	public Pos realTarget;
	private String origin;
	
	static void resetCache() {
		cacheFE = 0;
	}
	
	@Override
	public String toString() {
		switch(type) {
			case MOVE: return "MOVE "+amount+" "+from.x+" "+from.y+" "+to.x+" "+to.y+";";
			case WAIT: return "WAIT;";
			case BUILD: return "BUILD "+to.x+" "+to.y+";";
			case SPAWN:return "SPAWN "+amount+" "+to.x+" "+to.y+";";
			default : return "Unknown type "+type;
		}
	}

	public void debug() {
		System.out.println(debugString());
	}

	public String debugString() {
		if (type == MOVE || type == SPAWN) 
			return toString()+" (real="+realTarget+") ["+origin+"]";
		else 
			return toString()+"["+origin+"]";
	}

	public static Action move(int amount, Pos from, Pos to, String origin) {
		return move(amount, from, to, to, origin);
	}
	
	public static Action move(int amount, Pos from, Pos to, Pos realTarget, String origin) {
		Action a = cache[cacheFE++];
		a.type = MOVE;
		a.amount = amount;
		a.from = from;
		a.to = to;
		a.realTarget = realTarget;
		a.origin = origin;
		
		return a;		
	}

	public static Action spawn(int amount, Pos pos, Pos target, String origin) {
		Action a = cache[cacheFE++];
		a.type = SPAWN;
		a.amount = amount;
		a.from = pos;
		a.to = pos;
		a.origin = origin;
		a.realTarget = target;
		return a;
	}

	
	public static Action spawn(int amount, Pos pos, String origin) {
		return spawn(amount, pos, pos, origin);
	}

	public static Action build(Pos pos, String origin) {
		Action a = cache[cacheFE++];
		a.amount = 1;
		a.type = BUILD;
		a.to = pos;
		a.origin = origin;
		return a;
	}

	public Pos to() {
		return to;
	}

	public Pos from() {
		return from;
	}

	public static Action copyOf(Action action) {
		Action a = new Action();
		a.type = action.type;
		a.amount = action.amount;
		a.from = action.from;
		a.to = action.to;
		a.realTarget = action.realTarget;
		a.origin = action.origin;
		return a;
	}

	public int type() {
		return type;
	}

	public int amount() {
		return amount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(amount, from, realTarget, to, type);
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}
}
