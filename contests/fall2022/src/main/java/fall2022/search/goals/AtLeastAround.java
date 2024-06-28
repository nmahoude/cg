package fall2022.search.goals;

import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.search.Goal;

public class AtLeastAround extends Goal{

	int units;
	
	public AtLeastAround(int amount, double scoreSuccess, double scoreFailure) {
		super(scoreSuccess, scoreFailure);
		this.units = amount;
	}

	@Override
	public double score(State state, Pos pos, int owner, int units, boolean recycler) {
		int count = 0;
		for (Pos n : pos.neighbors4dirs) {
			if (state.canMove(n) && state.o[n.o] == O.ME) count += state.u[n.o];
		}
		if (count >= units) {
			return scoreSuccess;
		} else {
			return scoreFailure;
		}
	}

	@Override
	public String toString() {
		return "AtLeastAound("+units+","+scoreSuccess+","+scoreFailure+")";
	}

}
