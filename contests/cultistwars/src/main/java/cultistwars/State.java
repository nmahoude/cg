package cultistwars;

import fast.read.FastReader;

public class State {
	public static final int SIZE = 13*17;

	public static final int WALL = -1;
  public static final int MY_TROOPS = 10;
  public static final int OPP_TROOPS = 11;
  
  int[][] initGrid = new int[13][7];
  int[][] grid = new int[13][7];
  int myId;
  
  
  Unit[] units = new Unit[30];
  Unit me;
  Unit opp;

  int unitsFE;
  {
    for (int i=0;i<units.length;i++) {
      units[i] = new Unit();
    }
  }
  
  public void readInit(FastReader in) {
    myId = in.nextInt();
    int width = in.nextInt(); // Width of the board
    int height = in.nextInt(); // Height of the board
    System.err.println(String.format("%d %d %d", myId, width, height));
    
    for (int y = 0; y < height; y++) {
      String line = in.next(); // A y of the board: "." is empty, "x" is obstacle
      for (int x=0;x<13;x++) {
        if (line.charAt(x) == 'x') initGrid[x][y] = WALL;
      }
      System.err.println(line);
    }
    
    Bresenham.initialize(grid);
  }

  public void readTurn(FastReader in) {
    unitsFE = 0;
    me = null;
    opp = null;
    
    initGrid();
    
    int numOfUnits = in.nextInt(); // The total number of units on the board
    for (int i = 0; i < numOfUnits; i++) {
      int unitId = in.nextInt(); // The unit's ID
      int unitType = in.nextInt(); // The unit's type: 0 = Cultist, 1 = Cult Leader
      int hp = in.nextInt(); // Health points of the unit
      int x = in.nextInt(); // X coordinate of the unit
      int y = in.nextInt(); // Y coordinate of the unit
      int owner = in.nextInt(); // id of owner player
   
      System.err.println(String.format("%2d %d %2d %2d %2d %d", unitId, unitType, hp, x, y, owner));
      
      units[unitsFE].read(unitId, unitType, hp, x, y, owner == 2 ? 2 : (owner == myId ? 0 : 1));
      if (units[unitsFE].unitType == Unit.CULT_LEADER_TYPE) {
        if (units[unitsFE].owner == 0) {
          me = units[unitsFE];
        } else {
          opp = units[unitsFE];
        }
      }
      
      grid[x][y] = 10 + units[unitsFE].owner;
      
      
      unitsFE++;
    }
    
    debug();
    
  }

	private void initGrid() {
		for (int y=0;y<7;y++) {
			for (int x=0;x<13;x++) {
				grid[x][y] = initGrid[x][y];
			}
		}
	}

	private void debug() {
		for (int y=0;y<7;y++) {
			for (int x=0;x<13;x++) {
				System.err.print(String.format("%2d ", grid[x][y]));
			}
			System.err.println();
		}
	}
  
  
  

}
