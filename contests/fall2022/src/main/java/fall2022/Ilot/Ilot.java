package fall2022.Ilot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fall2022.O;
import fall2022.Pos;
import fall2022.State;

public class Ilot {
  public static final int DISPUTED = 4;
  

  public Pos p[] = new Pos[Pos.MAX_OFFSET];
  public int pFE;
  
  public int ruler = O.NEUTRAL;
  public int myTroupsCount;
  public int oppTroupsCount;
  public int nbRecyclers;
  public boolean isFullCoverByMe ;
  
  public Ilot() {
  	isFullCoverByMe = true;
  }
  
  void build(State state, Pos start, int[] visited, int currentIndex) {
    p[pFE++] = start;
    visited[start.o] = currentIndex;
    

    if (state.o[start.o] != O.ME) {
    	isFullCoverByMe = false;
    }
    
    if (ruler == O.NEUTRAL) {
    	ruler = state.o[start.o];
    } else if (ruler == O.ME) {
    } else if (ruler == O.OPP) {
    }
    
    if (state.o[start.o] == O.ME) {
    	myTroupsCount += state.ou[start.o];
    } else {
    	oppTroupsCount+= state.ou[start.o];
    }
    
    if (state.o[start.o] != O.NEUTRAL && state.o[start.o] != ruler) {
      ruler = DISPUTED;
    }
    
    for (Pos n : start.neighbors4dirs) {
      if (visited[n.o] != currentIndex) {
        if (state.rec[n.o] > 0) {
          p[pFE++] = n;
          visited[n.o] = currentIndex;
          nbRecyclers++;
        } else if (state.canMove(n)) {
          build(state, n, visited, currentIndex);
        }
      }
    }
  }
  
  public void debug() {
    for (int i=0;i<pFE;i++) {
      System.err.print(p[i]+ " ");
    }
    System.err.println();
    System.err.println("  => ruler index "+ruler);
  }

  public static List<Ilot> build(State state, List<Pos> input) {
    int startIndex = 1;
    int[] visited = new int[Pos.MAX_OFFSET]; // TODO do not instantiate here, but need to remember last index and can overflow !
    List<Ilot> ilots = new ArrayList<>();
    
    for (Pos p : input) {
      if (visited[p.o] >= startIndex) continue;
      if (!state.canMove(p)) continue;
      
      Ilot ilot = new Ilot();
      ilot.build(state, p, visited, startIndex);
      ilots.add(ilot);
    }
    
    Collections.sort(ilots, (i1, i2) -> Double.compare(i2.urgence(), i1.urgence()));
    
    
    return ilots;
  }
  
  private double urgence() {
  	double score = 0;
  	if (isDisputed()) {
  		score = 100_000;
  	}
  	score += pFE;
  	
  	return score;
	}

	public static List<Ilot> build(State state) {
    return build(state, Pos.allMapPositions);
  }
  
  @Override
	public String toString() {
  	String output = "Ruler:"+ruler+", cells="+pFE+"(real : "+(pFE - nbRecyclers)+") => ";
  	for (int i=0;i<pFE;i++) {
  		output += p[i]+",";
  	}
		return output;
	}

	public boolean isFullCoverByMe() {
		return isFullCoverByMe;
	}

	public boolean isDisputed() {
		return ruler == DISPUTED;
	}
	
	
	public Set<Pos> oppReachablePositions(State state) {
		// 1. Get all his cells in the ilot
		Set<Pos> allOppReachablePositions = new HashSet<>(); 

		Deque<Pos> current = new ArrayDeque<>();
		// TODO le precalculer dans l'ilot, ca semble déjà fait, mais pas conservé
		for (int i=0;i<this.pFE;i++) {
			Pos opp = this.p[i];
			if (state.o[opp.o] != O.OPP) continue;
			if (!state.canMove(opp)) continue;
			
			allOppReachablePositions.add(opp);
			current.add(opp);
		}
		
		// 2. from all the positions,
		while(!current.isEmpty()) {
			Pos p = current.poll();
			
			for (Pos n : p.neighbors4dirs) {
				if (state.o[n.o] == O.ME || !state.canMove(n)) continue;
				if (allOppReachablePositions.contains(n)) continue;

				allOppReachablePositions.add(n);
				current.offer(n);
			}
		}
		
		return allOppReachablePositions;
	}

	public int size() {
		return pFE;
	}

	public static int mySureCells(List<Ilot> originalIlots) {
		int count = 0;
		for (Ilot ilot : originalIlots) {
			if (ilot.ruler == O.ME) count += ilot.size();
		}
		return count;
	}
	public static int myPotentialCells(List<Ilot> originalIlots) {
		int count = 0;
		for (Ilot ilot : originalIlots) {
			if (ilot.ruler == O.ME) count += ilot.size();
			if (ilot.isDisputed()) count += ilot.size();
		}
		return count;
	}

	public static int oppPotentialCells(List<Ilot> originalIlots) {
		int count = 0;
		for (Ilot ilot : originalIlots) {
			if (ilot.ruler == O.OPP) count += ilot.size();
			if (ilot.isDisputed()) count += ilot.size();
		}
		return count;
	}
}
