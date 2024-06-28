package coif_old.ai;

import java.util.ArrayList;
import java.util.List;

import coif_old.Pos;
import coif_old.State;
import coif_old.actions.Action;
import coif_old.actions.BuildTowerAction;
import coif_old.actions.MoveAction;
import coif_old.actions.TrainAction;
import coif_old.units.Unit;
import coif_old.units.UnitType;

public class Simulation {
  State state;

  List<Action> actionsRecord = new ArrayList<>();
  
  public Simulation(State state) {
    this.state = state;
    actionsRecord.clear();
  }

  public boolean block(Unit unit) {
    unit.done = true; // only block the unit 
    return true;
  }
  
  public boolean moveUnit(Unit unit, Pos newPos) {
    if (unit.done) {
      System.err.println("ERROR - trying to move again unit "+unit);
      return false;
    }
    if (unit.pos.manhattan(newPos) != 1) return false;
    Unit other = state.getUnitAtPos(newPos);
    if (other != null && !unit.canKill(other)) {
      return false;
    }
    
    // realize action
    state.move(unit, newPos);
    unit.done = true;
    
    // record action
    actionsRecord.add(new MoveAction(unit.unitId, newPos));
    
    return true;
  }
  
  public boolean trainUnit(UnitType type, Pos pos) {
    if (type == UnitType.SOLDIER_1 && state.me.gold < 10) return false;
    if (type == UnitType.SOLDIER_2 && state.me.gold < 20) return false;
    if (type == UnitType.SOLDIER_3 && state.me.gold < 30) return false;
    if (!pos.isValid()) return false;
    
    // TODO check if our type can kill the 'other' one ...
    Unit unit = new Unit();
    unit.type = type;
    state.move(unit, pos);
    state.me.gold-=unitValue(type);
    unit.done = true;
    
    
    actionsRecord.add(new TrainAction(type.level, pos));
    return true;
  }
  
  public void buildUnit(UnitType type, Pos pos) {
    Unit unit = new Unit();
    unit.type = type;
    state.me.gold -= unitValue(type);
    state.move(unit, pos);
    unit.done = true;
    actionsRecord.add(new BuildTowerAction(pos));
  }
  
  
  private int unitValue(UnitType type) {
    if (type == UnitType.SOLDIER_1 ) return 10;
    if (type == UnitType.SOLDIER_2 ) return 20;
    if (type == UnitType.SOLDIER_3 ) return 30;
    if (type == UnitType.TOWER ) return 15;
    throw new RuntimeException("unknwon unit "+type);
  }

  public String output() {
    StringBuffer sb = new StringBuffer();
    if (actionsRecord.isEmpty()) {
      return "WAIT";
    } else {
      for (Action action : actionsRecord) {
        sb.append(action.toString());
      }
      return sb.toString();
    }
  }

}
