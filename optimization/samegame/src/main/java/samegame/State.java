package samegame;

import java.util.Scanner;

public class State {
  public static final int EMPTY_CELL = -1;

  public int[] grid = new int[15*15];
  public int[] colorCount = new int[5];
  
  public double aiScore;
  
  public int score;
  public boolean finished = false;
  
  public State parent;

  public Pos picked;
  
  public void read(Scanner in) {
    for (int i=0;i<5;i++) {
      colorCount[i] = 0;
    }
    for (int y = 14; y >= 0; y--) {
      for (int x = 0; x < 15; x++) {
        int color = in.nextInt(); // Color of the tile
        if (color != -1) {
          colorCount[color]++;
        }
        grid[x+15*y] = color;
      }
    }
  }

  public void copyFrom(State model) {
    this.score = model.score;
    System.arraycopy(model.grid, 0, this.grid, 0, 15*15);
    System.arraycopy(model.colorCount, 0, this.colorCount, 0, 5);
    this.finished = model.finished;
  }
  
  public void print() {
    for (int i=0;i<5;i++) {
      System.err.print(colorCount[i]);
      System.err.print("-");
    }
    System.err.println();
    
    for (int y = 14; y >= 0; y--) {
      for (int x = 0; x < 15; x++) {
        int color = grid[x+15*y];
        
        System.err.print(color == EMPTY_CELL ? " " : color);
        System.err.print(" ");
      }
      System.err.println();
    }
  }
  
  
  /**
   * the simulation !
   * @param positions
   */
  public void remove(Pos[] positions, int positionsFE) {
    score += (positionsFE - 2 )*(positionsFE - 2 );
    colorCount[grid[positions[0].offset]] -= positionsFE;
    
    for (int i=0;i<positionsFE;i++) {
      grid[positions[i].offset] = EMPTY_CELL;
    }
    
    squash();
  }

  private void squash() {
    int currentX = 0;
    
    for (int x = 0; x < 15; x++) {
      int currentY = 0;
      
      for (int y = 0; y < 15; y++) {
        if (grid[x+15*y] != EMPTY_CELL) {
          grid[currentX+15*currentY] =  grid[x+15*y];
          currentY++;
        } else {
        }
      }
      if (currentY == 0) {
        // toutes les cases sont vides, on ne bouge pas de X!
        // on peut laisser la colonne as-is
      } else {
        // remplir la fin de la colonne avec des EMPTY_CELL(-1)
        for (int y=currentY;y<15;y++) {
          grid[currentX+15*y] = EMPTY_CELL;
        }
        // prochaine colonne
        currentX++;
      }
    }

    for (int x = currentX; x < 15; x++) {
      for (int y = 0; y < 15; y++) {
        grid[x+15*y] = EMPTY_CELL;
      }
    }
    
    if (currentX == 0) {
      score += 1000; // cleaned !
      finished = true;
    }
  }

  public State childOf(State original) {
    while (this.parent != original) {
      return this.parent.childOf(original);
    }
    return this;
  }

}
