package hypersonic.simulation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import hypersonic.Move;
import hypersonic.Player;

public class SimulationTest {

  @Before 
  public void setup() {
    Player.myId = 0;
  }
  
  @Test
  public void dead() throws Exception {
    String input = "..00.1.1.00..\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "01..2...2..10\r\n" + 
        "2X2X.X.X.X2X2\r\n" + 
        "..1.......1..\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "..1.......1..\r\n" + 
        "2X2X.X.X.X2X2\r\n" + 
        "01..2...2..10\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "..00.1.1.00..\r\n" + 
        "4\r\n"+
        "0 0 0 0 0 3\r\n" + 
        "0 1 12 9 0 3\r\n" + 
        "1 0 0 0 1 3\r\n" + 
        "1 1 11 10 3 3";
    
    Scanner in = new Scanner(input);
    Player player = new Player(in);
    player.readGameState();
    
    Simulation sim = new Simulation(player.board);
    sim.simulate(Move.STAY);
    
    
    assertThat(player.board.me.isDead, is(true));
  }
  
  @Test
  public void shouldDieAtTheEnd() throws Exception {
    String input = "..1.0...0.1..\r\n" + 
        ".X2X.X.X.X2X.\r\n" + 
        ".1.0.2.2.0.1.\r\n" + 
        "1X2X2X.X2X2X1\r\n" + 
        "1210.0.0.0121\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "1210.0.0.0121\r\n" + 
        "1X2X2X.X2X2X1\r\n" + 
        ".1.0.2.2.0.1.\r\n" + 
        ".X2X.X.X.X2X.\r\n" + 
        "..1.0...0.1..\r\n" + 
        "3\r\n" + 
        "0 0 0 0 1 3\r\n" + 
        "0 1 12 8 0 3\r\n" + 
        "1 1 11 10 5 3";
    
    Scanner in = new Scanner(input);
    Player player = new Player(in);
    player.readGameState();

    Move moves[] = readMoves("☢•,  •,  •,  •,  •,  •,  •,  •,  •,  •");
    
    Simulation sim = new Simulation(player.board);
    for (int i=0;i<moves.length;i++) {
      sim.simulate(moves[i]);
    }
    
    
    assertThat(player.board.me.isDead, is(true));
    
  }

  @Test
  public void shouldDieFromChainedBombs() throws Exception {
    String input = ".............\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".............\r\n" + 
        ".X.X2X.X2X.X.\r\n" + 
        ".1.........12\r\n" + 
        ".X.X0X.X0X.X.\r\n" + 
        "21.........1.\r\n" + 
        ".X0X2X.X2X.X.\r\n" + 
        "021..........\r\n" + 
        ".X2X.X.X.X.X.\r\n" + 
        "...0.........\r\n" + 
        "23\r\n" + 
        "0 0 2 5 0 6\r\n" + 
        "0 1 2 6 1 6\r\n" + 
        "0 2 8 9 0 5\r\n" + 
        "1 0 8 4 1 5\r\n" + 
        "1 1 8 4 1 5\r\n" + 
        "1 2 4 9 2 5\r\n" + 
        "1 2 4 10 4 5\r\n" + 
        "1 0 4 4 5 6\r\n" + 
        "1 1 4 4 5 6\r\n" + 
        "1 0 3 4 6 6\r\n" + 
        "1 2 6 10 6 5\r\n" + 
        "1 2 8 10 8 5\r\n" + 
        "2 0 0 4 2 2\r\n" + 
        "2 0 12 6 2 2\r\n" + 
        "2 0 11 2 2 2\r\n" + 
        "2 0 9 2 1 1\r\n" + 
        "2 0 3 2 1 1\r\n" + 
        "2 0 9 8 1 1\r\n" + 
        "2 0 8 2 1 1\r\n" + 
        "2 0 4 2 1 1\r\n" + 
        "2 0 4 6 1 1\r\n" + 
        "2 0 3 8 1 1\r\n" + 
        "2 0 8 6 1 1";

    Scanner in = new Scanner(input);
    Player player = new Player(in);
    Player.myId = 2;
    player.readGameState();

    Move moves[] = readMoves(" ↑,  →,  •,  ←,  ←,  ←,  ←,  •,  ←,  ←");
    
    player.board.updateBombs();
    Simulation sim = new Simulation(player.board);
    for (int i=0;i<moves.length;i++) {
      sim.simulate(moves[i]);
    }
    
    
    assertThat(player.board.me.isDead, is(true));

  
  }
  
  
  private Move[] readMoves(String movesFromArray) {
    String movesAsStr[] = movesFromArray.split(", ");
    Move[] moves = new Move[movesAsStr.length];
    int i=0;
    for (String moveAsStr : movesAsStr) {
      moves[i++] = Move.fromStr(moveAsStr);
    }
    return moves;
  }
}
