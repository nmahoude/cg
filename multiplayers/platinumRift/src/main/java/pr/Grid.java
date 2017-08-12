package pr;

public class Grid {
  Cell initCells[];
  Cell cells[];
  private int cellCount;
  
  public Grid(int cellCount) {
    this.cellCount = cellCount;
    initCells = new Cell[cellCount];
    cells = new Cell[cellCount];
    for (int i = 0; i < cells.length; i++) {
      initCells[i] = new Cell(i);
      cells[i] = initCells[i];
    }
  }
  
  public Cell getById(int id) {
    return initCells[id];
  }
}
