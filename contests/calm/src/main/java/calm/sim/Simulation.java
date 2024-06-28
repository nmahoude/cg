package calm.sim;

import calm.actions.Action;
import calm.state.Agent;
import calm.state.State;

public class Simulation {

  public void simulate(State state, Action actionP1, Action actionP2) {
    
    simulate(state, state.agent1, actionP1);
    if (actionP2 != null) simulate(state, state.agent2, actionP2);
  }

  private void simulate(State state, Agent agent, Action action) {
    action.applyEffect(state, agent);
  }
}
