package uttt.mcts;

import uttt.Player;
import uttt.state.State2;

public class MCTS {
  public Node best;
  private Node root;
  
  
  public MCTS() {
    root = NodeCache.pop();
    root.childArray = new Node[81];
    root.reset();
  }

  public State2 getCurrentState() {
    return root.state;
  }
  
  public void think() {
//    System.err.println("Reuse " + root.totalTrials+" nodes");
    long duration = System.currentTimeMillis() - Player.start;
    int sim = 0;
    Node.rollout = 0;
    
    while ( NodeCache.nodesIndex > 100 &&  duration < 95) {
      root.chooseChild(true);

      if (Player.DEBUG_MCTS) {
        Node.debugNodechilds("", root);
        System.err.println("-------------");
      }
      sim++;
      if ((sim & (512-1)) == 0) {
        duration = System.currentTimeMillis() - Player.start;
        // check winning condition
        for (int i=0;i<root.childArrayFE;i++) {
          if (root.childArray[i].Q == Integer.MAX_VALUE) {
            duration = 95;
          }
        }
        //System.err.println("Updating duration after "+ root.N+" => "+duration+" ms");
      }
    }
    if (Player.DEBUG) {
      System.err.println("Rollouts : "+Node.rollout);
      System.err.println("finished MCTS in "+duration+" with "+NodeCache.nodesIndex+" nodes remaining");
    }
    best = root.getBest();
  }
  
  public void output() {
    System.err.println("On repart avec  : " + root.N);
    
    int row = 0;
    int col = 0;
    
    row = 3 * (best.gDecal / 3);
    row += Integer.numberOfTrailingZeros(best.lDecal) / 3;
    
    col = 3 * (best.gDecal % 3);
    col += Integer.numberOfTrailingZeros(best.lDecal) % 3;
    
    System.out.println(""+ row + " " + col);
  }

  public static int getRowFromDecal(int gDecal, int lDecal) {
    int row = 3 * (gDecal / 3);
    row += Integer.numberOfTrailingZeros(lDecal) / 3;
    return row;
  }
  
  public static int getColFromDecal(int gDecal, int lDecal) {
    int col = 3 * (gDecal % 3);
    col += Integer.numberOfTrailingZeros(lDecal) % 3;
    return col;
  }
  
  public void doAction(boolean who, int gDecal, int lDecal) {
    // release all the nodes except best
    Node toKeep = null;
    for (int i=0;i<root.childArrayFE;i++) {
      Node node = root.childArray[i];
      if (node.lDecal == lDecal && node.gDecal == gDecal) {
        toKeep = node;
      } else {
        node.release();
      }
    }
    if (toKeep == null) {
      // no node for this ply !
      root.state.set(who, gDecal, lDecal);
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
    
    node.state.set(true, 4, 0b10000);
    node.update(root, false, 4, 0b10000);
    root.childArray[root.childArrayFE++] = node;
    root.unexploredFE = root.childArrayFE;
  }
  
}
