package utils;

import java.util.ArrayList;
import java.util.List;

public class Explore {
  private int width;
  private int height;
  private char[][] cells;
  
  static int[][] rot = { {1, 0}, {0,1}, {-1,0}, {0,-1}};
  public Explore(int width, int height, char[][] cells) {
    this.width = width;
    this.height = height;
    this.cells = cells;
    
  }
  public P findClosedReachableCell(P kirk) {
    List<P> exploredPs = new ArrayList<>();
    List<P> toExploredPs = new ArrayList<>();
    
    toExploredPs.add(kirk);
    while (!toExploredPs.isEmpty()) {
      P p = toExploredPs.remove(0);
      exploredPs.add(p);
      char value = cells[p.x][p.y];
      if (value == '#') {
        continue;
      }
      if (value == '?') {
        System.err.println("Found '?' at "+p+", returning ");
       return p; 
      }
      if (value == '.' || value == 'T') {
        for (int r=0;r<4;r++) {
          P newP = new P(p.x+rot[r][0], p.y+rot[r][1]);
          if (isOnBoard(newP) && !exploredPs.contains(newP)) {
            toExploredPs.add(newP);
          }
        }
      }
    }
    return null;
  }
  private boolean isOnBoard(P p) {
    return p.x >=0 && p.x < width && p.y>=0 && p.y<height;
  }
}
