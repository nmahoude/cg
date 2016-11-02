package hypersonic.montecarlo;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import hypersonic.Board;
import hypersonic.BoardTest;
import hypersonic.Move;
import hypersonic.Simulation;
import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.utils.P;

public class MonteCarloTest {
  @Test
  public void simpleSimulationShouldFindSomePoints() throws Exception {
    Board board = new Board();
    BoardTest.initBoard(board, 
     "..0..........",
     ".X0X.X.X.X.X.",
     "0............",
     ".X.X.X.X.X.X.",
     ".............",
     ".X.X.X.X.X.X.",
     ".............",
     ".X.X.X.X.X.X.",
     ".............",
     ".X.X.X.X.X.X.",
     "............."
    );
    
    Bomberman bomberman = new Bomberman(board, 1, new P(0,0), 1, 3);
    board.players.add(bomberman);
    board.me = bomberman;
    
    Simulation sim = new Simulation();
    sim.board = board;
    Node root = new Node();
    root.simulation = sim;
    root.simulate(9);
  }

  @Test
  public void noMoveWithBombWhenNoBombsLeft() throws Exception {
    Board board = new Board();
    BoardTest.initBoard(board, 
     "..0..........",
     ".X0X.X.X.X.X.",
     "0............",
     ".X.X.X.X.X.X.",
     ".............",
     ".X.X.X.X.X.X.",
     ".............",
     ".X.X.X.X.X.X.",
     ".............",
     ".X.X.X.X.X.X.",
     "............."
    );
    
    Bomberman bomberman = new Bomberman(board, 1, new P(0,0), 0, 3);
    board.players.add(bomberman);
    board.me = bomberman;
    
    Simulation sim = new Simulation();
    sim.board = board;
    
    List<Move> possibleMoves = sim.getPossibleMoves();
    
    assertThat(possibleMoves, not(hasItem(Move.DOWN_BOMB)));
    assertThat(possibleMoves, not(hasItem(Move.RIGHT_BOMB)));
    assertThat(possibleMoves, not(hasItem(Move.UP_BOMB)));
    assertThat(possibleMoves, not(hasItem(Move.LEFT_BOMB)));
    assertThat(possibleMoves, not(hasItem(Move.STAY_BOMB)));
  }

}
