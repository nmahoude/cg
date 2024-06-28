package coif.ai;

import java.util.ArrayList;
import java.util.List;

import coif.Board;
import coif.Cell;
import coif.Pos;
import coif.State;
import coif.units.Unit;
import coif.units.UnitType;

/**
 * Fill the front to protect against ennemies
 *
 */
public class DefenseAI implements AI {

  private Simulation sim;
  private State state;
  
  public DefenseAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;
  }
  
  @Override
  public void think() {
    int toDefend = 0;
    List<Pos> toDefendPos = new ArrayList<>();
    for (int y=0;y<12;y++)  {
      for (int x=0;x<12;x++) {
        Pos pos = Pos.get(x, y);
        Cell current = state.getCell(pos);
        
        if (current.getStatut() == Board.P0_ACTIVE) {
          for (Cell neighbor : current.neighbors) {
            int cValue = neighbor.getStatut();
            if (cValue == Board.P1_ACTIVE) {
              toDefend++;
              toDefendPos.add(pos);
              break;
            }
          }
        }
      }
    }
    
    // defend near HQ first
    // TODO check criticiy, some cells may be near HQ but no threat
    toDefendPos.sort((p1, p2) -> {
      return Integer.compare(p1.manhattan(state.me.HQ), p2.manhattan(state.me.HQ));
    });
    System.err.println("Cells to DEFEND : " + toDefend);
    System.err.println(toDefendPos);

    
    while (!toDefendPos.isEmpty() 
        && Unit.SOLDIER_1_COST  < state.me.gold) {
      // we can put a soldier in all cells to defend
      // TODO check level of soldier
      // TODO check if we can/should put pression by attacking with a soldier++ instead of simple defense
      Pos p = toDefendPos.remove(0);
      
      Cell current = state.getCell(p);

      // already defended  ?
      if (current.unit != null) {
        current.unit.done = true;
        System.err.println("DEFENSE : "+p+" already defended");
        continue;
      }
      
      // check if a free solder can move to position to defend
      boolean defendByMove = false;
      for (Cell neighbor : current.neighbors) {
        Unit u = neighbor.unit;
        if (u == null) continue;
        if (u.owner == 1) continue;
        if (u.done) continue; // already move or blocked
        
        sim.moveUnit(u, current.pos);
        defendByMove = true;
        System.err.println("DEFENSE : "+p+" move to defend by "+u);
        break;
      }
      
      // create a new soldier
      if (!defendByMove) {
        // TODO enough gold ? 
        System.err.println("DEFENSE : "+p+" need new soldier");
        sim.trainUnit(UnitType.SOLDIER_1, p);
      }
    }
  }

}
