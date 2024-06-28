package planner;

import java.util.List;
import java.util.Scanner;

import planner.actionplanner.Node;
import planner.actionplanner.Planner;
import planner.state.Item;
import planner.state.Order;
import planner.state.State;

public class PlannerPlayer {
  public static boolean DEBUG_INPUT = true;
  public static final String DEBUG_EOL = "\\r\\n\"+";
  public static boolean DEBUG_PLANNER = false;
  
  private static State currentState = new State();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int numAllCustomers = in.nextInt();
    for (int i = 0; i < numAllCustomers; i++) {
      String customerItem = in.next(); // the food the customer is waiting for
      int customerAward = in.nextInt(); // the number of points awarded for
                                        // delivering the food
    }
    in.nextLine();
    State.readWorldInit(in);

    // game loop
    while (true) {
      currentState.read(in);

      Planner planner = new Planner();
      State goal = new State();
      
      // do the commis
      if ((currentState.agent1.items & Item.BLUEBERRIES_TART) != 0
          || (currentState.agent1.items & Item.CROISSANT) != 0
          || (currentState.agent1.items & Item.CHOPPED_STRAWBERRIES) != 0
          ) {
        goal.agent1.items = 0;
      } else {
        int neededPies = 0;
        int neededCroissant = 0;
        int neededChoppedStrawberries = 0;
        for (Order order : currentState.orderList) {
          if ((order.items & Item.BLUEBERRIES_TART) != 0) neededPies++;
          if ((order.items & Item.CROISSANT) != 0) neededCroissant++;
          if ((order.items & Item.CHOPPED_STRAWBERRIES) != 0) neededCroissant++;
        }
        
        if ((currentState.agent2.items & Item.BLUEBERRIES_TART) != 0 
            || (currentState.agent2.items & Item.RAW_TART) != 0
            || (currentState.agent2.items & Item.CHOPPED_DOUGH) != 0
            || (currentState.ovenContents & Item.RAW_TART) != 0
            || (currentState.ovenContents & Item.BLUEBERRIES_TART) != 0
            ) {
          neededPies--;
        }
        if ((currentState.agent2.items & Item.DOUGH) != 0) {
          neededCroissant--;
        }
        if ((currentState.agent2.items & Item.STRAWBERRIES) != 0) {
          neededChoppedStrawberries--;
        }
        
        if (neededPies > 0) {
          goal.agent1.items = Item.BLUEBERRIES_TART;
        } else if (neededCroissant > 0) {
          goal.agent1.items = Item.CROISSANT;
        } else if (neededChoppedStrawberries > 0) {
          goal.agent1.items = Item.CHOPPED_STRAWBERRIES;
        } else {
          goal.agent1.items = 0;
        }
      }
      
      long start = System.currentTimeMillis();
      List<Node> actions = planner.findPlan(currentState, goal);
      long end = System.currentTimeMillis();
      System.err.println("Time for plan : "+(end-start)+" ms");
      if (!actions.isEmpty()) {
        actions.get(0).action.execute(currentState);
      } else {
        System.out.println("USE "+State.equipmentOven.pos.x+" "+State.equipmentOven.pos.y);
      }
      
    }
  }
}
