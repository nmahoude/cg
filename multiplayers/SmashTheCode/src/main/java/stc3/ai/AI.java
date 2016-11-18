package stc3.ai;

import stc3.game.GameState;

public class AI {
  private GameState gameState;
  private int playerIndex;
  private Move move = new Move(0,0,"Not Implemented");

  public AI(GameState state, int playerIndex) {
    this.gameState = state;
    this.playerIndex = playerIndex;
  }

  public void think() {
    move = new Move(GameState.random.nextInt(6), GameState.random.nextInt(4), "pure Random");
  }

  public Move outputMove() {
    return move;
  }
  
  public String output() {
    return outputMove().toString();
  }
}
