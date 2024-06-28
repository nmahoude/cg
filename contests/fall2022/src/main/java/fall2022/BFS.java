package fall2022;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class BFS {
	public int distances[] = new int[Pos.MAX_OFFSET];
	public List<Pos> allMyUnits = new ArrayList<>();

	public void calculate(State state) {
		List<Pos> init = new ArrayList<>();
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				Pos p = Pos.from(x, y);
				if (state.o[p.o] == O.ME && state.u[p.o] > 0) {
					init.add(Pos.from(x, y));
				}
			}
		}

		calculate(state, init, null);
	}

	public List<Pos> reconstructPath2(State state, TimeTraveler tt, Pos from, Pos to, DoubleFromPos eval) {
			List<Pos> init = new ArrayList<>();
			init.add(from);
			
			calculate(state, init, tt);
			int distance = distances[to.o];
			if (distance == Integer.MAX_VALUE) {
				Logger.error("No path from "+from+" "+to);
				return Collections.emptyList();
			}

			bestPath = new ArrayList<>();
			bestScore = Double.NEGATIVE_INFINITY;
			reconstructFrom(state, new ArrayList<>(), to, from, distance-1 ,0, eval);
			
			return bestPath;
	}
	
	
	List<Pos> bestPath = new ArrayList<>();
	double bestScore;
	private void reconstructFrom(State state, List<Pos> path, Pos current, Pos target, int distToFind, double score, DoubleFromPos eval) {
		
		
		for (Pos n : current.neighbors4dirs) {
			if (distances[n.o] != distToFind)  continue;
			path.add(0, current);

			if (n == target) {
				if (score > bestScore) {
					bestPath.clear();
					bestPath.addAll(path);
					bestScore =score;
				}
				return;
			}
			
			double localScore = eval.eval(n);
			
			reconstructFrom(state, path, n, target, distToFind-1, score + localScore, eval);
			path.remove(current);
		}
	}


	public void calculate(State state, Pos from, TimeTraveler tt) {
		calculate(state, from , tt , Collections.emptyList());
	}
	public void calculate(State state, Pos from, TimeTraveler tt, List<Pos> forbidenCells) {
		List<Pos> init = new ArrayList<>();
		init.add(from);
		calculate(state, init, Collections.emptyList(), tt, forbidenCells);
	}
	
	public void calculate(State state, List<Pos> init, TimeTraveler tt) {
		calculate(state, init, Collections.emptyList(), tt, Collections.emptyList());
	}
	
	public void calculate(State state, List<Pos> init, List<Pos> delayed, TimeTraveler tt) {
		calculate(state, init, delayed, tt, Collections.emptyList());
	}
	
	
	private static int gridIndex[] = new int[Pos.MAX_OFFSET];
	private static int INDEX = 1;
	public static List<Pos> quickReachableCells(State state, List<Pos> init) {
		INDEX++;
		List<Pos> visited = new ArrayList<>();
		
		List<Pos> next = new ArrayList<>();
		List<Pos> toVisit = new ArrayList<>();
		
		for (Pos i : init) {
			if (state.canMove(i)) {
				toVisit.add(i);
			}
		}
		
		
		while (!toVisit.isEmpty()) {
			next.clear();

			for (Pos p : toVisit) {
				if (gridIndex[p.o] == INDEX) continue;
				gridIndex[p.o] = INDEX;
				visited.add(p);
				
				for (Pos n : p.neighbors4dirs) {
					if (state.canMove(n) && gridIndex[n.o] != INDEX) {
						next.add(n);
					}
				}
			}

			toVisit.clear();
			toVisit.addAll(next);
		}
		
		
		return visited;
	}
	
	
	public void calculate(State state, List<Pos> init, List<Pos> delayed, TimeTraveler tt, List<Pos> forbiddenCells) {
		allMyUnits.clear();

		resetDistances();
		
		// add init positions
		allMyUnits.addAll(init);
		for (Pos pos : init) {
			distances[pos.o] = 0;
		}
		for (Pos pos : delayed) {
			distances[pos.o] = 1;
		}

		List<Pos> current = new ArrayList<>();
		current.addAll(init);
		current.addAll(delayed);
		
		List<Pos> next = new ArrayList<>();

		while (!current.isEmpty()) {
			next.clear();
			
			for (Pos p : current) {
				if (forbiddenCells.contains(p) && !init.contains(p)) continue; // can't go through
				int currentDist = distances[p.o];
				for (Pos n : p.neighbors4dirs) {
					if (tt != null) {
						if (tt.sliceAt(currentDist+1).state.canMove(n) && distances[n.o] > currentDist + 1) {
							distances[n.o] = currentDist + 1;
							next.add(n);
						}
						
					} else {
						if (state.canMove(n) && distances[n.o] > currentDist + 1) {
							distances[n.o] = currentDist + 1;
							next.add(n);
						}
					}
				}
			}
			
			List<Pos> swp = current;
			current = next;
			next = swp;
		}

		//printGrid();

	}

	private void resetDistances() {
		for (int y = 0; y < State.HEIGHT; y++) {
			for (int x = 0; x < State.WIDTH; x++) {
				Pos p = Pos.from(x, y);
				distances[p.o] = Integer.MAX_VALUE;
			}
		}
	}


	public Pos findClosest(Predicate<Pos> filter) {
		int minDist = Integer.MAX_VALUE;
		Pos best = null;
		for (Pos p : Pos.allMapPositions) {
			if (!filter.test(p)) continue;
			
			if (distances[p.o] < minDist) {
				minDist = distances[p.o];
				best = p;
			}
		}
		return best;
	}

}
