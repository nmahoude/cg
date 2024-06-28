package ooc.trailmapper;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import ooc.Direction;
import ooc.OOCMap;
import ooc.P;
import ooc.Player;
import ooc.SilentRangeCalculator;
import ooc.State;

public class FastDetector {
	private static final SilentRangeCalculator SRC = new SilentRangeCalculator();
	
	P pos[] = new P[OOCMap.S2];
	int posFE = 0;
	
	public void copyFrom(State stateModel, FastDetector model) {
	  this.posFE = model.posFE;
	  for (int i=0;i<posFE;i++) {
	    pos[i] = model.pos[i];
	  }
	}

  public void torpedo(P target) {
    int oldPosFE = posFE;
    posFE = 0;
    
    for (int i=0;i<oldPosFE;i++) {
      P c = pos[i];
      if (Player.map.isPosPossibleTorpedoLaunchPointToTarget(c, target)) {
        pos[posFE++] = c;
      }
    }
    
  }

  public void sonar(int value) {
    // no information
  }

	public void move(Direction direction) {
		int oldPosFE = posFE;
		posFE = 0;
		
		for (int i=0;i<oldPosFE;i++) {
			P c = pos[i].neighbors[direction.direction];
			if (c != P.I && !Player.map.cells.get(c.o)) {
				pos[posFE++] = c;
			}
		}
	}
	
	/**
	 * simulate a silence, but, to be fast, don't take each trail into account
	 * @param truePos
	 * @param visitedCells
	 */
	public void silence(P truePos, BitSet visitedCells) {
	  Set<P> newPositions = new HashSet<>();
	  int oldPosFE = posFE;
    posFE = 0;
    
    int lengths[] = SRC.run(truePos, visitedCells);
    
    for (int i=0;i<oldPosFE;i++) {
      newPositions.add(pos[i]);
      for (int d=0;d<4;d++) {
        P c = pos[i];
        for (int l=0;l<lengths[d];l++) {
          c = c.neighbors[d];
          if (Player.map.isIsland(c)) break; // need to recheck
          newPositions.add(c);
        }
      }
    }
    
    for (P p : newPositions) {
      pos[posFE++] = p;
    }
	}	
	
	public void surface(int sector) {
		int oldPosFE = posFE;
		posFE = 0;
		
		for (int i=0;i<oldPosFE;i++) {
			if (pos[i].sector == sector) {
				pos[posFE++] = pos[i];
			}
		}
	}
  public void trigger(P positions[], int startIndex, int length) {
    Set<P> differentPos = new HashSet<>();
    posFE = 0;
    for (int i=0;i<length;i++) {
      P tp = positions[startIndex + i];
      if (!differentPos.contains(tp)) {
        pos[posFE++] = tp;
      }
      differentPos.add(tp);
    }
  }
  
  public void mine(Direction from) {
    // Not needed, use trailmapper for triggering
  }

  
	public void addPositions(P p) {
		pos[posFE++] = p;
	}

	public boolean hasPositions(P p) {
		for (int i=0;i<posFE;i++) {
			if (pos[i] == p) return true;
		}
		return false;
	}

	public int count() {
		return posFE;
	}
	
	public void reset() {
		posFE=0;
	}
	
	public void debug() {
	  System.err.println("Fast detector size = "+posFE);
	  Player.map.debugMap("Fast detector Map", p -> hasPositions(p) ? "@" : " ");
	}

  public void init(State state, TrailMapper mapper) {
    posFE = 0;
    for (P p : mapper.potentialPositions) {
      pos[posFE++] = p;
    }
  }

  public void oppHasHit(int damage, P p) {
    int oldPosFE = posFE;
    posFE = 0;
    
    if (damage == 2) {
      pos[posFE++] = p;
      return;
    } else if (damage == 1) {
      for (int i=0;i<oldPosFE;i++) {
        P c = pos[i];
        if (c.blastDistance(p) == 1) {
          pos[posFE++] = c;
        }
      }
    } else/*damage 0 => filter out positions */ {
      for (int i=0;i<oldPosFE;i++) {
        P c = pos[i];
        if (c.blastDistance(p) > 1) {
          pos[posFE++] = c;
        }
      }
    }
  }

}
