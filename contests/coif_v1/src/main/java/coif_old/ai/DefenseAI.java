package coif_old.ai;

import java.util.ArrayList;
import java.util.List;

import coif_old.Board;
import coif_old.Dir;
import coif_old.Pos;
import coif_old.State;
import coif_old.units.Unit;
import coif_old.units.UnitOrder;
import coif_old.units.UnitType;

/** defend blue cell with red soldier near **/
public class DefenseAI implements AI {

  private State state;
  private int toDefend;
  private List<Pos> toDefendPos = new ArrayList<>();
  
  private Simulation sim;
  
  public DefenseAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;
  }
  
  public void think() {
    toDefend = 0;
    for (int y=0;y<12;y++)  {
      for (int x=0;x<12;x++) {
        int cellValue = state.board.board[x+12*y];
        if (cellValue == Board.P0_ACTIVE) {
          Pos pos = Pos.get(x, y);
          for (Dir dir : Dir.values()) {
            Pos c = pos.move(dir);
            if (!c.isValid()) continue;
            int cValue = state.board.board[c.index];
            if (cValue == Board.P1_ACTIVE) {
              Unit u = state.getUnitAtPos(pos);
              if (u != null) {
                u.order = UnitOrder.STAY; // defend this position
              } else {
                toDefend++;
                toDefendPos.add(pos);
              }
            }
          }
        }
      }
    }
    

    // defend near HQ first
    toDefendPos.sort((p1, p2) -> {
      return Integer.compare(p1.manhattan(state.me.HQ), p2.manhattan(state.me.HQ));
    });
    System.err.println("Cells to DEFEND : " + toDefend);
    System.err.println(toDefendPos);

    
    while (!toDefendPos.isEmpty() && Unit.SOLDIER_1_COST  < state.me.gold) {
      // we can put a soldier in all cells to defend
      // TODO check level of soldier
      // TODO check if we can/should put pression by attacking with a soldier++ instead of simple defense
      Pos p = toDefendPos.remove(0);
      
      // check if a free solder can move to position to defend
      for (Dir dir : Dir.values()) {
        Pos n = p.move(dir);
        if (!n.isValid()) continue;
        Unit u = state.getUnitAtPos(n);
        if (u == null) continue;
        break;
      }
      
      sim.trainUnit(UnitType.SOLDIER_1, p);
    }
  }
}
