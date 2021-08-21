package pr;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Player {
  public static boolean DEBUG_CALCULATION = true ;

  public static int playerCount;
  public static int myId;
  public static int zoneCount;
  public static int linkCount;
  public static Grid grid;
  public static final Random random= new Random();
  public static int round;
  public static int platinum;
  
  static Cell enemyHQ;
	static Cell myHQ;
  
  
  public static void main(String args[]) throws IOException {
    Scanner in = new Scanner(System.in);
    init(in);
    IFFT ai = new IFFT(grid);

    round = 1;
    round = 0;
    while (true) {
      readTurn(in);
      if (round == 1) {
      	System.err.println("my  hq : "+myHQ);
      	System.err.println("opp hq : "+enemyHQ);
      	System.err.println("Distance between HQ is "+myHQ.distanceTo(enemyHQ));
      }
      grid.buildClusters();
      
      ai.think();
      ai.output();
    }
  }

  private static void readTurn(Scanner in) throws IOException {
    round++;
    platinum = in.nextInt();
    long start = System.currentTimeMillis();
    for (int i = 0; i < zoneCount; i++) {
      /**
      zId: id of the zone
      ownerId: player id of the zone owner (-1 for a neutral zone)
      podsP0: number of PODs for player with id 0 on the zone
      podsP1: number of PODs for player with id 1 on the zone
      visible: 1 if the zone is visible for your PODs, 0 otherwise
      platinum: number of Platinum bars on the zone (0 for a non-visible zone)
      */
      
      
      
      int zID = in.nextInt();
      int ownerId = in.nextInt();
      int podsP0 = in.nextInt();
      int podsP1 = in.nextInt();
      boolean visible = in.nextInt() == 1;
      int platinum = in.nextInt();
      
      
      Cell cell = grid.getById(zID);
    	if (round == 1) {
    		if (ownerId == myId) {
    			myHQ = cell;
    			System.err.println("=> Setting my  HQ @"+myHQ);
    		} else if (ownerId != -1) {
    			enemyHQ = cell;
    			System.err.println("=> Setting opp HQ @"+enemyHQ);
    		}
    	}

    	if (visible) {
        cell.ownerId = ownerId;
      }
      int playerOnCellCount = 0;
      cell.podCount = 0;
      for (int p=0;p<2;p++) {
        cell.pods[p] = p == 0 ? podsP0 : podsP1;
        cell.podCount += cell.pods[p];
        playerOnCellCount += cell.pods[p]>0 ? 1 : 0;
      }
      // update platinum if cell is visible
      if (visible) {
        cell.platinum = platinum;
      }
      cell.atWar = playerOnCellCount > 1;
    }
    long end = System.currentTimeMillis();
    System.err.println("Time to read : " + (end - start));
    
  }

  private static void init(Scanner in) throws IOException {
    playerCount = in.nextInt();
    myId = in.nextInt();
    zoneCount = in.nextInt();
    linkCount = in.nextInt();

    System.err.println("ZoneCount : "+zoneCount);
    grid = new Grid(zoneCount);
    for (int i = 0; i < zoneCount; i++) {
      int zoneId = in.nextInt(); // this zone's ID (between 0 and zoneCount-1)
      Cell cell = grid.getById(zoneId);
      in.nextInt(); // the amount of Platinum this zone can provide per game turn
      cell.platinum = -1; // discarded because FOW
    }
    for (int i = 0; i < linkCount; i++) {
      Cell cell1 = grid.getById(in.nextInt());
      Cell cell2 = grid.getById(in.nextInt());

      cell1.addNeighbors(cell2);
      cell2.addNeighbors(cell1);
    }

    grid.build();
  }
}