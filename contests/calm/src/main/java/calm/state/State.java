package calm.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import calm.Player;
import calm.actions.Action;
import calm.actions.MoveAction;
import calm.actions.UseBell;
import calm.actions.UseDishWasher;
import calm.actions.UseEquipmentIngredient;
import calm.actions.UseKnife;
import calm.actions.UseOven;
import calmBronze.Item;
import util.P;
import util.PCache;

public class State {
  public static Table[] equipments = new Table[8];
  public static int equipmentsFE = 0;
  public static List<Table> fixedTables = new ArrayList<>();
  public static List<Order> orderList = new ArrayList<>();

  public static Actions actions[] = new Actions[77]; // what (equipment) actions at pos index
  private static void initAvailableActions() {
    for (int i=0;i<77;i++) {
      actions[i] = new Actions();
    }
    
    Action eqAction = null;
    for (Table t : equipments) {
      P eqPos = t.pos;
      switch(t.item) {
      case Item.EQUIPMENT_DISH:
        eqAction = new UseDishWasher(); break;
      case Item.EQUIPMENT_OVEN:
        eqAction = new UseOven(); break;
      case Item.EQUIPMENT_ICE_CREAM:
        eqAction = new UseEquipmentIngredient(t,Item.ICE_CREAM); break;
      case Item.EQUIPMENT_STRAWBERRY:
        eqAction = new UseEquipmentIngredient(t,Item.STRAWBERRIES); break;
      case Item.EQUIPMENT_BLUEBERRIES:
        eqAction = new UseEquipmentIngredient(t,Item.BLUEBERRIES); break;
      case Item.EQUIPMENT_DOUGH:
        eqAction = new UseEquipmentIngredient(t,Item.DOUGH); break;
      case Item.EQUIPMENT_BELL:
        eqAction = new UseBell(t); break;
      case Item.EQUIPMENT_CHOPPING_BOARD:
        eqAction = new UseKnife(); break;

      }
      for (int i=0;i<77;i++) {
        P groundPos = P.get(i);
        if (eqPos.neighborDistance(groundPos) <= 1) {
          actions[i].addAction(eqAction);
        }
      }
    }
  }
  
  
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
  public Board board = new Board();
  Order orders[] = new Order[3]; // always full
  public int turn;
  public int score;
  
  public void read(Scanner in) {
    score = 0;
    turn = 200-in.nextInt();
    Player.start = System.currentTimeMillis();

    if (Player.DEBUG_INPUT) {
      System.err.println("String input= \""+turn+Player.DEBUG_EOL);
    }
    agent1.read(in); 
    agent2.read(in);
    
    board.read(this, in);
    
    int ordersFE = 0;
    int numCustomers = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.println("\""+numCustomers+Player.DEBUG_EOL);
    }
    for (int i = 0; i < numCustomers; i++) {
      String customerItem = in.next();
      int customerAward = in.nextInt();
      orders[ordersFE++] = new Order(Item.getFromString(customerItem), customerAward); // TODO check back in the list of orders !
      if (Player.DEBUG_INPUT) {
        System.err.println("\""+customerItem+" "+customerAward+Player.DEBUG_EOL);
      }
    }
    
    if (Player.DEBUG_INPUT) {
      System.err.println("\"\";");
      System.err.println("state.read(new Scanner(input));");

    }
  }

  public static void readWorldInit(Scanner in) {
    equipmentsFE = 0;
    fixedTables.clear();
    if (Player.DEBUG_INPUT) {
      System.err.print("String init = ");
    }

    int numAllCustomers = in.nextInt();
    System.err.println("\""+numAllCustomers+Player.DEBUG_EOL);
    for (int i = 0; i < numAllCustomers; i++) {
      String customerItem = in.next(); 
      int customerAward = in.nextInt();
      System.err.println("\""+customerItem+" "+customerAward+Player.DEBUG_EOL);
    }
    in.nextLine();
    
    
    for (int y = 0; y < 7; y++) {
      String kitchenLine = in.nextLine();
      if (Player.DEBUG_INPUT) {
        System.err.println("\""+kitchenLine+Player.DEBUG_EOL);
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
    if (Player.DEBUG_INPUT) {
      System.err.println("\"\";");
      System.err.println("State.readWorldInit(new Scanner(init));");
    }
    
    initAvailableActions();
  }


  private static Table createTable(int x, int y, int item) {
    Table table = new Table(x,y);
    table.item = item;
    fixedTables.add(table);
    if (item != Item.EMPTY_TABLE) {
      equipments[equipmentsFE++] = table;
    }
    return table;
  }

  public void copyFrom(State model) {
    this.score = model.score;
    this.agent1.copyFrom(model.agent1);
    this.agent2.copyFrom(model.agent2);
    
    this.board.copyFrom(model.board);
    turn = model.turn;
  }

  public boolean isCompatible(State state) {
    return agent1.items == state.agent1.items;
  }

  public void addTurns(int time) {
    turn+=time;
    
    board.updateOven(turn);
  }

  public List<P> getGrabActions(Agent agent) {
    List<P> actionables = new ArrayList<>();
    Table tables[] = new Table[8];
    int tablesFE = board.getInteractiveNeighborsTable(this, agent, tables);
    for (int i=0;i<tablesFE;i++) {
      Table table = tables[i];
      actionables.add(table.pos);
    }
    for (Table equipement : equipments) {
      if (agent.canUseRelativeToDistance(equipement) && Item.canBeUseWithAgent(equipement.item, this, agent)) {
        actionables.add(equipement.pos);
      }
    }
    return actionables;
  }

  /**
   * return points where it is possible to drop stuff (empty tables)
   * @return
   */
  public List<P> getDropActions() {
    List<P> points = new ArrayList<>();
    for (Table table : fixedTables) {
      if (table.item != 0) continue; // equipment;
      if (board.tables.containsKey(table)) continue;
      points.add(table.pos);
    }
    return points;
  }

  public Table getTable(P pos) {
    for (Table t: equipments) {
      if (t.pos == pos) {
        return t;
      }
    }
    
    return board.getTable(pos);
  }

  // TODO optimize ! Array & FE
  public List<Action> getPossibleActions(Agent agent1, Agent agent2) {
    List<Action> allActions = new ArrayList<>();

    // movements
    List<P> points = PCache.getListOfMove(agent1.pos, agent2.pos);
    if (points == null) {
      System.err.println("points is null for pos : "+agent1.pos +" & "+agent2.pos);
    }
    for (P p: points) {
      // TODO add cache to this too !!! (don't reconstruct list of move but have it !
      allActions.add(MoveAction.moves[p.index]);
    }

    // all (equipment) actions available at agent pos !
    Actions actions = State.actions[agent1.pos.index];
    for (int i=0;i<actions.actionsFE;i++) {
      Action action = actions.actions[i];
      if (action.prerequisites(this, agent1)) {
        allActions.add(action);
      }
    }
    if (agent1.items == Item.NOTHING
        || (agent1.hasDish() && !this.agent1.fullDish())) {
      // TODO add use of temporary ingredients actions ! 
    }
    
    // TODO add drop items !
    
    return allActions;
  }

  public List<Action> getPossibleActionsForPlayer1() {
    return getPossibleActions(agent1, agent2);
  }
  public List<Action> getPossibleActionsForPlayer2() {
    return getPossibleActions(agent2, agent1);
  }
}
