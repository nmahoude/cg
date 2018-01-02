package h.entities;

import h.Cell;

public class Agent {
  public final int index;
  public final long mask;
  
  public Cell cell = Cell.invalid;
  public boolean dead;
  public int bombRange = 0;
  public int bombLeft = 0;
  
  public Cell _cell = Cell.invalid;
  public int _bombRange = 0;
  public int _bombLeft = 0;

  public Agent(int index) {
    this.index = index;
    mask = (int)Math.pow(2, 8+index);
  }
  
  public void setPosition(Cell cell) {
    if (this.cell != Cell.invalid) {
      this.cell.removeAgent(this); // TODO inline 
    }
    this.cell = cell;
    this.cell.placeAgent(this); // TODO inline
  }

  public void backup() {
    dead = false;
    
    _cell = cell;
    _bombRange = bombRange;
    _bombLeft = bombLeft;
  }
  
  public void restore() {
    dead = false;
    cell = _cell;
    bombRange = _bombRange;
    bombLeft = _bombLeft;
  }
}
