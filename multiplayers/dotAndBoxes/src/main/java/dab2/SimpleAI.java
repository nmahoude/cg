package dab2;

import java.util.ArrayList;
import java.util.List;

public class SimpleAI {

  
  
  private List<Box> filledFrom(State root, Box initial) {
    State state = new State();
    state.copyFrom(root);
    
    List<Box> toVisit = new ArrayList<>();
    List<Box> visited = new ArrayList<Box>();
    toVisit.add(initial);
    
    while (!toVisit.isEmpty()) {
      Box parent = toVisit.remove(0);
      visited.add(parent);
      
      for (Box box : parent.neighbors) {
        if (state.edgeCount(box.x, box.y) != 3) continue;
        
        Dir missingDir = Dir.missingDir(state.cell(box));
        state.set(box, missingDir);
        toVisit.add(box);
      }
    }
    
    return visited;
  }

  public void think(State root) {
    State state = new State();

    int bestScore = Integer.MIN_VALUE;
    Box bestBox = null;
    Dir bestDir = null;
    
    for (int x = 0; x < 7; x++) {
      for (int y = 0; y < 7; y++) {
        Box current = Box.box(x, y);

        
        if (root.edgeCount(x, y) != 3) continue;

        Dir missingDir = Dir.missingDir(root.cell(current));
        
        state.copyFrom(root);
        state.set(current, missingDir);
        
        int score = 1_000 + 1 + filledFrom(state, Box.box(x, y)).size();
        if (x == 0 && y == 0) {
          System.err.println("Root edges of "+current+" is "+root.edgeCount(current));
          System.err.println("score is "+score);
        }
        
        
        
        if (score > bestScore) {
          bestScore = score;
          bestBox = current;
          bestDir = missingDir;
        }
      }
    }

    for (int x = 0; x < 7; x++) {
      for (int y = 0; y < 7; y++) {
        if (root.edgeCount(x, y) >= 2) continue;

        Box current = Box.box(x, y);
        for (Dir dir : Dir.values()) {
          if (root.hasEdge(current, dir)) continue;
          
          Box adjacent = current.adjacent(dir);
          if (adjacent == Box.WALL || root.edgeCount(adjacent) < 2) {
            int score = 1;

            if (score > bestScore) {
              bestScore = score;
              bestBox = current;
              bestDir = dir;
            }
          }
        }
      }
    }
    
    // find the smaller field
    for (int x = 0; x < 7; x++) {
      for (int y = 0; y < 7; y++) {
        Box current = Box.box(x, y);
        if (root.edgeCount(current) != 2) continue;
        
        for (Dir dir : Dir.values()) {
          if (root.hasEdge(current, dir)) continue;

          state.copyFrom(root);
          state.set(current, dir);
          state.set(current, Dir.missingDir(state.cell(current))); // end filling
          
          
          List<Box> filled = filledFrom(state, current);
          System.err.println("setting "+x+" "+y+" "+dir+" let him filled "+filled.size());
          System.err.println(filled);
          int score = -100 - filled.size();

          if (score > bestScore) {
            bestScore = score;
            bestBox = current;
            bestDir = dir;
          }
        }
      }
    }
    
    if (bestScore == Integer.MIN_VALUE) {
      System.err.println("Can't fill any cells, falling back on old thinking");
      System.err.println("****** should not come here ...");
      thinkOld(root);
    } else {
      output(bestBox, bestDir);
    }
  }

  public void thinkOld(State root) {
    State state = new State();
    
    double bestScore = Double.NEGATIVE_INFINITY;
    boolean vertical = false;
    int bestX = -1, bestY = -1;

    for (int x = 0; x < 8; x++) {
      for (int y = 0; y < 7; y++) {
        if (!root.canSetVerticalEdge(x, y))
          continue;

        int edgeCount0, edgeCount1;
        if (x == 0) {
          edgeCount0 = root.edgeCount(0, y);
          edgeCount1 = -1;
        } else if (x == 7) {
          edgeCount0 = root.edgeCount(6, y);
          edgeCount1 = -1;
        } else {
          edgeCount0 = root.edgeCount(x - 1, y);
          edgeCount1 = root.edgeCount(x, y);
        }
        int score = evaluate(edgeCount0, edgeCount1);

        if (score < 0 && bestScore < 0) {
          // refine to know how many cell he can close
          state.copyFrom(root);
          state.setVerticalEdge(x, y);
          int filled = filledFrom(state, Box.box(x, y)).size();

          System.err.println("If I set vertical "+x+","+y+" he can fill "+filled+" cells");
          score = -filled;
        }
        if (score > bestScore) {
          bestScore = score;
          vertical = true;
          bestX = x;
          bestY = y;
        }
      }
    }

    for (int y = 0; y < 8; y++) {
      for (int x = 0; x < 7; x++) {
        if (!root.canSetHorizontalEdge(x, y))
          continue;

        int edgeCount0, edgeCount1;
        if (y == 0) {
          edgeCount0 = root.edgeCount(x, 0);
          edgeCount1 = -1;
        } else if (y == 7) {
          edgeCount0 = root.edgeCount(x, 6);
          edgeCount1 = -1;
        } else {
          edgeCount0 = root.edgeCount(x, y - 1);
          edgeCount1 = root.edgeCount(x, y);
        }
        int score = evaluate(edgeCount0, edgeCount1);
        if (score < 0 && bestScore < 0) {
          // refine to know how many cell he can close
          state.copyFrom(root);
          state.setHorizontalEdge(x, y);
          int filled = filledFrom(state, Box.box(x, y)).size();
          score = -filled;
        }
        if (score > bestScore) {
          bestScore = score;
          vertical = false;
          bestX = x;
          bestY = y;
        }
      }
    }

    output(vertical, bestX, bestY);
  }

  private void output(Box bestBox, Dir bestDir) {
    char letter = (char)('A'+bestBox.x);
    char number = (char) ('1' + bestBox.y);
    char dir = bestDir.dir();
    
    System.out.println("" + letter + number + " " + dir);
    
  }

  private void output(boolean vertical, int bestX, int bestY) {
    System.err.println("Best is " + bestX + " " + bestY + " " + vertical);

    if (vertical) {
      char letter;
      char number = (char) ('1' + bestY);
      char dir;
      if (bestX == 0) {
        letter = 'A';
        dir = 'L';
      } else {
        letter = (char) ('A' + (bestX - 1));
        dir = 'R';
      }
      System.out.println("" + letter + number + " " + dir);
    } else {
      char letter = (char) ('A' + bestX);
      char number;
      char dir;
      if (bestY == 0) {
        number = '1';
        dir = 'B';
      } else {
        number = (char) ('1' + (bestY - 1));
        dir = 'T';
      }

      System.out.println("" + letter + number + " " + dir);
    }
  }

  private int evaluate(int edgeCount0, int edgeCount1) {
    if (edgeCount1 == -1) {
      if (edgeCount0 == 3)
        return 100;
      else if (edgeCount0 == 2)
        return -100;
      else
        return 0;
    }

    int score = 0;

    if ((edgeCount0 == 3 && edgeCount1 == 2) || (edgeCount0 == 2 && edgeCount1 == 3)) {
      score = 10_000;
    } else if ((edgeCount0 == 3 && edgeCount1 == 3) || (edgeCount0 == 3 & edgeCount1 == 3)) {
      score = 5_000;
    } else if (edgeCount0 == 3 || edgeCount1 == 3) {
      score = 100;
    } else if (edgeCount0 == 2 || edgeCount1 == 2) {
      score = -100;
    }

    return score;
  }
}
