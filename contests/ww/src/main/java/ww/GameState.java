package ww;

import java.util.Scanner;

public class GameState {
  public int grid[][] = null;
  public  int size;
  public int unitsPerPlayer;
  public int unitX[];
  public int unitY[];
  public String best;

  GameState() {
  }

  public void readInit(Scanner in) {
    size = in.nextInt();
    unitsPerPlayer = in.nextInt();

    grid = new int[size][size];
    unitX = new int[2*unitsPerPlayer];
    unitY = new int[2*unitsPerPlayer];
  }

  public void readRound(Scanner in) {
    for (int y = 0; y < size; y++) {
      String row = in.next();
      for (int x=0;x<size;x++) {
        char c = row.charAt(x);
        if (c == '.') {
          grid[x][y] = -1;
        } else {
          grid[x][y] = c-'0';
        }
      }
    }
    
    for (int i = 0; i < unitsPerPlayer; i++) {
      unitX[i] = in.nextInt();
      unitY[i] = in.nextInt();
    }
    for (int i = 0; i < unitsPerPlayer; i++) {
      unitX[unitsPerPlayer+i] = in.nextInt();
      unitY[unitsPerPlayer+i] = in.nextInt();
    }
    
    int legalActions = in.nextInt();
    for (int i = 0; i < legalActions; i++) {
      String type = in.next();
      int index = in.nextInt();
      String dir1 = in.next();
      String dir2 = in.next();
    }
  }

  boolean isOccupied(int id, int x, int y) {
    for (int i=0;i<2*unitsPerPlayer;i++) {
      if (i == id ) continue; // Id won't be there after moving
      if (unitX[i] == x && unitY[i] == y) return true;
    }
    return false;
  }
  boolean isValid(int x, int y) {
    boolean isValid = true;
    isValid = isValid && x >= 0 && x<size && y>=0 && y<size;
    isValid = isValid && grid[x][y] >=0 && grid[x][y] < 4;
    return isValid;
  }

}
