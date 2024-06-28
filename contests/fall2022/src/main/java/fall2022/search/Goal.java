package fall2022.search;

import fall2022.Pos;
import fall2022.State;

public abstract class Goal {
	protected double scoreSuccess;
	protected double scoreFailure;

	public Goal(double scoreSuccess, double scoreFailure) {
		this.scoreSuccess = scoreSuccess;
		this.scoreFailure = scoreFailure;
	}

	public abstract double score(State state, Pos pos, int owner, int units, boolean recycler);
	
}
