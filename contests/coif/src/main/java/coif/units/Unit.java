package coif.units;

import java.util.Scanner;

import coif.Cell;
import coif.Player;
import coif.Pos;

public class Unit {
  public static final int TOWER_COST = 15;
  public static final int SOLDIER_1_COST = 10;
  
  public int owner;
  public Pos pos;
  public int id;
  public UnitType type;
  public boolean done; // action already done this turn
  public int degreeOfFreedom;
  public boolean dead;
  
  @Override
  public String toString() {
    return "Unit ("+id+") at "+pos;
  }
  
  public void readSoldier(Scanner in) {
    owner = in.nextInt();
    id = in.nextInt();
    int level = in.nextInt();
    int x = in.nextInt();
    int y = in.nextInt();
    
    pos = Pos.get(x, y);
    type = UnitType.getFromLevel(level);
    
    if (Player.DEBUG_INPUT) {
      System.err.println(String.format("%d %d %d %d %d", owner, id, level, x, y));
    }
  }

  public void readBuilding(Scanner in) {
    owner = in.nextInt();
    id = -1;

    int bt = in.nextInt();
    type = UnitType.getFromBuilding(bt);
    int x = in.nextInt();
    int y = in.nextInt();
    pos = Pos.get(x, y);

    if (Player.DEBUG_INPUT) {
      System.err.println(String.format("%d %d %d %d", owner, bt, x, y));
    }
  }

  public boolean canKill(Unit other) {
    if (other == null) 
      return true;
    else 
      return other.canBeKilledBy(this);
  }
  
  public boolean isStatic() {
    return type == UnitType.HQ || type == UnitType.MINE || type == UnitType.TOWER;
  }

  public boolean canBeKilledBy(Unit unit) {
    return type.neededLevelToKill <= unit.type.level;
  }

  public boolean canWalkOn(Cell cell) {
    // lvl 3 can go anywhere 
    // TODO maybe check it is not void ?
    if (this.type == UnitType.SOLDIER_3) return true;
    if (cell.unit == null) return true;
    if (cell.unit.canBeKilledBy(this)) return true;
    
    for (Cell neighbor : cell.neighbors) {
      if (neighbor.unit != null 
          && neighbor.unit.type == UnitType.TOWER 
          && neighbor.isActive() == true
          && neighbor.unit.owner != this.owner) {
        return false;
      }
    }
    return false;
  }
}
