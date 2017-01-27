package greatescape;

public class Cell {

  int x, y;

  Cell left;
  Cell right;
  Cell up;
  Cell down;
  
  boolean canGoUp, canGoDown, canGoRight, canGoLeft;
  boolean b_canGoUp, b_canGoDown, b_canGoRight, b_canGoLeft;
  
  Cell(int x, int y) {
    this.x = x;
    this.y = y;
    resetWalls();
  }

  public void resetWalls() {
    canGoUp = true;
    canGoDown = true;
    canGoRight = true;
    canGoLeft = true;
    backup();
  }

  public void backup() {
    b_canGoUp = canGoUp;
    b_canGoDown = canGoDown ;
    b_canGoRight = canGoRight;
    b_canGoLeft = canGoLeft;
  }

  public void restore() {
    canGoUp = b_canGoUp;
    canGoDown = b_canGoDown ;
    canGoRight = b_canGoRight;
    canGoLeft = b_canGoLeft;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + x;
    result = prime * result + y;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Cell other = (Cell) obj;
    if (x != other.x)
      return false;
    if (y != other.y)
      return false;
    return true;
  }
}
