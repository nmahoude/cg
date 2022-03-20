package samegame.ai;

import samegame.State;

public class StateCache {
  private final static int MAX_STATES = 300_000;
  private final static State[] states = new State[MAX_STATES];
  private static int cacheFE = 0;

  private final static State[] releasedStates = new State[MAX_STATES];
  private static int releasedCacheFE = 0;
  
  static {
    for (int i=0;i<MAX_STATES;i++) {
      states[i] = new State();
    }
  }
  

  public static void reset() {
    cacheFE = 0;
    releasedCacheFE= 0;
  }
  
  public static State get() {
    if (releasedCacheFE > 0) {
      return releasedStates[--releasedCacheFE];
    } else {
      return states[cacheFE++];
    }
  }
  
  public static void release(State state) {
    releasedStates[releasedCacheFE++] = state;
  }
}
