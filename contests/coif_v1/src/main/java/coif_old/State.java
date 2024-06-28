package coif_old;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import coif_old.units.Unit;
import coif_old.units.UnitType;

public class State {
  public Agent me = new Agent(null);
  public Agent opp = new Agent(null);
  public Board board = new Board();
  public List<Unit> units = new ArrayList<>();
  
  public Unit unitsToPlay[] = new Unit[144];
  public int unitsToPlayFE = 0;
  
  public void readInit(Scanner in) {
    int numberMineSpots = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.print(numberMineSpots+" ");
    }
    for (int i = 0; i < numberMineSpots; i++) {
      int x = in.nextInt();
      int y = in.nextInt();
      if (Player.DEBUG_INPUT) {
        System.err.print(""+x+" "+y+ " ");
      }
    }
    if (Player.DEBUG_INPUT) {
      System.err.println();
    }
  }

  public void readTurn(Scanner in) {
    unitsToPlayFE = 0;
    
    me.read(in);
    opp.read(in);
    
    board.read(in);

    units.clear();
    
    int buildingCount = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.println(buildingCount);
    }
    for (int i = 0; i < buildingCount; i++) {
      Unit building = new Unit();
      building.readBuilding(in);
      units.add(building);
      
      if (building.type == UnitType.HQ) {
        if (building.owner == 0) {
          me.HQ = building.pos;
        } else {
          opp.HQ = building.pos;
        }
      }
    }

    int unitCount = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.println(unitCount);
    }
    for (int i = 0; i < unitCount; i++) {
      Unit unit = new Unit();
      unit.readSoldier(in);
      
      units.add(unit);
      if (unit.owner == 0) {
        unitsToPlay[unitsToPlayFE++] = unit;
        me.unitsCount++;
      } else {
        opp.unitsCount++;
      }
    }
  }

  public int unitsCountOf(int i) {
    return i == 0 ? me.unitsCount : opp.unitsCount;
  }

  public Unit getUnitAtPos(Pos next) {
    for (Unit unit : units) {
      if (unit.pos == next) {
        return unit;
      }
    }
    return null;
  }

  public Unit getAnyActiveTowerNearPos(Pos next) {
    for (Unit b : units) {
      if (b.type == UnitType.TOWER 
          && b.pos.manhattan(next) == 1 
          && board.board[b.pos.x + 12 * b.pos.y] == Board.P1_ACTIVE) {
        return b;
      }
    }

    return null;
  }

  public void move(Unit unit, Pos pos) {
    unit.pos = pos;
    Unit other = getUnitAtPos(pos);
    remove(other);

  }

  private void remove(Unit other) {
    if (other != null) {
      units.remove(other);
    }
  }

}
