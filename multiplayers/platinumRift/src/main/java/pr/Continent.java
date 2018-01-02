package pr;

import java.util.HashSet;
import java.util.Set;

/** Cells forms a continent */
public class Continent {
  Set<Cell> cells = new HashSet<>();

  public int ownerId = -1; // -1 no one owns it
  
  public void addCell(Cell cell) {
    cells.add(cell);
    cell.continent = this;
  }
  
  
}
