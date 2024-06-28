package fall2022.search.goals;

import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.search.Goal;

/**
 * At least X units 
 *
 */
public class AtLeast extends Goal {
	int units;
	

	public AtLeast(int units, double scoreSuccess, double scoreFailure) {
		super(scoreSuccess, scoreFailure);
		this.units = units;
	}

	@Override
	public double score(State state, Pos pos, int owner, int units, boolean recycler) {
		if (owner == O.ME && units >= this.units) {
			return scoreSuccess;
		} else {
			return scoreFailure;
		}
	}
	
	@Override
	public String toString() {
		return "AtLeast("+units+","+scoreSuccess+","+scoreFailure+")";
	}

}
