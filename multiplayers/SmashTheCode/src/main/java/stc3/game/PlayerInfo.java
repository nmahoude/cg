package stc3.game;

import stc2.BitBoard;
import stc2.Simulation;
import stc3.ai.Move;

public class PlayerInfo {
  public String name;
  public int nuisance;
  public int points;
  public Pair[] pairs;
  public BitBoard board = new BitBoard();
  public BitBoard opponentBoard;
  private Simulation simulator = new Simulation();
  
  public void clearForRound() {
    points = 0;
    board.clear();
    pairs = null;
  }

  public boolean applyMove(Move move) {
    if (move == null) {
      return false;
    }
    simulator.board = board;
    boolean result = simulator.putBalls(pairs[0].color1, pairs[0].color2, move.rotation, move.column);
    if (result) {
      this.points += simulator.points;
      nuisance +=simulator.points/70;
    }
    return result;
  }

  public void applyNuisance() {
    while (nuisance > 6) {
      nuisance-=6;
      dropSkullLine();
    }
  }
  private void dropSkullLine() {
    for (int i=0;i<6;i++) {
      opponentBoard.pushBall(0, i);
    }
  }
}
