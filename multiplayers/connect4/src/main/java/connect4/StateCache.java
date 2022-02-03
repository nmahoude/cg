package connect4;

public class StateCache {
  public static final int MAX = 1_000_000;
  private static State states[] = new State[MAX];
  private static int statesFE;
  
  static {
    for (int i=0;i<states.length;i++) {
      states[i] = State.emptyState();
    }
  }
  
  public static void reset() {
    statesFE = 0;
  }
  
  public static State getFrom(State parent) {
    State state = states[statesFE++];
    state.init(parent);
    
    return state;
  }
}
