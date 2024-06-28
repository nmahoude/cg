package spring2023;

import fast.read.FastReader;
import spring2023.map.Cell;
import spring2023.map.Map;
import spring2023.map.MapData;
import spring2023.simulation.Simulation;

public class State {
  public static int numberOfCells;
  public static int numberOfBases;
  public static int originalTotalCrystalsGoal = -1;

  public int totalMyAnts = 0;
  public int totalResources = 0;
  public int totalCrystals= 0;
  public int totalEggs = 0;
  
  public Cell cells[];
  public int myScore;
  public int oppScore;

  
  public void copyFrom(State model) {
    this.copyResources(model);
    
    this.totalMyAnts = model.totalMyAnts;
    this.myScore = model.myScore;
    this.oppScore = model.oppScore;
    
    for (int i=0;i<numberOfCells;i++) {
      this.cells[i].copyFrom(model.cells[i]);
    }
  }

  public void copyResources(State model) {
    this.totalResources = model.totalResources;
    this.totalCrystals = model.totalCrystals;
    this.totalEggs = model.totalEggs;

    for (int i=0;i<numberOfCells;i++) {
      this.cells[i].copyResources(model.cells[i]);
    }
    
  }
  
  
  public static void readInit(FastReader in) {
    originalTotalCrystalsGoal = -1;
    numberOfCells = in.nextInt();

    Map.init(numberOfCells);
    
    for (int i = 0; i < numberOfCells; i++) {
      MapData current = Map.cells[i];

      int type = in.nextInt(); // 0 for empty, 1 for eggs, 2 for crystal
      int resource = in.nextInt(); // the initial amount of eggs/crystals on this cell
      if (type == Map.CELL_CRYSTAL) {
        originalTotalCrystalsGoal+=resource;
      }
      
      current.type = type;
      for (int n=0;n<6;n++) {
          int neigh = in.nextInt();
          
          if (neigh == -1) {
            current.neighborsArray[n] = MapData.VOID;
            continue;
          }

          MapData neighborCell = Map.cells[neigh];
          current.neighborsArray[n] = neighborCell;
          current.neighbors.add(neighborCell);
      }
    }
    originalTotalCrystalsGoal/=2; // only keep half
    
    
    numberOfBases = in.nextInt();
    Map.myBases = new MapData[numberOfBases];
    Map.oppBases = new MapData[numberOfBases];
    
    for (int i = 0; i < numberOfBases; i++) {
      int myBaseIndex = in.nextInt();
      Map.cells[myBaseIndex].isMyBase = true;
      Map.myBases[i] = Map.cells[myBaseIndex];
    }
    for (int i = 0; i < numberOfBases; i++) {
      int oppBaseIndex = in.nextInt();
      Map.cells[oppBaseIndex].isOppBase = true;
      Map.oppBases[i] = Map.cells[oppBaseIndex];
    }
    
    
    Map.calculateBaseDistances();
    Simulation.init();
  }

  
  public State() {
    cells = new Cell[numberOfCells];
    for (int i=0;i<numberOfCells;i++) {
      cells[i] = new Cell(Map.cells[i], i);
    }
  }
  
  public static void saveInit() {
    System.err.println("*** INIT ***");
    System.err.println("^" + numberOfCells);
    
    System.err.print("^ ");
    for (int i = 0; i < numberOfCells; i++) {
      MapData current = Map.cells[i];
      System.err.print(current.type + " " + 0 + " ");
      for (int n=0;n<6;n++) {
        System.err.print(current.neighborsArray[n].index+" ");
      }
      if (i % 10 == 0) {
        System.err.println();
        System.err.print("^ ");
      }
    }
    System.err.println();
    
    System.err.println("^" + numberOfBases);
    System.err.print("^ ");
    for (int i=0;i<numberOfBases;i++) {
      System.err.print(Map.myBases[i].index+" ");
    }
    for (int i=0;i<numberOfBases;i++) {
      System.err.print(Map.oppBases[i].index+" ");
    }
    System.err.println();
  }

  public void readOptional(FastReader in) {
    Player.turn = in.nextInt();
    originalTotalCrystalsGoal = in.nextInt();
  }

  public void saveOptional() {
    System.err.println("*** OPTIONAL ***");
    System.err.println("^" + Player.turn+" "+originalTotalCrystalsGoal);
  }

  public void read(FastReader in) {
    initTurn();
    
    myScore = in.nextInt();
    oppScore = in.nextInt();

    saveInit();
    saveOptional();
    System.err.println("*** TURN");
    System.err.print("^ "+myScore+" "+oppScore+" ");

    for (int i = 0; i < numberOfCells; i++) {
      if (i % 20 == 0 ) {
        System.err.println();
        System.err.print("^ ");
      }
      int resources = in.nextInt();
      int myAnts = in.nextInt();
      int oppAnts = in.nextInt();

      readCell(i, resources, myAnts, oppAnts);
      
      System.err.print(packed(resources, myAnts, oppAnts)+" ");
    }
    
    postInit();

    System.err.println();
    System.err.println("*** END");
  }

  private void initTurn() {
    totalMyAnts = 0;
    totalResources = 0;
    totalCrystals = 0;
    totalEggs = 0;
  }

  private void readCell(int index, int resources, int myAnts, int oppAnts) {
    Cell cell = cells[index];
    
    cell.beacon = 0;
    cell.resources = resources;
    cell.myAnts = myAnts;
    cell.vAnts = myAnts;
    cell.oppAnts = oppAnts;
    
    totalMyAnts += myAnts;
    totalResources += resources;
    if (cell.data.type == Map.CELL_EGGS) {
      totalEggs += resources;
    } else {
      totalCrystals+= resources;
    }
  }

  public void readPacked(FastReader in) {
    initTurn();
    
    this.myScore = in.nextInt();
    this.oppScore = in.nextInt();
    
    for (int i = 0; i < numberOfCells; i++) {
      int packed = in.nextInt();
      int myAnts = packed / 100_000;
      int oppAnts = (packed - myAnts * 100_000) / 1_000;
      int resources = packed % 1000;
      readCell(i, resources, myAnts, oppAnts);
    }    
    
    postInit();
  }
  
  private void postInit() {
    if (State.originalTotalCrystalsGoal == -1) {
      State.originalTotalCrystalsGoal = totalCrystals / 2;
    }    
  }

  private String packed(int resources, int myAnts, int oppAnts) {
    int value = 100_000 * myAnts + 1_000 * oppAnts + resources;
    return ""+value;
  }

  public void resetDedicated() {
    for (Cell cell : cells) {
      cell.dedicatedBase = -1;
    }
  }





}
