package coif.ai;

import java.util.ArrayList;
import java.util.List;

import coif.Pos;
import coif.State;
import coif.units.Unit;
import coif.units.UnitType;

public class Simulation {
  State state;

  List<String> actionsRecord = new ArrayList<>();
  
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
    if (unit.canWalkOn(state.getCell(newPos)))
    
    // realize action
    state.move(unit, newPos);
    unit.done = true;
    
    // record action
    actionsRecord.add(String.format("MOVE %d %d %d;", unit.id, newPos.x, newPos.y));
    
    return true;
  }
  
  public boolean trainUnit(UnitType type, Pos pos) {
    System.err.println("TRAIN "+type+" @ "+pos);
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
    
    
    actionsRecord.add(String.format("TRAIN %d %d %d;", type.level, pos.x, pos.y));
    return true;
  }
  
  public void buildUnit(UnitType type, Pos pos) {
    Unit unit = new Unit();
    unit.type = type;
    state.me.gold -= unitValue(type);
    state.move(unit, pos);
    unit.done = true;
    
    if (type == UnitType.TOWER) {
      actionsRecord.add(String.format("BUILD TOWER %d %d;", pos.x, pos.y));
    } else if (type == UnitType.MINE) {
      state.numberOfMines++; // TODO in state !!!!
      actionsRecord.add(String.format("BUILD MINE %d %d;", pos.x, pos.y));
    }
  }
  
  
  private int unitValue(UnitType type) {
    if (type == UnitType.SOLDIER_1 ) return 10;
    if (type == UnitType.SOLDIER_2 ) return 20;
    if (type == UnitType.SOLDIER_3 ) return 30;
    if (type == UnitType.TOWER ) return 15;
    if (type == UnitType.MINE) return 20+state.getNumberOfMines();
    throw new RuntimeException("unknwon unit "+type);
  }

  public String output() {
    StringBuffer sb = new StringBuffer();
    if (actionsRecord.isEmpty()) {
      return "WAIT";
    } else {
      for (String action : actionsRecord) {
        sb.append(action.toString());
      }
      return sb.toString();
    }
  }

  public void moveToHQ(Unit unit, Pos hQ) {
    // TODO FIX this method to use path finding
    actionsRecord.add(String.format("MOVE %d %d %d;", unit.id, hQ.x, hQ.y));
    unit.done = true;
  }

}
