package cotc.ai.ag.ref2;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import cgcollections.arrays.FastArray;
import cotc.GameState;
import cotc.ai.AISolution;
import cotc.ai.ag.AGAction;
import cotc.entities.Action;
import cotc.entities.Ship;

public class AGSolution implements AISolution {
  private static final Action[] ACTION_VALUES = Action.values();
  public static int DEPTH = 5;
  public static Random rand = new Random();
  
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
    for (int s=0;s<state.teams[0].shipsAlive.length;s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];
      actions.put(ship, new AGAction[DEPTH]);
    }
  }
  
  @Override
  public String[] output() {
    String[] output = new String[shipCount];
    int i=0;
    for (int s=0;s<state.teams[0].shipsAlive.length;s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];
      output[i++] = actions.get(ship)[0].toString();
    }
    return output;
  }

  public void randomize(GameState state) {
    for (int s=0;s<state.teams[0].shipsAlive.length;s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];
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
        shipActions[i] = new AGAction(action, null);
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

  public void updateEnergyTurn(int turn, GameState state2) {
    // TODO nothing atm
  }

  public void updateEnergyEnd(GameState state2) {
    // feature after turn DEPTH, less precise, but more insight
    Feature feature = new Feature();
    feature.calculateFeatures(state);
    energy += 0
        + feature.myHealtFeature 
        - feature.hisHealthFeature
        + feature.speedFeature
        + 0.1*feature.distanceToClosestBarrelFeature
        //+ feature.myMobilityFeature
        //+ distanceToCenterFeature
        //+ 0.1*(sol.barrelDomination.rumCount0-sol.barrelDomination.rumCount1)
        ;
  }
  @Override
  public void setEnergy(int energy) {
    this.energy = energy;
  }

  @Override
  public void resetEnergy() {
    this.energy = 0;
  }
  @Override
  public Map<Ship, AGAction[]> getActions() {
    return actions;
  }

  @Override
  public FastArray<AGAction> getActionsNew() {
    // TODO Auto-generated method stub
    return null;
  }

}
