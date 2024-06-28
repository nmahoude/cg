package coif.ai;

import coif.State;
import coif.units.Unit;

/**
 * All soldier who didn't move go to hq
 * @author nmahoude
 *
 */
public class RushToHQAI implements AI {

  private Simulation sim;
  private State state;
  public RushToHQAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;
  }
  
  @Override
  public void think() {
    for (Unit unit : state.units) {
      if (unit.done 
          || unit.owner == 1 
          || unit.dead 
          || unit.isStatic()) continue;
      sim.moveToHQ(unit, state.opp.HQ);
    }

  }

}
