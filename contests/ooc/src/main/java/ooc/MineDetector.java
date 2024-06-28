package ooc;

import java.util.HashSet;
import java.util.Set;

/**
 * Follow the mines of the opponent
 *
 */
public class MineDetector {

  private final OOCMap map;
  Set<P> potentialMines = new HashSet<>();
  
  public MineDetector(OOCMap map) {
    this.map = map;
  }
  
  public void dropMine(Set<P> potentialPositions) {
    for (P pos : potentialPositions) {
      for (P n : pos.neighbors) {
        if (map.isIsland(n)) continue;
        potentialMines.add(n);
      }
    }
  }
  
  public void triggerMine(P pos) {
    // not sure there was a mine, but now there is not
    potentialMines.remove(pos);
  }

  public void debug() {
    map.debugMap("Potential mines ", (P p) -> potentialMines.contains(p)? "x" : " "); 
  }

  
}
