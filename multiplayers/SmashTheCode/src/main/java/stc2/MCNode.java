package stc2;

import java.beans.beancontext.BeanContextChildSupport;
import java.util.concurrent.ThreadLocalRandom;

import utils.Cache;

public class MCNode {
  public static Game game;
  public static int MAX_DEPTH = 8;
  
  static final ThreadLocalRandom random = ThreadLocalRandom.current();
  private static final MCNode IMPOSSIBLE_NODE = new MCNode();
  private static final int WORST_SCORE = -10_000_000;
  static Cache<MCNode> cache = new Cache<>();
  static {
    for(int i=0;i<150_000;i++) {
      cache.push(new MCNode());
    }
  }
  
  public Simulation simulation = new Simulation();
  public BitBoard board = new BitBoard();
  
  MCNode childs[] = new MCNode[24];
  int childsCount = 0;
  
  int bestTotalPoints;
  int bestChildTotalPoints[] = new int[24];
  int bestChildTotalPointsSum = 0;
  int depth;
  int simCount;
  private int key;
  
  private MCNode() {
    simulation.board = board;
  }
  
  void initSums() {
    int maxKeys = game.nextBalls[depth] == game.nextBalls[depth] ? 12 : 24;
    bestChildTotalPointsSum = 0;
    
    for (int i=0;i<24;i++) {
      bestChildTotalPoints[i] = 0;
    }
    for (int i=0;i<maxKeys;i++) {
      bestChildTotalPoints[i] = 210;
      bestChildTotalPointsSum += bestChildTotalPoints[i];
    }
  }

  public final void simulate(int depth) {
    if (depth >= MAX_DEPTH) {
      return;
    }
    
//    getRandomKey();
    getWeightedKey();
    int rotation = getRotation(key);
    int column = getColumn(key);
    
    MCNode child = childs[key];
    if (child != null) {
      // exploit
      if (child == IMPOSSIBLE_NODE) {
        return;
      }
      child.simCount++;
      child.simulate(depth+1);
    } else {
      // explore
      if (!board.canPutBalls(rotation, column)) {
        childs[key] = IMPOSSIBLE_NODE;
        return;
      }
      child = buildNewChild(rotation, column, depth, game);
      childs[key] = child;
      childsCount++;
    }
    updateBestTotalPoints(key, child);
  }


  private void updateBestTotalPoints(int key, MCNode child) {
    if (bestChildTotalPoints[key] < child.bestTotalPoints) {
      bestChildTotalPointsSum = bestChildTotalPointsSum - bestChildTotalPoints[key] + child.bestTotalPoints;
      bestChildTotalPoints[key] = child.bestTotalPoints;
      bestTotalPoints = child.bestTotalPoints + simulation.points ;
    }
  }

  final private void getWeightedKey() {
    
    int rand = random.nextInt(bestChildTotalPointsSum-1)+1;
    int bestKey = -1;
    while (rand > 0) {
      rand -= bestChildTotalPoints[++bestKey];
    }
    this.key = bestKey;
  }

  final private void getRandomKey() {
    if (game.nextBalls[depth] == game.nextBalls[depth]) {
      key = random.nextInt(12);
    } else {
      key = random.nextInt(24);
    }
  }
  
  public MCNode buildNewChild(int rotation, int column, int depth, Game game) {
    MCNode child = get();
    child.simCount = 1;
    child.depth = depth+1;
    child.board.copyFrom(this.board);
    child.initSums();
    child.play(rotation, column);
    return child;
  }
  
  private void play(int rotation, int column) {
    simulation.putBallsNoCheck(game.nextBalls[depth-1], game.nextBalls2[depth-1], rotation, column);
    bestTotalPoints = simulation.points;
  }
  
  public double getScore() {
    if (this == IMPOSSIBLE_NODE) {
      return -1_000_000;
    }
    if (simulation.points > 420 ) {
      return simulation.points 
          + getColorGroupScore()
          + getColumnScore()
          + getSkullsScore();
    } else {
      return getColorGroupScore()
          + getColumnScore()
          + getSkullsScore();
    }
  }
  
  private int getSkullsScore() {
    return 0; //-2*simulation.board.layers[BitBoard.SKULL_LAYER].bitCount();
  }

  private double getColorGroupScore() {
        return 
            - 40*simulation.groupsCount[1] 
            + 10*simulation.groupsCount[2]
            + 40*simulation.groupsCount[3];
  }

  private double getColumnScore() {
    return 
        -1*simulation.board.getColHeight(0)
        -0*simulation.board.getColHeight(1)
        +1*simulation.board.getColHeight(2)
        +1*simulation.board.getColHeight(3)
        -0*simulation.board.getColHeight(4)
        -1*simulation.board.getColHeight(5);
  }


  double getBestScore() {
    if (childsCount == 0) {
      return getScore();
    } else {
      double maxScore = Integer.MIN_VALUE;
      for ( int i=0;i<24;i++) {
        MCNode child = childs[i];
        if (child == null) {
          continue;
        }
        double score = child.getBestScore();
        if (score > maxScore) {
          maxScore = score;
        }
      }
      return 0.8*maxScore + getScore();
    }
  }
  
  public static MCNode get() {
    if (cache.isEmpty()) {
      return new MCNode();
    } else {
      MCNode node = cache.pop();
      return node;
    }
  }
  
  
  public void release() {
    if (this == IMPOSSIBLE_NODE) {
      return; // don't give back IMP_NODE
    }
    
    childsCount = 0;
    bestTotalPoints = 0;
    depth = 0;
    bestChildTotalPointsSum = 0;
    for (int i=0;i<24;i++) {
      MCNode child = childs[i];
      bestChildTotalPoints[i] = 0;
      if (child != null && child != IMPOSSIBLE_NODE) {
        child.release();
      }
      childs[i] = null;
    }
    simulation.clear();
    cache.retrocede(this);
  }
  
  public static int getColumn(int key) {
    return key % 6;
  }

  public static int getRotation(int key) {
    return key / 6;
  }
}
