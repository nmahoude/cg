package hypersonic.ai.search;

import java.util.Random;
import java.util.Scanner;

import org.junit.Test;

import hypersonic.Player;

public class SearchTest {
  @Test
  public void doNotDropTooManyBombs() throws Exception {
    Player.rand = new Random(0);
    Player.DEBUG_AI = true;
    String input = ".......22.1..\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".......1.02..\r\n" + 
        ".X.X.X.X1X.X.\r\n" + 
        "..0.2........\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "..0..........\r\n" + 
        ".X1X1X.X.X.X.\r\n" + 
        "..20.1.......\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "..1.22.2.....\r\n" + 
        "14\r\n" + 
        "0 0 5 0 1 5\r\n" + 
        "0 1 6 9 2 4\r\n" + 
        "1 0 2 1 3 5\r\n" + 
        "1 0 2 0 4 5\r\n" + 
        "1 0 4 0 6 5\r\n" + 
        "1 1 6 10 8 4\r\n" + 
        "2 0 10 10 1 1\r\n" + 
        "2 0 4 3 1 1\r\n" + 
        "2 0 7 8 1 1\r\n" + 
        "2 0 8 10 2 2\r\n" + 
        "2 0 5 2 1 1\r\n" + 
        "2 0 4 6 2 2\r\n" + 
        "2 0 10 3 1 1\r\n" + 
        "2 0 8 4 2 2";
    Scanner in = new Scanner(input);
    Player.myId = 0;
    
    Player player = new Player(in);
    player.readGameState();
    
    Search ai = new Search();
    
    Player.startTime = System.currentTimeMillis()+100;//_000;
    ai.think(player.state);
    
  }
}
