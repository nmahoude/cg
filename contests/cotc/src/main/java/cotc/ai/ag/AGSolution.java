package cotc.ai.ag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import cotc.GameState;
import cotc.ai.AISolution;
import cotc.entities.Action;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;

public class AGSolution implements AISolution{
  public final static int AGACTION_SIZE = 1000;
  private static final Action[] ACTION_VALUES = Action.values();
  public static int DEPTH = 5;
  public static Random rand = new Random();
  public Map<Ship, AGAction[]> actions;
  protected int shipCount;
  
  protected GameState state;
  public double energy;
  private FeatureWeight weights;
 

  protected AGSolution() {
  }
  
  public AGSolution(GameState state, FeatureWeight weights) {
    this.state = state;
    this.weights = weights;
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

  public void randomize(GameState state, StateAnalyser analyser) {
    
    for (Ship ship : state.teams.get(0).shipsAlive) {
      AGAction[] shipActions = actions.get(ship);
      StateAnalyser.PerShip info = analyser.analyse.get(ship);
      Action action;
      
      // i = 0;
      action = ACTION_VALUES[rand.nextInt(ACTION_VALUES.length)];
      Coord target = Simulation.MAP_CENTER;
      
      // eliminate impossible actions
      if (action == Action.SLOWER && ship.speed == 0) {
        action = Action.FASTER;
      }
      if (action == Action.FASTER && ship.speed == 2) {
        action = Action.WAIT;
      }
      if (action == Action.MINE && info.enemyAtStern != true) {
        action = Action.WAIT;
      }
      if (action == Action.FIRE) {
        if (analyser.analyse.get(info.closestEnemy).canMove[0] == false) {
          target = info.closestEnemy.position;
        } else {
          action = Action.WAIT;
        }
      }
      
      shipActions[0] = new AGAction(action, target);
      
      // other turns is more random
      for (int i=1;i<DEPTH;i++) {
        action = ACTION_VALUES[rand.nextInt(ACTION_VALUES.length)];
        if (action == Action.FIRE) {
          action = Action.WAIT;
        }
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

  public void updateEnergyTurn1(GameState state2) {
    // feature after turn 1, will be more precise, but no insight
    // TODO nothing atm
  }

  public void updateEnergy(GameState state) {
    // feature after turn DEPTH, less precise, but more insight
    Feature feature = new Feature();
    feature.calculateFeatures(state);
    energy = feature.applyWeights(weights);
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
}
