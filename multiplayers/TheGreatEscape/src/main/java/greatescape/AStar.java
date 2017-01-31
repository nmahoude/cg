package greatescape;

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
public class AStar {
  Map<Cell, PathItem> closedList = new HashMap<>();
  List<PathItem> openList = new ArrayList<>();
  
  List<PathItem> path = new ArrayList<>();
  
  Board board;
  Cell from;
  Target target;
  
  public AStar(Board board, Cell from, Target target) {
    this.board = board;
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
      Cell pos = visiting.pos;
      
      if (posIsOnTarget(pos, target)) {
        return visiting;
      }
      if (closedList.containsKey(pos)) {
        continue;
      }
      closedList.put(pos, visiting);
      if (pos.y > 0 && pos.wallUp == 0) {
        addToOpenList(visiting, pos , pos.up);
      }
      if (pos.y < 9 - 1 && pos.wallDown == 0) {
        addToOpenList(visiting, pos , pos.down);
      }
      if (pos.x > 0 && pos.wallLeft == 0) {
        addToOpenList(visiting, pos , pos.left);
      }
      if (pos.x < 9 - 1 && pos.wallRight == 0) {
        addToOpenList(visiting, pos , pos.right);
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

  private boolean posIsOnTarget(Cell pos, Target target) {
    return (pos.x == 0 && target == Target.LEFT)
        || (pos.x == 8 && target == Target.RIGHT) 
        || (pos.y == 8 && target == Target.DOWN);
  }

  void addToOpenList(PathItem visiting, Cell fromCell, Cell toCell) {
    if (closedList.containsKey(toCell)) {
      return;
    }
    PathItem pi = new PathItem();
    pi.pos = toCell;
    pi.cumulativeLength = visiting.cumulativeLength + 1;
    int lengthToTarget = 0;
    switch(target) {
      case DOWN:
        lengthToTarget = 8-toCell.y;
        break;
      case LEFT:
        lengthToTarget = toCell.x;
        break;
      case RIGHT:
        lengthToTarget = 8-toCell.x;
        break;
    }
    pi.totalPrevisionalLength = pi.cumulativeLength + lengthToTarget;
    pi.precedent = visiting;
    openList.add(pi);
  }
}   
/** End of PATH */