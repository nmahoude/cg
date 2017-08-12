package pr;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

public class Player {
  public static int playerCount;
  public static int myId;
  public static int zoneCount;
  public static int linkCount;
  public static Grid grid;
  public static final Random random= new Random();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    init(in);

    // game loop
    int round = 0;
    while (true) {
      round++;
      int platinum = in.nextInt(); // my available Platinum
      
      for (int i = 0; i < zoneCount; i++) {
        Cell cell = grid.getById(in.nextInt());
        cell.ownerId = in.nextInt();
        int presentPlayer = 0;
        cell.totalPods = 0;
        for (int p=0;p<4;p++) {
          cell.pods[p] = in.nextInt();
          cell.totalPods += cell.pods[p];
          presentPlayer += cell.pods[p]>0 ? 1 : 0;
        }
        cell.atWar = presentPlayer > 1;
      }

      // order by platinium
      Arrays.sort(grid.cells, new Comparator<Cell>() {
        @Override
        public int compare(Cell o1, Cell o2) {
          return Integer.compare(o2.platinum, o1.platinum); // more to less
        }
      });
      
      // think

      // move troops
      String moveOrder = "";
      for (Cell cell : grid.cells) {
        if (cell.ownerId != myId) continue;
        if (cell.pods[myId] == 0) continue;
        
        int freePods = cell.pods[myId]-1;
        boolean allNeighborsAreMine = true;
        boolean neighborsToEnemy = false;
        for (int c =0;c<cell.neighborsFE;c++) {
          Cell neighbor = cell.neighbors[c];
          if (neighbor.ownerId == myId) continue;
          allNeighborsAreMine = false;
          if (neighbor.ownerId != -1) neighborsToEnemy = true; 
          if (neighbor.totalPods == 0 && freePods >0 ) {
            freePods-=1;
            System.err.println("Move pods on "+neighbor.id+ "("+1+" troops)");
            moveOrder+=" "+1+" "+cell.id+" "+neighbor.id;
          }
        }
        if (allNeighborsAreMine || (cell.platinum < 4 && round < 10 && !neighborsToEnemy)) {
          // all neighbors are my friends, move all randomly
          int rand = random.nextInt(cell.neighborsFE);
          Cell to = cell.neighbors[rand];
          System.err.println("Move all pods ("+cell.pods[myId]+") from safe zone "+cell.id + " to "+to.id);
          moveOrder+=" "+cell.pods[myId]+" "+cell.id+" "+to.id;
        } else {
          // try to attack
          Arrays.sort(cell.neighbors, new Comparator<Cell>() {
            @Override
            public int compare(Cell o1, Cell o2) {
              return Integer.compare(o2.platinum, o1.platinum);
            }
          });
          for (int c =0;c<cell.neighborsFE;c++) {
            Cell neighbor = cell.neighbors[c];
            if (neighbor.ownerId == myId) continue;
            if (neighbor.isNeutral()) continue;
            if (freePods > neighbor.pods[neighbor.ownerId] ) {
              int needed = neighbor.pods[neighbor.ownerId]+1;
              freePods-=needed;
              System.err.println("attack on "+neighbor.id+ "("+needed+" troops)");
              moveOrder+=" "+needed+" "+cell.id+" "+neighbor.id;
            }
          }
        }
      }      
      
      // buy pods and dispatch
      String dropPodsOrder ="";
      int freePods = (int)(platinum / 20);
      System.err.println("Available pods : "+freePods);
      for (Cell cell : grid.cells) {
        if (cell.ownerId == myId) {
          if (cell.atWar && cell.platinum > 0) {
            int neededTroupsToOverflow = cell.neededTroopsFor(myId);
            if (freePods >= neededTroupsToOverflow) {
              freePods -= neededTroupsToOverflow;
              System.err.println("Reinforcement on "+cell.id+ "("+neededTroupsToOverflow+" troops)");
              dropPodsOrder+=" "+neededTroupsToOverflow+" "+cell.id;
            }
          }
          if (cell.pods[myId] == 0 && !cell.isSafe()) {
            if (freePods >= 1) {
              freePods -= 1;
              System.err.println("Refilling on "+cell.id+ "("+1+" troops)");
              dropPodsOrder+=" "+1+" "+cell.id;
            }
          }
        }
      }

      // take neutral
      for (int c=0;c<grid.cells.length && freePods > 0;c++) {
        Cell cell = grid.cells[c];
        if (cell.isNeutral()) {
          int needed = 1;
          if ((round == 1 && random.nextBoolean()) && cell.platinum>=3 && freePods >= 2) {
            needed = 2;
          }
          freePods-=needed;
          System.err.println("drop on "+cell.id+" because ownerId is "+cell.ownerId);
          dropPodsOrder+=" "+needed+" "+cell.id;
        }
      }
      
      // reinforce
      for (int c = 0; c < grid.cells.length && freePods > 0; c++) {
        Cell cell = grid.cells[c];
        if (cell.ownerId != myId) continue;
        
        int needed = 3-cell.pods[myId];
        if (needed > 0 && freePods >= needed) {
          freePods -= needed;
          System.err.println("Building on " + cell.id + "(" + needed + " troops)");
          dropPodsOrder += " " + needed + " " + cell.id;
        }
      }
      
      System.out.println(moveOrder);
      System.out.println(dropPodsOrder);
    }
  }

  private static void init(Scanner in) {
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
  }
}