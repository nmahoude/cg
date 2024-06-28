package coif.ai;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import coif.Board;
import coif.Cell;
import coif.Dir;
import coif.Player;
import coif.Pos;
import coif.State;
import coif.units.UnitType;

public class CreateUnitAI implements AI {

  private Simulation sim;
  private State state;

  public CreateUnitAI(Simulation sim, State state) {
    this.sim = sim;
    this.state = state;
  }
  
  @Override
  public void think() {
    createUnits();
  }

  private void createUnits() {
    Set<Pos> availablePositionsSet = new HashSet<>();
    // trouver une position où deposer l'unité
    for (int y = 0; y < 12; y++) {
      for (int x = 0; x < 12; x++) {
        Cell current = state.getCell(Pos.get(x, y));
        if (current.getStatut() == Board.P0_ACTIVE) {
          addAllCellsForTrain(availablePositionsSet, x, y);
          
          // check ifan inside cell is under threat
          for (Cell neighbor : current.neighbors) {
            if (neighbor.getStatut() == Board.P1_ACTIVE) {
              addAllCellsForTrain(availablePositionsSet, x, y);
            }
          }
        }
      }
    }

    List<Cell> availableCells = availablePositionsSet.stream().map(p -> state.getCell(p)).collect(Collectors.toList());
    
    
    availableCells.forEach(c -> c.calculateThreatScore());
    availableCells.sort((c1, c2) -> {
      return Integer.compare(c2.threat, c1.threat);
    });
    
    if (Player.DEBUG_AI) {
      System.err.println("availableCells : ");
      for (Cell cell : availableCells) {
        System.err.print("[ "+cell.pos+ ", ("+cell.threat+") ],");
      }
      System.err.println();
    }
    
    if (availableCells.size() == 0) {
      System.err.println("ERROR, NO PLACE TO BUILD");
      return;
    }
    while (state.me.gold >= 10 
        && (state.me.income + 1  > state.unitsCountOf(0)) 
        && !availableCells.isEmpty()) {

      Cell cell  = availableCells.remove(0);
      
      if (cell.threat > 0 && state.getUnitAtPos(cell.pos) == null) {
        if (true
            && cell.getStatut() == Board.P0_ACTIVE
            && cell.pos.manhattan(state.opp.HQ) < 12 /* second half */ 
            && state.me.gold >= 15) {
          
          sim.buildUnit(UnitType.TOWER, cell.pos);
          
        } else {
          sim.trainUnit(UnitType.SOLDIER_1/*state.getUnitToConquer( cell)*/, cell.pos);
        }
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
}
