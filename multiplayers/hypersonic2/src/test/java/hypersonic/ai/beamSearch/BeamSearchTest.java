package hypersonic.ai.beamSearch;

import java.util.Random;
import java.util.Scanner;

import org.junit.Test;

import hypersonic.Player;
import hypersonic.ai.search.Search;

public class BeamSearchTest {
  @Test
  public void doNotDropTooManyBombs() throws Exception {
    Player.rand = new Random(0);
    Player.DEBUG_AI = true;
    String input = "..12.101.21..\r\n" + 
        ".X2X.X1X.X2X.\r\n" + 
        "21022...22012\r\n" + 
        ".X0X.X.X.X0X.\r\n" + 
        "0..1.0.0.1..0\r\n" + 
        "2X1X0X.X0X1X2\r\n" + 
        "0..1.0.0.1..0\r\n" + 
        ".X0X.X.X.X0X.\r\n" + 
        "21022...22012\r\n" + 
        ".X2X.X1X.X2X.\r\n" + 
        "..12.101.21..\r\n" + 
        "2\r\n" + 
        "0 0 0 0 1 3\r\n" + 
        "0 1 12 10 1 3";
    Scanner in = new Scanner(input);
    Player.myId = 0;
    
    Player player = new Player(in);
    player.readGameState();
    
    BeamSearch ai = new BeamSearch();
    
    Player.startTime = System.currentTimeMillis()+100_000;
    ai.think(player.state);
    
  }
}
