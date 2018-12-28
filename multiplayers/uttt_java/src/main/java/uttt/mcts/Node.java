package uttt.mcts;

import uttt.Player;
import uttt.state.PossibleMoveCache;
import uttt.state.State2;

public class Node {
  private static final int LOG_DECAL = 1;
  private static final int LOG_CACHE_SIZE = 10_000;
  static final double logCache[] = new double[LOG_CACHE_SIZE];
  static {
    logCache[0] = 0;
    for (int i=1;i<logCache.length;i++) {
      logCache[i] = Math.log((double)(i<<LOG_DECAL));
    }
  }
  
  private static final double SCORE_C = 0.8; //Math.sqrt(2);

  static final State2 tempState = new State2();
  
  
  Node parent;
  State2 state = new State2();

  Node childArray[] = new Node[81];
  int childArrayFE = -1;
  int unexploredFE = 0;
  
  boolean player = true;
  int Q;
  int N;

  public int gDecal;
  public int lDecal;
  public static int rollout;

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
    this.Q= 0;
    this.N = 0;
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
      // normalement on ne vient plus la
      runSimulation();
      return;
    } else {
      if (unexploredFE != 0) {
        int rand = unexploredFE-1; //Player.random.nextInt(unexploredFE);
        Node toTake = childArray[rand];
        childArray[rand] = childArray[--unexploredFE]; 
        childArray[unexploredFE] = toTake;

        if (toTake.state.winner() == 0) {
          if (player) {
            toTake.Q = Integer.MAX_VALUE;
            this.backPropagate(Integer.MIN_VALUE);
          } else {
            toTake.Q = Integer.MIN_VALUE;
            this.backPropagate(Integer.MAX_VALUE);

          }
        } else if (toTake.state.winner() == 1) {
          if (player) {
            toTake.Q = Integer.MIN_VALUE;
            this.backPropagate(Integer.MAX_VALUE);
            
          } else {
            toTake.Q = Integer.MAX_VALUE;
            this.backPropagate(Integer.MIN_VALUE);
          }
        } else {
          toTake.runSimulation();
        }
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
    int winner = rollout();
    
    if (winner == 2) {
      // tie !
      this.backPropagate(0);
      return;
    }
    
    if (!player) {
      if (winner == 0) {
        this.backPropagate(1);
      } else {
        this.backPropagate(-1);
      }
    } else {
      if (winner == 0) {
        this.backPropagate(-1);
      } else {
        this.backPropagate(1);
      }
    }
  }

  static int possibleMoves[] = new int[9*9];
  static int possibleMovesFE = 0;
  
  static int myInstaWins[] = new int[9];
  static int hisInstaWins[] = new int[9];
  static State2 instaWinState = new State2();
  
  static PossibleMovesAccelerator pma = new PossibleMovesAccelerator();
  
  private void calcInstaWins(State2 state) {
    for (int i=0;i<9;i++) {
      myInstaWins[i] = 0;
      hisInstaWins[i] = 0;
    }
    
    instaWinState.copyFrom(state);
    instaWinState.nextPlayGrid = -1;
    getPossibleMoves(state);
    for (int i=0;i<possibleMovesFE;i++) {
      instaWinState.copyFrom(state);
      int full = possibleMoves[i];
      int gDecal = full >> 9;
      int lDecal = full & 0b111111111;
      instaWinState.set(true, gDecal, lDecal);
      if (instaWinState.winner == 0) {
        myInstaWins[gDecal]|= lDecal;
      }
    }
    for (int i=0;i<possibleMovesFE;i++) {
      instaWinState.copyFrom(state);
      int full = possibleMoves[i];
      int gDecal = full >> 9;
      int lDecal = full & 0b111111111;
      instaWinState.set(false, gDecal, lDecal);
      if (instaWinState.winner == 1) {
        hisInstaWins[gDecal]|=lDecal;
      }
    }
  }
  
  private int rollout() {
    rollout++;
    tempState.copyFrom(state);
//    calcInstaWins(tempState);
    boolean currentPlayer = this.player;
//    pma.build(tempState);
    while (tempState.winner == -1) {
      getPossibleMoves(tempState);
      int full = -1;

//      full = instaWins(currentPlayer, full);
      
//      full = pmaChoose();
        // no instawin found, continue random
      int rand = Player.random.nextInt(possibleMovesFE);
      full = possibleMoves[rand];
      
      
      int gDecal = full >> 9;
      int lDecal = full & 0b111111111;
      tempState.set(currentPlayer, gDecal, lDecal);
     
//      // cutoff early if start of game
//      if (Player.turn < 10) {
//        int cutoffWinner = evaluationCutoff(tempState);
//        if (cutoffWinner != -1) {
//          return cutoffWinner;
//        }
//      }
      currentPlayer = !currentPlayer;
    }
    return tempState.winner();
  }
  
  private int evaluationCutoff(State2 state) {
    int p0cells = Integer.bitCount(state.global & 0b111111111);
    int p1cells = Integer.bitCount(state.global >> 9);
    if (p0cells - p1cells >= 2) return 0;
    if (p1cells - p0cells >= 2) return 1;
    return -1;
  }
  
  private int pickRandom(int full) {
    if (full == -1) {
      // no instawin found, continue random
      int rand = Player.random.nextInt(possibleMovesFE);
      full = possibleMoves[rand];
    }
    return full;
  }
  private int pmaChoose() {
    int full;
    if (tempState.nextPlayGrid != -1) {
      full = pma.pickAMove(tempState.nextPlayGrid);
    } else {
      full = pma.pickAMove();
    }
    return full;
  }
  private int instaWins(boolean currentPlayer, int full) {
    for (int i=0;(i<possibleMovesFE) ;i++) {
       int move = possibleMoves[i];
       int gDecal = move >> 9;
       int lDecal = move & 0b111111111;

       if (currentPlayer) {
         if ((myInstaWins[gDecal]& lDecal) != 0) {
           full = possibleMoves[i];
           break;
         }
       } else {
         if ((hisInstaWins[gDecal]& lDecal) != 0) {
           full = possibleMoves[i];
           break;
         }
       }
     }
    return full;
  }

  private final void getPossibleMoves(final State2 state) {
    possibleMovesFE = 0;
    if (state.nextPlayGrid != -1) {
      getPossibleMovesForGrid(state.nextPlayGrid, state.cells[state.nextPlayGrid]);
    } else {
//      int index = 0;
//      for (int d = 1; d <= 0b100_000_000; d *= 2) {
//        if ((state.globalMask & d) == 0) getPossibleMovesForGrid(index, state.cells[index]);
//        index++;
//      }
      getPossibleMovesForGrid(0, state.cells[0]);
      getPossibleMovesForGrid(1, state.cells[1]);
      getPossibleMovesForGrid(2, state.cells[2]);
      getPossibleMovesForGrid(3, state.cells[3]);
      getPossibleMovesForGrid(4, state.cells[4]);
      getPossibleMovesForGrid(5, state.cells[5]);
      getPossibleMovesForGrid(6, state.cells[6]);
      getPossibleMovesForGrid(7, state.cells[7]);
      getPossibleMovesForGrid(8, state.cells[8]);
    }
  }

  private final void getPossibleMovesForGrid(int gDecal, int complete) {
    int mask = (complete | ((complete >> 9) )) & State2.ALL_MASK;
    for (int i=PossibleMoveCache.possibleMovesFE[mask]-1;i>=0;i--) {
      possibleMoves[possibleMovesFE++] = PossibleMoveCache.possibleMoves[512*81*gDecal+81*mask+i];
    }
  }
  
  private final void getPossibleMovesForGrid_old(int gDecal, int mask) {
    int all = (mask | ((mask >> 9) )) & State2.ALL_MASK;
    if (all == State2.ALL_MASK) return; // no possible move  
    
    int baseY = gDecal << 9;

    for (int d = 1; d <= 0b100_000_000; d *= 2) {
      if ((all & d) == 0) {
        possibleMoves[possibleMovesFE++] = baseY + d;
      }
    }
  }

  
  void getAllChildren() {
    childArrayFE = 0;
    unexploredFE = 0;
    getPossibleMoves(state);

    int full;

    for (int i=possibleMovesFE-1;i>=0;i--) {
      Node node = NodeCache.pop();

      node.state.copyFrom(this.state);
      
      full = possibleMoves[i];
      int gDecal = full >> 9;
      int lDecal = full & 0b111111111;
      
      node.state.set(this.player, gDecal, lDecal);
      node.update(this, !this.player, gDecal, lDecal);
      childArray[childArrayFE++] = node;

    }
    unexploredFE = childArrayFE;
  }

  private double score(Node child) {
    if (child.Q == Integer.MAX_VALUE || child.Q == Integer.MIN_VALUE) {
      return Double.NEGATIVE_INFINITY;
    }
    
    double n = child.N;
    double log;
    int t = this.N >> LOG_DECAL;
    if (t >= LOG_CACHE_SIZE) {
      log = Math.log(t); 
    } else {
      log = logCache[t];
    }
    
    return child.Q / n +  SCORE_C * Math.sqrt(log / n);
  }

  private void backPropagate(int result) {
    N++;

    if (result == Integer.MAX_VALUE) {
      // proven win, so we need to look at all children
      boolean fullProvenWin = true;
      for (int i=0;i<childArrayFE;i++) {
        if (childArray[i].Q != Integer.MIN_VALUE) {
          fullProvenWin = false;
          break;
        }
      }
      if (fullProvenWin) {
        Q = Integer.MAX_VALUE; // proven loss for us :(
        if (parent != null) {
          parent.backPropagate(Integer.MIN_VALUE);
        }
      } else {
        // still not a proven loss, we have alternatives
        Q++; // just a regular win
      }
      
      
    } else if (result == Integer.MIN_VALUE) {
      // proven loss from child, so it's a win for us
      Q = Integer.MIN_VALUE;
      if (parent != null) {
        parent.backPropagate(Integer.MAX_VALUE);
      }
    } else {
      Q += result;
      
      if (parent != null) {
        parent.backPropagate(-result);
      }
    }
  }

  public Node getBest() {
    if (Player.DEBUG) {
      System.err.println("Total sims = " + N);
    }
    double best = Double.NEGATIVE_INFINITY;
    Node bestNode = childArray[0];
    for (int i=childArrayFE-1;i>=0;i--) {
      Node node = childArray[i];
      if (node.Q == Integer.MAX_VALUE) {
        best = Double.POSITIVE_INFINITY;
        bestNode = node;
        if (Player.DEBUG) {
          System.err.println("Found a proven win ! ");
          debugNodeInfo(node);
        }
        return bestNode;
      }
      if (Player.DEBUG) {
        debugNodeInfo(node);
      }
      
      double score = 1.0 * node.Q / node.N; //1.0 * (node.won + node.tie / 2) / node.totalTrials;
      if (score > best) {
        best = score;
        bestNode = node;
      }
    }
    return bestNode;
  }
  private void debugNodeInfo(Node node) {
    int row = MCTS.getRowFromDecal(node.gDecal, node.lDecal);
    int col = MCTS.getColFromDecal(node.gDecal, node.lDecal);
    System.err.println("Node : ("+node.gDecal+","+Integer.numberOfTrailingZeros(node.lDecal)
    + ") => "+ node.Q +" / " + node.N
    + " - row/col:"+row+","+col);
  }
  
  public static void debugNodechilds(String decal, Node child) {
    System.err.println(decal+(!child.player?"ME : " :"HIM: " ) + "Node : ("+child.gDecal+","+Integer.numberOfTrailingZeros(child.lDecal)+") => "+ child.Q + " / " + child.N);
    
    if (child.state.winner() == 0 || child.state.winner() == 1) {
      System.err.println(decal + "It's a win");
    }

    for (int i=child.childArrayFE-1;i>=0;i--) {
      Node node = child.childArray[i];
      debugNodechilds(decal+"  ", node);
    }
  }
}
