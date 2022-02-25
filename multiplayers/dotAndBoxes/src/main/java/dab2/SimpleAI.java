package dab2;

import java.util.ArrayList;
import java.util.List;

public class SimpleAI {

  
  
  private List<Box> filledFrom(State root, List<Box> initial) {
    State state = new State();
    state.copyFrom(root);
    
    List<Box> toVisit = new ArrayList<>();
    List<Box> visited = new ArrayList<Box>();
    toVisit.addAll(initial);
    
    while (!toVisit.isEmpty()) {
      Box parent = toVisit.remove(0);
      visited.add(parent);
      
      for (Box box : parent.neighbors) {
        if (state.edgeCount(box.x, box.y) != 3) continue;
        
        Dir missingDir = Dir.missingDir(state.cell(box));
        toVisit.addAll(state.set(box, missingDir));
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
        List<Box> initialFilled = state.set(current, missingDir);
        
        int filledCells = filledFrom(state, initialFilled).size();
        int score = 1_000 + filledCells;
        
        if (score > bestScore) {
          bestScore = score;
          bestBox = current;
          bestDir = missingDir;
        }
        
        
        System.err.println(""+current+" "+missingDir+" => filled cells = "+filledCells);
        
        if (filledCells == 2 && root.emptyCells() != 2) {
          // TODO check that there is still cells to fill after this two ! forcing him to open another serie
          // IE : we can close a 2 cells instead of filling them in order to  open another serie ourselve !
          System.err.println("Checking if we need to let a 2 empty spaces");
          state.copyFrom(root);
          state.set(current, missingDir);
          List<Box> boxes = findAdjacentBoxWithEdges(state, current, 3);
          System.err.println("Adjacent boxes with 3 edges : "+boxes);
          if (boxes.size() == 1) {
            Box adjacent = boxes.get(0);
            Dir missingDir2 = Dir.missingDir(state.cell(adjacent));
            state.set(adjacent, missingDir2);
            boolean stillRoomToExplore = false;
            for (int yy=0;yy<7;yy++) {
              for (int xx=0;xx<7;xx++) {
                if (state.edgeCount(xx, yy) < 2) {
                  stillRoomToExplore = true;
                  break;
                }
              }
            }
            System.err.println("Still room after this one ? "+stillRoomToExplore);
            if (!stillRoomToExplore) {
              bestScore = 1003;
              bestBox = adjacent;
              bestDir = missingDir2;
            }
          }
        } else {
          
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
    if (bestScore == Integer.MIN_VALUE) {
      for (int x = 0; x < 7; x++) {
        for (int y = 0; y < 7; y++) {
          Box current = Box.box(x, y);
          if (root.edgeCount(current) != 2) continue;
          
          for (Dir dir : Dir.values()) {
            if (root.hasEdge(current, dir)) continue;
  
            state.copyFrom(root);
            state.set(current, dir);
            List<Box> initial = state.set(current, Dir.missingDir(state.cell(current))); // end filling
            
            
            List<Box> filled = filledFrom(state, initial);
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
    }
    
    if (bestScore == Integer.MIN_VALUE) {
      System.err.println("Can't fill any cells, falling back on old thinking");
      System.err.println("****** should not come here ...");
      thinkOld(root);
    } else {
      output(bestBox, bestDir);
    }
  }

  private List<Box> findAdjacentBoxWithEdges(State state, Box current, int count) {
    List<Box> boxes = new ArrayList<Box>();
    for (Box box : current.neighbors) {
      if (state.edgeCount(box) == count) {
        boxes.add(box);
      }
    }
    return boxes;
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
          List<Box> initial = state.setVerticalEdge(x, y);
          int filled = filledFrom(state, initial).size();

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
          List<Box> initial = state.setHorizontalEdge(x, y);
          int filled = filledFrom(state, initial).size();
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
