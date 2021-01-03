package xmasrush;

import cgutils.test.TestOutputer;
import fast.read.FastReader;

public class State {
  public static final Pos Center = Pos.get(3,3);
  
  public int turnType;
  public Cell[] cells = new Cell[7*7];
  public Agent[] agents = new Agent[2];

  public Cell[] itemsCellByIndex = new Cell[200];
  
  public State() {
    for (int y=0;y<7;y++) {
      for (int x=0;x<7;x++) {
        cells[x+7*y] = new Cell(x,y);
      }
    }
    
    for (int y=0;y<7;y++) {
      for (int x=0;x<7;x++) {
        Cell currentCell = cells[x+7*y];
        if (x>0) currentCell.neighbors[Cell.LEFT] = cells[(x-1)+7*y]; 
        if (x<6) currentCell.neighbors[Cell.RIGHT] = cells[(x+1)+7*y]; 
        if (y>0) currentCell.neighbors[Cell.UP] = cells[x+7*(y-1)]; 
        if (y<6) currentCell.neighbors[Cell.DOWN] = cells[x+7*(y+1)]; 
        
      }
    }

    
    agents[0] = new Agent(0, this);
    agents[1] = new Agent(1, this);
  }
  
  
  public void read(FastReader in) {
    turnType = in.nextInt();
    TestOutputer.output(turnType);
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 7; x++) {
        Cell cell = cells[x + y *7];
        cell.reset();
        char[] tile = in.nextChars();
        TestOutputer.output(tile);
        cell.setDirections(tile);
      }
    }
    for (int i = 0; i < 2; i++) {
      agents[i].read(in);
    }
    int numItems = in.nextInt(); // the total number of items available on board and on player tiles
    TestOutputer.output(numItems);
    for (int i = 0; i < numItems; i++) {
      char[] itemName = in.nextChars();
      int itemX = in.nextInt();
      int itemY = in.nextInt();
      int itemPlayerId = in.nextInt();
      TestOutputer.output(itemName, itemX, itemY, itemPlayerId);
      Cell itemCell;
      if (itemX == -1) {
        itemCell = agents[0].playerCell;
      } else if (itemX == -2) {
        itemCell = agents[1].playerCell;
      } else {
        itemCell = cells[itemX+7*itemY];

        // set the cell to reach
        int itemIndex = Item.getItem(itemName);
        if (itemPlayerId == 1) itemIndex+=100;
        itemsCellByIndex[itemIndex] = cells[itemX + 7*itemY];
      }
      itemCell.setItem(itemName, itemPlayerId);
    }
    int numQuests = in.nextInt(); // the total number of revealed quests for both players
    TestOutputer.output(numQuests);
    agents[0].resetItems();
    agents[1].resetItems();
    for (int i = 0; i < numQuests; i++) {
      char[] questItemName = in.nextChars();
      int questPlayerId = in.nextInt();
      TestOutputer.output(questItemName, questPlayerId);

      int itemIndex = Item.getItem(questItemName);
      if (questPlayerId == 1) itemIndex+=100;
      
      for(Cell cell : cells) {
        if (cell.itemIndex == itemIndex) {
          System.err.println("For agent "+questPlayerId+" QI @ "+cell);
          agents[questPlayerId].addQuestItem(cell.pos);
          break;
        }
      }
    }
    
    
    debugGrid();
  }

  public void debugGrid() {
    System.err.println("agent "+agents[0]);
    System.err.println("  01234567  01234567");
    for (int y = 0; y < 7; y++) {
      System.err.print(y+" ");
      for (int x = 0; x < 7; x++) {
        Cell cell = cells[x + y *7];
        System.err.print(cell.debug());
      }
      System.err.print("  ");
      for (int x = 0; x < 7; x++) {
        Cell cell = cells[x + y *7];
        char c = (char) ((int)'0'+(cell.itemIndex % 100));
        System.err.print(c);
      }
      System.err.println();
    }
  }

  public void debug() {
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 7; x++) {
        Cell cell = cells[x + y *7];
        System.err.print("From "+cell+" => ");
        for (int dir=0;dir<4;dir++) {
          Cell neighbor = cell.getVisitableNeighbor(dir);
          if (neighbor != Cell.WALL) {
            System.err.print(" "+neighbor);
          }
        }
        System.err.println();
      }
    }
  }


  public Cell getCellAt(int x, int y) {
    return cells[x + 7*y];
  }


  public Cell getCellOf(Agent agent) {
    return cells[agent.pos.offset];
  }
}
