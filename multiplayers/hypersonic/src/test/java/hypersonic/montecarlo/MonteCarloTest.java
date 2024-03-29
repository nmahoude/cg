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
import hypersonic.entities.Item;
import hypersonic.utils.P;

public class MonteCarloTest {
  public static class Tests {

    @Test
    public void simpleSimulationShouldFindSomePoints() throws Exception {
      final Board board = new Board();
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
          ".............");
  
      final Bomberman bomberman = new Bomberman(board, 1, new P(0, 0), 1, 3);
      board.players.add(bomberman);
      board.me = bomberman;
  
      final Simulation sim = new Simulation();
      sim.board = board;
      final Node root = new Node();
      root.simulation = sim;
      root.simulate(9);
    }
  
    @Test
    public void noMoveWithBombWhenNoBombsLeft() throws Exception {
      final Board board = new Board();
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
          ".............");
  
      final Bomberman bomberman = new Bomberman(board, 1, new P(0, 0), 0, 3);
      board.players.add(bomberman);
      board.me = bomberman;
  
      final Simulation sim = new Simulation();
      sim.board = board;
  
      final List<Move> possibleMoves = sim.getPossibleMoves();
  
      assertThat(possibleMoves, not(hasItem(Move.DOWN_BOMB)));
      assertThat(possibleMoves, not(hasItem(Move.RIGHT_BOMB)));
      assertThat(possibleMoves, not(hasItem(Move.UP_BOMB)));
      assertThat(possibleMoves, not(hasItem(Move.LEFT_BOMB)));
      assertThat(possibleMoves, not(hasItem(Move.STAY_BOMB)));
    }
  }
  
  static public class Performance {
    @Test
    public void emptyBoard() throws Exception {
      // warmup
      Bomb.cache.isEmpty();
      Bomberman.cache.isEmpty();
      Item.cache.isEmpty();
      Node.cache.isEmpty();
      
      //
      final Board board = new Board();
      BoardTest.initBoard(board,
          ".............",
          ".X.X.X.X.X.X.",
          ".............",
          ".X.X.X.X.X.X.",
          ".............",
          ".X.X.X.X.X.X.",
          ".............",
          ".X.X.X.X.X.X.",
          ".............",
          ".X.X.X.X.X.X.",
          ".............");
  
      final Bomberman bomberman = new Bomberman(board, 1, new P(6,5), 5, 10);
      board.players.add(bomberman);
      board.me = bomberman;
  
      final Simulation sim = new Simulation();
      sim.board = board;

      final MonteCarlo mc = new MonteCarlo();

      final long t1 = System.currentTimeMillis();
      mc.simulate(100_000_000 ,System.nanoTime(), sim);
      final long t2 = System.currentTimeMillis();
    }
  }

  static public class Understand {
    @Test
    public void understandStart() throws Exception {
      final Board board = new Board();
      BoardTest.initBoard(board,
          "..000........",
          ".X.X.X.X.X.X.",
          ".00..........",
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
  
      final Simulation sim = new Simulation();
      sim.board = board;

      final long t1 = System.currentTimeMillis();
      final MonteCarlo mc = new MonteCarlo();
      mc.simulate(100_000_000 ,System.nanoTime(), sim);
      mc.findNextBestMove();
      final long t2 = System.currentTimeMillis();
    }

    @Test
    public void understandWhy() throws Exception {
      final Board board = new Board();
      BoardTest.initBoard(board,
          ".............",
          ".X.X.X.X.X.X.",
          "b.i111.......",
          ".X.X.X.X.X.X.",
          "1.11.........",
          ".X.X.X.X.X.X.",
          ".............",
          ".X.X.X.X.X.X.",
          ".............",
          ".X.X.X.X.X.X.",
          ".............");
  
      final Bomberman bomberman = new Bomberman(board, 1, new P(1, 2), 1, 4);
      board.players.add(bomberman);
      board.me = bomberman;
  
      BoardTest.createBomb(board).at(0, 2).withRange(4).withTimer(5).build();
      BoardTest.createItem(board).at(2, 2).withType(1).build();
      
      final Simulation sim = new Simulation();
      sim.board = board;

      final long t1 = System.currentTimeMillis();
      final MonteCarlo mc = new MonteCarlo();
      mc.simulate(100_000_000 ,System.nanoTime(), sim);
      //mc.findNextBestMove();
      mc.debugAllMoves(mc.root.childs.get(Move.RIGHT));
      final long t2 = System.currentTimeMillis();
      
    }

    @Test
    public void understandStepByStep() throws Exception {
      final Board board = new Board();
      BoardTest.initBoard(board,
          "..0.02020.0..",
          ".X1X0X.X0X1X.",
          "112..101..211",
          ".X.X2X.X2X.X.",
          ".221.2.2.122.",
          "1X1X.X.X.X1X1",
          ".221.2.2.122.",
          ".X.X2X.X2X.X.",
          "112..101..211",
          ".X1X0X.X0X1X.",
          "..0.02020.0..");
  
      final Bomberman bomberman = new Bomberman(board, 1, new P(12, 10), 1, 3);
      board.players.add(bomberman);
      board.me = bomberman;

      final Simulation sim = new Simulation();
      sim.board = board;

      final Node root= new Node();
      root.simulation = sim;
      
      final Node child1 = newStepFrom(root, Move.LEFT_BOMB);
      final Node child2 = newStepFrom(child1, Move.LEFT);
      final Node child3 = newStepFrom(child2, Move.LEFT);
      final Node child4 = newStepFrom(child3, Move.STAY);
      final Node child5 = newStepFrom(child4, Move.STAY);
      final Node child6 = newStepFrom(child5, Move.STAY);
      final Node child7 = newStepFrom(child6, Move.STAY);
      final Node child8 = newStepFrom(child7, Move.STAY);
      final Node child9 = newStepFrom(child8, Move.STAY);

      assertThat(child9.getScore(), is(Simulation.DEAD_MALUS));
    }

    private Node newStepFrom(final Node parent, final Move move) {
      final Node child = new Node();
      child.move = move;
      child.simulation.copyFrom(parent.simulation);
      child.simulation.simulate(move);
      return child;
    }
}
}
