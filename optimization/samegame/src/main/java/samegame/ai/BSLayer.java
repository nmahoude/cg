package samegame.ai;

import java.util.Arrays;

import samegame.Pos;
import samegame.State;

public class BSLayer {
  public final static int MAX_NODES  = 40;
  private static State expandStates[] = new State[10_000];
  private static int expandStatesFE = 0;
  
  private static int[] checked = new int[15*15];
  private static int toCheckCurrentId = 1;
  
  State[] states = new State[MAX_NODES];
  int statesFE = 0;
  public int layer;

  public void init(State original) {
    states[0]= original;
    statesFE = 1;
  }

  public void expand(BSLayer parent) {
    expandStatesFE = 0;
    
    for (int i=0;i<parent.statesFE;i++) {
      expand(parent.states[i]);
    }
    
    // only take the MAX_NODES bests
    Arrays.sort(expandStates, 0, expandStatesFE, (s1, s2) -> Double.compare(s2.aiScore, s1.aiScore));
    statesFE = 0;
    for (int i=0;i<expandStatesFE;i++) {
      if (statesFE >= MAX_NODES) {
        StateCache.release(expandStates[i]);
      } else {
        states[statesFE++] = expandStates[i];
      }
    }
  }

  Pos[] positions = new Pos[15*15];
  int positionsFE = 0;
  private void expand(State state) {
    toCheckCurrentId++;
    
    if (state.finished) {
      return;
    }
    
    for (int y = 14; y >= 0; y--) {
      for (int x = 0; x < 15; x++) {
        if (checked[x+15*y]  == toCheckCurrentId) continue;
        if (state.grid[x+15*y] == State.EMPTY_CELL ) continue;

        positionsFE = 0;
        floodfill(state, state.grid[x+15*y], x,y);
        
        int count = positionsFE;
        if (count >= 2) {
          State newState = StateCache.get();
          newState.copyFrom(state);
          newState.remove(positions, positionsFE);
          newState.aiScore = eval(state, newState);
          newState.picked = Pos.from(x, y);
          newState.parent =state;
          expandStates[expandStatesFE++] = newState;
        }
      }
    }
  }
  
  private double eval(State parent, State state) {
    if (state.finished) {
      return 1.0 + state.score;
    }
    
    int overestimatedRemainingScore = 0;
    boolean canClear = true;
    for (int i=0;i<5;i++) {
      if (state.colorCount[i] == 1) canClear = false;
      overestimatedRemainingScore += (state.colorCount[i]*state.colorCount[i]);
    }

    // newState.aiScore = state.score + layer * newState.score;
    return 0.0
        + 1.0 
        + parent.aiScore + layer * state.score 
        + overestimatedRemainingScore
        + (canClear ? 1000 : 0);
    
  }

  static Pos toCheck[] = new Pos[15*15];
  static int toCheckFE;
  
  private void floodfill(State state, int color, int startX, int startY) {
    
    toCheck[0] = Pos.from(startX, startY);
    toCheckFE = 1;
    
    while (toCheckFE != 0) {
      Pos current = toCheck[--toCheckFE];

      if (state.grid[current.offset] != color) continue;
      if (checked[current.offset] == toCheckCurrentId) continue;
      
      checked[current.offset] = toCheckCurrentId;
      positions[positionsFE++] = current;

      for (Pos p : current.neighbors) {
        toCheck[toCheckFE++] = p;
      }
    }
    
  }


}
