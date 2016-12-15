package hypersonic.ag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import hypersonic.Board;
import hypersonic.BoardTest;
import hypersonic.Move;
import hypersonic.Simulation;
import hypersonic.entities.Bomberman;
import hypersonic.montecarlo.Node;
import hypersonic.utils.P;

public class AGSolutionTest {

  @Test
  public void simpleSimulationShouldFindSomePoints() throws Exception {
    final Board board = new Board();
    BoardTest.initBoard(board,
        "..0..........",
        ".X0X.X.X.X.X.",
        ".0...........",
        "0X.X.X.X.X.X.",
        ".............",
        ".X.X.X.X.X.X.",
        ".............",
        ".X.X.X.X.X.X.",
        ".............",
        ".X.X.X.X.X.X.",
        ".............");

    final Bomberman bomberman = new Bomberman(board, 1, new P(0, 0), 1, 3);
    board.players.add(bomberman);
    board.me = bomberman;

    AGSolution ag1 = new AGSolution();
    ag1.keys = new Move[]{ Move.RIGHT, Move.LEFT_BOMB, Move.DOWN, Move.STAY, Move.STAY, Move.STAY, Move.STAY, Move.STAY, Move.STAY, Move.STAY, Move.STAY };
    double energy1 = playTheMoves(board, ag1);
    
    AGSolution ag2 = new AGSolution();
    ag2.keys = new Move[]{ Move.DOWN, Move.DOWN, Move.UP_BOMB, Move.UP, Move.RIGHT, Move.STAY, Move.STAY, Move.STAY, Move.STAY, Move.STAY, Move.STAY };
    double energy2 = playTheMoves(board, ag2);

    assertThat(ag1.energy < ag2.energy, is (true));
  }

  private double playTheMoves(Board board, AGSolution ag) {
    Simulation simulation = new Simulation();
    simulation.board = board.duplicate();
    for (int i =0;i<ag.keys.length;i++) {
      Move move =ag.keys[i];
      simulation.simulate(move);
      if (simulation.board.me.isDead) {
        ag.energy = Double.NEGATIVE_INFINITY;
        return ag.energy; // dead
      } else {
        ag.energy += ag.pow [i]*simulation.getScoreHeuristic();
      }
    }
    return ag.energy;
  }

}
