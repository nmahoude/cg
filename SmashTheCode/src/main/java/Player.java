import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import javax.transaction.InvalidTransactionException;

class Player {
  private static final int MAX_ITERATIONS = 3;
  private static Scanner in;

  enum Ball {
    BALL1, BALL2;
  }
  
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
    int getDeltaColumn(Ball ball, int rotation) {
      if (ball == Ball.BALL1) {
        return 0;
      } else if (rotation == 0 || rotation == 3) {
        return 0;
      } else {
        if (rotation == 1) return 1; else return -1;
      }
    }
  }

  public static class Board {
    private static final int EMPTY = 0;
    private static final int SKULL = 9;
    final int WIDTH;
    final int HEIGHT;
    int[][] board;
    int[] heights;
    
    Board(int W, int H) {
      WIDTH = W;
      HEIGHT = H;
      board = new int[WIDTH][HEIGHT];
      heights = new int[WIDTH];
    }

    public Board(Board b) {
      this(b.WIDTH, b.HEIGHT);
      // copy board
      for (int x = 0; x < WIDTH; x++) {
        for (int y = 0; y <= b.heights[x]; y++) {
          board[x][y] = b.board[x][y];
        }
        heights[x] = b.heights[x];
      }
    }

    public void updateRow(int rowIndex, String row) {
      rowIndex = (HEIGHT-1)-rowIndex;
      for (int i = 0; i < WIDTH; i++) {
        if (row.charAt(i) >= '1' && row.charAt(i) <= '5') {
          board[i][rowIndex] = row.charAt(i) - '0';
          heights[i] = Math.max(heights[i], rowIndex);
        } else if (row.charAt(i) == '0') {
          board[i][rowIndex] = SKULL;
          heights[i] = Math.max(heights[i], rowIndex);
        } else {
          board[i][rowIndex] = EMPTY; // empty
        }
      }
    }

    int B=0;  // block destroyed
    int CP=0; // chain power
    int CB=0; // color blocks
    int GB=0; // group blocks
    int nuisancePoints = 0;
    
    boolean colorDestroyed[] = new boolean[6];
    private int points;
    void simulate(Block[] blocks, int step) {
      CP = 0;
      while (destroyGroups());
      
      for (int i=0;i<6;i++) {
        CB+= colorDestroyed[i] ? 1 : 0;
      }
      points = (10 * B) * (CP + CB + GB);
      nuisancePoints = (int)(points / 70);
      if (step >= MAX_ITERATIONS) {
        return;
      }
      
    }
    
    boolean destroyGroups() {
      boolean someDestroyed = false;
      for (int x=0;x<WIDTH;x++) {
        for (int y=0;y<HEIGHT;y++) {
          int neighbours = countNeigbours(x,y);
          if (neighbours >= 4) {
            someDestroyed = true;
            int color = board[x][y];
            colorDestroyed[color] = true;
            killNeighbours(color, x,y);

            B+=neighbours;
            GB += neighbours >= 11 ? 8 : neighbours -4;
          }
        }
      }
      return someDestroyed;
    }
    
    void killNeighbours(int color, int x, int y) {
      if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
        return;
      }
      if (board[x][y] != color && board[x][y] != SKULL) {
        return;
      }

      board[x][y] = EMPTY;
      if (board[x][y] == SKULL) {
        return;
      } else {
        killNeighbours(color, x - 1, y);
        killNeighbours(color, x + 1, y);
        killNeighbours(color, x, y + 1);
        killNeighbours(color, x, y - 1);
      }
    }

    private int countNeigbours(int x, int y) {
      int color = board[x][y];
      if (color <0 || color > 6) {
        return 0; 
      }
      Set<P> alreadyCounted = new HashSet<>();
      int count = countNeighbours(alreadyCounted, color, x, y);
      return count;
    }
    int countNeighbours(Set<P> alreadyCounted, int color, int x, int y) {
      if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
        return 0;
      }
      if (alreadyCounted.contains(new P(x, y))) {
        return 0;
      }
      if (board[x][y] != color) {
        return 0;
      }
      alreadyCounted.add(new P(x, y));
      int count = 1;
      count += countNeighbours(alreadyCounted, color, x - 1, y);
      count += countNeighbours(alreadyCounted, color, x + 1, y);
      count += countNeighbours(alreadyCounted, color, x, y + 1);
      count += countNeighbours(alreadyCounted, color, x, y - 1);
      return count;
    }

//    public int[] destroyFromOneBlock(int x, int y, int[] colorBlocksCleared) {
//      int GB = 0;
//      int col = board[x][y];
//      int partialScore = 0;
//      if (col > 0 && col < 6) {
//        int voisins = countNeighbours(x, y);
//        if (voisins >= 4) {
//          GB += voisins >= 11 ? 8 : voisins -4;
//          colorBlocksCleared[col]+=voisins;
//          int[] result = destroyNeighbours(col, x, y);
//          partialScore += result[0];
//        }
//      }
//      return new int[]{partialScore, GB};
//    }
//    
//    public int destroyBlocks() {
//      return destroyBlocksFrom(null, null);
//    }
//
//    private int destroyBlocksFrom(P p1, P p2) {
//      int score = 0;
//      int partialScore;
//      boolean updateDone;
//      int[] colorBlocksCleared = new int[6];
//      int GB = 0;
//      int step = 0;
//      do {
//        partialScore = 0;
//        if (p1 == null) {
//          for (int y = 0; y < HEIGHT; y++) {
//            for (int x = 0; x < WIDTH; x++) {
//              int[] result = destroyFromOneBlock(x,y,colorBlocksCleared);
//              partialScore+=result[0];
//              GB+=result[1];
//            }
//          }
//        } else {
//          int[] result = destroyFromOneBlock(p1.x,p1.y,colorBlocksCleared);
//          partialScore+=result[0];
//          GB+=result[1];
//
//          result = destroyFromOneBlock(p2.x,p2.y,colorBlocksCleared);
//          partialScore+=result[0];
//          GB+=result[1];
//        }
//        if (partialScore > 0) { // only do if we destroyed something
//          int CB = calculateColorBonus(colorBlocksCleared);
//          int CP = 8*step;
//          int bonus = Math.min(Math.max(1, CP + CB + GB), 999);
//          score += (10 * partialScore) * bonus;
//          updateDone = update();
//          p1 = null; // reset targetted search
//        } else {
//          updateDone = false;
//        }
//      } while (updateDone);
//      return score;
//    }
//
//    private int calculateColorBonus(int[] colorBlocksCleared) {
//      int colorBonus = 1;
//      for (int i=0;i<WIDTH;i++) {
//        if (colorBlocksCleared[i] > 0) {
//          colorBonus*=2;
//        }
//      }
//      return colorBonus <= 2 ? 0 : colorBonus / 2;
//    }
//
//    int[] destroyNeighbours(int col, int x, int y) {
//      int[] result = new int[2];
//      if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
//        return result;
//      }
//      if (board[x][y] != col && board[x][y] != SKULL) {
//        return result;
//      }
//      if (board[x][y] == SKULL) {
//        board[x][y] = EMPTY;
//        result[1]++;
//        return result;
//      }
//      board[x][y] = EMPTY;
//      result[0] = 1;
//      int[] mx = destroyNeighbours(col, x - 1, y);
//      int[] px = destroyNeighbours(col, x + 1, y);
//      int[] py = destroyNeighbours(col, x, y + 1);
//      int[] my = destroyNeighbours(col, x, y - 1);
//      
//      result[0] += mx[0]+px[0]+py[0]+my[0];
//      result[1] += mx[1]+px[1]+py[1]+my[1];
//      return result;
//    }
//
//    int countNeighbours(int x, int y) {
//      /**
//       * TODO optimization needed ? Possibilite d'amélioration : copier le board
//       * et supprimer les entités comptées plutot que le SET
//       */
//      Set<P> alreadyCounted = new HashSet<>();
//      int col = board[x][y];
//      int count = countNeighbours(alreadyCounted, col, x, y);
//      return count;
//    }
//
//    int countNeighbours(Set<P> alreadyCounted, int col, int x, int y) {
//      if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
//        return 0;
//      }
//      if (alreadyCounted.contains(new P(x, y))) {
//        return 0;
//      }
//      if (board[x][y] != col) {
//        return 0;
//      }
//      alreadyCounted.add(new P(x, y));
//      int count = 1;
//      count += countNeighbours(alreadyCounted, col, x - 1, y);
//      count += countNeighbours(alreadyCounted, col, x + 1, y);
//      count += countNeighbours(alreadyCounted, col, x, y + 1);
//      count += countNeighbours(alreadyCounted, col, x, y - 1);
//      return count;
//    }
//
//    public P putBall(int column, int color) {
//      int y = getFuturePos(column);
//      if (y==-1) {
//        return null;
//      }
//      board[column][y] = color;
//      heights[column]--;
//      return new P(column,y);
//    }
//
//    public int getColumnFromBlock(Ball ball, Block block, int rotation) {
//      if (rotation == 1 || rotation == 3) {
//          return 0; 
//      } else {
//        if (rotation == 0) {
//          return ball == Ball.BALL1 ? 0 : 1;
//        } else {
//          return ball == Ball.BALL1 ? 0 : -1;
//        }
//      }
//    }
//    
//    public void putBlock(int col, Block block, int rotation) {
//      if (rotation == 1 || rotation == 3) {
//        int pos = getFuturePos(col);
//        if (rotation == 1) {
//          board[col][pos - 1] = block.color2;
//          board[col][pos] = block.color1;
//        } else {
//          board[col][pos - 1] = block.color1;
//          board[col][pos] = block.color2;
//        }
//      } else {
//        if (rotation == 0) {
//          int pos1 = getFuturePos(col);
//          int pos2 = getFuturePos(col + 1);
//          board[col][pos1] = block.color1;
//          board[col + 1][pos2] = block.color2;
//        } else {
//          if (col == 0) { return; /**/ }
//          int pos1 = getFuturePos(col);
//          int pos2 = getFuturePos(col - 1);
//          board[col][pos1] = block.color1;
//          board[col - 1][pos2] = block.color2;
//        }
//      }
//    }
//
//    public int getFuturePos(int col) {
//      int futurePos = 0; // can't be zero
//      while (futurePos < HEIGHT && board[col][futurePos] == EMPTY) {
//        futurePos += 1;
//      }
//      return futurePos - 1;// precedent was empty, not the found one !
//    }

    public String row(int rowIndex) {
      rowIndex = (HEIGHT-1)-rowIndex;
      String result = "";
      for (int i = 0; i < WIDTH; i++) {
        if (board[i][rowIndex] == EMPTY) {
          result += ".";
        } else if (board[i][rowIndex] == SKULL) {
          result +="0";
        } else {
          result += "" + board[i][rowIndex];
        }
      }
      return result;
    }

//    public boolean update() {
//      boolean moveDone = false;
//      for (int x = 0; x < WIDTH; x++) {
//        int[] col = new int[HEIGHT];
//        int current = 0;
//        for (int y = HEIGHT - 1; y >= 0; y--) {
//          if (board[x][y] != EMPTY) {
//            col[current++] = board[x][y];
//          } else {
//            moveDone = true;
//          }
//        }
//        heights[x] = (HEIGHT ) - current;
//        for (int y = HEIGHT - 1; y >= 0; y--) {
//          board[x][y] = col[(HEIGHT - 1) - y];
//        }
//      }
//      return moveDone;
//    }
//
//    int[] simulateOneStep(Block[] blocks, int step, int maxIteration) {
//      if (step >= maxIteration) {
//        // calculate a score based on resulting height
//        int heightScore = HEIGHT;
//        for (int hcol=0;hcol<WIDTH;hcol++) {
//          heightScore = Math.min(heightScore,getFuturePos(hcol));
//        }
//        return new int[] { heightScore, 0, 0 };
//      }
//      int bestCol = 0;
//      int bestScore = -1;
//      int bestRotation = 0;
//      Block currentBlock = blocks[step];
//      for (int rotation = 3; rotation >=0; rotation--) {
//        for (int i = 0; i < WIDTH; i++) {
//          Board newBoard = new Board(this);
//          int columnFromBlock1 = i+getColumnFromBlock(Ball.BALL1, currentBlock, rotation);
//          if (!newBoard.legalColumn(columnFromBlock1)) {
//            continue;
//          }
//          P p1 = newBoard.putBall(columnFromBlock1, currentBlock.color1);
//          int columnFromBlock2 = i+getColumnFromBlock(Ball.BALL2, currentBlock, rotation);
//          if (!newBoard.legalColumn(columnFromBlock2)) {
//            continue;
//          }
//          P p2 = newBoard.putBall(columnFromBlock2, currentBlock.color2);
//          
//          int score = newBoard.destroyBlocksFrom(p1, p2);
//          int[] bestChild = newBoard.simulateOneStep(blocks, step + 1, maxIteration);
//          if (bestChild[0] > 0) {
//            score += bestChild[0];
//          }
//          if (score > bestScore) {
//            bestScore = score;
//            bestCol = i;
//            bestRotation = rotation;
//          }
//        }
//      }
//      return new int[] { bestScore, bestCol, bestRotation };
//    }
//
//    private boolean legalColumn(int columnFromBlock1) {
//      return columnFromBlock1 >= 0 
//          && columnFromBlock1 < WIDTH
//          && board[columnFromBlock1][0] == EMPTY;
//    }
//
//    private boolean canPutBlock(int i, Block currentBlock, int rotation) {
//      switch (rotation) {
//        case 0:
//          return (i < WIDTH - 1)
//              && (board[i][0] == EMPTY)
//              && (board[i + 1][0] == EMPTY);
//        case 2:
//          return (i > 0)
//              && (board[i][0] == EMPTY)
//              && (board[i - 1][0] == EMPTY);
//        case 1:
//        case 3:
//          return (i < WIDTH ) 
//              && (board[i][0] == EMPTY)
//              && (board[i][1] == EMPTY);
//        default:
//          return false;
//      }
//    }
//
//    int[] getBestChoice(Block[] blocks, int maxIteration) {
//
//      int[] best = simulateOneStep(blocks, 0, maxIteration);
//      if (best[0] == -1) {
//        // choose the lower col
//        int bestSize = 0;
//        for (int i = 0; i < WIDTH; i++) {
//          int size = getFuturePos(i);
//          if (size > bestSize) {
//            bestSize = size;
//            best[0] = i;
//          }
//        }
//      }
//      //System.err.println("Best choice : " + best[0] + "," + best[1] + "," + best[2]);
//      return best;
//    }

    public void resetHeights() {
      for (int i=0;i<WIDTH;i++) {
        heights[i] = 0;
      }
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
      int[] bestSolution = board.getBestChoice(blocks, MAX_ITERATIONS);
      System.out.println("" + (bestSolution[1]) + " " + bestSolution[2]);
    }
  }

  private void updateReadings() {
    for (int i = 0; i < 8; i++) {
      blocks[i] = new Block(in.nextInt(), in.nextInt());
    }
    score1 = in.nextInt();
    board.resetHeights();
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
