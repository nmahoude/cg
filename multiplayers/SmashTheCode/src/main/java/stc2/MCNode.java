package stc2;

import java.util.concurrent.ThreadLocalRandom;

import utils.Cache;

public class MCNode {
  static final ThreadLocalRandom random = ThreadLocalRandom.current();
  public static final MCNode IMPOSSIBLE_NODE = new MCNode();
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
  
  int color1;
  int color2;
  int simCount;
  public int rotation;
  public int column;
  private int key;
  
  public MCNode() {
    simulation.board = board;
  }
  
  public int getKey() {
    return key;
  }
  
  public final MCNode simulate(Game game, int depth, int maxDepth, int[] bestPointsAtDepth) {
    if (depth >= maxDepth) {
      return this;
    }

    key = getRandomKey();
    
    MCNode child = childs[key];
    if (child != null) {
      if (child == IMPOSSIBLE_NODE) {
        return this;
      }
      child.simCount++;
      child.simulate(game, depth+1, maxDepth, bestPointsAtDepth);
      return child;
    } else {
      if (!board.canPutBalls(rotation, column)) {
        childs[key] = IMPOSSIBLE_NODE;
        return this;
      }
      // build a child
      child = buildNewChild(rotation, column, depth, game);
      if (bestPointsAtDepth[depth] < child.simulation.points) {
        bestPointsAtDepth[depth] = child.simulation.points;
      }
      childs[key] = child;
      childsCount++;
      return child;
    }
  }


  final public int getRandomKey() {
    rotation = random.nextInt(color1 == color2 ? 2 : 4); 
    column = random.nextInt(6); 
    return (rotation + column*4); 
  }
  
  public MCNode buildNewChild(int rotation, int column, int depth, Game game) {
    MCNode child = get();
    child.simCount = 1;
    child.color1 = game.nextBalls[depth+1];
    child.color2 = game.nextBalls2[depth+1];
    child.board.copyFrom(this.board);
    child.simulation.putBallsNoCheck(color1, color2, rotation, column);
    return child;
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
  
  public int getSkullsScore() {
    return 0; //-2*simulation.board.layers[BitBoard.SKULL_LAYER].bitCount();
  }


  public double getColorGroupScore() {
        return 
            - 40*simulation.groupsCount[1] 
            + 10*simulation.groupsCount[2]
            + 40*simulation.groupsCount[3];
  }


  public double getColumnScore() {
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
//      return Math.max(0.8*maxScore, getScore());
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
    for (int i=0;i<24;i++) {
      MCNode child = childs[i];
      if (child != null && child != IMPOSSIBLE_NODE) {
        child.release();
      }
      childs[i] = null;
    }
    simulation.clear();
    cache.retrocede(this);
  }


  public int getBestPoints(int depth) {
    if (childsCount == 0 || depth == 1) {
      return simulation.points;
    } else {
      int maxScore = Integer.MIN_VALUE;
      for ( int i=0;i<24;i++) {
        MCNode child = childs[i];
        if (child == null) {
          continue;
        }
        int score = simulation.points + child.getBestPoints(depth-1);
        if (score > maxScore) {
          maxScore = score;
        }
      }
      return maxScore;
    }
  }
}
