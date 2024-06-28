package coif;

import coif.units.Unit;
import coif.units.UnitType;

public class Cell {
  public static final Cell VOID = new Cell(Pos.INVALID);
  final public Pos pos;
  public final Cell neighbors[] = new Cell[4];
  
  public Unit unit;
  private int statut;
  public boolean cut; // is the cell cut
  public int threat = 0;
  public int cutValue = 0;
  
  public Cell(Pos pos) {
    this.pos = pos;
    if (pos == Pos.INVALID) {
      statut = Board.VOID;
    }
  }
  
  public void setStatut(int statut) {
    this.statut = statut;
  }
  public int getStatut() {
    return statut;
  }
  public boolean isActive() {
    return statut == Board.P0_ACTIVE || statut == Board.P1_ACTIVE;
  }
  public void initCell(Cell[] cells) {
    for (Dir dir : Dir.values()) {
      Pos n = pos.move(dir);
      if (!n.isValid() || cells[n.index] == Cell.VOID) {
        neighbors[dir.decal] = Cell.VOID;
      } else {
        neighbors[dir.decal] = cells[n.index];
      }
    }
  }

  public boolean isProtectedByTower() {
    for (Cell neighbor : neighbors) {
      if (neighbor != Cell.VOID 
          && neighbor.isActive() 
          && neighbor.unit != null
          && neighbor.unit.type == UnitType.TOWER
          && neighbor.unit.owner == neighbor.owner()) {
        return true;
      }
    }
    return false;
  }

  private int owner() {
    if (statut == Board.P0_ACTIVE) return 0;
    if (statut == Board.P1_ACTIVE) return 1;
    return -1;
  }

  public int calculateThreatScore() {
    threat = 0;
    for (Cell c : neighbors) {
      if (c == VOID) continue;
      if (c.getStatut() == Board.EMPTY) {
        threat+=1;
      }
//      if (c.getStatut() == Board.P0_ACTIVE && c.unit == null) {
//        threat+=50;
//      }
      if (c.getStatut() == Board.P1_ACTIVE) {
        threat+=10;
        if (c.unit != null) {
          threat += 100;
        }
      }
    }
    return threat;
  }

  @Override
  public String toString() {
    return ""+pos;
  }
}
