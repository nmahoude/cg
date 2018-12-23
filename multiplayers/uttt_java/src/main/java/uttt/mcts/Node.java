package uttt.mcts;

import uttt.Player;
import uttt.state.State2;

public class Node {
  private static final int LOG_CACHE_SIZE = 5_000;
  static final double logCache[] = new double[LOG_CACHE_SIZE];
  static {
    logCache[0] = 0;
    for (int i=1;i<logCache.length;i++) {
      logCache[i] = Math.log((double)(i<<4));
    }
  }

  
  private static final double SCORE_C = 1.1; //Math.sqrt(2);

  static final State2 tempState = new State2();
  
  
  Node parent;
  State2 state = new State2();

  Node childArray[] = new Node[81];
  int childArrayFE = -1;
  int unexploredFE = 0;
  
  boolean player = true;
  private int won;
  private int lose;
  private int tie; // should be able to discard it
  int totalTrials;

  public int gDecal;
  public int lDecal;

  public void release() {
    for (int i=childArrayFE-1;i>=0;i--) {
      childArray[i].release();
    }
    NodeCache.push(this);
  }
  public void update(Node parent, boolean player, int gDecal, int lDecal) {
    this.parent = parent;
    this.player = player;
    
    this.gDecal = gDecal;
    this.lDecal = lDecal;
    reset(); 
  }

  void reset() {
    this.won = 0;
    this.lose = 0;
    this.tie = 0;
    this.totalTrials = 0;
 }

  public boolean hasDirectChildWithLose() {
    for (int i=0;i<childArrayFE;i++) {
      if (childArray[i].state.winner() == 1) {
        return true;
      }
    }
    return false;
  }

  void chooseChild(boolean isParentRoot) {
    //if (state.winner() != -1) return;
    
    if (childArrayFE == -1) {
      // expand
      this.getAllChildren();
    }
    if (childArrayFE == 0) {
      int result = state.winner();
      backPropagate(result);
      return;
    } else {
      if (unexploredFE != 0) {
        int rand = Player.random.nextInt(unexploredFE);
        Node toTake = childArray[rand];
        childArray[rand] = childArray[--unexploredFE]; 
        childArray[unexploredFE] = toTake;
        
        toTake.runSimulation();
      } else {
        
        Node bestChild = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        double score;
        
        for (int i=childArrayFE-1;i>=0;i--) {
          Node child = childArray[i];
          score = score(child);
          if (score > bestScore) {
            bestScore = score;
            bestChild = child;
          }
        }
        if (bestChild != null) {
          bestChild.chooseChild(false);
        }
      }
    }
  }

  private void runSimulation() {
    int winner = randomSimulate();
    this.backPropagate(winner);
  }

  static int possibleFE = 0;
  static int possibleGrid[] = new int[9*9];
  
  private int randomSimulate() {
    tempState.copyFrom(state);
    boolean currentPlayer = this.player;
    while (!tempState.terminated()) {
      getPossibleMoves(tempState);
      int rand = Player.random.nextInt(possibleFE);
     
      int full = possibleGrid[rand];
      int gDecal = full >> 16;
      int lDecal = full - (gDecal << 16);
      tempState.set(currentPlayer, gDecal, lDecal);
     
      currentPlayer = !currentPlayer;
    }
    return tempState.winner();
  }

  private final void getPossibleMoves(final State2 state) {
    possibleFE = 0;
    if (state.nextPlayGrid != -1) {
      getPossibleMovesForGrid_New(state.nextPlayGrid, state.cells[state.nextPlayGrid]);
    } else {
      getPossibleMovesForGrid_New(0, state.cells[0]);
      getPossibleMovesForGrid_New(1, state.cells[1]);
      getPossibleMovesForGrid_New(2, state.cells[2]);
      getPossibleMovesForGrid_New(3, state.cells[3]);
      getPossibleMovesForGrid_New(4, state.cells[4]);
      getPossibleMovesForGrid_New(5, state.cells[5]);
      getPossibleMovesForGrid_New(6, state.cells[6]);
      getPossibleMovesForGrid_New(7, state.cells[7]);
      getPossibleMovesForGrid_New(8, state.cells[8]);
    }
  }

  private final void getPossibleMovesForGrid_New(int decal, int mask) {
    int all = State2.complete(mask);
    
    if (all == State2.ALL_MASK) return; // no possible move  
    
    int baseY = decal << 16;
    
    for (int d=1;d<=0b100_000_000;d*=2) {
      if ((all & d) == 0) possibleGrid[possibleFE++] = baseY + d;
    }
  }

  
  void getAllChildren() {
    childArrayFE = 0;
    unexploredFE = 0;
    getPossibleMoves(state);

    int full;

    for (int i=possibleFE-1;i>=0;i--) {
      Node node = NodeCache.pop();

      node.state.copyFrom(this.state);
      
      full = possibleGrid[i];
      int gDecal = full >> 16;
      int lDecal = full - (gDecal << 16);
      
      node.state.set(this.player, gDecal, lDecal);
      node.update(this, !this.player, gDecal, lDecal);

      childArray[childArrayFE++] = node;
    }
    unexploredFE = childArrayFE;
  }

  private double score(Node child) {
    double bias = 0.0;
    
    // TODO review performance ...
    double w;
    if (player) {
      w = child.won + 0.0 * child.tie - child.lose;
    } else {
      w = child.lose + 0.0 * child.tie - child.won;
    }
    int n = child.totalTrials;
    int t = totalTrials >> 4;
    if (t >= LOG_CACHE_SIZE) t = LOG_CACHE_SIZE-1;
    
    return bias + w / n +  SCORE_C * Math.sqrt(logCache[t] / n);
  }

  private void backPropagate(int winner) {
    totalTrials++;

    if (winner == 0) {
      won++;
    } else if (winner == 1) {
      lose++;
    } else {
      tie++;
    }

    if (parent != null) {
      parent.backPropagate(winner);
    }
  }

  public Node getBest() {
    if (Player.DEBUG) {
      System.err.println("Total sims = " + totalTrials);
    }
    double best = Double.NEGATIVE_INFINITY;
    Node bestNode = childArray[0];
    for (int i=childArrayFE-1;i>=0;i--) {
      Node node = childArray[i];
      if (node.state.winner() == 0) {
        best = Double.POSITIVE_INFINITY;
        bestNode = node;
        return bestNode;
      }
      if (Player.DEBUG) {
        int row = MCTS.getRowFromDecal(node.gDecal, node.lDecal);
        int col = MCTS.getColFromDecal(node.gDecal, node.lDecal);
        System.err.println("Node : ("+node.gDecal+","+Integer.numberOfTrailingZeros(node.lDecal)+") => "+ node.won+"/"+node.lose+"/"+node.tie+ " - row/col:"+row+","+col);
        //debugNodechilds("  ", node);
      }
      
      double score = node.totalTrials; //1.0 * (node.won + node.tie / 2) / node.totalTrials;
      if (score > best) {
        best = score;
        bestNode = node;
      }
    }
    return bestNode;
  }
  
  public static void debugNodechilds(String decal, Node parent) {
    for (int i=parent.childArrayFE-1;i>=0;i--) {
      Node node = parent.childArray[i];
      System.err.println(decal+(parent.player?"ME :" :"HIM:" ) + "Node : ("+node.gDecal+","+Integer.numberOfTrailingZeros(node.lDecal)+") => "+ node.won+"/"+node.lose+"/"+node.tie);
      
      if (node.state.winner() == 0 || node.state.winner() == 1) {
        System.err.println("It's a win");
      }
      debugNodechilds(decal+"  ", node);
    }
  }
}
