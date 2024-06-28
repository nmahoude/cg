package ooc;

import java.util.ArrayList;
import java.util.List;

public class FreeCellsDetector2 {
  public int visitedCells[] = new int[OOCMap.S2];
  
  
  static int cache[] = new int[OOCMap.S2];
  
  public static void resetCache() {
    for (int i=0;i<OOCMap.S2;i++) {
      cache[i] = -1;
    }
  }
  
  /** how many cells possible from the pos */
  public int countFreeCellInDir(State state, P pos) {
    
    if (cache[pos.o]!= -1 ) {
      return cache[pos.o];
    }
    
    List<P> toVisit = new ArrayList<>();
    for (int i=0;i<4;i++) {
      P next = pos.neighbors[i];
      toVisit.add(next);
    }
    int accessibleCells = 0;
    
    while (!toVisit.isEmpty()) {
      P current = toVisit.remove(0);
      if (state.map.isIsland(current)) continue;
      if (visitedCells[current.o] != 0) continue;
      if (state.isVisitedCells(current)) continue;

      visitedCells[current.o] = 1;
      accessibleCells++;
      
      for (int i=0;i<4;i++) {
        P next = current.neighbors[i];
        toVisit.add(next);
      }
    }
    
    cache[pos.o]= accessibleCells; 
    return accessibleCells;
  }
}
