import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class Player {
  private static final int MAX_ITERATIONS = 3;
  private static Scanner in;
  
  static int score1;
  
  static int lastScore2 = 0;
  static int score2 = 0;
  static int currentThreatSkulls;
  static int currentPoints;
  static int fearFactor = 0 ;
  
  static void updateOponentScore(int currentOpponentPoints) {
    fearFactor = 0; // reset fearFactor
    lastScore2 = score2;
    score2 = currentOpponentPoints;

    int points = score2 - lastScore2;

    if (points < 0) {
      return;
    }
    
    points +=currentPoints;
    currentThreatSkulls += (int)(points / 70);
    currentPoints = points % 70;    
    currentThreatSkulls = currentThreatSkulls % 6; // too late for the others, there's already in
    fearFactor = currentThreatSkulls;
  }
  
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
  public static class CalculationResult {
    int points;
  }
  
  public static class Simulation {
    private StepSimulation zeroThStep; // this is the 0'th step, nothing to get from it !
    
    public Simulation(Board board, Block[] blocks, int maxIterations) {
      zeroThStep = new StepSimulation(board, blocks, 0, maxIterations, -1, -1);
      zeroThStep.simulate();
    }

    StepSimulation firstStep() {
      return zeroThStep.bestSS;
    }
    StepSimulation simulatedResult() {
      StepSimulation preLast = zeroThStep, last = zeroThStep;
      while (last != null) {
        preLast = last;
        last = last.bestSS;
      }
      return preLast;
    }

  }

  public static class StepSimulation {
    boolean successful = false;
    Player.Board board;
    Block[] blocks;
    int step;
    int maxIterations;
    int column;
    int rotation;
    private P[] positionnedBlocks;

    int points;
    int totalPoints;
    double score; // only output is a score
    public StepSimulation bestSS;
    
    public StepSimulation(Player.Board board, Block[] blocks, int step, int maxIterations, int column, int rotation) {
      this.board = board;
      this.blocks = blocks;
      this.step = step;
      this.maxIterations = maxIterations;
      this.column = column;
      this.rotation = rotation;
    }
    
    StepSimulation nextStep() {
      return bestSS;
    }
    private void simulate() {
      Block currentBlock;
      if (step > 0) {
        currentBlock = blocks[step-1];
        positionnedBlocks = board.placeBlock(currentBlock, column, rotation);
        board.calculate(this);
      }
      
      bestSS = null;
      double bestScore = -10000;

      // do one step further
      if (step < maxIterations) {
        currentBlock = blocks[step];
        int maxRotation = currentBlock.color1 == currentBlock.color2 ? 2 : 4;
        for (int x=0;x<board.WIDTH;x++) {
          for (int rotation=0;rotation<maxRotation;rotation++) {
            if (!board.canPlaceBlock(currentBlock, x, rotation)) {
              continue;
            }
            StepSimulation ss = new StepSimulation(new Board(board), blocks, step+1, maxIterations, x, rotation);
            ss.simulate();
            if (ss.score > bestScore) {
              bestScore = ss.score;
              bestSS = ss;
            }
          }
        }
      }
      //TODO calculate score from simulation data & heuristics
      updatePoints();
      calculateScoreFromHeuristics();
    }
    void updatePoints() {
      points = board.points;
      if (bestSS != null) {
        totalPoints+=score + bestSS.score;
      }
    }
    
    private void calculateScoreFromHeuristics() {
      totalPoints = points;
      score = points 
          - board.highestCol()
          - board.skullCount
          ;
      if (positionnedBlocks != null) { score += nextToNeighbours(); }
      if (bestSS != null) { score += 0.5*bestSS.score; }
    }

    private int nextToNeighbours() {
      int neighbours = board.countNeighbours(positionnedBlocks[0].x, positionnedBlocks[0].y);
      neighbours += board.countNeighbours(positionnedBlocks[1].x, positionnedBlocks[1].y);
      return neighbours;
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

    public void calculate(StepSimulation stepSimulation) {
      List<P> pointsToCheck = Arrays.asList(stepSimulation.positionnedBlocks[0],
          stepSimulation.positionnedBlocks[1]);
      CP = 0;
      B = 0;
      points = 0;
      while (destroyGroups(pointsToCheck)) {
        CB = getCB();
        points += getPoints();
        // reset some values
        B = 0;
        GB = 0;
        CB = 0;
        CP = (CP == 0) ? 8 : 2*CP;
        pointsToCheck = update();
      }
      
      nuisancePoints = 1.0 * points / 70;
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

    int B=0;  // block destroyed
    int CP=0; // chain power
    int CB=0; // color blocks
    int GB=0; // group blocks
    
    int points;
    double nuisancePoints = 0;
    
    boolean colorDestroyed[] = new boolean[6];
    int skullsDestroyed;
    int skullsDestroyedPoints;
    int placedBlockX1;
    int placedBlockY1;
    int placedBlockX2;
    int placedBlockY2;
    


    double H_POINTS = 1;
    double H_SKULLSDESTROYED = 100;
    double H_HEIGHT = -10;
    double H_BESTCHILD = 0.9 ;
    double H_SKULLTHREAT = 2;

    int getPoints() {
      return (10 * B) * Math.min(999, Math.max(1, CP + CB + GB));
    }
    
    private void playBoard() {
      List<P> pointsToCheck = Arrays.asList(
          new P(placedBlockX1, placedBlockY1), 
          new P(placedBlockX2, placedBlockY2));
      while (destroyGroups(pointsToCheck)) {
        points += getPoints();
        CP = (CP == 0) ? 8 : 2*CP;
        pointsToCheck = update();
        B = 0;
        GB = 0;
      }
      update();
      
      CB = getCB();
      nuisancePoints = 1.0 * points / 70;
    }

    int highestCol() {
      int high = 0;
      for (int i=0;i<WIDTH;i++) {
        high = Math.max(high, heights[i]);
      }
      return high;
    }

    int getCB() {
      int CB = 1;
      for (int i=0;i<6;i++) {
        CB*= colorDestroyed[i] ? 2 : 1;
        colorDestroyed[i] = false; // reset color destroyed
      }
      if (CB <= 2) {
        CB = 0;
      } else {
        CB/=2;
      }
      return CB;
    }
    
    int futurePos(int x) {
      return heights[x];
    }

    P[] placeBlock(Block block, int x, int rotation) {
      P[] ps = new P[2];
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
      ps[0] = new P(placedBlockX1, placedBlockY1);
      ps[1] = new P(placedBlockX2, placedBlockY2);
      return ps;
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
    
     List<P> update() {
      boolean lastWasEmpty = false;
      List<P> pointsToCheck = new ArrayList<>();
      for (int x=0;x<WIDTH;x++) {
        int current = 0;
        for (int y=0;y<heights[x];y++) {
          if (board[x][y] != EMPTY) {
            if (lastWasEmpty) {
              lastWasEmpty = false;
              pointsToCheck.add(new P(x, current));
            }
            board[x][current] = board[x][y];
            current++;
          } else {
            lastWasEmpty = true;
          }
        }
        heights[x] = current;
        for (int y=current;y<HEIGHT;y++) {
          board[x][y] = EMPTY;
        }
      }
      return pointsToCheck;
    }

    boolean destroyGroups(List<P> pointsToCheck) {
      boolean someDestroyed = false;
      for (P p : pointsToCheck) {
        someDestroyed |= destroyNeighbours(p.x, p.y);
      }
      return someDestroyed;
    }

    private boolean destroyNeighbours(int x, int y) {
      int neighbours = countNeighbours(x,y);
      boolean someDestroyed = false;
      if (neighbours >= 4) {
        someDestroyed  = true;
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

    public void debug() {
      System.out.println("------");
      for (int y=HEIGHT-1;y>=0;y--) {
        for (int x=0;x<WIDTH;x++) {
          if (board[x][y] == EMPTY) {
            System.out.print(" ");
          } else if (board[x][y] == SKULL) {
            System.out.print("@");
          } else {
            System.out.print(board[x][y]);
          }
        }
        System.out.println("");
      }
      System.out.println("------");
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
      Simulation s = new Simulation(board, blocks, MAX_ITERATIONS);
      System.out.println("" + s.firstStep().column + " " + s.firstStep().rotation);
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
    updateOponentScore(in.nextInt());
    for (int i = 0; i < 12; i++) {
      String row = in.next(); 
    }
  }

}
