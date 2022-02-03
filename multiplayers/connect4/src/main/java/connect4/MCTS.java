package connect4;

import cgutils.random.FastRandom;

public class MCTS {
  private static FastRandom random = new FastRandom(0);
  
  public int think(State root) {
    StateCache.reset();
    
    root.wins = 0;
    root.count = 0;
    root.childsFE = 0;
    
    long start = System.currentTimeMillis();
    for (int i=0;i<1_000;i++) {
      State toExpand = selection(root);
      expand(toExpand);
    }
    long end = System.currentTimeMillis();
    System.err.println("MCTS in "+(end-start)+" ms");
    
    double bestScore = -1;
    State best = null;
    for (int i=0;i<root.childsFE;i++) {
      double score = 1.0 * root.childs[i].wins / root.childs[i].count;
      System.err.println("For column "+i+" wins = "+root.childs[i].wins+" / "+root.childs[i].count);
      if (score > bestScore) {
        bestScore = score;
        best = root.childs[i];
      }
    }
    // find back the column to play! 
    for (int i=0;i<9;i++) {
      if (root.firstEmptyCell(i) != best.firstEmptyCell(i)) {
        return i;
      }
    }
    return -1;
    
  }

  private void expand(State state) {
    state.childsFE = 0;
    
    for (int col=0;col<9;col++) {
      if (!state.canPutOn(col)) {
        continue;
      } else {
        State child = StateCache.getFrom(state);
        child.put(col, state.turn); // simulate
        child.turn = !state.turn;
        
        state.childs[state.childsFE++] = child;
        
        simulate(child);
        
      }
    }
  }

  State tmp = State.emptyState();
  private void simulate(State state) {
    
    boolean turn = state.turn;
    tmp.copyFrom(state);
    
    while (!tmp.end()) {
      int col = tmp.possibleColumns[random.nextInt(tmp.possibleColumnsFE)];
      tmp.put(col, turn);
      turn = !turn;
    }
    
    backpropagate(state);
  }

  private void backpropagate(State state) {
    State current = state;
    while (current != null) {
      if (tmp.winner == 0 || tmp.winner == 2 /* draw */) {
        current.wins++;
      }
      current.count ++;
      
      current = current.parent;
    }
  }

  private State selection(State state) {
    if (state.childsFE == 0) {
      return state;
    }
    
    double bestScore = Double.NEGATIVE_INFINITY;
    State best = null;
    for (int i=0;i<state.childsFE;i++) {
      State child = state.childs[i];
      double score =  1.0 * child.wins / child.count 
                    + Math.sqrt(2.0) * Math.sqrt( Math.log(state.count) / child.count);
      if (score > bestScore) {
        bestScore = score;
        best = child;
      }
    }
    return best;
  }
}
