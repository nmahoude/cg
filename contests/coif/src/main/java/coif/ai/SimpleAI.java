package coif.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import coif.Board;
import coif.Dir;
import coif.Player;
import coif.Pos;
import coif.State;
import coif.units.Unit;
import coif.units.UnitType;

public class SimpleAI implements AI {

  private Simulation sim;
  private State state;
  public boolean wannaPlay = false;

  public SimpleAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;
  }

  public void think() {

    wannaPlay = true;

    moveUnits();
    createUnits();
    createDefense();

  }

  void createDefense() {
    Pos pos = getPrimaryDefenseTowerPosition();
    if(state.me.gold > Unit.TOWER_COST && state.getUnitAtPos(pos) == null) {
      sim.buildUnit(UnitType.TOWER, pos);
    }
  }

  private Pos getPrimaryDefenseTowerPosition() {
    if (state.me.HQ.x == 0) 
      return Pos.get(1, 1);
    else
      return Pos.get(10, 10);
  }

  private void createUnits() {
    Set<Pos> availablePositionsSet = new HashSet<>();
    // trouver une position où deposer l'unité
    for (int y = 0; y < 12; y++) {
      for (int x = 0; x < 12; x++) {
        if (state.board.getCellValue(Pos.get(x, y)) == Board.P0_ACTIVE) {
          addAllCellsForTrain(availablePositionsSet, x, y);
        }
      }
    }

    List<Pos> availablePosition = new ArrayList<>(availablePositionsSet);
    if (Player.DEBUG_AI) {
      System.err.println("availablePosition : ");
      System.err.println(availablePosition);
    }
    
    if (availablePosition.size() == 0) {
      System.err.println("ERROR, NO PLACE TO BUILD");
      return;
    }
    while (state.me.gold > 10 
        && (state.me.income +1  > state.unitsCountOf(0)) 
        && !availablePosition.isEmpty()) {

      Pos pos = availablePosition.remove(0);
      if (state.getUnitAtPos(pos) == null) {
        state.me.gold-=10;
        sim.trainUnit(UnitType.SOLDIER_1, pos);
      }
    }
  }

  private void addAllCellsForTrain(Collection<Pos> availablePositions, int x, int y) {
    for (Dir dir : Dir.randomValues()) {
      Pos pos = Pos.get(x + dir.dx, y + dir.dy);
      if (pos != Pos.INVALID) {
        int cell = state.board.getCellValue(pos);
        if (cell == Board.EMPTY || cell == Board.P1_ACTIVE || cell == Board.P1_INACTIVE) {
          availablePositions.add(pos);
        }
      }
    }
  }

  private void moveUnits() {
    for (Unit unit : state.units) {
      if (unit.owner == 1)  continue; // not mine
      if (unit.isStatic()) continue;
      updateDegreeOfFreedom(unit);
    }
    
    state.units.sort((u1, u2) -> Integer.compare(u1.degreeOfFreedom, u2.degreeOfFreedom));
    
    for (Unit unit : state.units) {
      if (unit.owner == 1)  continue; // not mine
      if (unit.isStatic()) continue;
      if (unit.done) continue; // have already an order
      moveUnit(unit);

    }
  }

  private void updateDegreeOfFreedom(Unit unit) {
    if (unit.isStatic()) {
      return;
    }
    int dof = 0;
    for (Dir dir : Dir.values()) {
      Pos pos = Pos.get(unit.pos.x + dir.dx, unit.pos.y + dir.dy);
      if (pos != Pos.INVALID) {
        int cell = state.board.getCellValue(pos);
        if (cell == Board.EMPTY || cell == Board.P1_ACTIVE || cell == Board.P1_INACTIVE) {
          dof++;
        }
      }
    }
    unit.degreeOfFreedom = dof;
  }

  private void moveUnit(Unit unit) {
    for (Dir dir : Dir.randomValues()) {
      Pos pos = Pos.get(unit.pos.x + dir.dx, unit.pos.y + dir.dy);
      if (pos != Pos.INVALID) {
        int cell = state.board.getCellValue(pos);
        if (cell == Board.EMPTY || cell == Board.P1_ACTIVE || cell == Board.P1_INACTIVE) {
          sim.moveUnit(unit, pos);
          return;
        }
      }
    }
    // no extend the territory possibility, move towards opp hq
    sim.moveUnit(unit, state.opp.HQ);
  }
}
