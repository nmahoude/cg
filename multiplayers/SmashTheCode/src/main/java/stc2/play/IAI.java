package stc2.play;

import stc2.Game;

public interface IAI {
  public static class Move {
    public Move(int rotation, int column) {
      this.rotation = rotation;
      this.column = column;
    }
    int rotation;
    int column;
  }
  
  Move getMove();
  public void prepare(Game game, int player);
}
