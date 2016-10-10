import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PlayerOldSilver {
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
  
  public static class SimulationWeight {
    final double pointsWeights;
    final double highestColWeights;
    final double skullCountWeight;
    final double nextIterWeight;
    final double nextToNeighboursWeight;

    public SimulationWeight(double pointsWeights, double highestColWeights, double skullCountWeight, double nextIterWeight, double nextToNeighboursWeight) {
      super();
      
      double norm = Math.sqrt(pointsWeights*pointsWeights
          + highestColWeights*highestColWeights
          + skullCountWeight *skullCountWeight 
          + nextIterWeight * nextIterWeight
          + nextToNeighboursWeight * nextToNeighboursWeight);
      
      
      this.pointsWeights = pointsWeights / norm;
      this.highestColWeights = highestColWeights / norm;
      this.skullCountWeight = skullCountWeight / norm;
      this.nextIterWeight = nextIterWeight / norm;
      this.nextToNeighboursWeight = nextToNeighboursWeight / norm;
    }

    public void debug() {
      System.out.println("  weights : ("
          + pointsWeights+","
          + highestColWeights+","
          + skullCountWeight +","
          + nextIterWeight +","
          + nextToNeighboursWeight 
          + ")");      
    }

    public SimulationWeight mutate(SimulationWeight ow) {
      return new SimulationWeight(
          this.pointsWeights + ow.pointsWeights,
          this.highestColWeights + ow.highestColWeights,
          this.skullCountWeight + ow.skullCountWeight,
          this.nextIterWeight + ow.nextIterWeight,
          this.nextToNeighboursWeight + ow.nextToNeighboursWeight
          );
    }
  }
  public static class Simulation {
    private StepSimulation zeroThStep; // this is the 0'th step, nothing to get from it !
    
    public Simulation(SimulationWeight weights, Board board, Block[] blocks, int maxIterations) {
      zeroThStep = new StepSimulation(board, blocks, 0, maxIterations, -1, -1);
      zeroThStep.simulate(weights);
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
    PlayerOldSilver.Board board;
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
    
    public StepSimulation(PlayerOldSilver.Board board, Block[] blocks, int step, int maxIterations, int column, int rotation) {
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
    private void simulate(SimulationWeight weights) {
      Block currentBlock;
      if (step > 0) {
        currentBlock = blocks[step-1];
        positionnedBlocks = board.placeBlock(currentBlock, column, rotation);
        board.putBlocksAndUpdate(this);
      }
      
      bestSS = null;
      double bestScore = -100000;

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
            ss.simulate(weights);
            if (bestSS == null || ss.score > bestScore) {
              bestScore = ss.score;
              bestSS = ss;
            }
          }
        }
      }
      //TODO calculate score from simulation data & heuristics
      updatePoints();
      fitnesseScore(weights);
    }

    void updatePoints() {
      points = board.points;
      totalPoints=points;
      if (bestSS != null) {
        totalPoints+=bestSS.totalPoints;
      }
    }
    
    private void fitnesseScore(SimulationWeight weight) {
      score = weight.pointsWeights * points 
          + weight.highestColWeights * board.highestCol()
          + weight.skullCountWeight * board.skullCount;
      if (positionnedBlocks != null) { 
        score += weight.nextToNeighboursWeight * nextToNeighbours(); 
      }
      if (urgeToDoSomething != 1) {
        if (bestSS != null) { score += weight.nextIterWeight*bestSS.score; }
      }
    }

    private int nextToNeighbours() {
//      int neighbours = board.countNeighbours(positionnedBlocks[0].x, positionnedBlocks[0].y);
//      neighbours += board.countNeighbours(positionnedBlocks[1].x, positionnedBlocks[1].y);

      int neighbours = board.getCacheNeighbours(positionnedBlocks[0].x, positionnedBlocks[0].y)
          +board.getCacheNeighbours(positionnedBlocks[1].x, positionnedBlocks[1].y);
      return neighbours;
    }
  }
  
  public static class Board {
    private static final int EMPTY = 0;
    private static final int SKULL = 9;
    static int[] empty = new int[12*6+2];
    
    final int WIDTH;
    final int HEIGHT;
    int[] colorGrid;
    int[] neighboursGrid;
    int[][] groupsGrid;
    
    int[] heights;
    int skullCount = 0;
    
    Board(int W, int H) {
      WIDTH = W;
      HEIGHT = H;
      colorGrid = new int[WIDTH*HEIGHT];
      neighboursGrid = new int[WIDTH*HEIGHT+2];
      groupsGrid = new int[WIDTH][HEIGHT];
      heights = new int[WIDTH];
    }

//    final int countNeighbours(final int x, final int y) { 
//      int color = colorGrid[x+WIDTH*y]; 
//      if (color <1 || color > 6) { 
//        return 0;  
//      } 
//      Board b = new Board(this); 
//      int count = countNeighbours(b, color, x, y); 
//      return count; 
//    } 
//    
//    int countNeighbours(Board b, int color, int x, int y) { 
//      if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) { 
//        return 0; 
//      } 
//      int position = x+WIDTH*y; 
//      if (b.colorGrid[position] != color) { 
//        return 0; 
//      } 
//      b.colorGrid[position] = - color; 
//      int count = 1; 
//      count += countNeighbours(b, color, x - 1, y); 
//      count += countNeighbours(b, color, x + 1, y); 
//      count += countNeighbours(b, color, x, y + 1); 
//      count += countNeighbours(b, color, x, y - 1); 
//      return count; 
//    } 
    
    void updateNeighboursCache(int color, int x, int y) {
      int groupsToMerge[] = new int[4];
      int index = 0;
      
      if (x > 0 && colorGrid[x-1+WIDTH*y] == color) {
        groupsToMerge[index++] = groupsGrid[x-1][y];
      }
      if (x < WIDTH-1 && colorGrid[x+1+WIDTH*y] == color) {
        groupsToMerge[index++] = groupsGrid[x+1][y];
      }
      if (y > 0 && colorGrid[x+WIDTH*(y-1)] == color) {
        groupsToMerge[index++] = groupsGrid[x][y-1];
      }
      if (y < HEIGHT-1 && colorGrid[x+WIDTH*(y+1)] == color) {
        groupsToMerge[index++] = groupsGrid[x][y+1];
      }
      // new we have the 4 potentials groups, need to remove duplicates
      int total = 1; // me
      if (index == 0) {
        
      }
      for (int i=0;i<index;i++) {
        int groupIndex = groupsToMerge[i];
        if (groupIndex != 0) {
          total += neighboursGrid[groupIndex];
          for (int j=i+1;j<index;j++) {
            if (groupsToMerge[j] == groupIndex) {
              groupsToMerge[j] = 0;
            }
          }
        }
      }
      for (int i=0;i<index;i++) {
        if (groupsToMerge[i] != 0) {
          neighboursGrid[groupsToMerge[i]] = total;
        }
      }
    }

    void updateNeighboursCache() {
      System.arraycopy(empty, 0, neighboursGrid, 0, neighboursGrid.length);
      
      int groupIndex = 0;
      neighboursGrid[0] = 0;
      for (int y=0;y<HEIGHT;y++) {
        for (int x=0;x<WIDTH;x++) {
          int color = colorGrid[x+y*WIDTH];
          if (color == EMPTY || color == SKULL) {
            groupsGrid[x][y] = 0;
            continue;
          }
          int precedentXIndex = 0;
          int precedentYIndex = 0;

          if (x > 0 && colorGrid[x-1+y*WIDTH] == colorGrid[x+y*WIDTH]) {
            precedentXIndex = groupsGrid[x-1][y];
          } 
          if (y > 0 && colorGrid[x+(y-1)*WIDTH] == colorGrid[x+y*WIDTH]) {
            if (precedentXIndex == 0) {
              precedentXIndex = groupsGrid[x][y-1];
            } else {
              precedentYIndex = groupsGrid[x][y-1];
            }
          }
          if (precedentXIndex == 0 && precedentYIndex == 0) {
            precedentXIndex = ++groupIndex;
          } else if (precedentXIndex == precedentYIndex) {
            precedentYIndex = 0; // don't count it 2 times, but same group
          } else {
            if (precedentYIndex != 0) {
              mergeNeighbors(x,y, precedentXIndex, precedentYIndex);
              precedentYIndex = 0;
            } else {
            }
          }
          neighboursGrid[precedentXIndex] += 1;
          groupsGrid[x][y] = precedentXIndex;
        }
      }
    }

    private void mergeNeighbors(int xMax, int yMax, int to, int from) {
      for (int y=0;y<yMax;y++) {
        for (int x=0;x<WIDTH;x++) {
          if (groupsGrid[x][y] == from) {
            groupsGrid[x][y] = to;
          }
        }
      }
      neighboursGrid[to] +=neighboursGrid[from];
      neighboursGrid[from] = 0;
    }
    
    public void putBlocksAndUpdate(StepSimulation stepSimulation) {
      List<P> pointsToCheck = Arrays.asList(stepSimulation.positionnedBlocks[0],
          stepSimulation.positionnedBlocks[1]);
      CP = 0;
      B = 0;
      points = 0;
      boolean hasDestroyed = false;
      while (destroyGroups(pointsToCheck)) {
        hasDestroyed = true;
        CB = getCB();
        points += getPoints();
        // reset some values
        B = 0;
        GB = 0;
        CB = 0;
        CP = (CP == 0) ? 8 : 2*CP;
        pointsToCheck = update();
        updateNeighboursCache();
      }
      nuisancePoints = 1.0 * points / 70;
    }

    public Board(Board b) {
      WIDTH = b.WIDTH;
      HEIGHT = b.HEIGHT;
      heights = new int[WIDTH];
      // copy board
      colorGrid = b.colorGrid.clone();
      neighboursGrid = b.neighboursGrid.clone();
      groupsGrid = b.groupsGrid.clone();
      
      //System.arraycopy(b.board, 0, board, 0, b.board.length);
      for (int x = 0; x < WIDTH; x++) {
        heights[x] = b.heights[x];
      }
    }

    public void updateRow(int rowIndex, String row) {
      int y = (HEIGHT-1)-rowIndex;

      for (int x = 0; x < WIDTH; x++) {
        if (row.charAt(x) >= '1' && row.charAt(x) <= '5') {
          colorGrid[x+WIDTH*y] = row.charAt(x) - '0';
          heights[x] = Math.max(heights[x], Math.min(y+1, HEIGHT));
        } else if (row.charAt(x) == '0') {
          colorGrid[x+WIDTH*y] = SKULL;
          heights[x] = Math.max(heights[x], Math.min(y+1, HEIGHT));
          skullCount++;
        } else {
          colorGrid[x+WIDTH*y] = EMPTY;
        }
      }
    }

    int B=0;  // block destroyed
    int CP=0; // chain power
    int CB=0; // color blocks
    int GB=0; // group blocks
    
    int points;
    double nuisancePoints = 0;
    
    boolean colorDestroyed[] = new boolean[10];
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
      colorGrid[placedBlockX1+WIDTH*placedBlockY1] = block.color1;
      colorGrid[placedBlockX2+WIDTH*placedBlockY2] = block.color2;
      ps[0] = new P(placedBlockX1, placedBlockY1);
      ps[1] = new P(placedBlockX2, placedBlockY2);
      //updateNeighboursCache(block.color1, placedBlockX1, placedBlockY1);
      //updateNeighboursCache(block.color2, placedBlockX2, placedBlockY2);
      updateNeighboursCache();
      return ps;
    }

    boolean canPlaceBlock(Block block, int x, int rotation) {
      return (rotation % 2 == 1 && heights[x] < HEIGHT-1)
          ||
          (rotation == 0 && x+1 <= WIDTH-1 && heights[x] < HEIGHT && heights[x+1] < HEIGHT)
          || 
          (rotation == 2 && x-1 >= 0 && heights[x] < HEIGHT && heights[x-1] < HEIGHT);
    }
    
     List<P> update() {
      boolean lastWasEmpty = false;
      List<P> pointsToCheck = new ArrayList<>();
      for (int x=0;x<WIDTH;x++) {
        int current = 0;
        for (int y=0;y<heights[x];y++) {
          if (colorGrid[x+WIDTH*y] != EMPTY) {
            if (lastWasEmpty) {
              lastWasEmpty = false;
              pointsToCheck.add(new P(x, current));
            }
            colorGrid[x+WIDTH*current] = colorGrid[x+WIDTH*y];
            current++;
          } else {
            lastWasEmpty = true;
          }
        }
        heights[x] = current;
        for (int y=current;y<HEIGHT;y++) {
          colorGrid[x+WIDTH*y] = EMPTY;
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
      int neighbours = getCacheNeighbours(x,y);
      boolean someDestroyed = false;
      if (neighbours >= 4) {
        someDestroyed  = true;
        int color = colorGrid[x+WIDTH*y];
        if (color == 0) {
          return false; 
        }
        colorDestroyed[color] = true;
        killNeighbours(color, x,y);

        B+=neighbours;
        GB += neighbours >= 11 ? 8 : neighbours -4;
      }
      return someDestroyed;
    }
    
    int getCacheNeighbours(int x, int y) {
      return neighboursGrid[groupsGrid[x][y]];
    }

    void killNeighbours(int color, int x, int y) {
      if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
        return;
      }
      int colorToKill = colorGrid[x+WIDTH*y];
      if (colorToKill != color && colorToKill != SKULL) {
        return;
      }

      colorGrid[x+WIDTH*y] = EMPTY;
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

    public String row(int rowIndex) {
      rowIndex = (HEIGHT-1)-rowIndex;
      String result = "";
      for (int i = 0; i < WIDTH; i++) {
        if (colorGrid[i+WIDTH*rowIndex] == EMPTY) {
          result += ".";
        } else if (colorGrid[i+WIDTH*rowIndex] == SKULL) {
          result +="0";
        } else {
          result += "" + colorGrid[i+WIDTH*rowIndex];
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
          if (colorGrid[x+WIDTH*y] == EMPTY) {
            System.out.print(" ");
          } else if (colorGrid[x+WIDTH*y] == SKULL) {
            System.out.print("@");
          } else {
            System.out.print(colorGrid[x+WIDTH*y]);
          }
        }
        System.out.println("");
      }
      System.out.println("------");
    }
  }

  Board oBoard = new Board(6, 12);
  Board board = new Board(6, 12);
  Block[] blocks = new Block[8];

  public static void main(String args[]) {
    in = new Scanner(System.in);
    new PlayerOldSilver().play();
  }

  int col = 0;
  static int urgeToDoSomething;

  private void play() {
    SimulationWeight weights = new SimulationWeight(
        0.0395941534020874,
        -0.039134807544185114,
        -0.3239394470974496,
        0.5776550471952032,
        0.7471804674367216
        );
    
    // game loop
    while (true) {
      updateReadings();
     
      // opponent simulation
       urgeToDoSomething = 0;
      Simulation opponentSimulation = new Simulation(weights, oBoard, blocks, 2);
      int futurePoints = opponentSimulation.firstStep() != null ? currentPoints + opponentSimulation.firstStep().totalPoints : currentPoints ;
      if ( futurePoints>= 70*6) {
        // opponent will be able to make a line in at least 2 runs
        urgeToDoSomething = 1;
      } else {
        urgeToDoSomething = 0;
      }
      // my simulation
      Simulation s = new Simulation(weights, board, blocks, MAX_ITERATIONS);
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
    board.updateNeighboursCache();
    
    updateOponentScore(in.nextInt());
    for (int i = 0; i < 12; i++) {
      String row = in.next(); 
      oBoard.updateRow(i, row);
    }
  }
}
