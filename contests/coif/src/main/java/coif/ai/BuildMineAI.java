package coif.ai;

import coif.Board;
import coif.Cell;
import coif.Pos;
import coif.State;
import coif.units.UnitType;

/**
 * Build mine if there is money left
 * @author nmahoude
 *
 */
public class BuildMineAI implements AI {

  private Simulation sim;
  private State state;

  public BuildMineAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;
  }
  @Override
  public void think() {
    System.err.println("BUILD MINE AI. turn is "+state.turn);
    if (state.turn > 0) return; // don't build mines too late in the game, ROI will be bad
    
    // near base first
    state.mineSpots.sort((m1, m2) -> Integer.compare(m1.manhattan(state.me.HQ), m2.manhattan(state.me.HQ)));
    
    for (Pos pos : state.mineSpots) {
      if (state.me.gold < 20) break; // no more money
      Cell cell = state.getCell(pos);
      if (cell.getStatut() != Board.P0_ACTIVE) continue;
      if (cell.unit != null) continue;
      
      sim.buildUnit(UnitType.MINE, pos);
    }
  }

}
