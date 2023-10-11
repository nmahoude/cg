package acoiaf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AI {
	private State work = new State();
	List<Pos> myUnits = new ArrayList<>();
	List<Pos> oppUnits = new ArrayList<>();
	List<Pos>  myOwnedPos = new ArrayList<>();
	List<Pos>  oppOwnedPos = new ArrayList<>();

	DistanceDMap myMap = new DistanceDMap();
	DistanceDMap oppMap = new DistanceDMap();
	DistanceDMap frontierMap = new DistanceDMap();
	
	Frontier frontier = new Frontier();

	
	public List<Action> think(State stateReadOnly) {
		work.copyFrom(stateReadOnly);
		
		extractInfos(work);
		
		
		List<Action> actions = new ArrayList<>();
		
		actions.addAll(doCuts(myUnits));
		actions.addAll(moveUnits(myUnits));
		actions.addAll(trainLevel1Units());
		
		return actions;
	}

	int[][] deltas = new int[][] { {1,0}, {-1,0}, { 0, 1}, {0, -1}};
	
	private Collection<? extends Action> doCuts(List<Pos> myUnits) {
	  List<Action> actions = new ArrayList<>();
	  
    FloodFill fill = new FloodFill();
    fill.calculate(work, State.oppHQ, O.OPP);
    int currentNumberOfOppUnits = fill.reachableUnits;
    
    double bestScore = Double.NEGATIVE_INFINITY;
    Pos bestPos = null;
    int bestDelta = -1;
    
    
    State work = new State();
	  // for each cell, try the cut
	  for (Pos current : Pos.allPositions) {
	    if (this.work.owner[current.offset] != O.ME) continue;
	    if (!this.work.active[current.offset]) continue;
	    
	    for (int i=0;i<4;i++) {
	      int[] d = deltas[i];
	      
	      Pos p = current;
	      work.copyFrom(this.work);
	      
	      while (true) {
	        p = p.safeGet(p.x+d[0], p.y+d[1]);
	        if (p == Pos.VOID) break;
	        if (work.gold[0] < O.COST) break;
	        if (!work.isWalkable(p)) break;
	        if (!work.isTrainable(p)) continue;
	        
	        work.owner[p.offset] = O.ME;
	        work.gold[0] -= O.COST;
	      }
	      
	      // count the reachable ennemies from the tower
	      fill.calculate(work, State.oppHQ, O.OPP);
	      
	      double score = currentNumberOfOppUnits - fill.reachableUnits;
	      if (score > bestScore) {
	        bestScore = score;
	        bestPos = current;
	        bestDelta = i;
	      }
	    }
	  }
	  
	  if (bestScore > 0) {
	    System.err.println("Doing a cut ! ");
	    work.copyFrom(this.work);
	    
	    Pos p = bestPos;
      int[] d = deltas[bestDelta];
      while (true) {
        p = p.get(p.x+d[0], p.y+d[1]);
        
        if (work.gold[0] < O.COST) break;
        if (!work.isWalkable(p)) break;
        if (work.owner[p.offset] != O.OPP) break;
        
        work.gold[0] -= O.COST;
        Action train = Action.train(1, p);
        this.work.apply(train);
        actions.add(train);
        
      }
	  }
	  
	  
	  
    return actions;
  }

  private void extractInfos(State state) {
		myUnits.clear();
		oppUnits.clear();
		myOwnedPos.clear();
		oppOwnedPos.clear();
		
		for (Pos pos : Pos.allPositions) {
			if (work.owner[pos.offset] == O.ME && work.unitId[pos.offset] >= 0) {
				myUnits.add(pos);
			}

			if (work.owner[pos.offset] == O.OPP && work.unitId[pos.offset] >= 0) {
				oppUnits.add(pos);
			}
			
			if (state.owner[pos.offset] == O.OPP) {
				oppOwnedPos.add(pos);
			}
			if (state.owner[pos.offset] == O.ME) {
				myOwnedPos.add(pos);
			}
		}

		myMap.calculate(state, myOwnedPos);
		oppMap.calculate(state, oppOwnedPos);
		frontier.calculate(state, myMap, oppMap);
		
		frontierMap.calculate(state, frontier.frontier());
		
		
		myUnits.sort((u1, u2) -> Double.compare(unitScore(u2), unitScore(u1))); // the greater the first
		
	}
	
	private double unitScore(Pos unit) {
	  double score = 0.0;
	  
	  double neighboursAdvantage = 0.0;
	  for (Pos n : unit.neighbors4dirs) {
	    if (work.isWalkable(n) && work.unitId[n.offset] <= 0) neighboursAdvantage += 1.0; 
	  }
	  
	  score += neighboursAdvantage;
	  
	  score += 10.0 * (frontier.frontier.contains(unit) ? 1.0 : 0.0); 
	  return score;
	}
	

	private Collection<? extends Action> moveUnits(List<Pos> myUnits) {
		List<Action> actions = new ArrayList<>();

		for (Pos unit : myUnits) {
			int unitId = work.unitId[unit.offset];
			int level = work.level[unit.offset];
			Pos bestMove = null;
			double bestScore = Double.NEGATIVE_INFINITY;
			
			
			
			for (Pos n : unit.neighbors4dirs) {
			  if (work.owner[n.offset] == O.OPP) {
			    bestMove = null;
			    break;
			  }

			  if (work.unitId[n.offset] > 0 && work.owner[n.offset] == O.ME) continue; // mine
				if (work.unitId[n.offset] > 0 && work.owner[n.offset] == O.OPP && work.level[n.offset]>= work.level[unit.offset]) continue; // can't attack 
				if (work.oppProtected[n.offset] && level < 3) continue;
				
				// if we are on the frontier, don't fall back in our territory
        if (frontier.frontier.contains(unit) 
            && !frontier.frontier.contains(n) 
            && myMap.grid[n.offset] < oppMap.grid[n.offset]) {
          continue;
        }

        
				double score = work.owner[n.offset] == O.ME ? 0.0 : 1000.0;
				
				score -= 10 * frontierMap.grid[n.offset]; // distance to frontier
				
				// prefer lot of 'empty spaces'
				double newPosScore = 0.0;
        for (Pos nn : n.neighbors4dirs) {
          if (work.isWalkable(nn) && work.owner[nn.offset] != O.ME) {
            newPosScore +=1.0;
          }
        }
        score += 1.0 * newPosScore;
				
				
				
				
				if (score > bestScore) {
					bestScore = score;
					bestMove = n;
				}
			}
			
			if (bestMove != null) {
				Action move = Action.move(unitId, unit, bestMove);
        actions.add(move);
        work.apply(move);
			}
			
		}
		
		return actions;
	}

	private Collection<? extends Action> trainLevel1Units() {
		
		
		if (work.gold[O.ME] < 10) return Collections.emptyList();

		
		List<Action> actions = new ArrayList<>();
	
		while( work.gold[O.ME] > 10) {
		
			Pos best= null;
			double bestScore = Double.NEGATIVE_INFINITY;
			
			for (Pos pos : Pos.allPositions) {
				if (work.owner[pos.offset] != O.ME) continue;
				if (!work.active[pos.offset]) continue;
				
				for (Pos n : pos.neighbors4dirs) {
        	if (work.unitId[n.offset] >= 0) continue; // something here
        	if (work.oppProtected[n.offset]) continue; // protected
        	if (!work.isTrainable(n)) continue;
        	
        	double score = 0.0;
        	
        	if (work.owner[n.offset] == O.OPP) {
        		score += 1000;
        	} else if (work.owner[n.offset] == O.NEUTRAL) {
        		score += 500;
        	} else {
        		score += 0.0;
        	}
        	
        	score -= 10 * frontierMap.grid[n.offset];
        	
        	double newPosScore = 0.0;
        	for (Pos nn : n.neighbors4dirs) {
        	  if (work.isWalkable(nn) && work.isTrainable(nn)) {
        	    newPosScore +=1.0;
        	  }
        	}
        	score += 1.0 * newPosScore;
        	
        	
        	if (score > bestScore) {
        		bestScore = score;
        		best = n;
        	}
        }
			}
			
			if (best != null) {
				int level = 1;
				
				Action train = Action.train(level, best);
        actions.add(train);
        work.apply(train);
				frontierMap.calculate(work, frontier.frontier());
			}
			
		}
		
		return actions;
	}
}
