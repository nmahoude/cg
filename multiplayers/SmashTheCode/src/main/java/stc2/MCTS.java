package stc2;

import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import stc.DFSNode;

public class MCTS {
  static int MAX_PLY = 20_000;
  Game game;
  ThreadLocalRandom random = ThreadLocalRandom.current();
  private MCNode bestNode;
  private double bestScore;
  private Integer bestKey;

  public MCTS() {
  }
  
  public void simulate() {
    MCNode root = MCNode.get();
    root.board.copyFrom(game.myBoard);
    
    for (int ply=MAX_PLY;--ply>=0;) {
      root.simulate(game, 0, 1);
    }
    
    bestScore = -1_000_000;
    bestNode = null;
    bestKey = null;
    
    for (Entry<Integer, MCNode> childEntry : root.childs.entrySet()) {
      MCNode child = childEntry.getValue();
      double score = child.getScore();
      double bScore = child.getBestScore();
      int key = childEntry.getKey();
      int rot = key & 0b11;
      int column = key >>> 2;
      System.err.println(""+key+" ("+column+","+rot+") (sim="+child.count+") -> " + score + " --> "+bestScore);
      if (bScore > bestScore) {
        bestScore = bScore;
        bestNode = child;
        bestKey = childEntry.getKey();
      }
    }
  }

  public String output() {
    int key = bestKey;
    int rot = key & 0b11;
    int column = (key >>> 2);
    return ""+column+" "+rot;
  }
}
