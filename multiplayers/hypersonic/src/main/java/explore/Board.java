package explore;

public abstract class Board {
  static int rot[][] = {
      {1, 0},
      {0, 1},
      {-1,0},
      {0,-1}
  };
  
  public abstract void copyFrom(Board board);
  public abstract int explode(int x, int y);
}
