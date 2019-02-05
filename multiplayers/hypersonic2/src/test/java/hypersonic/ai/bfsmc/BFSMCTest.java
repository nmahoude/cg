package hypersonic.ai.bfsmc;

import java.util.Random;
import java.util.Scanner;

import hypersonic.Player;

public class BFSMCTest {

public static void main(String[] args) {
  BFSMCNodeCache.reset();
  Player.rand = new Random(0);
  
  Player.DEBUG_AI = true;
  String input = ""
      +"......2......\r\n" + 
      ".X.X.X.X.X.X.\r\n" + 
      ".............\r\n" + 
      ".X.X.X.X.X.X.\r\n" + 
      ".....0.0.....\r\n" + 
      ".X.X.X.X.X.X.\r\n" + 
      ".....0.0.....\r\n" + 
      ".X.X.X0X.X.X.\r\n" + 
      ".............\r\n" + 
      ".X.X.X1X.X.X.\r\n" + 
      "......2......\r\n" + 
      "23\r\n" + 
      "0 0 4 6 2 6\r\n" + 
      "0 1 8 6 4 6\r\n" + 
      "0 2 4 6 2 6\r\n" + 
      "0 3 4 6 4 6\r\n" + 
      "1 1 10 2 3 6\r\n" + 
      "1 2 2 2 3 6\r\n" + 
      "1 0 2 3 4 6\r\n" + 
      "1 0 2 4 5 6\r\n" + 
      "1 1 10 4 5 6\r\n" + 
      "1 2 2 4 5 6\r\n" + 
      "1 3 2 4 5 6\r\n" + 
      "1 0 3 4 6 6\r\n" + 
      "1 2 3 4 6 6\r\n" + 
      "1 3 3 4 6 6\r\n" + 
      "1 0 4 4 7 6\r\n" + 
      "1 1 8 5 8 6\r\n" + 
      "2 0 8 7 2 2\r\n" + 
      "2 0 4 3 2 2\r\n" + 
      "2 0 4 7 2 2\r\n" + 
      "2 0 8 3 2 2\r\n" + 
      "2 0 6 1 1 1\r\n" + 
      "2 0 0 5 1 1\r\n" + 
      "2 0 12 5 1 1";
  
  Scanner in = new Scanner(input);
  Player.myId = 0;
  BFSMCNodeCache.reset();
  
  Player player = new Player(in);
  player.readGameState();
  
  BFSMC ai = new BFSMC();
  
  Player.startTime = System.currentTimeMillis() + 100_000;//_000;
  ai.think(player.state);
  
  }  
}
