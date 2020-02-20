package pr;

import java.util.HashSet;
import java.util.Set;

/** Cells forms a continent */
public class Continent {
  Set<Cell> cells = new HashSet<>();

  private int ownerId = -1; // -1 no one owns it
  public double meanPlatinum;

  private String name;
  Grid grid;
  
  public Continent(Grid grid){
    this.grid = grid;
  }
  public void addCell(Cell cell) {
    if (cell.id == 61) {
      setName("SudAm-Afrique");
    } else if (cell.id == 64) {
      setName("Europe-Asia");
    } else if (cell.id == 23) {
      setName("America");
    } else if (cell.id == 149) {
      setName("Japan");
    } else if (cell.id == 57) {
      setName("Antartica");
    }
    
    cells.add(cell);
    cell.continent = this;
  }

  private void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  public void closeContinent() {
    cells.forEach(Cell::calculateAttractivnessByBFS);
    cells.forEach(cell -> System.err.println("Attr : " + cell.id +" => " + cell.attractivness));
    int sum = cells.stream().mapToInt(c -> c.platinum).sum();
    this.meanPlatinum = 1.0 * sum / cells.size();
    
    System.err.println("Moyenne pour "+name+" => "+meanPlatinum);
  }
  
  public boolean isOwned() {
    return ownerId != -1;
  }
  public int owner() {
    return ownerId;
  }

  public void update() {
    int ownerId = -9;
    boolean oneForce = true;
    for (Cell c : this.cells) {
      if (ownerId == -9) {
        ownerId = c.ownerId;
      } else {
        if (c.ownerId != ownerId) {
          oneForce = false;
          break;
        }
      }
    }
    this.ownerId = oneForce ? ownerId : -1;
    if (oneForce) {
      System.err.println("Continent obeys to "+ ownerId);
    }    
  }

  public boolean hasNeutral() {
    for (Cell cell : cells) {
      if (cell.isNeutral()) return true;
    }
    return false;
  }

  /* number of pods in the continent */
  public int myPodsCount() {
    return cells.stream().mapToInt(c->c.pods[Player.myId]).sum();
  }
  
  /* total number of pods on this continent */
  public int totalPodsCount() {
    return cells.stream().mapToInt(c->c.podCount).sum();
  }

}
