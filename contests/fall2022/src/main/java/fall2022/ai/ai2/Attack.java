package fall2022.ai.ai2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import fall2022.Action;
import fall2022.DiffusionMap;
import fall2022.O;
import fall2022.Player;
import fall2022.Pos;
import fall2022.State;
import fall2022.Territory;
import fall2022.TimeTraveler;
import fall2022.Ilot.Ilot;

// only if I can take the opp cell !
public class Attack {
	Territory territory = new Territory();
	Set<Pos> forbidenCells;

	public Collection<? extends Action> think(State work, TimeTraveler tt, Ilot ilot, DiffusionMap myUnitsMap,
	    DiffusionMap oppUnitsMap, boolean lastFrontier) {

		territory.calculateTerritories(ilot, tt);
		forbidenCells = tt.forbidenCells();
		
		List<Action> actions = new ArrayList<>();

		actions.addAll(winningAttack(work, tt, ilot, territory));
		actions.addAll(aggressiveAttack(work, tt, ilot));
		
		if (lastFrontier) {
			actions.addAll(lastFrontier(work, tt, ilot));
		}
		
		if (actions.isEmpty()) {
			actions.addAll(spawnAttack(work, tt, ilot));
		}

		actions.addAll(dangerousExplorer(work, tt, ilot));
		actions.addAll(killerMoves(work, tt, ilot));
		
		return actions;
	}

	// take some risks
	private Collection<? extends Action> killerMoves(State work, TimeTraveler tt, Ilot ilot) {
		List<Action> actions = new ArrayList<>();
	
		//if (Territory.myTerritory >= Territory.oppTerritory) return Collections.emptyList();
		
		for (Pos frontier : territory.frontier) {
			if (!work.isMine(frontier)) continue;
			if (work.movableUnits(frontier) == 0) continue;
			if (Player.noAggressionMap[frontier.o] < 5) continue; // can't expect no move
			
			
			boolean noRisksOfSave = true;
			for (Pos n : frontier.neighbors4dirs) {
				if (tt.sliceAt(1).state.s[n.o] == 0 && work.isOpp(n) && work.u[n.o] > 0) {
					noRisksOfSave = false;
					break;
				}
				
			}
			if (!noRisksOfSave) continue; // he will move
			
			
			Pos attack = null;
			for (Pos n : frontier.neighbors4dirs) {
				if (tt.sliceAt(1).state.s[n.o] != 0 && work.isOpp(n) && work.units(n) > 0) {
					if (work.movableUnits(frontier) >  work.units(n)) {
						attack = n;
						break;
					}
				}
			}			
			
			if (attack != null) {
				Action move = Action.move(work.units(attack) +1, frontier, attack, "Very aggresive and bold move");
				actions.add(move);
				work.apply(move);
			}
			
		}
		
		
		
		return actions;
	}
	
	
	// if neutral next to me & even attacker, go visit the neutral !
	private Collection<? extends Action> dangerousExplorer(State work, TimeTraveler tt, Ilot ilot) {
		List<Action> actions = new ArrayList<>();
		
		for (Pos frontier : territory.frontier) {
			if (!work.isMine(frontier)) continue;
			if (work.movableUnits(frontier) == 0) continue;
			if (Player.noAggressionMap[frontier.o] <= 1 ) continue;
			
 			int numberOfRedSources = work.redAttackCount(frontier);
			int delta = work.movableUnits(frontier) - work.countRedAround(frontier); // seulement les movables pour s'"assurer" qu'il ne va pas attaquer

			if (delta >= 0 && numberOfRedSources == 1) {
				// here we have only one potential attacker, and we can sustain an attack
				// so check if non threat neutral around
				Pos neutralToCheck = null;
				for (Pos n : frontier.neighbors4dirs) {
					if (work.isNeutral(n) && work.countRedAround(n) == 0) {
						if (tt.sliceAt(1).state.canMove(n)) {
							if (neutralToCheck == null || territory.frontier.contains(n)) {
							neutralToCheck = n;
							}
						}
					}
				}
				if (neutralToCheck != null) {
					Action move = Action.move(1, frontier, neutralToCheck, "dangerous exploration");
					actions.add(move);
					work.apply(move);
				}
			}
			
		}
		
		return actions;
	}

	private Collection<? extends Action> lastFrontier(State work, TimeTraveler tt, Ilot ilot) {
		List<Action> actions = new ArrayList<>();
		
		if (territory.frontier.size() == 1) {
			Pos frontier = territory.frontier.get(0);
			if (!tt.sliceAt(1).state.canMove(frontier)) return Collections.emptyList();
			
			
			Pos target = null;
			
			if (work.oo[frontier.o] == O.ME) {
				target = frontier;
			} else {
				for (Pos p : frontier.neighbors4dirs) {
					if (forbidenCells.contains(p)) continue;
					if (!work.canMove(p)) continue;
					target = p;
					break;
				}
			}
			
			if (target != null) {
				int amount = work.myMatter / O.COST;
				if (amount > 0) {
					Action spawn = Action.spawn(amount, target, "Spawn all on last frontier");
					actions.add(spawn);
					work.apply(spawn);
				}
			}
		}
		
		
		
		return actions;
	}

	private Collection<? extends Action> aggressiveAttack(State originalState, TimeTraveler tt, Ilot ilot) {
		List<Action> actions = new ArrayList<>();
		
		State work = new State();
		work.copyFrom(originalState);
		
		for (Pos frontier : territory.frontier) {
			if (!work.isMine(frontier)) continue;
			if (work.movableUnits(frontier) == 0) continue;
			
			
			// int numberOfRedSources = originalState.redAttackCount(frontier);
			//if (numberOfRedSources > 1) continue; // pas aggro si on 'fixe' plusieurs cases
			
			int delta = work.units(frontier) - originalState.countRedAround(frontier); // tres conservateur
			if ( delta < 0) continue;
			
			int maximalSpawnCount = work.myMatter / O.COST;
			
			double bestScore = Double.NEGATIVE_INFINITY;
			Pos bestTarget = null;
			for (Pos n : frontier.neighbors4dirs) {
				if (work.isMine(n)) continue;
				if (!work.canMove(n)) continue;
				if (forbidenCells.contains(n)) continue;
				
				// TODO contre productif de mettre ça ? if (territory.blueTerritory.contains(n)) continue;
				
				// TODO what to account for ?
				double score = 0.0;
				
				score +=100*work.attack[n.o];
				score -=1.0 * work.units(n); // number of units
				
				if (score > bestScore) {
					bestScore = score;
					bestTarget = n;
				}
			}
			if (bestTarget != null) {
				int moveAmount = Math.min(work.movableUnits(frontier), delta+maximalSpawnCount);
				int needToSpawn = Math.max(0, originalState.countRedAround(frontier) - (work.units(frontier) - moveAmount));
				
				if (moveAmount > 0) {
					work.attack[bestTarget.o]+=moveAmount;
					Action move = Action.move(moveAmount, frontier, bestTarget, bestTarget, "Aggro - attack");
					work.apply(move);
					actions.add(move);
					work.u[bestTarget.o]-=moveAmount;
					
					if (needToSpawn > 0) {
						Action spawn = Action.spawn(needToSpawn, frontier, "Aggro - spawn to replace");
						actions.add(spawn);
						work.apply(spawn);
					}
				}
			}
		}
		
		// apply action to originalState
		for (Action a: actions) {
			originalState.apply(a);
		}
		
		
		return actions;
	}

	private Collection<? extends Action> spawnAttack(State work, TimeTraveler tt, Ilot ilot) {

		int myPotentialSpawn = work.myMatter / O.COST;
		if (myPotentialSpawn < 2) return Collections.emptyList();
		
		List<Action> actions = new ArrayList<>();
		
		for (Pos frontier : territory.frontier) {
			if (!work.isMine(frontier)) continue;
			if (!work.canMove(frontier)) continue;
			
			for (Pos n : frontier.neighbors4dirs) {
				if (!work.isOpp(n)) continue;
				
				
				int maxOppUnitsNextTurn = work.units(n) + tt.sliceAt(1).state.oppMatter / O.COST;
				if (maxOppUnitsNextTurn +1 < work.units(frontier) + myPotentialSpawn) {
					int spawns = Math.max(0, maxOppUnitsNextTurn - work.units(frontier)+1);
					if (spawns != 0) {
						Action spawn = Action.spawn(spawns, frontier, "Prepare attack by spawning");
						actions.add(spawn);
						work.apply(spawn);
						myPotentialSpawn -= spawns;
						break;
					}
				}
			}
		}
		
		
		return actions;
	}

	private List<Action> winningAttack(State work, TimeTraveler tt, Ilot ilot, Territory territory) {
		List<Action> actions = new ArrayList<>();
		List<Pos> redToCheck = new ArrayList<>();
		for (int i = 0; i < ilot.pFE; i++) {
			Pos p = ilot.p[i];
			if (work.isOpp(p) && work.canMove(p)) {
				redToCheck.add(p);
			}
		}
		for (Pos red : redToCheck) {
			int blue = work.countBlueAround(red);
			if (blue == 0)
				continue;
			// check around
			int redReinforcement = work.units(red) + work.countRedAround(red) + work.oppMatter / O.COST;
			int blueAttack = 0, maxBlueAttack = 0;
			for (Pos n : red.neighbors4dirs) {
				if (!work.isMine(n))
					continue;
				int redCount = work.countRedAround(n);
				int spared;
				if (forbidenCells.contains(n)) {
					// suicide
					spared = work.movableUnits(n);
				} else {
					spared = Math.max(0, work.movableUnits(n) - Math.max(0, (redCount - (work.units(n) - work.movableUnits(n)))));
				}
				maxBlueAttack += work.movableUnits(n);
				blueAttack += spared;
			}
			if (blueAttack > redReinforcement) {
				int needed = redReinforcement +1 ; // all reinforcement +1 
				for (Pos n : red.neighbors4dirs) {
					if (!work.isMine(n))
						continue;
					int redCount = work.countRedAround(n);
					int spared;
					if (forbidenCells.contains(n)) {
						// suicide
						spared = work.movableUnits(n);
					} else {
						spared = Math.max(0, work.movableUnits(n) - Math.max(0, (redCount - (work.units(n) - work.movableUnits(n)))));
					}
					int amount = Math.min(needed, spared);
					if (amount > 0) {
						Action move = Action.move(amount, n, red, "ATTACK!");
						actions.add(move);
						work.apply(move);
						needed -= amount;
					}
				}
			} else if (maxBlueAttack > redReinforcement) {
				// i still may be able to attack, but with spawn to replace moved units
				int spawnNeeded = Math.max(0, 1 + redReinforcement - blueAttack);
				if (spawnNeeded <= work.myMatter / O.COST) {
					// can spawn
					int needed = redReinforcement+1;
					attackWithSpawn(work, actions, forbidenCells, red, needed);
				}
			} else if (work.units(red) == 0 && maxBlueAttack >= 1) {
				// empty cell, try to get it for 'free'
				if (work.myMatter >= O.COST) {
					attackWithSpawn(work, actions, forbidenCells, red, 1);
				}
			}
		}
		return actions;
	}

	private void attackWithSpawn(State work, List<Action> actions, Set<Pos> forbidenCells, Pos red, int needed) {
		for (Pos n : red.neighbors4dirs) {
			if (!work.isMine(n)) continue;
			
			int redCount = work.countRedAround(n);
			int spared;
			
			if (forbidenCells.contains(n)) {
				spared = work.movableUnits(n); // suicide
			} else {
				spared = Math.max(0, work.movableUnits(n) - Math.max(0, (redCount - (work.units(n)-work.movableUnits(n))) ));
			}

			int amount = Math.min(needed, work.movableUnits(n));
			int spawns = Math.max(0, amount - spared);
			if (amount > 0) {
				Action move= Action.move(amount, n, red, "ATTACK!");
				actions.add(move);
				work.apply(move);
				
				if (spawns > 0) {
					Action spawn= Action.spawn(spawns, n, "ATTACK!");
					actions.add(spawn);
					work.apply(spawn);
				}
				
				needed -= amount;
			}
		}
	}

}
