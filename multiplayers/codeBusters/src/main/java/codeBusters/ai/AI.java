package codeBusters.ai;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import codeBusters.CheckPoint;
import codeBusters.P;
import codeBusters.Player;
import codeBusters.entities.Action;
import codeBusters.entities.Buster;
import codeBusters.entities.Ghost;
import codeBusters.entities.MoveType;
import codeBusters.entities.State;

public class AI {
	Consumer<Buster> function = t -> busterThink(t);
	
	private Set<Buster> stunnedEnnemies = new HashSet<>();
	private Set<Ghost> bustedGhosts = new HashSet<>();
	private Set<P> alreadyCheckpointed = new HashSet<>();
	
	
	public void think() {
		stunnedEnnemies.clear();
		bustedGhosts.clear();
		alreadyCheckpointed.clear();
		
		Player.myTeam.applyAll(function);
		
	}
	
	
	public void busterThink(Buster buster) {
		
		Action action = findBestAction(buster);
		if (action != null) {
			if (action.type == MoveType.STUN) {
				action.buster.stunned = 10;
				buster.stunCooldown = 20;
				stunnedEnnemies.add(action.buster);
			}
			if (action.type == MoveType.BUST) {
					if (action.ghost.energy <= 1/* TODO disputed ghost */) {
						bustedGhosts.add(action.ghost);
					}
			}
			if (action.type == MoveType.RADAR) {
				buster.hasRadar = false;
			}
			
			buster.action = action;
			return;
		} else {
			System.err.println("No action found for "+buster);
			buster.action = Action.doWait();
		}
		
	}


	private Action findBestAction(Buster buster) {
		Action action;

		System.err.println("Look for action for buster "+buster);
		if (buster.stunned > 0) {
			System.err.println("  => I'm stunned");
			return Action.doWait();
		}

		if ((action = visitCenter(buster)) != null) {
			System.err.println(" => Action from visit Center");
			return action;
		}
		
		if ((action = tryToRelease(buster)) != null) {
			System.err.println(" => Action from Try to release");
			return action;
		}

		if ((action = tryToEscort(buster)) != null) {
			System.err.println(" => Action from escort "+action);
			return action;
		}

		if ((action = tryToStun(buster)) != null) {
			System.err.println(" => Action from try to stun");
			return action;
		}
		
		if ((action = tryToBust(buster)) != null) {
			System.err.println(" => Action from try to bust "+action);
			return action;
		}
		
		if ((action = tryToRescueGhost(buster)) != null) {
			System.err.println(" => Action from try to rescue "+action);
			return action;
		}

		if ((action = goToNearestGhost(buster)) != null) {
			System.err.println(" => Action from go to nearest ghost "+ action);
			return action;
		}
		
//		if ((action = tryRadar(buster)) != null) {
//			System.err.println(" => Action from radar "+action);
//			return action;
//		}
		
		
		if ((action = explore(buster)) != null) {
			System.err.println(" => Action from explore "+action);
			return action;
		}
		//return Action.doWait();
		System.err.println(" => Action from random");
		return Action.move(new P(Player.rand.nextInt(Player.WIDTH), Player.rand.nextInt(Player.HEIGHT)));
	}


	private Action tryToEscort(Buster buster) {
//		if (!Player.catchHalfGhost) return null; // TODO pour l'instant on n'escort que sur le dernier ghost
		if (buster.carried != Ghost.noGhost) return null; // I myself have a ghost
		
		for (Buster b : Player.myTeam) {
			if (b == buster) continue;
			if (b.carried != Ghost.noGhost)  {
				boolean danger = false;
				for (Buster ennemy : Player.hisTeam) {
					if (ennemy.position == P.NOWHERE) continue;
					
					if (ennemy.canStun(b)) danger = true;
					
					if (ennemy.position.dist2(Player.myBase) < b.position.dist2(Player.myBase)) danger = true;
				}
				
				if (danger) {
					// TODO anticiper ? proteger ?
					System.err.println("Escort "+b);
					
					
					return Action.move(b.position);
				}
			}
		}
		
		return null;
	}


	private Action tryRadar(Buster buster) {
		if (!buster.hasRadar) return null;
		if (buster.notSeenAround < 2_000_000) return null;
		return Action.radar();
	}


	private Action tryToRescueGhost(Buster buster) {
		if (Player.ghostsToRescue.isEmpty()) return null;
		
		Ghost best = null;
		int bestDist2 = Integer.MAX_VALUE;
		for (Ghost ghost : Player.ghostsToRescue) {
			int dist2 = buster.position.dist2(ghost.position);
			if (dist2 < bestDist2) {
				bestDist2 = dist2;
				best = ghost;
			}
		}
		
		if (best == null) return null;
		
		if (buster.isInRange2(best.position, Player.STUN_RANGE_2)) {
			return Action.bust(best);
		} else {
			return Action.move(best.position);
		}
	}


	private Action visitCenter(Buster buster) {
		if (buster != Player.myTeam.elements[0]) return null;
		if (Player.ghosts.elements[0].state != State.START) {
			if (buster.hasRadar) {
				return Action.radar();
			}
			
			return null;
		}

		return Action.move(Player.CENTER);
	}


	private Action explore(Buster buster) {
		// check for ghost in fog
		Ghost bestGhost = bestGhostToBust(buster, -1);
		if (bestGhost != null) {
			System.err.println("  found a ghost to reach : "+bestGhost);
			return Action.move(bestGhost.position);
		} else {
			System.err.println("  no ghost found");
		}
		
		
		CheckPoint cp = Player.grid.findCheckpoint(buster, alreadyCheckpointed);
		if (cp != null) {
			// mark all points around as seen
			alreadyCheckpointed.addAll(Player.grid.around(cp));
			
			
			
			return Action.move(cp.position);
		}
		return null;
	}


	private Action goToNearestGhost(Buster buster) {
		Ghost bestGhost = bestGhostToBust(buster, -1);
		if( bestGhost != null) {
			return Action.move(bestGhost.position);
		}
		return null;
	}


	private Action tryToBust(Buster buster) {
		Ghost bestGhost = bestGhostToBust(buster, 10);
		
		
		if( bestGhost != null) {
			P ghostBelievablePos = bestGhost.position;
			if (!buster.isInRange2(bestGhost.position, Player.BUSTER_RANGE_2)) {
				// can't bust it right now
				return null;
			}
			if (buster.isInRange2(ghostBelievablePos, Player.RANGE_LIMIT_TO_BUST_GHOST_2)) {
				// ok we want to bust if but he is too close :(
				// TODO move farther is better than waiting for the ghostToMove
				System.err.println("Buster "+buster+" is too close from "+bestGhost);
				P nearest = null;
				int bestDist2 = Integer.MAX_VALUE;
				for (int angle=0;angle<360;angle+=10) {
					double x = 910 * Math.cos(1.0*Math.PI*angle/180) + ghostBelievablePos.x; 
					double y = 910 * Math.sin(1.0*Math.PI*angle/180) + ghostBelievablePos.y;
					if (x<0 || y< 0 || x >= Player.WIDTH || y>=Player.HEIGHT) continue;
					
					P tentative = new P((int)x, (int)y);
					int dist2 = buster.position.dist2(ghostBelievablePos);
					if (dist2 < bestDist2) {
						bestDist2 = dist2;
						nearest = tentative;
					}
				}
				if (nearest != null) {
					return Action.move(nearest);
				} else {
					System.err.println("No new position found to catch ghost");
					return Action.doWait();
				}
			}
			
			return Action.bust(bestGhost);
		}
		return null;
	}


	private Ghost bestGhostToBust(Buster buster, int maxTurns) {
		if (Player.catchHalfGhost) {
			Ghost closest = closestGhostToBase();
			if (closest != null) return closest;
		}
		
		Ghost bestGhost = null;
		int bestScore = Integer.MIN_VALUE;
		for (Ghost ghost : Player.ghosts) {
			if (bustedGhosts.contains(ghost)) continue;
			
			if (ghost.state != State.FREE && ghost.state != State.IN_FOG && ghost.lastSeenTurn > Player.turn - 10) continue;
			if (ghost.position == P.NOWHERE) continue;
			
			if (excludeFatGhosts() && ghost.energy>20) continue;
			//if (underWhelmed(buster, ghost)) continue;
			
			//if (!buster.isInRange2(ghost.position, Player.BUSTER_RANGE_2)) continue;
			int turnToGo = (int)(buster.position.dist(ghost.position) / Player.MOVE_DISTANCE);
			if (maxTurns != -1 && turnToGo > maxTurns) continue;
			
			int score = - turnToGo * 2 + (40 - ghost.energy);
			if (ghost.state != State.FREE) {
				score -= turnToGo * 5;
			}
//			System.err.println("Score for "+ghost+" = "+score);
			if (score > bestScore ) {
				bestScore = score;
				bestGhost = ghost;
			}
		}
		return bestGhost;
	}


	private Ghost closestGhostToBase() {
		Ghost closestGhost = null;
    long minDistToBase = Long.MAX_VALUE;
    for (Ghost ghost : Player.ghosts) {
        if (ghost.state != State.FREE && ghost.state != State.IN_FOG)
            continue;
        if (ghost.position == P.NOWHERE)
            continue;
        long dist = 0;
        for (Buster b : Player.myTeam) {
            dist += ghost.position.dist2(b.position);
        }
        if (dist < minDistToBase) {
            minDistToBase = dist;
            closestGhost = ghost;
        }
    }
    return closestGhost;
	}


	private boolean underWhelmed(Buster buster, Ghost ghost) {
		// TODO can we count better ???
		int myCountLastTurn = 0;
		int hisCountLastTurn = 0;
		for (Buster b : ghost.onIt) {
			if (b.team == Player.myTeamId) myCountLastTurn++; else hisCountLastTurn++;
		}
		
		if (!ghost.onIt.contains(buster)) {
			myCountLastTurn++; // add one of my team
		}
		return 2 * myCountLastTurn < ghost.bustersOnIt;
	}


	private boolean excludeFatGhosts() {
		return Player.turn < 25 && !Player.seenAllGhost;
	}


	private Action tryToStun(Buster buster) {
		System.err.println("Check stun for "+buster);
		if (buster.stunCooldown > 0) {
			System.err.println("still on cooldown");
			return null;
		}

		Buster bestTarget = null;
		int bestScore = Integer.MIN_VALUE;
		int targetCount = 0;
		for (Buster e : Player.hisTeam) {
			if (e.stunned > 1) continue;
			if (stunnedEnnemies.contains(e)) continue;
			
			
			if (!buster.isInRange2(e.position, Player.FOG_DISTANCE_2)) continue;
			int score = 0;
			if (buster.carried != Ghost.noGhost) {
				score += 1000;
			}
			targetCount++;
			if (bestScore < score) {
				bestScore = score;
				bestTarget = e;
			}
		}
		
		if (bestTarget == null) {	return null; }
		
		Action stunAction = buster.isInRange2(bestTarget.position, Player.STUN_RANGE_2) ? Action.stun(bestTarget) : null; 
		
		if (bestTarget.carried != Ghost.noGhost) {
			System.err.println("Buster with a ghost, auto stun");
			return stunAction;
		}
		
		
		Ghost bestGhost = bestGhostToBust(buster, 3); // check which ghost I would get
		if (bestGhost == null) {
			System.err.println("I have no best ghost");
			return stunAction;
		}

		// TODO comment ne pas stun des buster qui nous laisserait tranquille ?
//		if (!bestTarget.isInRange2(bestGhost.position, Player.RANGE_TO_BUST_GHOST_2)) {
//			System.err.println("Not in my best ghost range, leave it alone");
//			return null; // leave it alone
//		}
		
		if (bestGhost.energy < 7) {
			System.err.println("we may get the ghost back before he is not unstunned - so stun!");
			if (stunAction == null) {
				System.err.println("Need to move before");
				return Action.move(bestTarget.position);
			}
			return stunAction;
		}
		
		if (!buster.isInRange2(bestTarget.position, Player.STUN_RANGE_2)) {
			System.err.println("TODO - move to be in stun position");
			return null; // no stun
		} else {
			return null;
		}
	}


	private Action tryToRelease(Buster buster) {
		if (buster.carried == Ghost.noGhost) return null;

		
		if (false && Player.catchHalfGhost) {
			System.err.println("last ghost, go hide !");
			P hautDroite = new P(Player.WIDTH, 0);
			P basGauche = new P(0, Player.HEIGHT);
			if (buster.position.dist2(hautDroite) < buster.position.dist2(basGauche)) {
				return Action.move(hautDroite);
			} else {
				return Action.move(basGauche);
			}
		}
		
		// TODO Quaterback
		
		
		if (buster.stunCooldown == 0) {
			// check if there is somebody waiting for me
			for (Buster e : Player.hisTeam) {
				if (e.stunCooldown > 1) continue; // there is time to stun him later
				if (e.stunned > 1) continue; 
				
				if (buster.canSee(e)) {
					if (buster.inStunRange(e)) {
						System.err.println("Help to rescue ghost "+buster.carried);
						Player.ghostsToRescue.add(buster.carried);
						return Action.stun(e);
					} else {
						System.err.println("Eject Derniere chance avant d'etre (double)stun ?");
						return Action.eject(Player.myBase);
					}
				}
			}
		}
		
		
		if (buster.stunCooldown > 0 && buster.position.dist2(Player.myBase) > Player.CENTER.dist2(Player.myBase)) {
			System.err.println("Far and withoot stun");
			// TODO check for escort ?
			
			// follow the walls and prey
			int bestScore = Integer.MIN_VALUE;
			P best = null;
			for (int angle=0;angle<360;angle+=10) {
				double x = buster.position.x + Player.MOVE_DISTANCE * Math.cos(1.0*Math.PI*angle/180); 
				double y = buster.position.y + Player.MOVE_DISTANCE * Math.sin(1.0*Math.PI*angle/180);
				System.err.println(""+x+", "+y);
				if (x<0 || y< 0 || x >= Player.WIDTH || y>=Player.HEIGHT) continue;
				
				P tentative = new P((int)x, (int)y);
				int score = -tentative.dist2(Player.myBase) + tentative.dist2(Player.CENTER);
				System.err.println("tentative "+tentative+" => score = "+score);
				if( score > bestScore) {
					bestScore = score;
					best = tentative;
				}
			}
			if (best != null) {
				return Action.move(best);
			}
		}
		
		
		if (buster.isInRange2(Player.myBase, Player.BASE_RANGE_2)) {
			return Action.release();
		} else {
			return Action.move(Player.myBase);
		}
	}
}
