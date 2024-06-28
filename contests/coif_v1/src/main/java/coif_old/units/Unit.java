package coif_old.units;

import java.util.Scanner;

import coif_old.Player;
import coif_old.Pos;
import coif_old.actions.MoveAction;

public class Unit {
  public static final int TOWER_COST = 15;
  public static final int SOLDIER_1_COST = 10;
  public int owner;
  public Pos pos;
  public int unitId;
  public UnitType type;
  public UnitOrder order = UnitOrder.FREE;
  
  public int degreeOfFreedom; // TODO ?
  public boolean done = false; // free to move this turn
  
  @Override
  public String toString() {
    return "Unit ("+unitId+") at "+pos;
  }
  
  public void readSoldier(Scanner in) {
    owner = in.nextInt();
    unitId = in.nextInt();
    int level = in.nextInt();
    int x = in.nextInt();
    int y = in.nextInt();
    
    pos = Pos.get(x, y);
    type = UnitType.getFromLevel(level);
    
    if (Player.DEBUG_INPUT) {
      System.err.println(String.format("%d %d %d %d %d", owner, unitId, level, x, y));
    }
  }

  public void readBuilding(Scanner in) {
    owner = in.nextInt();
    unitId = -1;

    int bt = in.nextInt();
    type = UnitType.getFromBuilding(bt);
    int x = in.nextInt();
    int y = in.nextInt();
    pos = Pos.get(x, y);

    if (Player.DEBUG_INPUT) {
      System.err.println(String.format("%d %d %d %d", owner, bt, x, y));
    }
  }
  
  public MoveAction moveAction(Pos pos) {
    return new MoveAction(this.unitId, pos);
  }

  public boolean isStatic() {
    return type == UnitType.HQ || type == UnitType.MINE || type == UnitType.TOWER;
  }

  public boolean canKill(Unit other) {
    if (this.type == UnitType.SOLDIER_1 && other.type.neededLevelToKill <= 1) return true;
    if (this.type == UnitType.SOLDIER_2 && other.type.neededLevelToKill <= 2) return true;
    if (this.type == UnitType.SOLDIER_3 && other.type.neededLevelToKill <= 3) return true;
    return false;
  }
}
