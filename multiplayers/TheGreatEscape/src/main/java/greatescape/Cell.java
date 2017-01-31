package greatescape;

public class Cell {

  int x, y;

  Cell left;
  Cell right;
  Cell up;
  Cell down;
  
  int wallUp, wallDown, wallRight, wallLeft;
  int b_wallUp, b_wallDown, b_wallRight, b_wallLeft;
  
  Cell(int x, int y) {
    this.x = x;
    this.y = y;
    resetWalls();
  }

  public void resetWalls() {
    wallUp = 0;
    wallDown = 0;
    wallRight = 0;
    wallLeft = 0;
    backup();
  }

  public void backup() {
    b_wallUp = wallUp;
    b_wallDown = wallDown ;
    b_wallRight = wallRight;
    b_wallLeft = wallLeft;
  }

  public void restore() {
    wallUp = b_wallUp;
    wallDown = b_wallDown ;
    wallRight = b_wallRight;
    wallLeft = b_wallLeft;
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
