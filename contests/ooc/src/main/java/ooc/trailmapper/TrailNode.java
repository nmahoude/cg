package ooc.trailmapper;

import java.util.BitSet;

import ooc.OOCMap;
import ooc.P;
import ooc.Player;

public class TrailNode {

	public P currentPos;
	public BitSet trail = new BitSet(OOCMap.S2);
  BitSet mines = new BitSet(OOCMap.S2);
  BitSet dropMineAt = new BitSet(OOCMap.S2);
  int damageTaken = 0;
	int aggregate = 1;
  double potentiality;

	TrailNode(P pos) {
		currentPos = pos;
		clearTrail();
	}

	public void reset() {
		trail.clear();
		mines.clear();
		dropMineAt.clear();

		damageTaken = 0;
		aggregate = 1;
		currentPos = P.I;
		potentiality = 0.0;
	}
	
	void copyFrom(TrailNode model) {
	  this.currentPos = model.currentPos;
    this.trail.clear();
    this.trail.or(model.trail);
    
    this.mines.clear();
    this.mines.or(model.mines);
    dropMineAt.clear();
    this.dropMineAt.or(model.dropMineAt);
    
    this.damageTaken = model.damageTaken;
    this.aggregate = model.aggregate;
    this.potentiality = model.potentiality;
  }
	
	public void move(P nextPos) {
		trail.set(currentPos.o);
		currentPos = nextPos;
	}

	public void clearTrail() {
		trail.clear();
	}

	
	public TrailNode move(int direction) {
		P nextPos = currentPos.neighbors[direction];
		if (Player.map.isIsland(nextPos)) return null; // would be on an island
		if (trail.get(nextPos.o)) return null; // can't go here, trail
		
		// count possible direction & infer potentiallity
		int t=0;
		for (int d=0;d<4;d++) {
		  P n = nextPos.neighbors[d];
	    if (Player.map.isIsland(n) || trail.get(n.o)) {
	    } else {
	      t++;
	    }
		}
		potentiality+=1.0 / t ;
		
		
		move(nextPos);
		return this;
	}
	
	public TrailNode surfaceInSector(int sector) {
		if (currentPos.sector == sector) {
			clearTrail(); // no more trail :(
			// but keep mines & mineAt
			return this;
		} else {
			return null;
		}
	}
	
	public TrailNode fireTorpedo(P target) {
		if (Player.map.isPosPossibleTorpedoLaunchPointToTarget(currentPos, target)) {
		  damage(target);
			return this;
		} else {
			return null;
		}
	}
	
	public int silentMove(TrailNode nodes[], int currentFE) {
		
		nodes[currentFE++] = this;
		
		for (int d = 0; d < 4; d++) {
			TrailNode current = this;
      for (int r = 1; r <= 4; r++) {
      	// create a new reality
      	P nextPos = current.currentPos.neighbors[d];
        if (Player.map.isIsland(nextPos)) break;
        if (current.trail.get(nextPos.o)) break;
        
        TrailNode next = TNCache.pop();
        next.copyFrom(current);
        next.move(nextPos);
        nodes[currentFE++] = next;
        
        current = next;
      }
    }
		return currentFE;
	}

  public TrailNode sonarInSector(int sector) {
		if (currentPos.sector == sector) 
			return this; 
		else 
			return null;
	}

	public TrailNode sonarNOTInSector(int sector) {
		if (currentPos.sector != sector) 
			return this; 
		else 
			return null;
	}

	public void dropMine() {
	  dropMineAt.set(currentPos.o);
	  for (int d=0;d<4;d++) {
	    P droppedAt = currentPos.neighbors[d];
	    if (!Player.map.isIsland(droppedAt)) {
	      mines.set(droppedAt.o);
	    }
	  }
	}

  public TrailNode trigger(BitSet allTrigers, P pos) {
    if (mines.get(pos.o)) {
      if (Player.D_TRIGGERS) System.err.println("Trigger ! @"+pos);
      int count = 0;
      P _p = P.I;
      for (P n: pos.neighbors) {
        if (n == P.I) continue;
        if ( dropMineAt.get(n.o)) {
          count++;
          _p = n;
          if (Player.D_TRIGGERS) System.err.println("opp drops mine @"+n+" , count is "+count);
        }
      }
      if (count == 1) {
        if (Player.D_TRIGGERS) System.err.println(" on peut supprimer "+_p+", c'est le seul");
        dropMineAt.clear(_p.o);
        for (P n : _p.neighbors) {
          if (n == pos || n == P.I) continue;
          count = 0;
          for (P nn : n.neighbors) {
            if (nn == P.I) continue;
            if (dropMineAt.get(nn.o)) count++;
          }
          if (count == 0) {
            mines.clear(n.o);
          }
        }
      }

      mines.andNot(allTrigers);
      damage(pos);
      return this;
    } else {
      return null;
    }
  }

  public TrailNode checkDamageDealt(int deltaLife) {
    if (damageTaken == deltaLife) {
      return this;
    } else {
      return null;
    }
  }

  public void resetDamageTaken() {
    damageTaken = 0;
  }

  public void damage(P target) {
    if (target == null || target == P.I) {
      return;
    } else if (currentPos == target) {
      damageTaken+=2;
    } else if (currentPos.blastDistance(target) == 1) {
      damageTaken+=1;
    }
  }

  public int getDamageTaken() {
    return damageTaken;
  }
}
