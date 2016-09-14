import java.util.*;

import javax.lang.model.type.ArrayType;

import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {
  private static Scanner in;

  public static class Block {
    final int color1;
    final int color2;

    Block(int c1, int c2) {
      color1 = c1;
      color2 = c2;
    }
  }
  public static class Board {
    private static final int EMPTY = -1;
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
        if (row.charAt(i) >= '0' && row.charAt(i) <= '5') {
          board[i][rowIndex] = row.charAt(i) - '0';
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
    public int getScore(int col, Block block) {
      if (board[col][0] != EMPTY) {
        return 0;
      }
      Board fBoard = new Board(this);
      fBoard.put(col, block);
      int score = fBoard.destroyBlocks();
      return 1;
    }
    
    private int destroyBlocks() {
      return 0;
    }
    public void put(int col, Block block) {
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

      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");
      //board.debug();
      Block nextBlock = blocks.peekFirst();
      int bestCol = getBestCol(nextBlock);
      System.err.println("bestCol: "+bestCol+" for block: "+nextBlock.color1);
      System.out.println("" + bestCol);
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
