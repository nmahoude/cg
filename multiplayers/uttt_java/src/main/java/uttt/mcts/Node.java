package uttt.mcts;

import uttt.Player;
import uttt.state.Grid;
import uttt.state.State;

public class Node {
  

  private static final double SCORE_C = Math.sqrt(2);

  static final State tempState = new State();
  
  Node parent;
  State state = new State();

  int childArrayFE = -1;
  final Node childArray[] = new Node[81];
  final Node unexplored[] = new Node[81];
  int unexploredFE = 0;
  
  boolean player = true;
  private int won;
  private int lose;
  private int tie; // should be able to discard it
  int totalTrials;

  public int col;
  public int row;

  public void release() {
    for (int i=0;i<childArrayFE;i++) {
      childArray[i].release();
    }
    NodeCache.push(this);
  }
  public void update(Node parent, boolean player, int row, int col) {
    this.parent = parent;
    this.player = player;
    
    this.col = col;
    this.row = row;
    reset(); 
  }

  private void reset() {
    this.won = 0;
    this.lose = 0;
    this.tie = 0;
    this.totalTrials = 0;
  }
  
  void chooseChild() {
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
        Node toTake = unexplored[rand];
        unexplored[rand] = unexplored[--unexploredFE]; 
        toTake.runSimulation();
      } else {
        
        Node bestChild = childArray[0];
        double bestScore = score(bestChild);
        double score;
        for (int i=0;i<childArrayFE;i++) {
          Node child = childArray[i];
          score = score(child);
          if (score > bestScore) {
            bestScore = score;
            bestChild = child;
          }
        }
        bestChild.chooseChild();
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
      int row = full / 10;
      int col = full - (row* 10);
      tempState.set(currentPlayer, row, col);
     
      currentPlayer = !currentPlayer;
    }
    return tempState.winner();
  }

  private final void getPossibleMoves(final State state) {
    possibleFE = 0;
    if (state.nextPlayGrid != null) {
      getPossibleMovesForGrid(state.nextPlayGrid);
    } else {
      getPossibleMovesForGrid(state.grids[0]);
      getPossibleMovesForGrid(state.grids[1]);
      getPossibleMovesForGrid(state.grids[2]);
      getPossibleMovesForGrid(state.grids[3]);
      getPossibleMovesForGrid(state.grids[4]);
      getPossibleMovesForGrid(state.grids[5]);
      getPossibleMovesForGrid(state.grids[6]);
      getPossibleMovesForGrid(state.grids[7]);
      getPossibleMovesForGrid(state.grids[8]);
    }
  }

  private final void getPossibleMovesForGrid(final Grid grid) {
    if (grid.full) return; 
    
    final int all = grid.getComplete();
    final Grid.PMove moves[] = Grid.possibleMoves[all];
    final int size = Grid.possibleMovesFE[all];
    for (int i=size-1;i>=0;i--) {
      Grid.PMove move = moves[i];
      possibleGrid[possibleFE++] = 
                    /*row*/ 10 * (grid.baseY + move.y)
                  + /*col*/ (grid.baseX  + move.x);
    }
  }

  void getAllChildren() {
    childArrayFE = 0;
    unexploredFE = 0;
    getPossibleMoves(state);

    int full, col, row;

    for (int i=0;i<possibleFE;i++) {
      Node node = NodeCache.pop();

      node.state.copyFrom(this.state);
      
      full = possibleGrid[i];
      row = full / 10;
      col = full - (row * 10);

      node.state.set(this.player, row, col);
      node.update(this, !this.player, row, col);
      childArray[childArrayFE++] = node;
      unexplored[unexploredFE++] = node;
    }
  }

  private double score(Node child) {
    
    // TODO review performance ...
    double w;
    if (player) {
      w = child.won  + tie / 2  -  child.lose;
    } else {
      w = child.lose + tie / 2 -  child.won;
    }
    int n = child.totalTrials;
    double t = totalTrials;

    return w / n ; //  +  SCORE_C * t / (n*n); //Math.sqrt(Math.log(t) / n);
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
    Node bestNode = null;
    for (int i=0;i<childArrayFE;i++) {
      Node node = childArray[i];
      if (Player.DEBUG) {
        System.err.println("Node : ("+node.row+","+node.col+") => "+ node.won+"/"+node.lose+"/"+node.tie);
      }
      double score = node.totalTrials; //1.0 * (node.won + node.tie / 2) / node.totalTrials;
      if (score > best) {
        best = score;
        bestNode = node;
      }
    }
    return bestNode;
  }
  public void setState(State state) {
    this.state.copyFrom(state);
    this.state.nextPlayGrid = state.nextPlayGrid;
  }
}
