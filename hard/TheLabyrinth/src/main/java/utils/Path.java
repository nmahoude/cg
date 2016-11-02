package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * PATH : A*
 *
 */
public class Path {
  Map<P, PathItem> closedList = new HashMap<>();
  List<PathItem> openList = new ArrayList<>();
  
  List<PathItem> path = new ArrayList<>();
  
  P from;
  P target;
  char[][] cells;
  private int width;
  private int height;
  public P controlRoom = null;
  
  public Path(int width, int height, char[][] cells, P from, P target) {
    this.width = width;
    this.height = height;
    this.cells = cells;
    this.from = from;
    this.target = target;
  }

  public void debug() {
    System.err.println("found a path: "+target);
    System.err.println("path ("+path.size()+ ") :  ");
    for (PathItem i : path) {
      System.err.print(i.pos+" --> ");
    }
    System.err.println("");
  }

  public List<PathItem> find() {
    PathItem item = calculus();
    path.clear();
    if (item != null) {
      calculatePath(item);
    }
    return path;
  }

  void calculatePath(PathItem item) {
    PathItem i = item;
    while (i != null) {
      path.add(0, i);
      i = i.precedent;
    }
  }
  
  PathItem calculus() {
    PathItem root = new PathItem();
    root.pos = this.from;
    openList.add(root);

    while (openList.size() > 0) {
      PathItem visiting = openList.remove(0); // imagine it's the best
      P pos = visiting.pos;
      if (pos.equals(target)) {
        return visiting;
      }
      if (closedList.containsKey(pos)) {
        continue;
      }
      closedList.put(pos, visiting);
      if (pos.y > 0) {
        addToOpenList(visiting, pos , P.get(pos.x, pos.y-1), target);
      }
      if (pos.y < height- 1) {
        addToOpenList(visiting, pos , P.get(pos.x, pos.y+1), target);
      }
      if (pos.x > 0) {
        addToOpenList(visiting, pos , P.get(pos.x-1, pos.y), target);
      }
      if (pos.x < width - 1) {
        addToOpenList(visiting, pos , P.get(pos.x+1, pos.y), target);
      }
      // sort with distances
      Collections.sort(openList, new Comparator<PathItem>() {
        @Override
        public int compare(PathItem o1, PathItem o2) {
          return Integer.compare(o1.totalPrevisionalLength, o2.totalPrevisionalLength);
        }
      });
    }
    return null; // not found !
  }

  void addToOpenList(PathItem visiting, P fromCell, P toCell, P target) {
    if (closedList.containsKey(toCell)) {
      return;
    }
    char value = cells[toCell.x][toCell.y];
    if (value == 'C') {
      controlRoom = P.get(toCell.x, toCell.y);
    }
    if (value == '.'  || value == 'T' || ((value == '?' || value == 'C' ) && target.equals(toCell))) {
      PathItem pi = new PathItem();
      pi.pos = toCell;
      pi.cumulativeLength = visiting.cumulativeLength + 1;
      pi.totalPrevisionalLength = pi.cumulativeLength + fromCell.manhattanDistance(target);
      pi.precedent = visiting;
      openList.add(pi);
    }
  }
}   
/** End of PATH */