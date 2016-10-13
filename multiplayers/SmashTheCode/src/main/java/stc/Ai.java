package stc;

import java.util.Map.Entry;

public class Ai {
  private Game game;
  String command = "";
  DFSNode root = null;
  private double bestScore;
  private DFSNode bestChild;

  public Ai(Game game) {
    this.game = game;
  }
  
  public void think() {
    if (root != null) {
      root.release();
    }

    int nextSkullThreat = calculateSkullsThreatsInRound();
    int myDepth = 8;
    if (nextSkullThreat > 4) {
      myDepth = 1;
    } else if (nextSkullThreat > 2) {
      myDepth = 2;
    }

    root = DFSNode.getNode();
    game.myBoard.copy(root.board);
//    game.debug();
//    root.board.debug();
    root.simulate(game, 0, myDepth);
    
    Integer comm = null;
    bestScore = Integer.MIN_VALUE;
    bestChild = null;
    for (Entry<Integer, DFSNode> childEntry : root.childs.entrySet()) {
      DFSNode child = childEntry.getValue();
      double score = child.getScore();
      double bScore = child.getBestScore();
      if (score > 70*6*3) {
        bestScore = 1000*score;
        bestChild = child;
        comm = childEntry.getKey();
      } else if (bScore > bestScore) {
        bestScore = bScore;
        bestChild = child;
        comm = childEntry.getKey();
      }
    }
    if (comm != null) {
      int commAsInt = comm.intValue();
      System.err.println("best score : "+bestScore);
      command = ""+(commAsInt % 6)+" "+(commAsInt/6);
    } else {
      command = "0 0 Perdu";
    }
  }
  private int calculateSkullsThreatsInRound() {
    root = DFSNode.getNode();
    game.otherBoard.copy(root.board);

    root.simulate(game, 0, 2);
    
    int bestPoints = root.getBestPoints();
    root.release();
    return bestPoints / (70*6);
  }

  public final String output() {
    return command;
  }

  public void debug() {
    System.err.println("Direct root child score :");
    for (Entry<Integer, DFSNode> childEntry : root.childs.entrySet()) {
      DFSNode child = childEntry.getValue();
      int key = childEntry.getKey();
      double score = child.getScore();
      double bestScore = child.getBestScore();
      System.err.println(""+(key % 6)+" "+(key/6)+" -> "+score+ " (best:"+bestScore+")");
    }

    System.err.println("best score : "+bestScore);
    System.err.println("--------");
    //System.err.println("course: "+command+"->"+bestChild.debugCourse2());
    System.err.println("--------");
  }
}
