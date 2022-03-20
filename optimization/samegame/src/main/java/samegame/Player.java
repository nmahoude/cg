package samegame;

import java.util.Scanner;

public class Player {
  int[] grid = new int[15*15];
  int[] toCheck = new int[15*15];
  int maxX = 15;

  int toCheckCurrentId = 1;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    new Player().play(in);
  }

  private void play(Scanner in) {
    while (true) {
  
      for (int y = 14; y >= 0; y--) {
        for (int x = 0; x < 15; x++) {
          int color = in.nextInt(); // Color of the tile
          grid[x+15*y] = color;
          if (y == 0 && color == -1) {
            maxX = x;
          }
        }
      }

      int bestX = 0; 
      int bestY = 0;
      int bestCount = 0;
      
      toCheckCurrentId++;
      for (int y = 0; y < 15; y++) {
        for (int x = 0; x < 15; x++) {
          if (toCheck[x+15*y]  == toCheckCurrentId) continue;
          if (grid[x+15*y] == -1 ) continue;
      
          int count = floodfill(grid[x+15*y], x,y,toCheck);
          System.err.println("Count @ "+x+" "+y+" => "+count);
          if (count >= 2 && count > bestCount) {
            bestCount = count;
            bestX = x;
            bestY = y;
          }
        }
      }
      
      
      
      System.out.println(""+bestX+" "+bestY+" Hello SameGame\\n:-)"); // Selected tile "x y [message]".
    }

  }

  private int floodfill(int color, int x, int y, int[] toCheck) {
    if (grid[x+15*y] != color) return 0;
    if (toCheck[x+15*y] == toCheckCurrentId) return 0;
    
    toCheck[x+15*y] = toCheckCurrentId;
    
    int count = 1;
    if (x>0) count += floodfill(color, x-1, y, toCheck);
    if (y>0) count += floodfill(color, x, y-1, toCheck);
    
    if (x<14) count += floodfill(color, x+1, y, toCheck);
    if (y<14) count += floodfill(color, x, y+1, toCheck);
    
    return count;
  }
  
}
