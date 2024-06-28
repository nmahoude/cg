package coif_old;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Board {
  
  public static final int VOID = -1;
  public static final int EMPTY = 0;
  public static final int P0_ACTIVE = 1;
  public static final int P0_INACTIVE = 2;
  public static final int P1_ACTIVE = 3;
  public static final int P1_INACTIVE = 4;

  
  public int board[] = new int[12*12];
  
  public void read(Scanner in) {
    for (int y = 0; y < 12; y++) {
      String line = in.next();
      if (Player.DEBUG_INPUT) {
        System.err.println(line);
      }
      for (int x=0;x<12;x++) {
        Pos pos = Pos.get(x, y);
        switch(line.charAt(x)) {
        case '#': board[pos.index] = VOID;break;
        case '.': board[pos.index] = EMPTY;break;
        case 'O': board[pos.index] = P0_ACTIVE;break;
        case 'o': board[pos.index] = P0_INACTIVE;break;
        case 'X': board[pos.index] = P1_ACTIVE;break;
        case 'x': board[pos.index] = P1_INACTIVE;break;
        }
      }
    }
  }

  /**
   * return the frontier of the active map from the pos (out frontier)
   */
  public List<Pos> getFrontierOut(Pos init) {
    return getFrontier(init, false);
  }
  
  /**
   * return the frontier of the active map from the pos (IN frontier)
   */
  public List<Pos> getFrontierIn(Pos init) {
    return getFrontier(init, true);
  }

  private List<Pos> getFrontier(Pos init, boolean in) {
    List<Pos> frontier = new ArrayList<>();

    List<Pos> toVisit = new ArrayList<>();
    List<Pos> visited = new ArrayList<>();
    
    toVisit.add(init);
    while(!toVisit.isEmpty()) {
      Pos current = toVisit.remove(0);
      visited.add(current);
      
      for (Dir dir : Dir.values()) {
        Pos next = current.move(dir);
        if (!next.isValid()) continue;
        if (toVisit.contains(next)) continue;
        if (visited.contains(next)) continue;
        int cellValue = board[next.index];
        if (cellValue == P0_ACTIVE) {
          toVisit.add(next);
        } else if (cellValue != VOID) {
          if (in) {
            // add the current cell as it has a non P0_ACTIVE neighbor
            if (!frontier.contains(current)) {
              frontier.add(current);
            }
          } else {
            // add the next cell as it IS a non P0_ACTIVE cell
            if (!frontier.contains(next)) {
              frontier.add(next);
            }
          }
        }
      }
    }
    
    return frontier;
  }

  public int getCellValue(Pos pos) {
    return board[pos.index];
  }
}
