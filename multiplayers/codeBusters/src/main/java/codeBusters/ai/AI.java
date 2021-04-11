package codeBusters.ai;

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
	
	public void think() {
		Player.myTeam.applyAll(function);
		
	}
	
	
	public void busterThink(Buster buster) {
		
		Action action = findBestAction(buster);
		if (action != null) {
			if (action.type == MoveType.STUN) {
				action.buster.stunned = 10;
				buster.stunCooldown = 20;
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

		if (buster.stunned > 0) {
			return Action.doWait();
		}

		if ((action = visitCenter(buster)) != null) {
			return action;
		}
		
		if ((action = tryToRelease(buster)) != null) {
			return action;
		}
		
		if ((action = tryToStun(buster)) != null) {
			return action;
		}
		
		if ((action = tryToRescueGhost(buster)) != null) {
			return action;
		}

		if ((action = tryToBust(buster)) != null) {
			return action;
		}
		
		if ((action = goToNearestGhost(buster)) != null) {
			return action;
		}
		
		if ((action = explore(buster)) != null) {
			return action;
		}
		//return Action.doWait();
		return Action.move(new P(Player.rand.nextInt(Player.WIDTH), Player.rand.nextInt(Player.HEIGHT)));
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
		if (Player.ghosts.elements[0].state != State.START) return null;

		return Action.move(Player.CENTER);
	}


	private Action explore(Buster buster) {
		CheckPoint cp = Player.grid.findCheckpoint(buster);
		if (cp != null) {
			return Action.move(cp.position);
		}
		return null;
	}


	private Action goToNearestGhost(Buster buster) {
		Ghost bestGhost = null;
		int bestDist2 = Integer.MAX_VALUE;
		for (Ghost ghost : Player.ghosts) {
			if (ghost.state != State.FREE && ghost.state != State.IN_FOG) continue;
			int dist2 = ghost.position.dist2(buster.position);
			if (dist2 < bestDist2) {
				bestDist2 = dist2;
				bestGhost = ghost;
			}
		}
		if( bestGhost != null) {
			return Action.move(bestGhost.position);
		}
		return null;
	}


	private Action tryToBust(Buster buster) {
		Ghost bestGhost = bestGhostToBust(buster);
		if( bestGhost != null) {
			if (buster.isInRange2(bestGhost.position, Player.RANGE_LIMIT_TO_BUST_GHOST_2)) {
				// ok we want to bust if but he is too close :(
				// TODO move farther is better than waiting for the ghostToMove
				System.err.println("Buster "+buster+" is too close");
				for (int angle=0;angle<360;angle+=10) {
					double x = 910 * Math.cos(1.0*Math.PI*angle/180) + bestGhost.position.x; 
					double y = 910 * Math.sin(1.0*Math.PI*angle/180) + bestGhost.position.y;
					if (x<0 || y< 0 || x >= Player.WIDTH || y>=Player.HEIGHT) continue;
					
					P tentative = new P((int)x, (int)y);
					if (buster.isInRange2(tentative, Player.MOVE_DISTANCE_2)) {
						System.err.println("Found a correct new Positon @ "+tentative);
						return Action.move(tentative);
					}
				}
				
				return Action.doWait();
			}
			
			return Action.bust(bestGhost);
		}
		return null;
	}


	private Ghost bestGhostToBust(Buster buster) {
		Ghost bestGhost = null;
		int bestEnergy = Integer.MAX_VALUE;
		for (Ghost ghost : Player.ghosts) {
			if (ghost.state != State.FREE) continue;
			if (Player.turn < 30 && ghost.energy>20) continue;
			if (!buster.isInRange2(ghost.position, Player.BUSTER_RANGE_2)) continue; 
			
			if (bestEnergy > ghost.energy) {
				bestEnergy = ghost.energy;
				bestGhost = ghost;
			}
		}
		return bestGhost;
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
		
		
		Ghost bestGhost = bestGhostToBust(buster); // check which ghost I would get
		if (bestGhost == null) {
			System.err.println("I have no best ghost");
			return stunAction;
		}

		if (!bestTarget.isInRange2(bestGhost.position, Player.RANGE_TO_BUST_GHOST_2)) {
			System.err.println("Not in my best ghost range, leave it alone");
			return null; // leave it alone
		}
		
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
		
		
		if (buster.isInRange2(Player.myBase, Player.BASE_RANGE_2)) {
			return Action.release();
		} else {
			return Action.move(Player.myBase);
		}
	}
}
