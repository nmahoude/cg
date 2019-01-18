package hypersonic.ai;

import java.util.Scanner;

import org.junit.After;
import org.junit.Test;

import hypersonic.Player;

public class Perf {

  @After
  public void teardown() {
    Player.myId = 0;
  }
  
  @Test
  public void doNotDropTooManyBombs() throws Exception {
    String input = ".....01......\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".............\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".....11......\r\n" + 
        "2X.X2X.X.X.X.\r\n" + 
        ".....1111....\r\n" + 
        "2X2X.X1X.X2X2\r\n" + 
        "20.....1...02\r\n" + 
        ".X.X.X2X.X.X.\r\n" + 
        ".............\r\n" + 
        "18\r\n" + 
        "0 0 10 4 1 5\r\n" + 
        "0 1 4 6 0 4\r\n" + 
        "0 2 8 4 1 4\r\n" + 
        "0 3 2 2 0 4\r\n" + 
        "1 0 7 0 2 5\r\n" + 
        "1 1 7 10 2 3\r\n" + 
        "1 3 2 5 2 4\r\n" + 
        "1 2 8 0 3 4\r\n" + 
        "1 2 8 2 6 4\r\n" + 
        "1 3 0 3 6 4\r\n" + 
        "1 2 8 3 7 4\r\n" + 
        "1 0 10 3 8 5\r\n" + 
        "2 0 5 8 1 1\r\n" + 
        "2 0 12 5 2 2\r\n" + 
        "2 0 7 4 1 1\r\n" + 
        "2 0 8 5 2 2\r\n" + 
        "2 0 6 1 2 2\r\n" + 
        "2 0 6 3 1 1";
    Scanner in = new Scanner(input);
    Player.myId = 3;
    
    Player player = new Player(in);
    player.readGameState();
    
    MC mc = new MC();
    
    Player.startTime = System.currentTimeMillis()+100_000;
    mc.think(player.state);
    
  }
}
