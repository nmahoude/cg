package fall2022.search.goals;

import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.search.Goal;

/**
 * Cell is empty
 *
 */
public class Empty extends Goal {
	
	public Empty(double scoreSuccess, double scoreFailure) {
		super(scoreSuccess, scoreFailure);
	}
	
	@Override
	public double score(State state, Pos pos, int owner, int units, boolean recycler) {
		if (owner == O.ME && units == 0) {
			return scoreSuccess;
		} else {
			return scoreFailure;
		}
	}
	
	@Override
	public String toString() {
		return "Empty("+scoreSuccess+","+scoreFailure+")";
	}

}
