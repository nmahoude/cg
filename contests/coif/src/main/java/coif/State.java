package coif;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import coif.units.Unit;
import coif.units.UnitType;

public class State {
  public Agent me = new Agent(null);
  public Agent opp = new Agent(null);
  public Board board = new Board();
  public List<Pos> mineSpots = new ArrayList<>();
  public List<Unit> units = new ArrayList<>();
  public int turn;
  public int numberOfMines;
  
  public void readInit(Scanner in) {
    int numberMineSpots = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.print(numberMineSpots+" ");
    }
    for (int i = 0; i < numberMineSpots; i++) {
      int x = in.nextInt();
      int y = in.nextInt();
      mineSpots.add(Pos.get(x, y));
      if (Player.DEBUG_INPUT) {
        System.err.print(""+x+" "+y+ " ");
      }
    }
    if (Player.DEBUG_INPUT) {
      System.err.println();
    }
  }

  public void readTurn(Scanner in) {
    turn++;
    me.read(in);
    opp.read(in);
    
    board.read(in);
    units.clear();
    
    int buildingCount = in.nextInt();
    numberOfMines = 0;
    if (Player.DEBUG_INPUT) {
      System.err.println(buildingCount);
    }
    for (int i = 0; i < buildingCount; i++) {
      Unit building = new Unit();
      building.readBuilding(in);
      units.add(building);
      board.add(building);
      
      if (building.type == UnitType.HQ) {
        if (building.owner == 0) {
          me.HQ = building.pos;
        } else {
          opp.HQ = building.pos;
        }
      } else if (building.type == UnitType.MINE) {
        numberOfMines++;
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
      board.add(unit);
      if (unit.owner == 0) {
        me.unitsCount++;
      } else {
        opp.unitsCount++;
      }
    }

    board.initializeDistances(this.getCell(opp.HQ));
  }

  public int unitsCountOf(int i) {
    return i == 0 ? me.unitsCount : opp.unitsCount;
  }

  public Unit getUnitAtPos(Pos next) {
    return board.cells[next.index].unit;
  }

  public Unit getAnyActiveTowerNearPos(Pos next) {
    Cell cell = board.cells[next.index];
    for (int i=0;i<4;i++) {
      Cell neighbor = cell.neighbors[i];
      if (neighbor.unit != null 
          && neighbor.getStatut() == Board.P1_ACTIVE 
          && neighbor.unit.type == UnitType.TOWER) {
        return neighbor.unit;
      }
    }
    
    return null;
  }

  public void move(Unit unit, Pos pos) {
    // remove unit from current cell
    if (unit.pos != null) {
      board.cells[unit.pos.index].unit = null;
    }

    // move unit
    unit.pos = pos;
    Unit other = getUnitAtPos(pos);
    if (other != null) {
      other.dead = true;
    }
    // put unit in cell
    Cell cell = board.cells[pos.index];
    if (cell.getStatut() != Board.P0_ACTIVE) {
      this.me.income+=1;
      cell.setStatut(Board.P0_ACTIVE);
    }
    cell.unit = unit;
    // TODO recalc active/inactive/dead units ?
  }

  public Cell getCell(Unit unit) {
    return board.cells[unit.pos.index];
  }

  public Cell getCell(Pos pos) {
    return board.cells[pos.index];
  }

  public int getCostToConquerCell(Cell cell) {
    if (cell.isActive() && cell.isProtectedByTower()) return 30;
    if (cell.unit == null) return 10;
    Unit unit = cell.unit;
    switch (unit.type) {
      case HQ: return 10;
      case MINE: return 10;
      case SOLDIER_1: return 20;
      case SOLDIER_2: return 30;
      case SOLDIER_3: return 30;
      case TOWER: return 10;
    }
    throw new RuntimeException("QW2 : unknown case for cost");
  }

  public UnitType getUnitToConquer(Cell cell) {
    if (cell.isActive() && cell.isProtectedByTower()) return UnitType.SOLDIER_3;
    if (cell.unit == null) return UnitType.SOLDIER_1;
    Unit unit = cell.unit;
    switch (unit.type) {
      case HQ: return UnitType.SOLDIER_1;
      case MINE: return UnitType.SOLDIER_1;
      case SOLDIER_1: return UnitType.SOLDIER_2;
      case SOLDIER_2: return UnitType.SOLDIER_3;
      case SOLDIER_3: return UnitType.SOLDIER_3;
      case TOWER: return UnitType.SOLDIER_1;
    }
    throw new RuntimeException("QW2 : unknown case for cost");
  }

  public int getNumberOfMines() {
    return numberOfMines;
  }

}
