package coif.ai;

import coif.Board;
import coif.Cell;
import coif.Pos;
import coif.State;
import coif.units.UnitType;

/**
 * Fill the front to protect against ennemies
 *
 */
public class SimpleDefenseAI implements AI {

  private Simulation sim;
  private State state;
  
  public SimpleDefenseAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;
  }
  
  @Override
  public void think() {
    System.err.println("check for simple defense tower (gold is "+state.me.gold);
    if (state.me.gold < 15) return;
    
    // check for tower at 1,1
    Pos targetPos = state.me.HQ == Pos.get(0, 0) ? Pos.get(1, 1) : Pos.get(10, 10);
    Cell target = state.getCell(targetPos);
    if (target.getStatut() == Board.P0_ACTIVE && target.unit == null ) {
      System.err.println("Building simple defense tower");
      sim.buildUnit(UnitType.TOWER, target.pos);
    }
  }

}
