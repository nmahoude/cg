package numbershifting;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class State {
  int[] grid;
  private int width;
  private int height;
  int elementCount;

  public void read(Scanner in) {
    width = in.nextInt();
    height = in.nextInt();
    Pos.setDimension(width, height);
    
    elementCount = 0;
    grid = new int[1000*1000];
    
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Pos pos = Pos.from(x,y);
        grid[pos.offset] = in.nextInt();
        elementCount+= grid[pos.offset] == 0 ? 0 : 1;
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
  
        int currentCount = moves.size();
        addMovesIfPossible(moves, from, Pos.from(x, y+value));
        addMovesIfPossible(moves, from, Pos.from(x, y-value));
        addMovesIfPossible(moves, from, Pos.from(x+value, y));
        addMovesIfPossible(moves, from, Pos.from(x-value, y));
        
        //if (moves.size() == currentCount) return Collections.emptyList(); // no possibility for this one, dead end
      }
    }
    
    moves.sort((m1, m2) -> Integer.compare(m2.value(), m1.value()));
    
    return moves;
  }
  
  private void addMovesIfPossible(List<Move> moves, Pos from, Pos to) {
    if (to != Pos.WALL) {
      // TODO possible cut if fromValue + toValue is too big ?
      int fromValue = grid[from.offset];
      int toValue = grid[to.offset];
      if (toValue != 0) {
        moves.add(new Move(from, fromValue, to, toValue, false));
        moves.add(new Move(from, fromValue, to, toValue, true));
      }
    }
    
  }

  public void apply(Move move) {
    int value = grid[move.from.offset];
    grid[move.from.offset] = 0;
    elementCount--; // from
    
    if (move.addition) {
      grid[move.to.offset] += value; 
    } else {
      grid[move.to.offset] = Math.abs(grid[move.to.offset] - value);
      if (grid[move.to.offset] == 0) elementCount--; // result is zero
    }
  }

  // restore initial values
  public void unapply(Move move) {
    elementCount++; // always the from
    if (grid[move.to.offset] == 0) {
      elementCount++; // sometimes the to (if result was 0)
    }
    
    grid[move.from.offset] = move.fromValue;
    grid[move.to.offset] = move.toValue;
  }
  
  
  public void debug() {
    System.out.printf("%d %d", width, height);
    System.out.println();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Pos pos = Pos.from(x,y);
        System.out.printf("%2d ",grid[pos.offset]);
      }
      System.out.println();
    }
    System.out.println("Element count = "+elementCount);
  }
}