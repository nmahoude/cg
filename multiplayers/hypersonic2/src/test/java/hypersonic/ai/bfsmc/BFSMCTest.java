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
      +"......1......\r\n" + 
      ".X.X.X.X2X.X.\r\n" + 
      ".....2.......\r\n" + 
      ".X.X.X1X.X.X.\r\n" + 
      "..1.1...1....\r\n" + 
      ".X0X.X.X.X0X.\r\n" + 
      "..1.1...1.1..\r\n" + 
      ".X.X.X1X.X.X.\r\n" + 
      ".....2.2.....\r\n" + 
      ".X.X.X.X.X.X.\r\n" + 
      "......1......\r\n" + 
      "15\r\n" + 
      "0 0 4 0 2 4\r\n" + 
      "0 1 11 8 1 4\r\n" + 
      "0 2 6 2 2 4\r\n" + 
      "0 3 2 8 1 4\r\n" + 
      "1 0 2 2 4 4\r\n" + 
      "1 1 8 8 4 4\r\n" + 
      "1 3 4 8 4 4\r\n" + 
      "1 2 8 3 5 4\r\n" + 
      "1 0 4 2 6 4\r\n" + 
      "1 1 10 7 7 4\r\n" + 
      "1 2 7 2 8 4\r\n" + 
      "1 3 2 7 8 4\r\n" + 
      "2 0 8 9 2 2\r\n" + 
      "2 0 4 9 2 2\r\n" + 
      "2 0 10 4 1 1";
  
  Scanner in = new Scanner(input);
  Player.myId = 2;
  BFSMCNodeCache.reset();
  
  Player player = new Player(in);
  player.readGameState();
  
  BFSMC ai = new BFSMC();
  
  Player.startTime = System.currentTimeMillis() + 100_000;//_000;
  ai.think(player.state);
  
  }  
}
