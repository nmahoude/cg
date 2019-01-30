package hypersonic.ai.search;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.Scanner;

import org.junit.Test;

import hypersonic.Move;
import hypersonic.Player;
import hypersonic.simulation.SimulationTest;

public class SearchTest {
  
  @Test
  public void recalculatePath() throws Exception {
    Player.rand = new Random(0);
    Player.DEBUG_AI = true;
    String input = "...20...021..\r\n" + 
        ".X.X0X.X.X.X.\r\n" + 
        "......1......\r\n" + 
        ".X.X0X.X.X.X.\r\n" + 
        "..1..1.1..12.\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".21..1.1...2.\r\n" + 
        "2X0X0X.X.X.X.\r\n" + 
        "2....11......\r\n" + 
        ".X2X0X.X.X.X.\r\n" + 
        "..120...0....\r\n" + 
        "16\r\n" + 
        "0 0 1 2 1 4\r\n" + 
        "0 1 8 6 3 5\r\n" + 
        "0 2 8 6 0 3\r\n" + 
        "1 0 2 0 2 4\r\n" + 
        "1 2 10 1 2 3\r\n" + 
        "1 0 2 1 3 4\r\n" + 
        "1 2 10 2 3 3\r\n" + 
        "1 0 4 2 6 4\r\n" + 
        "1 1 8 8 7 5\r\n" + 
        "1 2 8 4 7 3\r\n" + 
        "2 0 7 2 1 1\r\n" + 
        "2 0 5 2 1 1\r\n" + 
        "2 0 1 4 2 2\r\n" + 
        "2 0 12 3 2 2\r\n" + 
        "2 0 9 10 2 2\r\n" + 
        "2 0 10 6 1 1";

    Scanner in = new Scanner(input);
    Player.myId = 1;
    SNodeCache.reset();
    
    Player player = new Player(in);
    player.readGameState();
    
    Search ai = new Search();
    
    Player.startTime = System.currentTimeMillis()+1000;//_000;
    SNode root = new SNode();
    root.state.copyFrom(player.state);
    Move[] moves = SimulationTest.readMoves("☢↑,  ↑, ☢→, ☢→,  •,  ↓,  ↓, ☢←,  ←,  →,  ←,  →,  ←,  ↑,  ↑,  →,  •,  •,  •,  •");
    double score = root.recalculate(moves);
    System.err.println(score);
  }
  @Test
  public void doNotDropTooManyBombs() throws Exception {
    Player.rand = new Random(0);
    Player.DEBUG_AI = true;
    String input = "...122.221...\r\n" + 
        ".X.X.X0X.X.X.\r\n" + 
        "..02.0.0.201.\r\n" + 
        ".X1X0X1X0X1X.\r\n" + 
        ".10.22122.010\r\n" + 
        ".X.X0X0X0X.X.\r\n" + 
        "010.22122.010\r\n" + 
        ".X1X0X1X0X1X.\r\n" + 
        ".102.0.0.2.1.\r\n" + 
        ".X.X.X0X.X.X.\r\n" + 
        "...122.22....\r\n" + 
        "5\r\n" + 
        "0 0 2 0 1 3\r\n" + 
        "0 1 12 10 0 3\r\n" + 
        "1 1 12 8 7 3\r\n" + 
        "2 0 1 2 1 1\r\n" + 
        "2 0 9 10 1 1";
    Scanner in = new Scanner(input);
    Player.myId = 0;
    SNodeCache.reset();
    
    Player player = new Player(in);
    player.readGameState();
    
    Search ai = new Search();
    
    Player.startTime = System.currentTimeMillis()+1000;//_000;
    ai.think(player.state);
    
  }
}
