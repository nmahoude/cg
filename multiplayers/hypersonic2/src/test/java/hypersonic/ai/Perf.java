package hypersonic.ai;

import java.util.Scanner;

import hypersonic.Player;
import hypersonic.ai.search.Search;

public class Perf {

  public static void main(String[] args) {
    String input = ".............\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "...........2.\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".............\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".............\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        ".2...........\r\n" + 
        ".X.X.X.X.X.X.\r\n" + 
        "..02.........\r\n" + 
        "12\r\n" + 
        "0 0 3 6 2 10\r\n" + 
        "0 1 1 6 1 13\r\n" + 
        "1 0 5 10 1 9\r\n" + 
        "1 0 4 10 2 9\r\n" + 
        "1 1 4 10 2 12\r\n" + 
        "1 1 4 9 3 12\r\n" + 
        "1 1 4 8 4 12\r\n" + 
        "1 0 2 8 6 10\r\n" + 
        "2 0 9 10 2 2\r\n" + 
        "2 0 12 3 2 2\r\n" + 
        "2 0 9 0 2 2\r\n" + 
        "2 0 0 7 2 2";
    Scanner in = new Scanner(input);
    Player.myId = 0;
    
    Player player = new Player(in);
    player.readGameState();
    
    Search mc = new Search();
    
    Player.startTime = System.currentTimeMillis()+100_000;
    mc.think(player.state);
    
  }
}
