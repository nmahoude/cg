package stc2;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import utils.Cache;

public class MCNode {
  static final ThreadLocalRandom random = ThreadLocalRandom.current();
  private static final MCNode IMPOSSIBLE_NODE = new MCNode();
  static Cache<MCNode> cache = new Cache<>();
  static {
    for(int i=0;i<10_000;i++) {
      cache.push(new MCNode());
    }
  }
  
  Simulation simulation = new Simulation();
  BitBoard board = new BitBoard();
  
  Map<Integer, MCNode> childs = new HashMap<>();
  private int color1;
  private int color2;
  int count;
  
  private MCNode() {
    simulation.board = board;
  }
  
  
  public final void simulate(Game game, int depth, int maxDepth) {
    if (depth >= maxDepth) {
      return;
    }

    int rotation = random.nextInt(color1 == color2 ? 2 : 4);
    int column = random.nextInt(6);
    Integer key = new Integer(rotation + column*4);
    
    MCNode child = childs.get(key);
    if (child != null) {
      if (child == IMPOSSIBLE_NODE) {
        return;
      }
      child.count++;
      child.simulate(game, depth+1, maxDepth);
    } else {
      if (!board.canPutBalls(rotation, column)) {
        childs.put(key, IMPOSSIBLE_NODE);
        return;
      }
      // build a child
      child = get();
      child.count = 1;
      child.color1 = game.nextBalls[depth];
      child.color2 = game.nextBalls2[depth];
      child.board.copyFrom(this.board);
      child.simulation.putBallsNoCheck(color1, color2, rotation, column);
      childs.put(key, child);
    }
  }
  
  public double getScore() {
    return simulation.points 
        - simulation.groupsCount[2]
        +2*simulation.groupsCount[3]
        -2*simulation.groupsCount[1];
  }
  
  double getBestScore() {
    if (childs.isEmpty()) {
      return getScore();
    } else {
      double maxScore = Integer.MIN_VALUE;
      for ( Entry<Integer, MCNode> childEntry : childs.entrySet()) {
        MCNode child = childEntry.getValue();
        double score = child.getBestScore();
        if (score > maxScore) {
          maxScore = score;
        }
      }
      return Math.max(0.8*maxScore, getScore());
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
    
    for (MCNode child : childs.values()) {
      child.release();
    }
    childs.clear();
    cache.retrocede(this);
  }
}
