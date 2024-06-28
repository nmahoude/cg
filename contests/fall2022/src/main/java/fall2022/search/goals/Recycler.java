package fall2022.search.goals;

import fall2022.Pos;
import fall2022.State;
import fall2022.search.Goal;

public class Recycler extends Goal {

	public Recycler(double scoreSuccess, double scoreFailure) {
		super(scoreSuccess, scoreFailure);
	}

	@Override
	public double score(State state, Pos pos, int owner, int units, boolean recycler) {
		if (recycler) {
			return scoreSuccess;
		} else {
			return scoreFailure;
		}
	}

	@Override
	public String toString() {
		return "Recycler("+scoreSuccess+","+scoreFailure+")";
	}

}
