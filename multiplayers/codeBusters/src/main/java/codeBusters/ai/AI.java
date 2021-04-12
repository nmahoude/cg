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

		System.err.println("Look for action for buster "+buster);
		if (buster.stunned > 0) {
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
		
		if ((action = tryToStun(buster)) != null) {
			System.err.println(" => Action from try to stun");
			return action;
		}
		
		if ((action = tryToRescueGhost(buster)) != null) {
			System.err.println(" => Action from try to rescue");
			return action;
		}

		if ((action = tryToBust(buster)) != null) {
			System.err.println(" => Action from try to bust "+action);
			return action;
		}
		
		if ((action = goToNearestGhost(buster)) != null) {
			System.err.println(" => Action from go to nearest ghost "+ action);
			return action;
		}
		
		if ((action = explore(buster)) != null) {
			System.err.println(" => Action from explore");
			return action;
		}
		//return Action.doWait();
		System.err.println(" => Action from random");
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
		Ghost bestGhost = bestGhostToBust(buster);
		if( bestGhost != null) {
			return Action.move(bestGhost.position);
		}
		return null;
	}


	private Action tryToBust(Buster buster) {
		Ghost bestGhost = bestGhostToBust(buster);
		
		if( bestGhost != null) {
			if (!buster.isInRange2(bestGhost.position, Player.BUSTER_RANGE_2)) {
				return null;
			}
			if (buster.isInRange2(bestGhost.position, Player.RANGE_LIMIT_TO_BUST_GHOST_2)) {
				// ok we want to bust if but he is too close :(
				// TODO move farther is better than waiting for the ghostToMove
				System.err.println("Buster "+buster+" is too close from "+bestGhost);
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
		int bestScore = Integer.MIN_VALUE;
		for (Ghost ghost : Player.ghosts) {
			if (ghost.state != State.FREE) continue;
			if (!Player.seenAllGhost && ghost.energy>20) continue;
			//if (!buster.isInRange2(ghost.position, Player.BUSTER_RANGE_2)) continue;
			int turnToGo = (int)(buster.position.dist(ghost.position) / Player.MOVE_DISTANCE);
			if (turnToGo > 3) continue;
			
			int score = - turnToGo * 2 + (40 - ghost.energy);
			
			if (score > bestScore ) {
				bestScore = score;
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
