package bruteforce;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class State {
  int grid[][];
  int colCount[];
  int rowCount[];
  int gridTotal;
  
  private int width;
  private int height;
  
  
  public void read(String theGrid) {
    Scanner in = new Scanner(theGrid);
    width = in.nextInt();
    height = in.nextInt();

    grid = new int[width][height];
    colCount = new int[width];
    rowCount = new int[height];
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int cell = in.nextInt();
            grid[x][y] = cell;
            colCount[x]++;
            rowCount[y]++;
            gridTotal+= cell;
        }
    }
  }
  
  public void solve() {
    solve(grid, "");
    
    for (String i : instructions) {
      System.out.println(i);
    }
  }
  
  List<String> instructions = new ArrayList<>();
  public boolean solve(int[][] grid, String decal) {
    boolean completed = true;
    if (!checkGrid()) return false;
    
    if (decal.length() < 7) debugGrid(decal);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        
        int value = grid[x][y];
        if (value == 0) continue;
        if (value > gridTotal / 2) continue;
        
        completed = false;
        
        if (x+value < width && grid[x+value][y] != 0) {
          if (move(x,y, x+value, y, -1, "R", decal)) return true;
        }
        if (x-value >=0  && grid[x-value][y] != 0) {
            if (move(x,y, x-value, y, -1, "L", decal)) return true;
        }
        if (y+value < height && grid[x][y+value] != 0) {
          if (move(x,y, x, y+value, -1, "D", decal)) return true;
        }
        
        if (y-value >= 0 && grid[x][y-value] != 0) {
          if (move(x,y, x, y-value, -1, "U", decal)) return true;
        }

        
        
        if (x+value < width && grid[x+value][y] != 0) {
          if (move(x,y, x+value, y, +1, "R", decal)) return true;
        }
        
        if (x-value >=0  && grid[x-value][y] != 0) {
            if (move(x,y, x-value, y, +1, "L", decal)) return true;
        }
        if (y+value < height && grid[x][y+value] != 0) {
          if (move(x,y, x, y+value, +1, "D", decal)) return true;
        }
        if (y-value >= 0 && grid[x][y-value] != 0) {
          if (move(x,y, x, y-value, +1, "U", decal)) return true;
        }
      }
    }

    return completed;
  }

  private boolean checkGrid() {
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (grid[x][y] == 0 ) continue;
        
        int count = 0;
        for (int dy = 0; dy < height; dy++) {
          if (grid[x][dy] != 0) count++;
        }
        for (int dx = 0; dx < width; dx++) {
          if (grid[dx][y] != 0) count++;
        }
        if (count <= 2) {
          // System.err.println("Error at "+x+","+y+" count is "+count);
          // debugGrid("");
          return false;
        }
      }
    }
    
    return true;
  }

  private boolean move(int fromX, int fromY, int toX, int toY, int op, String dir, String decal) {
    int value = grid[fromX][fromY];
    int oldValue = grid[toX][toY]; 
    boolean ok = true;

    grid[fromX][fromY] = 0;
    colCount[fromX]--;
    rowCount[fromY]--;
    ok &= check(fromX, fromY);
    
    int newValue = Math.abs(grid[toX][toY] + op * value);
    int delta = grid[fromX][fromY] + grid[toX][toY] - newValue;
    gridTotal-= delta;
    grid[toX][toY] = newValue;
    if (grid[toX][toY] == 0) {
      colCount[toX]--;
      rowCount[toY]--;
    }
    ok &= check(toX, toY);
    
    if ((colCount[toX] == 1 && rowCount[toY] == 1) || !ok || newValue > gridTotal/2) {
      // wont be able to finish
    } else {
      if (solve(grid, decal+"  ")) {
        instruction(fromX,fromY,dir, op == -1 ? "-" : "+");
        return true;
      }
    }
    gridTotal+= delta;
    grid[fromX][fromY] = value;
    colCount[fromX]++;
    rowCount[fromY]++;

    
    if (grid[toX][toY] == 0) {
      colCount[toX]++;
      rowCount[toY]++;
    }
    grid[toX][toY] = oldValue;
    return false;
  }

  private boolean check(int fromX, int fromY) {
    if (colCount[fromX] == 1) {
      for (int y=0;y<height;y++) {
        if (grid[fromX][y] != 0 && rowCount[y] == 1) return false;
      }
    }
    if (rowCount[fromY] == 1) {
      for (int x=0;x<width;x++) {
        if (grid[x][fromY] != 0 && colCount[x] == 1) return false;
      }
    }
    return true;
  }

  private void debugGrid(String decal) {
    for (int y = 0; y < height; y++) {
      System.err.print(decal);
      for (int x = 0; x < width; x++) {
        System.err.print(""+grid[x][y]+" ");
      }
      System.err.println();
    }
  }

  private void instruction(int x, int y, String dir, String op) {
    instructions.add(0, ""+x+" "+y+" "+dir+" "+op);
  }

}
