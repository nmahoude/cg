package acoiaf;

public class Unit {
	static Unit[] cache = new Unit[1000];
	static int cacheFE = 0;
	
	static {
		for (int i=0;i<cache.length;i++) {
			cache[i] = new Unit();
		}
	}
	
	private Unit() {
	}
	
	public static Unit getUnit(int id) {
		return cache[id];
	}
	
	public int id;
	public int owner;
	public int level;
	public Pos pos;
}
