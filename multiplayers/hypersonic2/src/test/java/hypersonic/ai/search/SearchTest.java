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
<<<<<<< HEAD
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
=======
        "........0....\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".......1....1\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "2...........2\r\n" + 
        "0X.X.X.X.X.X0\r\n" + 
        "21.........12\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "1...21.1.....\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "....0........\r\n" + 
        "11\r\n" + 
        "0 0 9 2 2 3\r\n" + 
        "0 1 8 8 0 4\r\n" + 
        "1 1 12 7 2 4\r\n" + 
        "1 0 10 2 8 3\r\n" + 
        "1 1 8 8 8 4\r\n" + 
        "2 0 0 2 1 1\r\n" + 
        "2 0 5 2 1 1\r\n" + 
        "2 0 1 4 1 1\r\n" + 
        "2 0 6 6 2 2\r\n" + 
        "2 0 11 4 1 1\r\n" + 
        "2 0 8 2 2 2";
>>>>>>> d37f6ab40a249ec03f6e2a9f432a954e486fea81
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
