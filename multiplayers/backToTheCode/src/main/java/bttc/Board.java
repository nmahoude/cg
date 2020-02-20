package bttc;

import java.util.List;
import java.util.Random;

public class Board {
  
  public static final int EMPTY = 0;
  public static final int WALL = -1;
  
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
  
  public int cells[][] = new int[35][20];
  
  public int free = 0;
  public int scores[] = new int[4];
  
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
        int player = value-'0';
        scores[player] += 1;
        cells[x][rowIndex] = player+1;
      }
    }
  }
  
  public P findClosestFreeCell(P startingPoint, List<P> pointsToCheck, List<P> pointsChecked) {
    while (!pointsToCheck.isEmpty()) {
      P currentP = pointsToCheck.remove(0);
      pointsChecked.add(currentP);
      if (getCell(currentP) == EMPTY && !startingPoint.equals(currentP)) {
        return currentP;
      } else {
        int decal = new Random().nextInt(4);
        for (int i=0;i<4;i++) {
          int index = (i+decal) % 4;
          P p = new P(currentP.x+rot[index][0], currentP.y+rot[index][1]);
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
  public void debugInfos() {
    for (int i=0;i<4;i++) {
      System.err.println("P"+i+" -> "+scores[i]);
    }
    System.err.println("Free: "+free);
  }
}
