package bttc;

public enum Direction {
  Up(0, -1), Right(1, 0), Down(0, 1), Left(-1, 0), UpRight(1, -1), DownRight(1, 1), DownLeft(-1, 1), UpLeft(-1, -1), None(0, 0);
  int dx, dy;

  Direction(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }

}
