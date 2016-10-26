package bttc;

import java.util.List;

public class Board {
  
  private static final int EMPTY = 0;
  private static final int WALL = -1;
  
  public static final int PLAYER1 = 1;
  public static final int PLAYER2 = 2;
  public static final int PLAYER3 = 3;
  public static final int PLAYER4 = 4;

  int rot[][] = {
      {1,0},
      {0,1},
      {-1,0},
      {0,-1}
  };
  
  int cells[][] = new int[35][20];
  
  int free = 0;
  int scores[] = new int[4+1];
  
  public void reinit() {
    free = 0;
    for (int i=0;i<scores.length;i++) {
      scores[i] = 0;
    }
  }
  public void addRow(int rowIndex, String row) {
    for (int x=0;x<35;x++) {
      char value = row.charAt(x);
      if (value == '.') {
        free++;
        cells[x][rowIndex] = EMPTY;
      } else {
        int player = 1+value-'0';
        scores[player] += 1;
        cells[x][rowIndex] = player;
      }
    }
  }
  
  public P findClosestFreeCell(List<P> pointsToCheck, List<P> pointsChecked) {
    while (!pointsToCheck.isEmpty()) {
      P currentP = pointsToCheck.remove(0);
      pointsChecked.add(currentP);
      if (getCell(currentP) == EMPTY) {
        return currentP;
      } else {
        for (int i=0;i<4;i++) {
          P p = new P(currentP.x+rot[i][0], currentP.y+rot[i][1]);
          if (!pointsChecked.contains(p) && !pointsToCheck.contains(p)) {
            pointsToCheck.add(p);
          }
        }
      }
    }
    return new P(0,0);
  }
  
  private int getCell(P p) {
    return getCell(p.x, p.y);
  }
  
  private int getCell(int i, int j) {
    if (i<0 || i>35-1 || j<0 || j>20-1) {
      return WALL;
    }
    return cells[i][j];
  }
}
