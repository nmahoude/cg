package h.entities;

import h.Cell;

public class Bomb {
  public final long id;

  public boolean dead;  // exploded ?
  public Cell cell;     // in which cell is the bomb
  public Agent agent; // who plant the bomb
  public int turnsLeft;
  public int range;

  private int _turnsLeft;
  
  public Bomb(int id) {
    this.id = id;
  }

  public void backup() {
    _turnsLeft = turnsLeft;
  }
  public void restore() {
    dead = false;
    turnsLeft = _turnsLeft;
  }

  public void explode() {
    dead = true;
    cell.explode();
    for (int dir=0;dir<4;dir++) {
      Cell nextCell = cell;
      for (int r=1;r<range;r++) {
        nextCell = nextCell.cells[dir];
        if (nextCell == Cell.invalid) break;
        if (nextCell.explode()) break;
      }
    }
    
  }
}
