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
    String input = 
        ".....2220....\r\n" +
        ".X.X.X.X.X.X.\r\n" + 
        "...1122211...\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".0.........0.\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".0.........0.\r\n" + 
        "1X.X1X.X.X.X1\r\n" + 
        "..01122211...\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "....0222.....\r\n" + 
        "9\r\n" + 
        "0 0 8 3 1 4\r\n" + 
        "0 1 8 4 1 3\r\n" + 
        "0 2 8 6 0 4\r\n" + 
        "1 1 10 6 5 3\r\n" + 
        "1 2 10 6 5 3\r\n" + 
        "2 0 0 3 1 1\r\n" + 
        "2 0 12 3 1 1\r\n" + 
        "2 0 4 3 1 1\r\n" + 
        "2 0 4 5 2 2";
    Scanner in = new Scanner(input);
    Player.myId = 0;
    Player.DEBUG_AI = true;
    Player.DEBUG_OPTIMIZE = true;
    SNodeCache.reset();
    
    Player player = new Player(in);
    player.readGameState();
    
    Search ai = new Search();
    
    Player.startTime = System.currentTimeMillis()+100;//_000;
    ai.think(player.state);

  }
}
