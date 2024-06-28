package fall2022.ai.ai2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import fall2022.Action;
import fall2022.BFS;
import fall2022.DistanceMap;
import fall2022.O;
import fall2022.Pos;
import fall2022.SpawnMap;
import fall2022.State;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;
import fall2022.ai.Navigator;

public class Spreader {
	static Navigator navigator = new Navigator();
	public static DistanceMap myDistances = new DistanceMap(O.ME);
	public static SpawnMap mySpawnDistances = new SpawnMap(O.ME);
	
	public static DistanceMap oppDistances = new DistanceMap(O.OPP);
	public static SpawnMap oppSpawnDistances = new SpawnMap(O.OPP);

	public List<Action> think(State originalWork, List<Action> alreadyDoneActions, Ilot ilot, TimeTraveler aiTT) {
		List<Action> actions = new ArrayList<>();

		// need to copy work because we do some weird thing with movable units to block them
		State work = new State();
		work.copyFrom(originalWork);

		for (Action a : alreadyDoneActions) {
			if (a.type() == Action.MOVE && work.oo[a.to().o] != O.ME) {
				// force the units back to get the frontier right
				work.u[a.to().o] = 0;
				work.u[a.from().o] += a.amount();
			}
		}
		TimeTraveler tt = new TimeTraveler();
		tt.init(work);
		Territory territory = new Territory();
		territory.calculateTerritories(ilot, tt);
		
		
		List<Pos> disputedFrontier = calculateForbiddenCells(work, ilot, tt, territory ); 
		myDistances.calculate(work, disputedFrontier);
		mySpawnDistances.calculate(work, disputedFrontier);
		
		oppDistances.calculate(work, disputedFrontier);
		oppSpawnDistances.calculate(work, disputedFrontier);
		
		
		actions.addAll(moves(work, alreadyDoneActions, ilot,tt, territory));

		
		replaceSpawnAndLockBySpawnAndMove(originalWork, actions);

		
		
		
		// apply action to originalWork
		for (Action a : actions) {
			originalWork.apply(a);
		}
		return actions;
	}

	private void replaceSpawnAndLockBySpawnAndMove(State originalWork, List<Action> actions) {
		State temp = new State();
		temp.copyFrom(originalWork);
		for (Action a : actions) {
			temp.apply(a);
		}
		
		// replace spawn at X and lock units
		List<Action> actionsToAdd = new ArrayList<>();
		for (Action a : actions) {
			if (a.type() == Action.SPAWN) {
				if (temp.movableUnits(a.to()) > 0) {
					Action move = Action.move(a.amount(), a.to(), a.realTarget, "Transfering spawn to move");
					actionsToAdd.add(move);
					temp.apply(move);
				}
			}
		}
		
		actions.addAll(actionsToAdd);
	}
	
	public static List<Pos> calculateForbiddenCells(State work, Ilot ilot, TimeTraveler tt, Territory territory) {
		List<Pos> disputedFrontier = new ArrayList<>();
		for (int i=0;i<ilot.pFE;i++) {
			Pos p = ilot.p[i];
			if (work.rec[p.o] != 0) continue;
			if (!tt.sliceAt(1).state.canMove(p)) continue; // will disappear next turn
			if (work.o[p.o] == O.OPP) disputedFrontier.add(p); 
			if (territory.disputed.contains(p)) disputedFrontier.add(p);
		}
		return disputedFrontier;
	}

	static BFS bfses[] = new BFS[Pos.MAX_OFFSET];
	static {
		for (int i=0;i<bfses.length;i++) {
			bfses[i] = new BFS();
		}
	}

	public List<Action> moves(State work, List<Action> alreadyDoneActions, Ilot ilot, TimeTraveler tt, Territory territory) {
		List<Action> actions = new ArrayList<>();
		List<Pos> frontier = new ArrayList<>();
		for (Pos p : territory.frontier) {
			if (work.rec[p.o] != 0)
				continue;
			if (!tt.sliceAt(1).state.canMove(p))
				continue;
			frontier.add(p);
			bfses[p.o].calculate(work, p, tt);
		}
		if (frontier.isEmpty()) {
			return Collections.emptyList();
		}
		List<Pos> myCells = new ArrayList<>();
		List<Pos> myUnits = new ArrayList<>();
		
		for (int i = 0; i < ilot.pFE; i++) {
			Pos unit = ilot.p[i];
			if (work.oo[unit.o] != O.ME) continue;
			myCells.add(unit);
			if (work.movableUnits(unit) > 0) myUnits.add(unit);
		}
		List<Pos> visitedCells = new ArrayList<>();
		
		int servedFrontier[] = new int[Pos.MAX_OFFSET];
		for (Action a : alreadyDoneActions) {
			if (frontier.contains(a.to()) && work.countRedAround(a.to()) == 0) {
				servedFrontier[a.to().o] = 1;
			}
		}
		
		for (Pos f : frontier) {
			if (work.isMine(f)) {
				servedFrontier[f.o] = 1;
			}
		}
		
		boolean hasMove = true;
		while (hasMove) {
			hasMove = false;
			Collections.sort(frontier, ( f1,	f2) -> Double.compare(cellOfFrontierEval(work, myCells, myUnits, servedFrontier, f1, territory.blueDangers, territory.frontier), cellOfFrontierEval(work, myCells, myUnits, servedFrontier, f2, territory.blueDangers, territory.frontier)));
			if (frontier.isEmpty())
				break;
			Pos f = frontier.get(0);
			if (work.owner(f) == O.ME && work.u[f.o] > 0 && servedFrontier[f.o] == 0) {
				servedFrontier[f.o] = 1;
				if (work.movableUnits(f) > 0) {
					work.mu[f.o] = Math.min(work.movableUnits(f), work.units(f) - Defense.neededToDefend(work, f));
					if (work.movableUnits(f) == work.units(f)) {
						work.mu[f.o]--;
					}
				}
			} else {
				servedFrontier[f.o]++;
			}
			Pos closestPos = chooseClosestPos(work, f, myUnits, myCells);
			if (closestPos == null) {
				frontier.remove(f);
				hasMove = true;
				continue;
			}
			if (work.movableUnits(closestPos) > 0) {
				List<Pos> path = navigator.navigate(work, closestPos, f, tt, visitedCells,	n -> {
					double localScore = 0.0;
					localScore -= visitedCells.contains(n) ? 2 : 0;
					localScore += work.o[n.o] == O.NEUTRAL ? 1 : 0;
					localScore += (frontier.contains(n) && work.u[n.o] == 0) ? 0.1 : 0;
					localScore += 0.001 * LowRecyclers.ratio[n.o];
					return localScore;
				});
				Pos nextPos = null;
				if (!path.isEmpty()) {
					for (Pos p : path) {
						work.o[p.o] = O.ME;
					}
					nextPos = path.get(0);
				}
				if (nextPos != null) {
					visitedCells.add(nextPos);
					if (closestPos == nextPos) {
						work.mu[closestPos.o]--;
						hasMove = true;
					} else {
						Action move = Action.move(1, closestPos, nextPos, f, Spreader.class.getSimpleName());
						actions.add(move);
						work.apply(move);
						hasMove = true;
					}
				}
			} else if (servedFrontier[f.o] <= 1 && work.myMatter >= O.COST) {
				int amount = 1;
				if (f.neighbors4dirs.contains(closestPos)) {
					amount = Math.min(work.myMatter / O.COST, 1 + work.countRedAround(f));
				}
				Action spawn = Action.spawn(amount, closestPos, f, Spreader.class.getSimpleName());
				actions.add(spawn);
				work.apply(spawn);
				hasMove = true;
			}
		}
		return actions;
	}



	private double cellOfFrontierEval(State work, List<Pos> myCells, List<Pos> myUnits, int[] servedFrontier, Pos f1, double[] danger, List<Pos> frontier) {
		return 0.0
						+ (servedFrontier[f1.o] * 100_000) 
						+ (work.u[f1.o] * 10_000)
						+ distanceToUnitsOrSpawn(work, f1, myUnits, myCells, work.myMatter >= O.COST) 
						+ (work.o[f1.o] == O.OPP ? 1 : 0)
						+ (isBorder(f1, frontier)? -2 : 0) // bonus for borders
						+ (work.myMatter >= O.COST && work.o[f1.o] != O.OPP && mySpawnDistances.grid[f1.o] > oppDistances.grid[f1.o] ? -500 : 0)
						- 0.01 * danger[f1.o]
						;
	}


	
	private boolean isBorder(Pos cell, List<Pos> frontier) {
		int count = 0;
		for (Pos n : cell.neighbors8dirs) {
			if (frontier.contains(n)) count++;
		}
		
		return count == 1;
	}

	private void bestCellToSpawn(SpreaderResult result, State work, Pos cell, List<Pos> myUnits, List<Pos> myCells) {
		BFS bfs = bfses[cell.o];

		double best = Double.POSITIVE_INFINITY;
		Pos bestPos = null;
		for (Pos p : myCells) {
			double dist = bfs.distances[p.o];
			dist += 1.1; // +1 for spawn
			dist += 0.001 * Math.abs(p.y - cell.y); // prefer same y cells if same distance (TODO it's biased on horizont game, but can not be good)
			if (dist < best) {
				best = dist;
				bestPos = p;
			}
		}
		
		result.pos = bestPos;
		result.dist = best;
	}
	
	private void bestCellToMoveFrom(SpreaderResult result, State work, Pos cell, List<Pos> myUnits, List<Pos> myCells) {
		BFS bfs = bfses[cell.o];

		double bestScore = Double.NEGATIVE_INFINITY;
		Pos bestPos = null;
		int bestDist = Integer.MAX_VALUE;
		for (Pos p : myUnits) {
			if (work.movableUnits(p) == 0) continue;
			if (work.units(p) <= Defense.neededToDefend(work, p)) continue; // need to defend
			
			double score = 0.0;
			score -= 1000 * bfs.distances[p.o]; // doit rester le plus grand des criteres
			score += 0.01 * work.movableUnits(p);
			
			
			if (score > bestScore) {
				bestDist = bfs.distances[p.o];
				bestScore = score;
				bestPos = p;
			}
		}
		
		result.pos = bestPos;
		result.dist = bestDist;
	}

	private boolean canSpare(State work, Pos p, Pos target, List<Pos> frontier) {
		boolean neighbor = false;
		for (Pos n : p.neighbors4dirs) {
			if (n == target) {
				neighbor = true;
			}
			if (!work.canMove(n)) continue;
			if (work.isOpp(n)) continue;
			if (work.units(n) > 0) continue;
			if (frontier.contains(n)) continue;
			
			return false;
		}
		return neighbor;
	}

	private Pos chooseClosestPos(State work, Pos cell, List<Pos> myUnits, List<Pos> myCells) {

		SpreaderResult resultMove = new SpreaderResult();
		SpreaderResult resultSpawn = new SpreaderResult();
		
		
		bestCellToMoveFrom(resultMove, work, cell, myUnits, myCells);
		bestCellToSpawn(resultSpawn, work, cell, myUnits, myCells);
		

		if (work.myMatter < O.COST) {
			// can't choose spawn, but maybe next turn ?
			if (resultSpawn.dist + 1  < resultMove.dist ) {
				return null;
			} else {
				return resultMove.pos;
			}
		} else {
			// I can spawn
			if (resultMove.dist < resultSpawn.dist) {
				return resultMove.pos;
			} else {
				return resultSpawn.pos;
			}
		}
	}


	private double distanceToUnitsOrSpawn(State work, Pos cell, List<Pos> myUnits, List<Pos> myCells, boolean canSpawn) {
		BFS bfs = bfses[cell.o];
		
		if (mySpawnDistances.grid[cell.o] > oppDistances.grid[cell.o]) {
			canSpawn = false;
		}
		
		
		
		double best = Double.POSITIVE_INFINITY;
		
		for (Pos p : myUnits) {
			if (work.movableUnits(p) == 0) continue;
			
			double dist = bfs.distances[p.o];
			if (dist < best) {
				best = dist;
			}
		}

		if (canSpawn) {
			for (Pos p : myCells) {
				double dist = bfs.distances[p.o];
				if (dist ==0 ) {
					dist += 1.1;
				} else {
					dist += 1.1;
				}
				
				if (dist < best) {
					best = dist;
				}
			}
		}
		
		return best;
	}

	public static List<Action> spreadQuickly(State work, Ilot ilot, TimeTraveler tt) {
		List<Action> actions = new ArrayList<>();
		Territory t = new Territory();
		t.calculateTerritories(ilot, tt);
		
		for (int i=0;i<ilot.pFE;i++) {
			Pos unit = ilot.p[i];
			if (work.owner(unit) != O.ME || work.movableUnits(unit) == 0) continue;
			if (ilot.ruler != O.ME && t.frontier.contains(unit)) continue;
			
			actions.addAll(spreadQuickly(work, ilot, tt, unit, true));
		}
		
		return actions;
	}

	private static BFS innerBfs = new BFS();
	public static List<Action> spreadQuickly(State work, Ilot ilot, TimeTraveler tt, Pos unit, boolean targetReds) {
		List<Action> actions = new ArrayList<>();
		
		innerBfs.calculate(work, unit, tt);
		Territory territory = new Territory();
		territory.calculateTerritories(ilot, tt);
		
		int closestDist = Integer.MAX_VALUE;
		Pos closestCell = null;
		for (int j=0;j<ilot.pFE;j++) {
			Pos target = ilot.p[j];
			if (!work.canMove(target)) continue;
			
			int dist;
			if (territory.redTerritory.contains(target) && targetReds) {
				dist = innerBfs.distances[target.o] - 1000;
			} else if (work.o[target.o] == O.NEUTRAL) {
				dist = innerBfs.distances[target.o];
			} else {
				dist = Integer.MAX_VALUE;
			}
			if (dist < closestDist) {
				closestDist = dist;
				closestCell = target;
			}
		}
		if ( closestCell != null) {
			List<Pos> path = navigator.navigate(work, unit, closestCell, tt, Collections.emptyList());
			Pos target = null;
			if (!path.isEmpty()) target = path.get(0);
			if (target != null) {
				Action move = Action.move(work.movableUnits(unit), unit, target, closestCell, "Mover - spreadQuickly");
				actions.add(move);
				work.apply(move);
			}
		}
		
		return actions;
	}

	public static Collection<? extends Action> spreadQuicklyWithSpawn(State state, Ilot ilot, TimeTraveler tt) {
		if (ilot.isFullCoverByMe()) return Collections.emptyList();

		List<Action> actions = new ArrayList<>();
		for (int i=0;i<ilot.pFE;i++) {
			if (state.myMatter < O.COST) break;
			Pos c = ilot.p[i];
			if (state.isNeutral(c) && state.countBlueAround(c) ==0) {
				Pos spawnOn = null;
				for (Pos n : c.neighbors4dirs) {
					if (state.oo[n.o] == O.ME) spawnOn = n;
				}
				if (spawnOn != null) {
					Action spawn = Action.spawn(1, spawnOn, "spawn to spread quickly");
					actions.add(spawn);
					state.apply(spawn);
				}
			}
		}
		
		actions.addAll(spreadQuickly(state, ilot, tt)); // now spread the rest
		return actions;
	}
	

}
