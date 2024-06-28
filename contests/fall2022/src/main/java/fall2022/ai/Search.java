package fall2022.ai;

import java.util.ArrayList;
import java.util.List;

import fall2022.Action;
import fall2022.O;
import fall2022.Pos;
import fall2022.State;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;
import fall2022.ai.ai2.AI2;
import fall2022.ai.ai2.Defense;
import fall2022.ai.ai2.LowRecyclers;

public class Search {
	
	public static int MAX_SEARCH_TIME = 30;
	
	private final int SPAWN_COST = 2;
	private int RECYCLER_COST = 5;
	private final int MOVE_COST = 1;

	public int goalNeeded[] = new int[Pos.MAX_OFFSET];

	int spawnOrRecyclerNeeded[] = new int[Pos.MAX_OFFSET];
	int moveFrom[] = new int[Pos.MAX_OFFSET * Pos.MAX_OFFSET];

	private double bestScore = Double.NEGATIVE_INFINITY;

	int bestSpawnOrRecyclerNeeded[] = new int[Pos.MAX_OFFSET];
	int bestMoveFrom[] = new int[Pos.MAX_OFFSET * Pos.MAX_OFFSET];

	public List<Action> actions = new ArrayList<>();

	public int FOUND;

	private double[] da;

	private List<Pos> frontier;
	boolean canRecycler = true;
	private long start;
	private boolean stopSearch;
	
	public void search(State stateReadOnly, TimeTraveler tt, List<Ilot> ilots, List<Pos> frontier, double[] danger) {
		this.frontier = frontier;
		
		boolean allZero = true;
		for (Pos f : frontier) {
			if (goalNeeded[f.o] != 0) {
				allZero = false;
				break;
			}
		}
		if (allZero) {
			System.err.println("No search");
			return;
		}
		
		this.da = danger;
		State work = new State();
		work.copyFrom(stateReadOnly);
		
		start = System.currentTimeMillis();
		stopSearch = false;
		
		canRecycler = AI2.fullFrontier.size() != 1;
		if (Territory.myTerritory <= Territory.oppTerritory) {
			RECYCLER_COST = 8;
		} else if (Defense.unitsNeededInTime(work, ilots) <= 0) {
			RECYCLER_COST = 4;
		} else {
			RECYCLER_COST = 1;
		}
		
		FOUND = 0;
		bestScore = Double.NEGATIVE_INFINITY;
		
		debugGoal();
		
		search(work, 0, 0, 0);
		System.err.println("Search time : " + (System.currentTimeMillis() - start) + " ms for " + FOUND + " found");
		if (bestScore != Double.NEGATIVE_INFINITY) {
			System.err.println("Found a solution in " + bestScore);
			reconstructActions(frontier);
		}
	}

	private void debugGoal() {
		System.err.println("Goals :");
		for (Pos p : frontier) {
			if (goalNeeded[p.o] > 0) {
				System.err.println("" + p + " = " + goalNeeded[p.o]);
			}
		}
	}

	private void reconstructActions(List<Pos> frontier) {
		actions.clear();
		for (Pos p : frontier) {
			if (bestSpawnOrRecyclerNeeded[p.o] == -1) {
				actions.add(Action.build(p, this.getClass().getSimpleName()));
			} else if (bestSpawnOrRecyclerNeeded[p.o] > 0) {
				actions.add(Action.spawn(bestSpawnOrRecyclerNeeded[p.o], p, this.getClass().getSimpleName()));
			}
			for (Pos n : p.neighbors4dirs) {
				if (bestMoveFrom[n.o + Pos.MAX_OFFSET * p.o] > 0) {
					actions.add(Action.move(bestMoveFrom[n.o + Pos.MAX_OFFSET * p.o], n, p, this.getClass().getSimpleName()));
				}
			}
		}
	}

	private double eval(State work, int totalCost) {
		double score = 0.0;
		score -= 0.01 * totalCost;
		for (Pos p : frontier) {
			if (goalNeeded[p.o] == 0)	continue;
			
			if (spawnOrRecyclerNeeded[p.o] == -1) {
				score += 0.1 * LowRecyclers.ratio[p.o]; // put on best ratio !
			} else {
			}
			
			
			if (work.rec[p.o] == 0 && work.u[p.o] < goalNeeded[p.o]) {
				if (work.isMine(p)) {
						score -= 500;
						score -= 0.001 * da[p.o];
				}
				if (work.isOpp(p)) {
					score -= 0.001 * da[p.o];
				} else {
					score -= da[p.o];
				}
			} else {
				score += 1;
				score += 0.001 * da[p.o];
			}
		}
		return score;
	}

	private void search(State work, int currentIndex, int neighborIndex, int totalCost) {
		
		if (currentIndex == frontier.size()) {
			FOUND++;
			double score = eval(work, totalCost);
			if (score > bestScore) {
				bestScore = score;
				System.arraycopy(spawnOrRecyclerNeeded, 0, bestSpawnOrRecyclerNeeded, 0, Pos.MAX_OFFSET);
				System.arraycopy(moveFrom, 0, bestMoveFrom, 0, Pos.MAX_OFFSET * Pos.MAX_OFFSET);
			}
			
			if (System.currentTimeMillis() -start > MAX_SEARCH_TIME) stopSearch = true;
			
			return;
		}

		if (stopSearch) return;

		
		
		Pos current = frontier.get(currentIndex);
		if (goalNeeded[current.o] == 0) {
			search(work, currentIndex + 1, 0, totalCost);
			return;
		}
		if (neighborIndex == current.neighbors4dirs.size()) {
			if (canRecycler && work.myMatter >= O.COST && work.canBuild(current)) {
				work.rec[current.o] = 1;
				spawnOrRecyclerNeeded[current.o] = -1;
				work.myMatter -= O.COST;
				search(work, currentIndex + 1, 0, totalCost + RECYCLER_COST);
				work.rec[current.o] = 0;
				spawnOrRecyclerNeeded[current.o] = 0;
				work.myMatter += O.COST;
			}
			
			int s = Math.min(work.myMatter / O.COST, Math.max(0, goalNeeded[current.o] - work.u[current.o]));
			if (work.canSpawn(current) && s > 0) {
				work.u[current.o] += s;
				work.myMatter -= s * O.COST;
				spawnOrRecyclerNeeded[current.o] = s;
				search(work, currentIndex + 1, 0, totalCost + s * SPAWN_COST);
				work.u[current.o] -= s;
				work.myMatter += s * O.COST;
				spawnOrRecyclerNeeded[current.o] = 0;
			}
			search(work, currentIndex + 1, 0, totalCost);
			return;
		}
		Pos n = current.neighbors4dirs.get(neighborIndex);
		search(work, currentIndex, neighborIndex + 1, totalCost);
		if (work.s[n.o] > 0 && work.movableUnits(n) > 0) {
			if (work.o[n.o] == O.ME) {
				if (moveFrom[current.o + Pos.MAX_OFFSET * n.o] != 0) {
				} else {
					for (int i = 1; i <= work.movableUnits(n); i++) {
						work.mu[n.o] -= i;
						work.u[n.o] -= i;
						work.u[current.o] += i;
						moveFrom[n.o + Pos.MAX_OFFSET * current.o] += i;
						search(work, currentIndex, neighborIndex + 1, totalCost + i * MOVE_COST);
						work.mu[n.o] += i;
						work.u[n.o] += i;
						work.u[current.o] -= i;
						moveFrom[n.o + Pos.MAX_OFFSET * current.o] -= i;
					}
				}
			}
		}
	}
	
}
