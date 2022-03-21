package samegame.ai;

import java.util.Arrays;

import samegame.Pos;
import samegame.State;

public class BSLayer {
  public final static int MAX_NODES  = 80;
  private static State expandStates[] = new State[10_000];
  private static int expandStatesFE = 0;
  
  private static int[] toCheck = new int[15*15];
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
        if (toCheck[x+15*y]  == toCheckCurrentId) continue;
        if (state.grid[x+15*y] == State.EMPTY_CELL ) continue;

        positionsFE = 0;
        floodfill(state, state.grid[x+15*y], x,y);
        
        int count = positionsFE;
        if (count >= 2) {
          State newState = StateCache.get();
          newState.copyFrom(state);
          newState.remove(positions, positionsFE);
          newState.aiScore = 1 + newState.score;
          newState.picked = Pos.from(x, y);
          newState.parent =state;
          expandStates[expandStatesFE++] = newState;
        }
      }
    }
  }
  
  
  private void floodfill(State state, int color, int x, int y) {
    if (state.grid[x+15*y] != color) return ;
    if (toCheck[x+15*y] == toCheckCurrentId) return;
    
    
    toCheck[x+15*y] = toCheckCurrentId;
    positions[positionsFE++] = Pos.from(x, y);
    
    if (x>0) floodfill(state, color, x-1, y );
    if (y>0) floodfill(state, color, x, y-1);
    
    if (x<14) floodfill(state, color, x+1, y);
    if (y<14) floodfill(state, color, x, y+1);
    
  }


}
