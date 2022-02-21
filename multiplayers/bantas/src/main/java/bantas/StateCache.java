package bantas;

public class StateCache {
	static State states[] = new State[200_000];
	static int statesFE = 0;
	
	static {
		for (int i=0;i<states.length;i++) {
			states[i] = new State();
		}
	}
	public static void reset() {
		statesFE = 0;
	}
	
	public static State get() {
		return states[statesFE++];
	}
}
