package pr;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Player {
  public static int playerCount;
  public static int myId;
  public static int zoneCount;
  public static int linkCount;
  public static Grid grid;
  public static final Random random= new Random();
  private static int round;
  private static int platinum;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    init(in);

    round = 0;
    while (true) {
      readTurn(in);
      updateContinents();
      
      // order by platinium
      Arrays.sort(grid.cells, new Comparator<Cell>() {
        @Override
        public int compare(Cell o1, Cell o2) {
          return Integer.compare(o2.platinum, o1.platinum); // more to less
        }
      });
      
      // think
      String moveOrder = moveTroops();      
      String dropPodsOrder = dropTroops();
      
      System.out.println(moveOrder);
      System.out.println(dropPodsOrder);
    }
  }

  private static void updateContinents() {
    for (Continent continent : grid.continents) {
      int ownerId = -9;
      boolean oneForce = true;
      for (Cell c : continent.cells) {
        if (ownerId == -9) {
          ownerId = c.ownerId;
        } else {
          if (c.ownerId != ownerId) {
            oneForce = false;
            break;
          }
        }
      }
      continent.ownerId = oneForce ? ownerId : -1;
      if (oneForce) {
        System.err.println("Continent obeys to "+ ownerId);
      }
    }
  }

  private static String dropTroops() {
    String dropPodsOrder ="";
    int freePods = (int)(platinum / 20);
    System.err.println("Available pods : "+freePods);
    for (Cell cell : grid.cells) {
      if (cell.continent.ownerId >= 0) continue; // don't think to drop if continent is own
      if (cell.ownerId >=0 && cell.ownerId != myId) continue; // can't drop on ennemy cells
      
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
          int neededPods = Math.min(freePods, cell.unsafeNeededBots());
          freePods -= neededPods;
          System.err.println("Refilling on "+cell.id+ "("+neededPods+" troops)");
          dropPodsOrder+=" "+neededPods+" "+cell.id;
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
    return dropPodsOrder;
  }

  private static String moveTroops() {
    String moveOrder = "";
    for (Cell cell : grid.cells) {
      if (cell.ownerId != myId) continue;
      if (cell.pods[myId] == 0) continue;
      
      int freePods = cell.pods[myId];
      List<Cell> freeCells = cell.getFreeNeighbors();
      for (Cell fc : freeCells) {
        if (freePods == 0) break; // no more pods to free
        freePods-=1;
        System.err.println("Move pods on "+fc.id+ "("+1+" troops)");
        moveOrder+=" "+1+" "+cell.id+" "+fc.id;
      }

      boolean allNeighborsAreMine = cell.neighborsAreMine();
      boolean neighborsToEnemy = cell.neighborsToEnemy();
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
    return moveOrder;
  }

  private static void readTurn(Scanner in) {
    round++;
    platinum = in.nextInt();
    
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
    
    for (Cell cell : grid.cells) {
      if (cell.continent != null) continue;

      Continent continent = null;
      continent = grid.createContinent();
      cell.spread(continent);
    }
    System.err.println("Counting " + grid.continents.size() +" continents");
  }
}