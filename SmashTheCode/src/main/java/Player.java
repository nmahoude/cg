import java.util.*;

import javax.lang.model.type.ArrayType;
import javax.naming.PartialResultException;

import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
  private static Scanner in;

  static class P {
    final int x;
    final int y;
    P(int x, int y) {
      this.x = x;
      this.y = y;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + x;
      result = prime * result + y;
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null || getClass() != obj.getClass())
        return false;
      P other = (P) obj;
      if (x != other.x || y != other.y)
        return false;
      return true;
    }
    @Override
    public String toString() {
      return "("+x+","+y+")";
    }
  }
  
  public static class Block {
    final int color1;
    final int color2;

    Block(int c1, int c2) {
      color1 = c1;
      color2 = c2;
    }
  }
  public static class Board {
    private static final int EMPTY = 0;
    private static final int SKULL = 9;
    final int WIDTH;
    final int HEIGHT;
    int[][] board ;

    Board(int W, int H) {
      WIDTH = W;
      HEIGHT = H;
      board = new int [WIDTH][HEIGHT];
    }
    public Board(Board b) {
      this(b.WIDTH, b.HEIGHT);
      // copy board
      for (int y=0;y<HEIGHT;y++) {
        for (int x=0;x<WIDTH;x++) {
          board[x][y] = b.board[x][y];
        }
      }
    }
    public void updateRow(int rowIndex, String row) {
      for (int i=0;i<WIDTH;i++) {
        if (row.charAt(i) >= '1' && row.charAt(i) <= '5') {
          board[i][rowIndex] = row.charAt(i) - '0';
        } else if (row.charAt(i) == '0'){
          board[i][rowIndex] = SKULL;
        } else {
          board[i][rowIndex] = EMPTY; // empty
        }
      }
    }

    public void debug() {
      for (int y=0;y<HEIGHT;y++) {
        for (int x=0;x<WIDTH;x++) {
          System.err.print(""+board[x][y]+"-");
        }
        System.err.println("");
      }
      System.err.println("--------");
    }


    int findOnTop(int color1) {
      for (int x=0;x<WIDTH;x++) {
        int y=2;
        while (y<HEIGHT) {
          if (board[x][y] == color1) {
            return x;
          }
          y++;
        }
      }
      return -1;
    }
    int findFreeSpot() {
      for (int x=0;x<WIDTH;x++) {
        for (int y=0;y<2;y++) {
          if (board[x][y] == EMPTY) {
            return x;
          }
        }
      }
      return -1;
    }
    public int findColWithColor(int color1) {
      int col = findOnTop(color1);
      if (col == -1) {
        col = findFreeSpot();
      }
      return col;
    }
    public int findOnSide(int i) {
      
      return 0;
    }
   

    public int simulate(int col, Block block) {
      if (board[col][0] != EMPTY) {
        return -1;
      }
      Board fBoard = new Board(this);
      fBoard.putBlock(col, block);
      int score = fBoard.destroyBlocks();
      return score;
    }
    
    private int destroyBlocks() {
      int score = 0;
      int partialScore = 0;
      do {
        partialScore = 0;
        for (int y=0;y<HEIGHT;y++) {
          for (int x=0;x<WIDTH;x++) {
            int col = board[x][y];
            if (col > 0 && col < 6) {
              int voisins = countNeighbours(x,y);
              if (voisins >= 4) {
                partialScore +=10*destroyNeighbours(col, x,y);
              }
            }
          }
        }
        score+=partialScore;
        update();
      } while (partialScore > 0);
      return score;
    }
    
    int destroyNeighbours(int col, int x, int y) {
      if (x < 0 || x >= WIDTH || y< 0 || y>=HEIGHT) {
        return 0;
      }
      if (board[x][y] != col) {
        return 0;
      }
      board[x][y] = EMPTY;
      int count = 1;
      count+=destroyNeighbours(col, x-1, y);
      count+=destroyNeighbours(col, x+1, y);
      count+=destroyNeighbours(col, x, y+1);
      count+=destroyNeighbours(col, x, y-1);
      return count;
    }
    int countNeighbours(int x, int y) {
      /**
       * TODO optimization needed ?
       * Possibilite d'amélioration : copier le board et supprimer les entités comptées plutot que le SET
       */
      Set<P> alreadyCounted = new HashSet<>();
      int col = board[x][y];
      int count = countNeighbours(alreadyCounted, col, x, y);
      return count;
    }
    
    int countNeighbours(Set<P> alreadyCounted, int col, int x, int y) {
      if (x < 0 || x >= WIDTH || y< 0 || y>=HEIGHT) {
        return 0;
      }
      if (alreadyCounted.contains(new P(x,y))) {
        return 0;
      }
      if (board[x][y] != col) {
        return 0;
      }
      alreadyCounted.add(new P(x,y));
      int count = 1;
      count+=countNeighbours(alreadyCounted, col, x-1, y);
      count+=countNeighbours(alreadyCounted, col, x+1, y);
      count+=countNeighbours(alreadyCounted, col, x, y+1);
      count+=countNeighbours(alreadyCounted, col, x, y-1);
      return count;
    }
    
    public void putBlock(int col, Block block) {
      int pos = getFuturePos(col);
      board[col][pos-1] = block.color1;
      board[col][pos] = block.color2;
    }
    
    public int getFuturePos(int col) {
      int futurePos = 1; // can't be zero
      while (futurePos < HEIGHT && board[col][futurePos] == EMPTY) {
        futurePos+=1;
      }
      return futurePos-1;// precedent was empty, not the found one !
    }
    public String row(int row) {
      String result="";
      for (int i=0;i<WIDTH;i++) {
        if (board[i][row] == EMPTY) {
          result+=".";
        } else  {
          result+=""+board[i][row];
        }
      }
      return result;
    }
    
    public void update() {
      for (int x=0;x<WIDTH;x++) {
        int[] col = new int[WIDTH];
        int current=0;
        for (int y=HEIGHT-1;y>=0;y--) {
          if (board[x][y] != EMPTY) {
            col[current++] = board[x][y];
          }
        }
        for (int y=HEIGHT-1;y>=0;y--) {
          board[x][y] = col[(HEIGHT-1)-y];
        }
      }
    }
  }
  
  Board board = new Board(6,12);
  
  Deque<Block> blocks = new ArrayDeque<>();
  private int score1;
  private int score2;
  public static void main(String args[]) {
    in = new Scanner(System.in);
    new Player().play();
  }

  int col = 0;
  private void play() {
    // game loop
    while (true) {
      updateReadings();

      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");
      //board.debug();
      Block nextBlock = blocks.peekFirst();
      int bestCol = getBestCol(nextBlock);
      System.err.println("bestCol: "+bestCol+" for block: "+nextBlock.color1);
      System.out.println("" + bestCol);
    }
  }

  private void updateReadings() {
    blocks.clear();
    for (int i = 0; i < 8; i++) {
      Block block = new Block(in.nextInt(), in.nextInt());
      blocks.addLast(block);
    }
    score1 = in.nextInt();
    for (int i = 0; i < 12; i++) {
      String row = in.next();
      board.updateRow(i, row);
    }
    score2 = in.nextInt();
    for (int i = 0; i < 12; i++) {
      String row = in.next(); // One line of the map ('.' = empty, '0' = skull
                              // block, '1' to '5' = colored block)
    }
  }

  int getBestCol(Block block) {
    int bestCol = board.findColWithColor(block.color1);
    if (bestCol == -1) {
      col = (col + 1) % 6;
      return col;
    } 
    return bestCol;
  }

}
