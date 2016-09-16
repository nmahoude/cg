import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

class Player {
  private static final int MAX_ITERATIONS = 4;
  private static Scanner in;
  
  static int score1;
  static int score2;

  int fearFactor = 0 ;
  
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
    int skullCount = 0;
    
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
        int height = Math.min(HEIGHT-1,b.heights[x]);
        for (int y = 0; y <= height; y++) {
          board[x][y] = b.board[x][y];
        }
        heights[x] = b.heights[x];
      }
    }

    public void updateRow(int rowIndex, String row) {
      int y = (HEIGHT-1)-rowIndex;

      for (int x = 0; x < WIDTH; x++) {
        if (row.charAt(x) >= '1' && row.charAt(x) <= '5') {
          board[x][y] = row.charAt(x) - '0';
          heights[x] = Math.max(heights[x], Math.min(y+1, HEIGHT));
        } else if (row.charAt(x) == '0') {
          board[x][y] = SKULL;
          heights[x] = Math.max(heights[x], Math.min(y+1, HEIGHT));
          skullCount++;
        } else {
          board[x][y] = EMPTY; // empty
        }
      }
    }

    double score;
    Board bestSubBoard;
    int bestRotation;
    int bestColumn;

    int B=0;  // block destroyed
    int CP=0; // chain power
    int CB=0; // color blocks
    int GB=0; // group blocks
    
    int points;
    double nuisancePoints = 0;
    
    boolean colorDestroyed[] = new boolean[6];
    int skullsDestroyed;
    int placedBlockX1;
    int placedBlockY1;
    int placedBlockX2;
    int placedBlockY2;
    

    int getPoints() {
      return (10 * B) * Math.min(999, Math.max(1, CP + CB + GB));
    }

    void simulate(Block[] blocks, int iter) {
      simulate(blocks, 0, iter);
    }
    
    void simulate(Block[] blocks, int step, int iter) {
      if (step >= iter) {
        return;
      } else {
        Board bestBoard = null;
        int bestScore = -1;
        int maxRotation = blocks[step].color1 == blocks[step].color2 ? 2 : 4;
        for (int x=0;x<WIDTH;x++) {
          for (int rotation=0;rotation<maxRotation;rotation++) {
            Board board = prepareBoardFor(blocks[step], x,rotation);
            if (board != null) {
              board.CP = 0;
              boolean firstTime = true;
              while (board.destroyGroups(firstTime)) {
                firstTime = false;
                
                board.points += board.getPoints();
                board.CP = (board.CP == 0) ? 8 : 2*board.CP;
                board.update();
                board.B = 0;
                board.GB = 0;
              }
              board.updateCB();
              board.nuisancePoints = 1.0 * board.points / 70;

              board.simulate(blocks, step+1, iter);
              
              int score = board.points
                        + (HEIGHT-board.highestCol()) 
                        + board.skullsDestroyed*400;
              if (score > bestScore) {
                bestScore = score;
                bestBoard = board;
                bestRotation = rotation;
                bestColumn = x;
              }
            }
          }
        }
        if (bestBoard != null) {
          this.skullsDestroyed +=bestBoard.skullsDestroyed;
          
          // fear
          int fear = step + skullCount/4 + highestCol() + score2/70;
          this.score += bestBoard.score * Math.pow(0.95, fear);
          this.points += bestBoard.points;
          this.bestSubBoard = bestBoard;
        }
      }
    }

    int highestCol() {
      int high = 0;
      for (int i=0;i<WIDTH;i++) {
        high = Math.max(high, heights[i]);
      }
      return high;
    }

    void updateCB() {
      CB = 1;
      for (int i=0;i<6;i++) {
        CB*= colorDestroyed[i] ? 2 : 1;
      }
      if (CB <= 2) {
        CB = 0;
      } else {
        CB/=2;
      }
    }
    int futurePos(int x) {
      return heights[x];
    }
    Board prepareBoardFor(Block block, int x, int rotation) {
      if (!canPlaceBlock(block, x, rotation)) {
        return null;
      }
      Board board = new Board(this);
      board.placeBlock(block, x, rotation);
      return board;
    }

    void placeBlock(Block block, int x, int rotation) {
      int y = heights[x];
      if (rotation == 1 || rotation == 3) {
        if (rotation == 1) {
          placedBlockX1 = x; placedBlockY1 = y;
          placedBlockX2 = x; placedBlockY2 = y+1;
        } else {
          placedBlockX1 = x; placedBlockY1 = y+1;
          placedBlockX2 = x; placedBlockY2 =  y;
        }
        heights[x]+=2;
      } else {
        if (rotation == 0) {
          int y2 = heights[x+1];
          placedBlockX1 = x; placedBlockY1 = y;
          placedBlockX2 = x+1; placedBlockY2 =  y2;
          heights[x]+=1;
          heights[x+1]+=1;
        } else {
          int y2 = heights[x-1];
          placedBlockX1 = x; placedBlockY1 = y;
          placedBlockX2 = x-1; placedBlockY2 =  y2;
          heights[x]+=1;
          heights[x-1]+=1;
        }
      }
      board[placedBlockX1][placedBlockY1] = block.color1;
      board[placedBlockX2][placedBlockY2] = block.color2;

    }

    boolean canPlaceBlock(Block block, int x, int rotation) {
      if (rotation == 1 || rotation == 3) {
        if (board[x][(HEIGHT-1)-1] != EMPTY) {
          return false;
        }
      } else {
        if (rotation == 0) {
          if (x+1 > WIDTH-1 || board[x][HEIGHT-1] != EMPTY || board[x+1][HEIGHT-1] != EMPTY) {
            return false;
          }
        } else {
          if (x-1 < 0 || board[x-1][HEIGHT-1] != EMPTY || board[x][HEIGHT-1] != EMPTY) {
            return false;
          }
        }
      }
      return true;
    }
    
    void update() {
      for (int x=0;x<WIDTH;x++) {
        int current = 0;
        for (int y=0;y<heights[x];y++) {
          if (board[x][y] != EMPTY) {
            board[x][current++] = board[x][y];
          }
        }
        heights[x] = current;
        for (int y=current;y<HEIGHT;y++) {
          board[x][y] = EMPTY;
        }
      }
    }

    boolean destroyGroups(boolean firstTime) {
      boolean someDestroyed = false;
      if (firstTime) {
        // optimisation : the firsttime, we know which blocks to assess
        someDestroyed = destroyNeighbours(someDestroyed, placedBlockX1, placedBlockY1);
        someDestroyed |= destroyNeighbours(someDestroyed, placedBlockX2, placedBlockY2);
      } else {
        for (int x=0;x<WIDTH;x++) {
          for (int y=0;y<heights[x];y++) {
            someDestroyed = destroyNeighbours(someDestroyed, x, y);
          }
        }
      }
      return someDestroyed;
    }

    private boolean destroyNeighbours(boolean someDestroyed, int x, int y) {
      int neighbours = countNeighbours(x,y);
      if (neighbours >= 4) {
        someDestroyed = true;
        int color = board[x][y];
        colorDestroyed[color] = true;
        killNeighbours(color, x,y);

        B+=neighbours;
        GB += neighbours >= 11 ? 8 : neighbours -4;
      }
      return someDestroyed;
    }
    
    void killNeighbours(int color, int x, int y) {
      if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
        return;
      }
      int colorToKill = board[x][y];
      if (colorToKill != color && colorToKill != SKULL) {
        return;
      }

      board[x][y] = EMPTY;
      if (colorToKill == SKULL) {
        skullsDestroyed++;
        return;
      } else {
        killNeighbours(color, x - 1, y);
        killNeighbours(color, x + 1, y);
        killNeighbours(color, x, y + 1);
        killNeighbours(color, x, y - 1);
      }
    }

    int countNeighbours(int x, int y) {
      int color = board[x][y];
      if (color <1 || color > 6) {
        return 0; 
      }
      Board b = new Board(this);
      int count = countNeighbours(b, color, x, y);
      return count;
    }
    
    int countNeighbours(Board b, int color, int x, int y) {
      if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
        return 0;
      }
      if (b.board[x][y] != color) {
        return 0;
      }
      b.board[x][y] = - color;
      int count = 1;
      count += countNeighbours(b, color, x - 1, y);
      count += countNeighbours(b, color, x + 1, y);
      count += countNeighbours(b, color, x, y + 1);
      count += countNeighbours(b, color, x, y - 1);
      return count;
    }

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

    public void resetHeights() {
      for (int i=0;i<WIDTH;i++) {
        heights[i] = 0;
      }
    }
  }

  Board board = new Board(6, 12);
  Block[] blocks = new Block[8];

  public static void main(String args[]) {
    in = new Scanner(System.in);
    new Player().play();
  }

  int col = 0;

  private void play() {
    // game loop
    while (true) {
      updateReadings();
      board.simulate(blocks, MAX_ITERATIONS);
      System.out.println("" + board.bestColumn + " " + board.bestRotation);
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
      String row = in.next(); 
    }
  }

}
