package fall2022;

import java.util.ArrayList;
import java.util.List;

public class DistanceMap extends DiffusionMap {
	
	public DistanceMap(int targetOwner) {
		super(targetOwner);
	}
	
	boolean isHearth(State state, Pos p) {
		return state.o[p.o] == targetOwner;
	}

	double initValue(Pos p) {
		if (state.o[p.o] == targetOwner) {
			if (state.u[p.o] > 0) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return Integer.MAX_VALUE;
		}
	}

	double diffuse(double d) {
		return d + 1;
	}

	boolean needToDiffuse(State state, Pos from,Pos to, double oldValue, double newValue) {
		return newValue < oldValue;
	}

	
	public Pos reconstructPath2(State state, TimeTraveler tt, Pos from, Pos to, List<Pos> frontier) {
		return reconstructPath2(state, tt, from, to, new ArrayList<>(), frontier);
	}
	
	public Pos reconstructPath2(State state, TimeTraveler tt, Pos from, Pos to, List<Pos> visitedCell, List<Pos> frontier) {
			List<Pos> init = new ArrayList<>();
			init.add(from);
			
			int distance = (int)grid[to.o];
			if (distance == Integer.MAX_VALUE) {
				Logger.error("No path from "+from+" "+to);
				return null;
			}

			bestPos = null;
			bestScore = Double.NEGATIVE_INFINITY;
			reconstructFrom(state, to, from, distance-1 ,0, visitedCell, frontier);
			
			return bestPos;
	}
	
	
	Pos bestPos;
	double bestScore;
	private void reconstructFrom(State state, Pos current, Pos target, int distToFind, double score, List<Pos> visitedCells, List<Pos> frontier) {
		
		
		for (Pos n : current.neighbors4dirs) {
			if ((int)grid[n.o] != distToFind)  continue;

			if (n == target) {
				if (score > bestScore) {
					bestScore =score;
					bestPos = current;
				}
				return;
			}
			
			double localScore = 0.0;
			localScore -= visitedCells.contains(n) ? 2 : 0;
			localScore += state.o[n.o] == O.NEUTRAL ? 1 : 0;
			localScore += (frontier.contains(n) && state.u[n.o] == 0) ? 0.1 : 0;
			
			reconstructFrom(state, n, target, distToFind-1, score + localScore, visitedCells, frontier);
		}
	}

	
}
