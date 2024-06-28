package ooc;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

public class OOCMap {
	public static final int S = 15;
	public static final int S2 = S*S;
	public static final P CENTER = P.get(7, 7);
	
  public BitSet cells = new BitSet(S2);
  public Map<P, Set<P>> torpedoTargetsByPos = new HashMap<>(); 
  public Map<P, Set<P>> positionsByTorpedoTargets = new HashMap<>();
	public Map<P, Set<P>> squaredNeighbors = new HashMap<>();
	
  int distances[] = new int[S2 * S2];
  int blastD[] = new int[S2 * S2];
  int possibleDamageFromPosition[] = new int [S2 * S2];
  
  public void read(Scanner in) {
    in.nextInt();
    in.nextInt();
    in.nextInt();
    
    if (in.hasNextLine()) {
      in.nextLine();
    }
    for (int y = 0; y < S; y++) {
      String line = in.nextLine();
      for (int x = 0; x < 15; x++) {
        if (line.charAt(x) == 'x') {
          cells.set(x + S * y);
        }
      }
    }
    
    prepareSquaredNeighbors();
    prepareTorpedoTargets();
    calculateDistances();
  }

  private void calculateDistances() {
  	for (int i=0;i<S2*S2;i++) {
  		distances[i] = Integer.MAX_VALUE;
  		blastD[i] = Integer.MAX_VALUE;
  		possibleDamageFromPosition[i] = 0;
  	}
  	for (int i=0;i<S2;i++) {
      P current = P.getFromOffset(i);
      for (int dy=-1;dy<=1;dy++) {
        for (int dx=-1;dx<=1;dx++) {
          P p = P.get(current.x + dx, current.y + dy);
          if (isIsland(p)) continue;
          if (dx == 0 && dy == 0) {
            blastD[current.o * OOCMap.S2 + p.o] = 0;
          } else {
            blastD[current.o * OOCMap.S2 + p.o] = 1;
          }
        }
        
        
        List<P> tv = new ArrayList<>();
        
        distances[current.o * OOCMap.S2 + current.o] = 0;
        possibleDamageFromPosition[current.o * OOCMap.S2 + current.o] = 2;
        tv.add(current);

        while (!tv.isEmpty()) {
        	P pos = tv.remove(0);
        	for (int d=0;d<4;d++) {
        		P next = pos.neighbors[d];
        		if (isIsland(next)) {
        		  continue;
        		}
        		if (tv.contains(next) || distances[OOCMap.S2 * current.o + next.o] != Integer.MAX_VALUE) continue;

        		tv.add(next);
        		distances[OOCMap.S2 * current.o + next.o] = distances[OOCMap.S2 * current.o + pos.o]+1;
        		if (distances[OOCMap.S2 * current.o + next.o] <= 4) {
        			possibleDamageFromPosition[OOCMap.S2 * current.o + next.o] = 2;
        			for (P p : next.squaredNeighbors) {
          			possibleDamageFromPosition[OOCMap.S2 * current.o + p.o] = Math.max(possibleDamageFromPosition[OOCMap.S2 * current.o + p.o], 1);
        			}
        		}
        	}
        }
      }
    }
	}

	private void prepareSquaredNeighbors() {
    for (int y = 0; y < 15; y++) {
      for (int x = 0; x < 15; x++) {
        P current = P.get(x, y);
        
        Set<P> possibleTargets = new HashSet<P>();
        for (int dy=-1;dy<=1;dy++) {
	        for (int dx=-1;dx<=1;dx++) {
	        	if (dx == 0 && dy == 0) continue; // pass center
	        	P n = P.get(current.x+dx, current.y+dy);
	        	if (!this.isIsland(n)) {
	        		possibleTargets.add(n);
	        	}
	        }
        }
        
        squaredNeighbors.put(current, possibleTargets);
      }
    }
	}

	private void prepareTorpedoTargets() {
		initPositionsByTarget();
		
    for (int y = 0; y < 15; y++) {
      for (int x = 0; x < 15; x++) {
        P current = P.get(x, y);
        Set<P> possibleTargets = getAccessibleCellsFrom(current, 4);
        torpedoTargetsByPos.put(current, possibleTargets);

        for (P p : possibleTargets) {
        	positionsByTorpedoTargets.get(p).add(current);
        }
      }
    }
  }

	private void initPositionsByTarget() {
		for (int i=0;i<S2;i++) {
			positionsByTorpedoTargets.put(P.getFromOffset(i), new HashSet<P>());
		}
	}

  public void debugMap(String title, Function<P, String> fn) {
    System.err.println(title);
    System.err.println("____012345678901234 ");
    System.err.println("-------------------");
    
    for (int y=0;y<15;y++) {
      System.err.print(String.format("%2d |", y));
      for (int x=0;x<15;x++) {
        P current = P.get(x, y);
        char cell;
        if (isIsland(current)) cell = '-';
        else cell = fn.apply(current).charAt(0);

        System.err.print(cell);
      }
      System.err.println(String.format("| %2d", y));
    }
  }
  
  
  private Set<P> getAccessibleCellsFrom(P pos, int range) {
    Set<P> visited = new HashSet<>();
    visited.add(pos);
    for (int i=0;i<range;i++) {
      visited = addNeighbors(visited);
      
    }
    return visited;
  }

  private Set<P> addNeighbors(Set<P> existing) {
    Set<P> visited = new HashSet<>();

    for (P pos : existing) {
      visited.add(pos);
      for (int i=0;i<4;i++) {
        P next = pos.neighbors[i];
        if (isIsland(next)) continue;
        visited.add(next);
      }
    }
    return visited;
  }

  public boolean isIsland(P pos) {
    return (pos == P.I) || cells.get(pos.o);
  }

	public boolean isPosPossibleTorpedoLaunchPointToTarget(P pos, P target) {
		return distances(pos, target) <= 4;
	}

	public int distances(P from, P to) {
		return distances[from.o * S2 + to.o];
	}

  public int blastDistance(P from, P to) {
    return blastD[from.o * S2 + to.o];
  }

	public int possibleDamageFromPosition(P from, P to) {
		return possibleDamageFromPosition[from.o * S2 +  to.o];
	}
}
