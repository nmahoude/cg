package fall2022.ai;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fall2022.BFS;
import fall2022.DoubleFromPos;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.TimeTraveler;

public class Navigator {
	BFS bfs = new BFS();
	
	public List<Pos> navigate(State work, Pos from, Pos to, TimeTraveler tt, List<Pos> frontier) {
		return navigate(work, from, to, tt, Collections.emptyList(), frontier, Collections.emptyList());
	}

	public List<Pos> navigate(State work, Pos from, Pos to, TimeTraveler tt, List<Pos> visitedCells, List<Pos> frontier, List<Pos> forbidenCells) {
		if (from == to) return Arrays.asList(from);
		bfs.calculate(work, from, tt, forbidenCells);
		
		return bfs.reconstructPath2(work, tt, from, to, n -> {
			double localScore = 0.0;
			localScore -= visitedCells.contains(n) ? 2 : 0;
			localScore += work.o[n.o] == O.NEUTRAL ? 1 : 0;
			localScore += (frontier.contains(n) && work.u[n.o] == 0) ? 0.1 : 0;
			return localScore;
		});
	}
	
	public List<Pos> navigate(State work, Pos from, Pos to, TimeTraveler tt, List<Pos> forbidenCells, DoubleFromPos eval) {
		if (from == to) return Arrays.asList(from);
		bfs.calculate(work, from, tt, forbidenCells);
		
		return bfs.reconstructPath2(work, tt, from, to, eval);
	}

}
