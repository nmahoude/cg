package spring2023.old;

class State {
  public static int originalTotalCrystalsGoal = -1;
  
private static int numberOfCells;

public static int numberOfBases;

public static Cell[] myBases;

public static Cell[] oppBases;

public int totalMyAnts = 0;

int myScore;

int oppScore;

public static void readInit(FastReader in) {
 numberOfCells = in.nextInt();
 Map.init(numberOfCells);
 for (int i = 0; i < numberOfCells; i++) {
   Cell current = Map.cells[i];
   int type = in.nextInt();
   in.nextInt();
   current.type = type;
   current.resources = 0;
   for (int n = 0; n < 6; n++) {
     int neigh = in.nextInt();
     if (neigh == -1) {
       current.neighborsArray[n] = Cell.VOID;
       continue;
     }
     Cell neighborCell = Map.cells[neigh];
     current.neighborsArray[n] = neighborCell;
     current.neighbors.add(neighborCell);
   }
 }
 numberOfBases = in.nextInt();
 myBases = new Cell[numberOfBases];
 oppBases = new Cell[numberOfBases];
 for (int i = 0; i < numberOfBases; i++) {
   int myBaseIndex = in.nextInt();
   Map.cells[myBaseIndex].isMyBase = true;
   myBases[i] = Map.cells[myBaseIndex];
 }
 for (int i = 0; i < numberOfBases; i++) {
   int oppBaseIndex = in.nextInt();
   Map.cells[oppBaseIndex].isOppBase = true;
   oppBases[i] = Map.cells[oppBaseIndex];
 }
 Map.calculateBaseDistances();
}

public static void saveInit() {
 System.err.println("*** INIT ***");
 System.err.println("^" + numberOfCells);
 System.err.print("^ ");
 for (int i = 0; i < numberOfCells; i++) {
   Cell current = Map.cells[i];
   System.err.print(current.type + " " + 0 + " ");
   for (int n = 0; n < 6; n++) {
     System.err.print(current.neighborsArray[n].index + " ");
   }
   if (i % 10 == 0) {
     System.err.println();
     System.err.print("^ ");
   }
 }
 System.err.println();
 System.err.println("^" + numberOfBases);
 System.err.print("^ ");
 for (int i = 0; i < numberOfBases; i++) {
   System.err.print(myBases[i].index + " ");
 }
 for (int i = 0; i < numberOfBases; i++) {
   System.err.print(oppBases[i].index + " ");
 }
 System.err.println();
}

public void readOptional(FastReader in) {
 OldPlayer.turn = in.nextInt();
}

public void saveOptional() {
 System.err.println("*** OPTIONAL ***");
 System.err.println("^" + OldPlayer.turn);
}

public void read(FastReader in) {
 totalMyAnts = 0;
 
 myScore = in.nextInt();
 oppScore = in.nextInt();
 
 int totalCrystals = 0;
 
 for (int i = 0; i < numberOfCells; i++) {
   int resources = in.nextInt();
   if (i == 0) {
     saveInit();
     saveOptional();
     System.err.println("*** TURN");
     System.err.print("^ ");
   }
   int myAnts = in.nextInt();
   int oppAnts = in.nextInt();
   readCell(i, resources, myAnts, oppAnts);
   if (Map.cells[i].type == 2) totalCrystals += resources;
   totalMyAnts += myAnts;
   System.err.print(packed(resources, myAnts, oppAnts) + " ");
   if (i % 20 == 0) {
     System.err.println();
     System.err.print("^ ");
   }
 }
 
 postInit(totalCrystals);

 
 System.err.println();
 System.err.println("*** END");
}

private void readCell(int index, int resources, int myAnts, int oppAnts) {
 Map.cells[index].beacon = 0;
 Map.cells[index].resources = resources;
 Map.cells[index].myAnts = myAnts;
 Map.cells[index].vAnts = 0;
 Map.cells[index].oppAnts = oppAnts;
}

public void readPacked(FastReader in) {
 int totalCrystals = 0;
  for (int i = 0; i < numberOfCells; i++) {
   int packed = in.nextInt();
   int myAnts = packed / 100_000;
   int oppAnts = (packed - myAnts * 100_000) / 1_000;
   int resources = packed % 1000;
   readCell(i, resources, myAnts, oppAnts);
   
   if (Map.cells[i].type == 2) totalCrystals += resources;
 }
 postInit(totalCrystals);
}

private String packed(int resources, int myAnts, int oppAnts) {
 int value = 100_000 * myAnts + 1_000 * oppAnts + resources;
 return "" + value;
}

private void postInit(int totalCrystals) {
  if (State.originalTotalCrystalsGoal == -1) {
    State.originalTotalCrystalsGoal = totalCrystals / 2;
  }    
}

}