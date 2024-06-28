package coif;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import coif.units.Unit;

public class Board {
  
  public static final int VOID = -1;
  public static final int EMPTY = 0;
  public static final int P0_ACTIVE = 1;
  public static final int P0_INACTIVE = 2;
  public static final int P1_ACTIVE = 3;
  public static final int P1_INACTIVE = 4;

  
  public boolean cellsInited = false;
  public Cell cells[] = new Cell[12*12];
  public int[] distances;
  {
    for (int y = 0; y < 12; y++) {
      for (int x= 0; x<12; x++) {
        Pos pos = Pos.get(x, y);
        cells[pos.index] = new Cell(pos);
      }
    }
  }
  
  public void read(Scanner in) {
    for (int y = 0; y < 12; y++) {
      String line = in.next();
      if (Player.DEBUG_INPUT) {
        System.err.println(line);
      }
      for (int x=0;x<12;x++) {
        Pos pos = Pos.get(x, y);
        Cell cell = cells[pos.index];
        cell.cut = false;
        cell.unit = null;
        
        switch(line.charAt(x)) {
        case '#': cells[pos.index] = Cell.VOID;break;
        case '.': cells[pos.index].setStatut(EMPTY);break;
        case 'O': cells[pos.index].setStatut(P0_ACTIVE);break;
        case 'o': cells[pos.index].setStatut(P0_INACTIVE);break;
        case 'X': cells[pos.index].setStatut(P1_ACTIVE);break;
        case 'x': cells[pos.index].setStatut(P1_INACTIVE);break;
        }
      }
    }
    initCellsGraphIfNeeds();
  }

  private void initCellsGraphIfNeeds() {
    if (!cellsInited) {
      cellsInited = true;
      for (int y = 0; y < 12; y++) {
        for (int x=0;x<12;x++) {
          cells[x+12*y].initCell(cells);
        }
      }
    }
  }

  public void initializeDistances(Cell init) {
    if (distances != null) return;
    
     distances = new int[12*12];
     for (int i=0;i<144;i++) {
       distances[i] = 1000;
     }
     distances[init.pos.index] = 0;
     calcDistanceFrom(init, 0);
  }

  private void calcDistanceFrom(Cell current, int cDistance) {
    if (current == Cell.VOID) return;
    if (distances[current.pos.index] <= cDistance) return;
    System.err.println("Distance at "+current.pos+" is "+cDistance);
    distances[current.pos.index] = cDistance;
    for (Cell c : current.neighbors) {
      calcDistanceFrom(c, cDistance+1);
    }
  }

  private void debugCells() {
    System.err.println("CELLS : ");
    for (int i=0;i<144;i++) {
        System.err.println(cells[i]);
    }    
  }

  /**
   * return the frontier of the active map from the pos (out frontier)
   */
  public List<Pos> getFrontierOut(Pos init, int ownCell) {
    return getFrontier(init, ownCell, false);
  }
  
  /**
   * return the frontier of the active map from the pos (IN frontier)
   */
  public List<Pos> getFrontierIn(Pos init, int ownCell) {
    return getFrontier(init, ownCell, true);
  }

  private List<Pos> getFrontier(Pos init, int ownCell, boolean in) {
    List<Pos> frontier = new ArrayList<>();

    List<Pos> toVisit = new ArrayList<>();
    List<Pos> visited = new ArrayList<>();
    
    toVisit.add(init);
    while(!toVisit.isEmpty()) {
      Pos current = toVisit.remove(0);
      visited.add(current);
      
      for (Dir dir : Dir.values()) {
        Pos next = current.move(dir);
        if (!next.isValid()) continue;
        if (toVisit.contains(next)) continue;
        if (visited.contains(next)) continue;
        Cell cell = cells[next.index];
        if (cell.getStatut() == ownCell) {
          toVisit.add(next);
        } else if (cell.getStatut() != VOID) {
          if (in) {
            // add the current cell as it has a non P0_ACTIVE neighbor
            if (!frontier.contains(current)) {
              frontier.add(current);
            }
          } else {
            // add the next cell as it IS a non P0_ACTIVE cell
            if (!frontier.contains(next)) {
              frontier.add(next);
            }
          }
        }
      }
    }
    
    return frontier;
  }

  public int getCellValue(Pos pos) {
    return cells[pos.index].getStatut();
  }

  public void add(Unit unit) {
    cells[unit.pos.index].unit = unit;
  }
}
