package coif.ai;

import coif.Board;
import coif.Cell;
import coif.Pos;
import coif.State;

/**
 * Mark all defending soldiers to stay still if the defend a position
 * @author nmahoude
 *
 */
public class StationDefenseAI implements AI {
  private Simulation sim;
  private State state;
  
  public StationDefenseAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;
  }
  
  @Override
  public void think() {

    for (int y=0;y<12;y++)  {
      for (int x=0;x<12;x++) {
        Pos pos = Pos.get(x, y);
        Cell current = state.getCell(pos);
        
        if (current.getStatut() == Board.P0_ACTIVE && current.unit != null) {
          for (Cell neighbor : current.neighbors) {
            int cValue = neighbor.getStatut();
            if (cValue == Board.P1_ACTIVE) {
              // don't move
              current.unit.done = true;
              break; 
            }
          }
        }
      }
    }
  }

}
