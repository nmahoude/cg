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
  private static final int MAX_ITERATIONS = 8;
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
      return "(" + x + "," + y + ")";
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
    int[][] board;
    private boolean result;

    Board(int W, int H) {
      WIDTH = W;
      HEIGHT = H;
      board = new int[WIDTH][HEIGHT];
    }

    public Board(Board b) {
      this(b.WIDTH, b.HEIGHT);
      // copy board
      for (int y = 0; y < HEIGHT; y++) {
        for (int x = 0; x < WIDTH; x++) {
          board[x][y] = b.board[x][y];
        }
      }
    }

    public void updateRow(int rowIndex, String row) {
      for (int i = 0; i < WIDTH; i++) {
        if (row.charAt(i) >= '1' && row.charAt(i) <= '5') {
          board[i][rowIndex] = row.charAt(i) - '0';
        } else if (row.charAt(i) == '0') {
          board[i][rowIndex] = SKULL;
        } else {
          board[i][rowIndex] = EMPTY; // empty
        }
      }
    }

    public void debug() {
      for (int y = 0; y < HEIGHT; y++) {
        for (int x = 0; x < WIDTH; x++) {
          System.err.print("" + board[x][y] + "-");
        }
        System.err.println("");
      }
      System.err.println("--------");
    }

    private int destroyBlocks() {
      int score = 0;
      int partialScore = 0;
      do {
        partialScore = 0;
        for (int y = 0; y < HEIGHT; y++) {
          for (int x = 0; x < WIDTH; x++) {
            int col = board[x][y];
            if (col > 0 && col < 6) {
              int voisins = countNeighbours(x, y);
              if (voisins >= 4) {
                int[] result = destroyNeighbours(col, x, y);
                partialScore += 10 * result[0];
              }
            }
          }
        }
        score += partialScore;
        update();
      } while (partialScore > 0);
      return score;
    }

    int[] destroyNeighbours(int col, int x, int y) {
      int[] result = new int[2];
      if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
        return result;
      }
      if (board[x][y] != col && board[x][y] != SKULL) {
        return result;
      }
      if (board[x][y] == SKULL) {
        board[x][y] = EMPTY;
        result[1]++;
        return result;
      }
      board[x][y] = EMPTY;
      result[0] = 1;
      int[] mx = destroyNeighbours(col, x - 1, y);
      int[] px = destroyNeighbours(col, x + 1, y);
      int[] py = destroyNeighbours(col, x, y + 1);
      int[] my = destroyNeighbours(col, x, y - 1);
      
      result[0] = mx[0]+px[0]+py[0]+my[0];
      result[1] = mx[1]+px[1]+py[1]+my[1];
      return result;
    }

    int countNeighbours(int x, int y) {
      /**
       * TODO optimization needed ? Possibilite d'amélioration : copier le board
       * et supprimer les entités comptées plutot que le SET
       */
      Set<P> alreadyCounted = new HashSet<>();
      int col = board[x][y];
      int count = countNeighbours(alreadyCounted, col, x, y);
      return count;
    }

    int countNeighbours(Set<P> alreadyCounted, int col, int x, int y) {
      if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
        return 0;
      }
      if (alreadyCounted.contains(new P(x, y))) {
        return 0;
      }
      if (board[x][y] != col) {
        return 0;
      }
      alreadyCounted.add(new P(x, y));
      int count = 1;
      count += countNeighbours(alreadyCounted, col, x - 1, y);
      count += countNeighbours(alreadyCounted, col, x + 1, y);
      count += countNeighbours(alreadyCounted, col, x, y + 1);
      count += countNeighbours(alreadyCounted, col, x, y - 1);
      return count;
    }

    public void putBlock(int col, Block block, int rotation) {
      if (rotation == 1) {
        int pos = getFuturePos(col);
        if (pos == 0) {
          // FIXME don't even call in this case !
          return; // no block put
        }
        board[col][pos - 1] = block.color1;
        board[col][pos] = block.color2;
      } else {
        int pos1 = getFuturePos(col);
        int pos2 = getFuturePos(col + 1);
        if (pos1 == -1 || pos2 == -1) {
          // FIXME don't even call in this case !
          return; // no block put
        }
        board[col][pos1] = block.color1;
        board[col + 1][pos2] = block.color2;
      }
    }

    public int getFuturePos(int col) {
      int futurePos = 0; // can't be zero
      while (futurePos < HEIGHT && board[col][futurePos] == EMPTY) {
        futurePos += 1;
      }
      return futurePos - 1;// precedent was empty, not the found one !
    }

    public String row(int row) {
      String result = "";
      for (int i = 0; i < WIDTH; i++) {
        if (board[i][row] == EMPTY) {
          result += ".";
        } else {
          result += "" + board[i][row];
        }
      }
      return result;
    }

    public void update() {
      for (int x = 0; x < WIDTH; x++) {
        int[] col = new int[HEIGHT];
        int current = 0;
        for (int y = HEIGHT - 1; y >= 0; y--) {
          if (board[x][y] != EMPTY) {
            col[current++] = board[x][y];
          }
        }
        for (int y = HEIGHT - 1; y >= 0; y--) {
          board[x][y] = col[(HEIGHT - 1) - y];
        }
      }
    }

    int[] simulateOneStep(Block[] blocks, int step, int maxIteration) {
      if (step >= maxIteration) {
        // calculate a score based on resulting height
        int heightScore = HEIGHT;
        for (int hcol=0;hcol<WIDTH;hcol++) {
          heightScore = Math.min(heightScore,getFuturePos(hcol));
        }
        return new int[] { heightScore, 0, 0 };
      }
      int bestCol = 0;
      int bestScore = -1;
      int bestRotation = 0;
      Block currentBlock = blocks[step];
      for (int rotation = 1; rotation >=0; rotation--) {
        for (int i = 0; i < WIDTH; i++) {
          if (!this.canPutBlock(i, currentBlock, rotation)) {
            continue;
          }
          Board newBoard = new Board(this);
          newBoard.putBlock(i, currentBlock, rotation);
          int score = newBoard.destroyBlocks();
          int[] bestChild = newBoard.simulateOneStep(blocks, step + 1, maxIteration);
          if (bestChild[0] > 0) {
            score += bestChild[0];
          }
          if (score > bestScore) {
            bestScore = score;
            bestCol = i;
            bestRotation = rotation;
          }
        }
      }
      return new int[] { bestScore, bestCol, bestRotation };
    }

    private boolean canPutBlock(int i, Block currentBlock, int rotation) {
      if (rotation == 0) {
        return (i < WIDTH - 1)
            && (board[i][0] == EMPTY)
            && (board[i + 1][0] == EMPTY);
      } else {
        return (i < WIDTH ) 
            && (board[i][0] == EMPTY)
            && (board[i][1] == EMPTY);
      }
    }

    int[] getBestChoice(Block[] blocks, int maxIteration) {

      int[] best = simulateOneStep(blocks, 0, maxIteration);
      if (best[0] == -1) {
        // choose the lower col
        int bestSize = 0;
        for (int i = 0; i < WIDTH; i++) {
          int size = getFuturePos(i);
          if (size > bestSize) {
            bestSize = size;
            best[0] = i;
          }
        }
      }
      System.err.println("Best choice : " + best[0] + "," + best[1] + "," + best[2]);
      return best;
    }
  }

  Board board = new Board(6, 12);
  Block[] blocks = new Block[8];

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
      // board.debug();
      int[] bestSolution = board.getBestChoice(blocks, MAX_ITERATIONS);
      System.out.println("" + bestSolution[1] + " " + bestSolution[2]);
    }
  }

  private void updateReadings() {
    for (int i = 0; i < 8; i++) {
      blocks[i] = new Block(in.nextInt(), in.nextInt());
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

}
