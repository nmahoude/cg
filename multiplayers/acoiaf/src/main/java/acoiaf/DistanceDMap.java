package acoiaf;

import java.util.List;

public class DistanceDMap extends DiffusionMap {

	private List<Pos> myCells;

	public void calculate(State state, List<Pos> myCells) {
		this.myCells = myCells;
		super.calculate(state);
	}
	
	@Override
	boolean isHearth(State state, Pos p) {
		return myCells.contains(p);
	}

	@Override
	double initValue(Pos p) {
		if (myCells.contains(p)) {
			return 0.0;
		} else {
			return Double.MAX_VALUE;
		}
	}

	@Override
	double diffuse(double oldValue) {
		return oldValue+1;
	}

	@Override
	boolean needToDiffuse(State state, Pos from, Pos to, double oldValue, double newValue) {
		return oldValue > newValue;
	}

}
