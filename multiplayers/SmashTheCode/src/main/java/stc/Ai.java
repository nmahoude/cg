package stc;

import java.util.Map.Entry;

public class Ai {
  private Game game;
  String command = "";
  DFSNode root = null;

  public Ai(Game game) {
    this.game = game;
  }
  
  public void think() {
    if (root != null) {
      root.release();
    }
    root = new DFSNode();
    game.myBoard.copy(root.board);
    //game.debug();
    //root.board.debug();
    root.simulate(game, 0);
    
    Integer comm = null;
    double bestScore = -1;
    DFSNode bestChild = null;
    for (Entry<Integer, DFSNode> childEntry : root.childs.entrySet()) {
      DFSNode child = childEntry.getValue();
      double score = child.getBestScore();
      if (score > bestScore) {
        bestScore = score;
        bestChild = child;
        comm = childEntry.getKey();
      }
    }
    if (comm != null) {
      int commAsInt = comm.intValue();
      root = bestChild;
      command = ""+(commAsInt % 6)+" "+(commAsInt/6);
      System.err.println("best score : "+bestScore);
      System.err.println("course: "+command+"->"+bestChild.debugCourse());
    } else {
      command = "0 0 Perdu";
    }
  }
  public final String output() {
    return command;
  }
}
