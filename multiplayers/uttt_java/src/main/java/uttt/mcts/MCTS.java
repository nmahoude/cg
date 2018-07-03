package uttt.mcts;

import uttt.Player;
import uttt.state.State;

public class MCTS {
  public Node best;
  private Node root;
  
  
  public MCTS() {
    root = NodeCache.pop();
  }

  public State getCurrentState() {
    return root.state;
  }
  
  public void think() {
    System.err.println("Reuse " + root.totalTrials+" nodes");
    long duration = System.currentTimeMillis() - Player.start;
    while ( root.totalTrials < 200_000 &&  duration < 95) {
      root.chooseChild();

      if (100 * (root.totalTrials / 100) == root.totalTrials) {
        duration = System.currentTimeMillis() - Player.start;
      }
      
//      if (Player.DEBUG && root.totalTrials % 1000 == 0) {
//        System.err.println("stil calculating : " + root.totalTrials + " in "+ duration +" ms");
//      }
    }

    best = root.getBest();
  }
  
  public void output() {
    System.out.println(""+ best.row + " " + best.col);
  }


  public void doAction(boolean who, int row, int col) {
    // release all the nodes except best
    Node toKeep = null;
    for (int i=0;i<root.childArrayFE;i++) {
      Node node = root.childArray[i];
      if (node.row == row && node.col == col) {
        toKeep = node;
      } else {
        node.release();
      }
    }
    if (toKeep == null) {
      // no node for this ply !
      root.state.set(who, row, col);
      root.player = !who;
      root.childArrayFE = -1; // reinit childs
    } else {
      root = toKeep; // we are losing one cache node here, shouldn' be a problem
      root.parent = null;
    }
  }

  /**
   * the best play is (4,4) so block it from root
   */
  public void firstToPlay() {
    root.childArrayFE = 0;
    root.unexploredFE = 0;
    
    Node node = NodeCache.pop();

    node.state.copyFrom(root.state);
    
    node.state.set(true, 4, 4);
    node.update(root, false, 4, 4);
    root.childArray[root.childArrayFE++] = node;
    root.unexplored[root.unexploredFE++] = node;

  }
  
}
