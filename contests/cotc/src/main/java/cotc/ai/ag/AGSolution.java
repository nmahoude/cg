package cotc.ai.ag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import cotc.BarrelDomination;
import cotc.GameState;
import cotc.ai.AISolution;
import cotc.entities.Action;
import cotc.entities.Barrel;
import cotc.entities.Ship;
import cotc.utils.Coord;

public class AGSolution implements AISolution{
  private static final Coord MAP_CENTER = Coord.get(11, 10);
  public static int DEPTH = 5;
  public static Random rand = new Random();
  
  public Map<Ship, Action[]> actions;
  private int shipCount;
  
  private GameState state;

  public double energy;
 
  public double myHealtFeature;
  public double speedFeature;
  public double distToBarrelFeature;
  public double movementFeature;
  public int hisHealthFeature;
  public BarrelDomination barrelDomination;
  private double distanceToCenterFeature;

  protected AGSolution() {
  }
  
  public AGSolution(GameState state) {
    this.state = state;
    this.shipCount = state.shipCount;
    actions = new HashMap<>();
    for (Ship ship : state.teams.get(0).shipsAlive) {
      actions.put(ship, new Action[DEPTH]);
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
      Action[] shipActions = actions.get(ship);
      for (int i=0;i<DEPTH;i++) {
        Action action = Action.values()[rand.nextInt(Action.values().length)];
        if (action == Action.FIRE) {
          action = Action.WAIT;
        }
        if (i == 0) {
          // eliminate impossible actions
          if (action == Action.SLOWER && ship.speed == 0) {
            action = Action.FASTER;
          }
        }
        // debug NPE :(
//        if (shipActions == null) {
//          System.err.println("shipActions == null");
//          System.err.println("i is "+i);
//          System.err.println("ship.id is "+ship.id);
//          System.err.println(""+ship.id+" y est avec "+actions.get(ship).toString());
//        }
        shipActions[i] = action;
      }
    }
  }

  public void calculateFeatures(GameState state) {
    myHealtFeature = 0;
    hisHealthFeature = 0;
    speedFeature = 0;
    distToBarrelFeature = 0;
    movementFeature = 0;
    distanceToCenterFeature = 0;
    
    barrelDomination = state.getBarrelDominitation();
    
    for (Ship ship : state.teams.get(0).shipsAlive) {
      if (ship.position != ship.b_position) {
        movementFeature+=1.0;
      }
      distanceToCenterFeature += 10-(ship.position.distanceTo(MAP_CENTER));
    }
    
    for (Ship ship : state.teams.get(0).shipsAlive) {
      myHealtFeature += ship.health;
      speedFeature += ship.speed;
    }
    
    for (Ship ship : state.teams.get(1).shipsAlive) {
      hisHealthFeature += ship.health;
    }
    
    // distToBarrel
    for (Ship ship : state.teams.get(0).shipsAlive) {
      Barrel barrel = state.getClosestBarrel(ship);
      if (barrel != null) {
        int dist = barrel.position.distanceTo(ship.position);
        distToBarrelFeature +=(45.0-dist) / 45.0;
      } else {
        break; // don't go further, no more barrels
      }
    }
  }
  
  public void debugOutput() {
    System.err.println("Actions ");
    for (Entry<Ship, Action[]> entry : actions.entrySet()) {
      System.err.print("  For "+entry.getKey().id);
      for (Action action : entry.getValue()) {
        if (action != null) {
          System.err.print(action.toString()+", ");
        }
      }
      System.err.println();
    }
    
    System.err.println("Energy : "+energy+" from "
      + "health: "+myHealtFeature 
      + " speed: "+speedFeature
      + " distToB: "+distToBarrelFeature
      );
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
    calculateFeatures(state);
    energy += 0
        + myHealtFeature 
        - hisHealthFeature
        + speedFeature
        //+ distanceToCenterFeature
        //+ 0.1*(sol.barrelDomination.rumCount0-sol.barrelDomination.rumCount1)
        ;
  }
}
