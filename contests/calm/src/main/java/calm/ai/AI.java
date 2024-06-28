package calm.ai;

import java.util.List;

import calm.Player;
import calm.actions.Action;
import calm.sim.Simulation;
import calm.state.Order;
import calm.state.State;
import calmBronze.Item;

public class AI {
  private static double[] patience;
  static {
    patience = new double[200];
    patience[0] = 1;
    for (int i=1;i<patience.length;i++) {
      patience[i] = patience[i-1]* 0.99;
    }
  }
  static Simulation sim = new Simulation();
  static Action bestActions[] = new Action[100];
  static int bestActionsFE = 0;

  double bestScore = Double.NEGATIVE_INFINITY;
  
  public void think(State currentState) {
    bestScore = Double.NEGATIVE_INFINITY;
    
    State test = new State();
    long start = System.currentTimeMillis();
    int sim = 0;
    Action actions[] = new Action[100];
    int actionsFE = 0;
    bestActionsFE = 0;
    
    while (System.currentTimeMillis() - start < Player.TIME_LIMIT) {
      actionsFE = 0;
      sim++;
      test.copyFrom(currentState);
      double score = 0;
      for (int i=0;i<10;i++) {
        Action action = thinkOnePly(test);
        actions[actionsFE++] = action;
        score += patience[i] * eval(test);
      }
      if (score > bestScore) {
        bestScore = score;
        //System.err.println("new best actions : ");
        for (int i=0;i<actionsFE;i++) {
          bestActions[i] = actions[i];
          //System.err.print(actions[i]+",");
        }
        //System.err.println();
        bestActionsFE = actionsFE;
      }
    }
    System.err.println("Best score = " + bestScore);
    System.err.println("In "+Player.TIME_LIMIT+" ms : "+sim);
    
  }

  private double eval(State test) {
    double score = 0.0;

    // TODO cache order needs
    int needs[] = new int[8];
    for (Order order : test.orderList) {
      updateNeeds(order.items, needs);
    }
    
    int items = test.agent1.items;
    int got[] = new int[8];
    updateNeeds(items, got);
    for (int i=0;i<5;i++) {
      if (got[i] > 0 && needs[i] > 0) {
        score += 1000;
      }
    }
    return score;
  }

  private void updateNeeds(int items, int[] needs) {
    if ((items & Item.ICE_CREAM) != 0) needs[0]++;
    if ((items & Item.BLUEBERRIES) != 0) needs[1]++;
    if ((items & Item.CHOPPED_STRAWBERRIES) != 0) needs[2]++;
    if ((items & Item.CROISSANT) != 0) needs[3]++;
    if ((items & Item.BLUEBERRIES_TART) != 0) needs[4]++;
  }

  private Action thinkOnePly(State state) {
    List<Action> allActions = state.getPossibleActionsForPlayer1();
    
    Action action = allActions.get(Player.random .nextInt(allActions.size()));
    sim.simulate(state, action, null);
    return action;
  }

  public void output(State state) {
    if (bestActions[0] == null) {
      System.err.println("FirstBest is null, wait ...");
      System.out.println("WAIT");
    } else {
      bestActions[0].execute(state, state.agent1);
    }
  }
}
