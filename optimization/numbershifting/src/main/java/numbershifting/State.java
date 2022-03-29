package numbershifting;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class State {
  int[] grid;
  private int width;
  private int height;
  public void read(Scanner in) {
    width = in.nextInt();
    height = in.nextInt();
    Pos.setDimension(width, height);
    
    grid = new int[width*height];
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Pos pos = Pos.from(x,y);
        grid[pos.offset] = in.nextInt();
      }
    }
  }
  
  List<Move> possibleMoves() {
    List<Move> moves = new ArrayList<>();
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Pos from = Pos.from(x,y);
        
        int value = grid[from.offset];
        if (value == 0) continue;
        
        Pos down = Pos.from(x, y+value);
        if (down != Pos.WALL && grid[down.offset] != 0) moves.add(new Move(from, down));
        
        Pos up = Pos.from(x, y-value);
        if (up != Pos.WALL && grid[up.offset] != 0) moves.add(new Move(from, up));

        Pos right = Pos.from(x+value, y);
        if (right != Pos.WALL && grid[right.offset] != 0) moves.add(new Move(from, right));

        Pos left = Pos.from(x-value, y);
        if (left != Pos.WALL && grid[left.offset] != 0) moves.add(new Move(from, left));
      }
    }
    
    return moves;
  }
  
  public void apply(Move move, boolean addition) {
    int value = grid[move.from.offset];
    grid[move.from.offset] = 0;
    
    if (addition) {
      grid[move.to.offset] += value; 
    } else {
      grid[move.to.offset] -= value;
    }
  }
}