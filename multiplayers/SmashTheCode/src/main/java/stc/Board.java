package stc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;

public class Board {
  public static final int EMPTY = 0;
  public static final int SKULL = 9;
  int[][] cells = new int[6][12];
  int[] heights = new int[6];

  public final Board copy(Board board) {
    for (int x=0;x<6;x++) {
      for (int y=0;y<12;y++) {
        board.cells[x][y]= cells[x][y];
      }
      board.heights[x] = heights[x];
    }
    return board;
  }
  
  public final void updateRow(int y, String row) {
    for (int x = 0; x < 6; x++) {
      if (row.charAt(x) >= '1' && row.charAt(x) <= '5') {
        cells[x][y] = row.charAt(x) - '0';
        heights[x] = Math.max(y+1, heights[x]);
      } else if (row.charAt(x) == '0' || row.charAt(x) == '@') {
        cells[x][y] = SKULL;
        heights[x] = Math.max(y+1, heights[x]);
      } else {
        cells[x][y] = EMPTY;
      }
    }
  }

  final void putBlocks(int color1, int color2, int rotation, int baseColumn) {
    int otherColumn = baseColumn;
    switch (rotation) {
    case 0:
      otherColumn = baseColumn + 1;
      break;
    case 1:
      otherColumn = baseColumn;
      break;
    case 2:
      otherColumn = baseColumn - 1;
      break;
    case 3:
      int temp = color1;
      color1 = color2;
      color2 = temp;
      otherColumn = baseColumn;
      break;
    }
    putBlock(color1, baseColumn);
    putBlock(color2, otherColumn);
    destroyBlocks();
  }

  int points = 0;
  int CP;
  int B;
  int CB;
  int GB;
  boolean colorDestroyed[] = new boolean[6];
  final public void destroyBlocks() {
    boolean destruction = false;

    CP = 0;
    B = 0;
    points = 0;

    do {
      destruction = false;
      for (int x = 6; --x > 0;) {
        for (int y = heights[x]; --y > 0;) {
          int color = cells[x][y];
          if (color > 0 && color <= 7) {
            // int destroyed = destroyNeighbours(color, x, y, 0);
            int destroyed = nonRecursiveDestroyNeighbours(color, x, y);
            if (destroyed >= 4) {
              B+=destroyed;
              GB += destroyed >= 11 ? 8 : destroyed -4;
              GB = Math.min(8, GB);
              destruction = true;
              colorDestroyed[color] = true;
            }
          }
        }
      }
      if (destruction) {
        CB = getColorBonus();
        points += getPoints();
        B = 0;
        GB = 0;
        CB = 0;
        CP = (CP == 0) ? 8 : 2*CP;
        
        updateBoard();
        // reset some values
      }
    } while(destruction);
  }
  int getPoints() {
    return (10 * B) * Math.min(999, Math.max(1, CP + CB + GB));
  }
  int getColorBonus() {
    int CB = 1;
    for (int i=0;i<6;i++) {
      CB*= colorDestroyed[i] ? 2 : 1;
      colorDestroyed[i] = false; // reset color destroyed
    }
    if (CB <= 2) {
      CB = 0;
    } else {
      CB/=2;
    }
    return CB;
  }

  
  final public void updateBoard() {
    for (int x = 5; --x >= 0;) {
      int index = 0;
      for (int y = 0; y < 12; y++) {
        int value = cells[x][y];
        if (value != 0) {
          cells[x][index++] = cells[x][y];
        }
      }
      heights[x] = index;
      for (int y = index; y < 12; y++) {
        cells[x][y] = 0;
      }
    }
  }

  final int destroyNeighbours(int color, int x, int y, int count) {
    if (cells[x][y] == color) {
      cells[x][y] = EMPTY;
      count++;
      count = x < 5 ? destroyNeighbours(color, x + 1, y, count) : count;
      count = x > 0 ? destroyNeighbours(color, x - 1, y, count) : count;
      count = y < 11 ? destroyNeighbours(color, x, y + 1, count) : count;
      count = y > 0 ? destroyNeighbours(color, x, y - 1, count) : count;
      if (count < 4) {
        cells[x][y] = color;
        return 0;
      } else {
        // check for skulls
        if (x < 5 && cells[x + 1][y] == SKULL)
          cells[x + 1][y] = 0;
        if (x > 0 && cells[x - 1][y] == SKULL)
          cells[x - 1][y] = 0;
        if (y < 11 && cells[x][y + 1] == SKULL)
          cells[x][y + 1] = 0;
        if (y > 0 && cells[x][y - 1] == SKULL)
          cells[x][y - 1] = 0;
      }
    }
    return count;
  }

  Deque<P> toBeVisited = new ArrayDeque<>();
  Deque<P> visited = new ArrayDeque<>();
  List<P> skulls = new ArrayList<>();

  final int nonRecursiveDestroyNeighbours(int color, int x, int y) {
    toBeVisited.clear();
    visited.clear();
    skulls.clear();

    toBeVisited.add(P.ps[x][y]);
    cells[x][y] = 0;
    while (!toBeVisited.isEmpty()) {
      P p = toBeVisited.poll();
      visited.add(p);

      if (p.x < 5) {
        int value = cells[p.x + 1][p.y];
        if (value == color) {
          toBeVisited.push(P.ps[p.x + 1][p.y]);
          cells[p.x + 1][p.y] = 0;
        } else if (value == SKULL) {
          skulls.add(P.ps[p.x + 1][p.y]);
          cells[p.x + 1][p.y] = 0;
        }
      }
      if (p.x > 0) {
        int value = cells[p.x - 1][p.y];
        if (value == color) {
          toBeVisited.offer(P.ps[p.x - 1][p.y]);
          cells[p.x - 1][p.y] = 0;
        } else if (value == SKULL) {
          skulls.add(P.ps[p.x - 1][p.y]);
          cells[p.x - 1][p.y] = 0;
        }
      }
      if (p.y < 11) {
        int value = cells[p.x][p.y + 1];
        if (value == color) {
          toBeVisited.offer(P.ps[p.x][p.y + 1]);
          cells[p.x][p.y + 1] = 0;
        } else if (value == SKULL) {
          skulls.add(P.ps[p.x][p.y + 1]);
          cells[p.x][p.y + 1] = 0;
        }

      }
      if (p.y > 0) {
        int value = cells[p.x][p.y - 1];
        if (value == color) {
          toBeVisited.offer(P.ps[p.x][p.y - 1]);
          cells[p.x][p.y - 1] = 0;
        } else if (value == SKULL) {
          skulls.add(P.ps[p.x][p.y - 1]);
          cells[p.x][p.y - 1] = 0;
        }
      }
    }
    if (visited.size() >= 4) {
      // validate the kills, do nothing :)
      return visited.size();
    } else {
      // revert the change which will be small because we don't have 4 colored
      // balls
      for (P p : visited) {
        cells[p.x][p.y] = color;
      }
      for (P p : skulls) {
        cells[p.x][p.y] = SKULL;
      }
      return 0;
    }
  }

  final public void checkNeighbours(int color, P p) {
    if (p.x < 5) {
      // checkCellForColorOrSkull(color, p.x+1,p.y, toBeVisited, skulls);
      int value = cells[p.x + 1][p.y];
      if (value == color) {
        toBeVisited.push(P.ps[p.x + 1][p.y]);
        cells[p.x + 1][p.y] = 0;
      } else if (value == SKULL) {
        skulls.add(P.ps[p.x + 1][p.y]);
        cells[p.x + 1][p.y] = 0;
      }
    }
    if (p.x > 0) {
      // checkCellForColorOrSkull(color, p.x-1,p.y, toBeVisited, skulls);
      int value = cells[p.x - 1][p.y];
      if (value == color) {
        toBeVisited.push(P.ps[p.x - 1][p.y]);
        cells[p.x - 1][p.y] = 0;
      } else if (value == SKULL) {
        skulls.add(P.ps[p.x - 1][p.y]);
        cells[p.x - 1][p.y] = 0;
      }
    }
    if (p.y < 11) {
      // checkCellForColorOrSkull(color, p.x,p.y+1, toBeVisited, skulls);
      int value = cells[p.x][p.y + 1];
      if (value == color) {
        toBeVisited.push(P.ps[p.x][p.y + 1]);
        cells[p.x][p.y + 1] = 0;
      } else if (value == SKULL) {
        skulls.add(P.ps[p.x][p.y + 1]);
        cells[p.x][p.y + 1] = 0;
      }

    }
    if (p.y > 0) {
      // checkCellForColorOrSkull(color, p.x,p.y-1, toBeVisited, skulls);
      int value = cells[p.x][p.y - 1];
      if (value == color) {
        toBeVisited.push(P.ps[p.x][p.y - 1]);
        cells[p.x][p.y - 1] = 0;
      } else if (value == SKULL) {
        skulls.add(P.ps[p.x][p.y - 1]);
        cells[p.x][p.y - 1] = 0;
      }
    }
  }

  public final void checkCellForColorOrSkull(int color, int x, int y, Deque<P> toBeVisited, Set<P> skulls) {
    int value = cells[x][y];
    if (value == color) {
      toBeVisited.push(P.ps[x][y]);
      cells[x][y] = 0;
    } else if (value == SKULL) {
      skulls.add(P.ps[x][y]);
      cells[x][y] = 0;
    }
  }

  final void putBlock(int color, int column) {
    int height = heights[column];
    if (height == 11) {
      return;
    }
    cells[column][height] = color;
    heights[column] = height+1;
  }

  final public void prepare() {
    heights[0] = 0;
    heights[1] = 0;
    heights[2] = 0;
    heights[3] = 0;
    heights[4] = 0;
    heights[5] = 0;
  }

  void debug() {
    for (int y = 12 - 1; y >= 0; y--) {
      String row = "";
      for (int x = 0; x < 6; x++) {
        row += cells[x][y];
      }
      System.err.println(row);
    }
  }
}
