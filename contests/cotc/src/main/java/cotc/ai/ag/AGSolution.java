package cotc.ai.ag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import cotc.GameState;
import cotc.ai.AISolution;
import cotc.entities.Action;
import cotc.entities.Ship;
import cotc.utils.Coord;

public class AGSolution implements AISolution{
  private static final Action[] ACTION_VALUES = Action.values();
  public static int DEPTH = 5;
  public static Random rand = new Random();
  private final static int AGACTION_SIZE = 1000;
  public Map<Ship, AGAction[]> actions;
  private int shipCount;
  
  private GameState state;

  public double energy;
 

  protected AGSolution() {
  }
  
  public AGSolution(GameState state) {
    this.state = state;
    this.shipCount = state.shipCount;
    actions = new HashMap<>();
    for (Ship ship : state.teams.get(0).shipsAlive) {
      actions.put(ship, new AGAction[DEPTH]);
    }
  }
  
  @Override
  public String[] output() {
    String[] output = new String[shipCount];
    int i=0;
    for (Ship ship : state.teams.get(0).shipsAlive) {
      output[i++] = actions.get(ship)[0].toString();
    }
    return output;
  }

  public void randomize(GameState state) {
    for (Ship ship : state.teams.get(0).shipsAlive) {
      AGAction[] shipActions = actions.get(ship);
      for (int i=0;i<DEPTH;i++) {
        Action action = ACTION_VALUES[rand.nextInt(ACTION_VALUES.length)];
        if (action == Action.FIRE) {
          action = Action.WAIT;
        }
        if (i == 0) {
          // eliminate impossible actions
          if (action == Action.SLOWER && ship.speed == 0) {
            action = Action.FASTER;
          }
          if (action == Action.FASTER && ship.speed == 2) {
            action = Action.WAIT;
          }
        }
        // debug NPE :(
//        if (shipActions == null) {
//          System.err.println("shipActions == null");
//          System.err.println("i is "+i);
//          System.err.println("ship.id is "+ship.id);
//          System.err.println(""+ship.id+" y est avec "+actions.get(ship).toString());
//        }
        if (i == 0 && action == Action.WAIT) {
          shipActions[i] = new AGAction(Action.FIRE, Coord.get(rand.nextInt(23),rand.nextInt(21)));
        } else {
          shipActions[i] = new AGAction(action, null);
        }
      }
    }
  }
  public void debugOutput() {
    System.err.println("Actions ");
    for (Entry<Ship, AGAction[]> entry : actions.entrySet()) {
      System.err.print("  For "+entry.getKey().id);
      for (AGAction action : entry.getValue()) {
        if (action != null) {
          System.err.print(action.toString()+", ");
        }
      }
      System.err.println();
    }
  }
  public static AGSolution createFake() {
    AGSolution fake = new AGSolution();
    fake.energy = Double.NEGATIVE_INFINITY;
    return fake;
  }

  public void updateEnergyTurn1(GameState state2) {
    // feature after turn 1, will be more precise, but no insight
    // TODO nothing atm
  }

  public void updateEnergy(GameState state2) {
    // feature after turn DEPTH, less precise, but more insight
    Feature feature = new Feature();
    feature.calculateFeatures(state);
    energy += 0
        + feature.myHealtFeature 
        - 0.2*feature.hisHealthFeature // don't take too much credit for 
        + feature.speedFeature
        + 0.1*feature.distanceToClosestBarrelFeature
        //+ feature.myMobilityFeature
        //+ distanceToCenterFeature
        //+ 0.1*(sol.barrelDomination.rumCount0-sol.barrelDomination.rumCount1)
        ;
  }
}
