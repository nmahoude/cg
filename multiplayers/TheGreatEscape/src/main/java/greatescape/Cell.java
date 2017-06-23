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

  public boolean canGoDir(int i) {
    switch (i) {
    case 0:
      return canGoUp();
    case 1:
      return canGoDown();
    case 2:
      return canGoLeft();
    case 3:
      return canGoRight();
    default:
      throw new RuntimeException("Unknown dir " + i);
    }
  }

  public Cell goDir(int i) {
    switch (i) {
    case 0:
      return up();
    case 1:
      return down();
    case 2:
      return left();
    case 3:
      return right();
    default:
      throw new RuntimeException("Unknown dir " + i);
    }
  }

  
  public boolean canGoUp() {
    return y > 0 && wallUp == 0;
  }

  public boolean canGoDown() {
    return y < Player.H - 1 && wallDown == 0;
  }

  public boolean canGoLeft() {
    return x > 0 && wallLeft == 0;
  }

  public boolean canGoRight() {
    return x < Player.W - 1 && wallRight == 0;
  }

  public Cell up() {
    return up;
  }

  public Cell down() {
    return down;
  }

  public Cell right() {
    return right;
  }

  public Cell left() {
    return left;
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
    b_wallDown = wallDown;
    b_wallRight = wallRight;
    b_wallLeft = wallLeft;
  }

  public void restore() {
    wallUp = b_wallUp;
    wallDown = b_wallDown;
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
