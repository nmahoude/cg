package stc;

import java.util.Map.Entry;

public class Ai {
  private Game game;
  String command = "";
  DFSNode root = new DFSNode();

  public Ai(Game game) {
    this.game = game;
  }
  
  public void think() {
    game.myBoard.copy(root.board);
    root.board.debug();
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
    int commAsInt = comm.intValue();
    
    root = bestChild;
    command = ""+(commAsInt % 6)+" "+(commAsInt/6);

    System.err.println("best score : "+bestScore);
    System.err.println("course: "+command+"->"+bestChild.debugCourse());
  }
  public final String output() {
    return command;
  }
}
