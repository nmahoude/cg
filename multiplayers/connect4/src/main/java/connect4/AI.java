package connect4;

public class AI {
  Minimax max = new Minimax();

  public int think(State root) {

    return max.think(root);
  }
}
