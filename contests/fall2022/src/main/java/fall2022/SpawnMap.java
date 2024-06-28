package fall2022;

public class SpawnMap extends DistanceMap {
	
	public SpawnMap(int targetOwner) {
		super(targetOwner);
	}
	
	boolean isHearth(State state, Pos p) {
		return state.o[p.o] == targetOwner;
	}

	double initValue(Pos p) {
		if (state.o[p.o] == targetOwner) {
			return 1;
		} else {
			return Integer.MAX_VALUE;
		}
	}

	double diffuse(double d) {
		return d + 1;
	}

	boolean needToDiffuse(State state, Pos from, Pos to, double oldValue, double newValue) {
		return newValue < oldValue;
	}

}
