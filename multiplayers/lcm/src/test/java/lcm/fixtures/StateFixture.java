package lcm.fixtures;

import lcm.State;

public class StateFixture {

  public static State createBattleState() {
    State state = new State();
    state.turn = 31;
    return state;
  }
}
