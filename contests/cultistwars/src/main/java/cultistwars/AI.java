package cultistwars;

import java.util.List;

public class AI {
	BFS bfs = new BFS();
	private int lastHealth = -1;
	
	
	int damageGrid[] = new int[State.SIZE];
	
  public void think(State state) {
  	prepareOppDamageGrid(state);
  	debugDamageGrid(damageGrid);
  	
  	
  	if (state.me != null) {
  		bfs.init(state.grid, state.me.pos);

  		if (retreatFromDanger(state)) {
  			return;
  		}
  	}
  	
  	
  	
  	// convert a point blank soldier is the best move I think
  	if (convert(state, 1)) {
  		return;
  	}

  	if (shootLeader(state)) {
      return;
    }

    if (convert(state, 4)) {
      return;
    }


    if (shootCultists(state)) {
      return;
    }

    if (retreatFromShoot(state)) {
      return;
    }
    
    if (convert(state, 1000)) {
      return;
    }
    
    
    
    System.err.println("Don't know what to do ... ");
    System.out.println("WAIT");
  }

  private void prepareOppDamageGrid(State state) {
    System.err.println("Building damage grid");
  	for (int j=0;j<state.unitsFE;j++) {
      if (state.units[j].owner != 1) continue;
      if (state.units[j].unitType == Unit.CULT_LEADER_TYPE) continue;
      
      System.err.println("  Checking unit @ "+state.units[j].pos);
      for (Pos target : Pos.allPositions()) {
      	if (target == state.units[j].pos) continue;

      	int dist = target.manhattan(state.units[j].pos);
        if (dist <= 6) {
        	if (! canShoot(state.grid, state.units[j].pos, target, false)) continue;
        	
        	damageGrid[target.offset] = Math.max(damageGrid[target.offset], 7 - dist);
        }
      }
    }		
	}

  public void debugDamageGrid(int[] grid) {
  	System.err.println("Damage grid : ");
		for (int y=0;y<7;y++) {
			for (int x=0;x<13;x++) {
				System.err.print(String.format("%2d ", grid[x + 13 * y]));
			}
			System.err.println();
		}

  }
  
	private boolean retreatFromDanger(State state) {
  	System.err.println("Checking for reatreat from danger for leader .... "+state.me.hp+" (last = "+lastHealth+")");
  	if (lastHealth  == -1 || lastHealth <= state.me.hp) {
  		lastHealth = state.me.hp;
  		return false;
  	}
  	
  	System.err.println("Check for retreat from danger has my health decrease ...");
		lastHealth = state.me.hp;
  	return retreatFromShoot(state);
	}

	private boolean retreatFromShoot(State state) {
		if (state.me == null) return false;
		
		// check if we can take some damage next turn
		if (damageGrid[state.me.pos.offset] > 0) {
			// check near cells for less damage
			
			Pos best = null;
			int lessDamage = damageGrid[state.me.pos.offset];
			
			for (Pos near : state.me.pos.near()) {
				if (state.grid[near.x][near.y] == State.WALL) continue;
				
				if (damageGrid[near.offset] < lessDamage) {
					lessDamage = damageGrid[near.offset];
					best = near;
				}
			}

			if (best != null) {
				System.err.println("Going to cell "+best+" to get only "+lessDamage);
				System.out.println(state.me.unitId+" MOVE "+best.x + " "+best.y);
				return true;
			}
		}
		
		
    return false;
  }

  private boolean shootCultists(State state) {
    int bestShooter = -1;
    int bestTarget = -1;
    double bestScore = -1;
    
    for (int i=0; i<state.unitsFE;i++) {
      if (state.units[i].owner != 0) continue;
      if (state.units[i].unitType == Unit.CULT_LEADER_TYPE) continue;
    
      for (int j=0;j<state.unitsFE;j++) {
        if (state.units[j].owner != 1) continue;
        if (state.units[j].unitType == Unit.CULT_LEADER_TYPE) continue;
        
        int dist = manhattanDist(state.units[i], state.units[j]);
        if (dist <= 6) {
        	if (! canShoot(state.grid, state.units[i].pos, state.units[j].pos, true)) continue;
        	
          double score = 0.0 
              + 1000.0 * ((7-dist) >= state.units[j].hp ? 1 : 0)
              + 1.0 * (7-dist)
              ;
          if (score > bestScore) {
            bestScore = score;
            bestShooter = i;
            bestTarget = j;
          }
        }
        
      }
    }

    if (bestShooter != -1) {
      // shoot ! TODO check if we can actually shoot ....
      System.err.println("Shoot on cultist");
      System.out.println(state.units[bestShooter].unitId+" SHOOT "+state.units[bestTarget].unitId);
      return true;
    }
    
    
    return false;
  }

  private boolean shootLeader(State state) {
    // check for all cultist if they can shoot the leader
    if (state.opp == null) return false;
    
    int bestShooter = -1;
    double bestScore = -1;
    
    for (int i=0; i<state.unitsFE;i++) {
      if (state.units[i].owner != 0) continue;
      if (state.units[i].unitType == Unit.CULT_LEADER_TYPE) continue;
    
      int dist = manhattanDist(state.units[i], state.opp);
      if (dist <= 6) {
      	if (! canShoot(state.grid, state.units[i].pos, state.opp.pos, true)) continue;
      	
      	
        double score = 0.0 
            + 1000.0 * ((7-dist) >= state.opp.hp ? 1 : 0)
            + 1.0 * (7-dist)
            ;
        if (score > bestScore) {
          bestScore = score;
          bestShooter = i;
        }
      }
    }

    if (bestShooter != -1) {
      // shoot ! TODO check if we can actually shoot ....
      System.err.println("Shoot on cult leader");
      System.out.println(state.units[bestShooter].unitId+" SHOOT "+state.opp.unitId);
      return true;

    }
    
    
    
    return false;
  }

  private boolean canShoot(int[][] grid, Pos from, Pos to, boolean debug) {
  	List<Pos> lineOfSight = Bresenham.line(from, to);
  	if (lineOfSight.isEmpty()) return false;
  	
  	if (debug) System.err.println("Check shooting possibilities from "+from+" to "+ to +" with this LoS : "+lineOfSight);
  	
  	for (Pos toCheck : lineOfSight) {
  		if (toCheck == to) continue;
  		
  		if (grid[toCheck.x][toCheck.y] != 0) {
  			if (debug)  System.err.println("Can't shoot from "+from+" to "+to+" something block at "+toCheck);
  			return false;
  		}
  	}
  	if (debug)  System.err.println("Can shoot ! ");
		return true;
	}

	private boolean convert(State state, int maxDist) {
    if (state.me == null) {
      System.err.println("no leader, need something else to do");
      return false;
    }
    
    
    Unit[] units = state.units;
    int bestId = -1;
    int bestDist = maxDist+1;
    double bestScore = Double.NEGATIVE_INFINITY;

    Pos currentPos = state.me.pos;
    Pos bestNextPos = null;
		int currentDamage = damageGrid[currentPos.offset];
    
    for (int i=0; i<state.unitsFE;i++) {
      if (units[i].owner == 0) continue;
      if (units[i].unitType == Unit.CULT_LEADER_TYPE) continue;
      
      int dist = bfs.dist(units[i].pos);
      double score = 0.0;
      
      if (units[i].unitType == Unit.CULTIST  && units[i].owner == 1) {
      	if (dist > 1) {
      		score -= 10_000.0;
      	} else if (dist == 1) {
      		score += 10_000.0;
      	}
      }

      score += 1.0 * (20 - dist);

      for (Pos p : currentPos.near()) {
      	if (state.grid[p.x][p.y] != 0) continue;
      	
      	double newScore = score;
    		newScore -= 100.0 * damageGrid[p.offset]; 
    		newScore -= 0.1 * units[i].pos.manhattan(p);
    		System.err.println("Manhattan from "+p+" to " + units[i].pos+" is "+units[i].pos.manhattan(p));
    		System.err.println("unit "+units[i].unitId+" => dist="+dist+" score  = "+newScore+" with next Step "+p);
        if (newScore > bestScore) {
          bestScore = newScore;
          bestDist = dist;
          bestNextPos = p;
          bestId = units[i].unitId;
        }
      }
      
      
    }

    if (bestDist == 1) {
      System.err.println("convert");
      System.out.println(state.me.unitId+ " CONVERT "+bestId);
      return true;
    } else if (bestId  != -1) {
      System.err.println("Move to "+bestId);
      System.out.println(state.me.unitId+" MOVE "+bestNextPos.x + " "+bestNextPos.y);
      return true;
    }

    return false;
  }

  private int manhattanDist(Unit one, Unit two) {
    return Math.abs(one.x - two.x) + Math.abs(one.y - two.y);
  }

}
