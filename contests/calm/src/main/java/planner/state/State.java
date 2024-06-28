package planner.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import calmBronze.Table;
import planner.PlannerPlayer;

public class State {
  public static List<Table> fixedTables = new ArrayList<>();
  public static List<Order> orderList = new ArrayList<>();
  public static Table equipmentDishWasher;
  public static Table equipmentBlueberries;
  public static Table equipmentIceCream;
  public static Table equipmentBell;
  public static Table equipmentStrawberries;
  public static Table equipmentDough;
  public static Table equipmentChoppingBoard;
  public static Table equipmentOven;
  
  public Agent agent1 = new Agent();
  public Agent agent2 = new Agent();
  

  public Map<Table,Integer> tables = new HashMap<>();
  
  public int turn;
  public int ovenContents;
  public int ovenTimer;
  
  public void read(Scanner in) {
    tables.clear();
    
    turn = 200-in.nextInt();
    if (PlannerPlayer.DEBUG_INPUT) {
      System.err.println("String input= \""+turn+PlannerPlayer.DEBUG_EOL);
    }
    agent1.read(in); 
    agent2.read(in);
    
    init();
    int numTablesWithItems = in.nextInt();
    if (PlannerPlayer.DEBUG_INPUT) {
      System.err.println("\""+numTablesWithItems+PlannerPlayer.DEBUG_EOL);
    }
    for (int i = 0; i < numTablesWithItems; i++) {
      int tableX = in.nextInt();
      int tableY = in.nextInt();
      Table table = new Table(tableX,tableY);
      String itemStr = in.next();
      tables.put(table, Item.getFromString(itemStr));
      if (PlannerPlayer.DEBUG_INPUT) {
        System.err.println("\""+tableX+" "+tableY+" "+itemStr+PlannerPlayer.DEBUG_EOL);
      }
    }
    
    String ovenContentsStr = in.next();
    ovenContents = Item.getFromString(ovenContentsStr);
    ovenTimer = turn + in.nextInt(); // TODO check for +1/-1 shift
    if (PlannerPlayer.DEBUG_INPUT) {
      System.err.println("\""+ovenContents+" "+ovenTimer+PlannerPlayer.DEBUG_EOL);
    }
    
    orderList.clear();
    int numCustomers = in.nextInt();
    if (PlannerPlayer.DEBUG_INPUT) {
      System.err.println("\""+numCustomers+PlannerPlayer.DEBUG_EOL);
    }
    for (int i = 0; i < numCustomers; i++) {
      String customerItem = in.next();
      int customerAward = in.nextInt();
      if (PlannerPlayer.DEBUG_INPUT) {
        System.err.println("\""+customerItem+" "+customerAward+PlannerPlayer.DEBUG_EOL);
      }
      orderList.add(new Order(Item.getFromString(customerItem), customerAward));
    }
    
    if (PlannerPlayer.DEBUG_INPUT) {
      System.err.println("\"\";");
      System.err.println("state.read(new Scanner(input));");

    }
  }

  public void init() {
    tables.clear();
  }
  
  public static void readWorldInit(Scanner in) {
    fixedTables.clear();
    
    if (PlannerPlayer.DEBUG_INPUT) {
      System.err.print("String init = ");
    }
    for (int y = 0; y < 7; y++) {
      String kitchenLine = in.nextLine();
      if (PlannerPlayer.DEBUG_INPUT) {
        System.err.println("\""+kitchenLine+PlannerPlayer.DEBUG_EOL);
      }
      for (int x = 0; x < 11; x++) {
        char c = kitchenLine.charAt(x);
        switch (c) {
        case 'D':
          equipmentDishWasher = createTable(x,y,Item.EQUIPMENT_DISH);
          break;
        case 'B':
          equipmentBlueberries = createTable(x,y,Item.EQUIPMENT_BLUEBERRIES);
          break;
        case 'I':
          equipmentIceCream = createTable(x,y,Item.EQUIPMENT_ICE_CREAM);
          break;
        case 'W':
          equipmentBell = createTable(x,y,Item.EQUIPMENT_BELL);
          break;
        case 'S':
          equipmentStrawberries = createTable(x,y,Item.EQUIPMENT_STRAWBERRY);
          break;
        case 'H':
          equipmentDough = createTable(x,y,Item.EQUIPMENT_DOUGH);
          break;
        case 'C':
          equipmentChoppingBoard = createTable(x,y,Item.EQUIPMENT_CHOPPING_BOARD);
          break;
        case 'O':
          equipmentOven = createTable(x,y,Item.EQUIPMENT_OVEN);
          break;
        case '#': // emptyTable
          createTable(x,y,Item.EMPTY_TABLE);
          break;
        case '.': // floor
        case '0': // player 0
        case '1': // player 1
          break;
        default:
          throw new RuntimeException("Equipment inconnu : " + c);
        }
      }
    }
    if (PlannerPlayer.DEBUG_INPUT) {
      System.err.println("\"\";");
      System.err.println("State.readWorldInit(new Scanner(init));");
    }
  }


  private static Table createTable(int x, int y, int item) {
    Table table = new Table(x,y);
    table.item = item;
    fixedTables.add(table);
    return table;
  }

  public void copyFrom(State model) {
    this.agent1.copyFrom(model.agent1);
    this.agent2.copyFrom(model.agent2);
    
    this.tables.clear();
    this.tables.putAll(model.tables);
    ovenContents = model.ovenContents;
    ovenTimer = model.ovenTimer;
    turn = model.turn;
  }

  public boolean isCompatible(State state) {
    return agent1.items == state.agent1.items;
  }

  public void addTurns(int time) {
    turn+=time;
    
    if (ovenContents != 0) {
      if (ovenTimer <= turn) {
        // bake or dead
        if (ovenContents == Item.DOUGH) {
          ovenContents = Item.CROISSANT;
          ovenTimer+= 10;
        } else if (ovenContents == Item.RAW_TART) {
          ovenContents = Item.BLUEBERRIES_TART;
          ovenTimer+= 10;
        } else {
          ovenTimer = 0;
          ovenContents = 0; // burned
      }
    }
      
    }
  }

}
