package hypersonic.montecarlo;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import hypersonic.Board;
import hypersonic.BoardTest;
import hypersonic.Move;
import hypersonic.Simulation;
import hypersonic.entities.Bomberman;
import hypersonic.utils.P;

public class MonteCarloTest {
  public static class Tests {

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
          ".............");
  
      Bomberman bomberman = new Bomberman(board, 1, new P(0, 0), 1, 3);
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
          ".............");
  
      Bomberman bomberman = new Bomberman(board, 1, new P(0, 0), 0, 3);
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
  
  static public class Performance {
    @Test
    public void emptyBoard() throws Exception {
      Board board = new Board();
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
  
      Bomberman bomberman = new Bomberman(board, 1, new P(2, 0), 5, 3);
      board.players.add(bomberman);
      board.me = bomberman;
  
      Simulation sim = new Simulation();
      sim.board = board;

      MonteCarlo mc = new MonteCarlo();

      long t1 = System.currentTimeMillis();
      mc.simulate(sim);
      long t2 = System.currentTimeMillis();
      
      assertThat(t2-t1, is(1000L));
    }
  }

  static public class Understand {
    @Test
    public void understandStart() throws Exception {
      Board board = new Board();
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
  
      Bomberman bomberman = new Bomberman(board, 1, new P(0, 0), 1, 3);
      board.players.add(bomberman);
      board.me = bomberman;
  
      Simulation sim = new Simulation();
      sim.board = board;

      long t1 = System.currentTimeMillis();
      MonteCarlo mc = new MonteCarlo();
      mc.simulate(sim);
      mc.findNextBestMove();
      long t2 = System.currentTimeMillis();
      
      assertThat(t2-t1, is(1000L));
    }

    @Test
    public void understandStepByStep() throws Exception {
      Board board = new Board();
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
  
      Bomberman bomberman = new Bomberman(board, 1, new P(12, 4), 0, 18);
      board.players.add(bomberman);
      board.me = bomberman;

      BoardTest.createBomb(board).at(12, 3).withRange(18).withTimer(3).build();
      BoardTest.createBomb(board).at(12, 4).withRange(18).withTimer(8).build();
      BoardTest.createBomb(board).at(12, 5).withRange(18).withTimer(7).build();
      BoardTest.createBomb(board).at(12, 6).withRange(18).withTimer(6).build();

      Simulation sim = new Simulation();
      sim.board = board;

      Node root= new Node();
      root.simulation = sim;
      
      Node child1 = newStepFrom(root, Move.STAY);
      Node child2 = newStepFrom(child1, Move.LEFT);
      Node child3 = newStepFrom(child2, Move.RIGHT);
      Node child4 = newStepFrom(child3, Move.LEFT);

      assertThat(child4.getScore(), is(not(-999)));
    }

    private Node newStepFrom(Node parent, Move move) {
      Node child = new Node();
      child.move = move;
      child.simulation.copyFrom(parent.simulation);
      child.simulation.simulate(move);
      return child;
    }
}
}
