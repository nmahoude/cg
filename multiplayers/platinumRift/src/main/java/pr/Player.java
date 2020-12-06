package pr;

import java.io.IOException;
import java.util.Random;

import fast.read.FastReader;

public class Player {
  public static int playerCount;
  public static int myId;
  public static int zoneCount;
  public static int linkCount;
  public static Grid grid;
  public static final Random random= new Random();
  public static int round;
  public static int platinum;
  
  
  public static void main(String args[]) throws IOException {
    //Scanner in = new Scanner(System.in);
    FastReader in = new FastReader();
    init(in);
    IFFT ai = new IFFT(grid);

    round = 1;
    round = 0;
    while (true) {
      readTurn(in);
      grid.updateContinents();
      grid.buildClusters();
      
      ai.think();
      ai.output();
    }
  }

  private static void readTurn(FastReader in) throws IOException {
    round++;
    platinum = in.nextInt();
    long start = System.currentTimeMillis();
    for (int i = 0; i < zoneCount; i++) {
      Cell cell = grid.getById(in.nextInt());
      cell.ownerId = in.nextInt();
      int presentPlayer = 0;
      cell.podCount = 0;
      for (int p=0;p<4;p++) {
        cell.pods[p] = in.nextInt();
        cell.podCount += cell.pods[p];
        presentPlayer += cell.pods[p]>0 ? 1 : 0;
      }
      cell.atWar = presentPlayer > 1;
    }
    long end = System.currentTimeMillis();
    System.err.println("Time to read : " + (end - start));
  }

  private static void init(FastReader in) throws IOException {
    playerCount = in.nextInt();
    myId = in.nextInt();
    zoneCount = in.nextInt();
    linkCount = in.nextInt();

    grid = new Grid(zoneCount);
    for (int i = 0; i < zoneCount; i++) {
      int zoneId = in.nextInt(); // this zone's ID (between 0 and zoneCount-1)
      Cell cell = grid.getById(zoneId);
      cell.platinum = in.nextInt(); // the amount of Platinum this zone can provide per game turn
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