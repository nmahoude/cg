package cotc.ai.ag;

import java.util.List;
import java.util.Map;
import java.util.Random;

import cotc.GameState;
import cotc.ai.AISolution;
import cotc.entities.Action;
import cotc.entities.Ship;
import cotc.utils.Coord;
import cotc.utils.FastArray;

public class AGSolution implements AISolution{
  public final static int AGACTION_SIZE = 1000;
  private static final Action[] ACTION_VALUES = Action.values();
  public static int DEPTH = 5;
  public static Random rand = new Random();

  public static double patience[] = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0};
  
  public FastArray<AGAction> actions = new FastArray<AGAction>(AGAction.class, 3 * DEPTH);
  protected int shipCount;
  
  protected GameState state;
  public double energy;
  private FeatureWeight weights;
 

  protected AGSolution() {
  }
  
  public AGSolution(GameState state, FeatureWeight weights) {
    this.state = state;
    this.weights = weights;
    this.shipCount = state.teams[0].shipsAlive.size();
    for (int i=0;i<3*DEPTH;i++) {
      actions.add(new AGAction());
    }
  }
  
  @Override
  public String[] output() {
    String[] output = new String[shipCount];
    for (int s=0;s<state.teams[0].shipsAlive.FE;s++) {
      output[s] = actions.elements[0 + s*DEPTH].toString();
    }
    return output;
  }

  public void randomize(GameState state, StateAnalyser analyser, List<FastArray<Action>> turn0PossibleActions) {
    for (int s=0;s<state.teams[0].shipsAlive.FE;s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];
      ShipStateAnalysis shipAnalysis = analyser.analyse.get(ship);
      FastArray<Action> possibleActions = turn0PossibleActions.get(s);
      
      for (int i=0;i<DEPTH;i++) {
        Action action;
      
        if (i == 0) {
          action = possibleActions.elements[rand.nextInt(possibleActions.FE)];
          Coord target = Coord.ZERO;
          
          if (action == Action.MINE) {
            if (shipAnalysis.enemyAtStern[0] == true) {
              action = Action.MINE;
            } else if (shipAnalysis.enemyAtStern[1] == true && shipAnalysis.closestEnemy.speed == 2) {
              action = Action.MINE;
            } else {
              action = Action.WAIT;
            }
          }
          if (action == Action.FIRE) {
            ShipStateAnalysis otherShipAnalysis = analyser.analyse.get(shipAnalysis.closestEnemy);
            if (shipAnalysis.closestEnemy.position.distanceTo(ship.position) <= 3) {
              // target the next position of the ship
              Coord c1 = shipAnalysis.closestEnemy.position.neighborsCache[shipAnalysis.closestEnemy.orientation];
              Coord c2 = c1.neighborsCache[shipAnalysis.closestEnemy.orientation];
              if (shipAnalysis.closestEnemy.speed == 2 && c2.isInsideMap()) {
                target = c2;
              } else {
                target = c1;
              }
            }
            if (otherShipAnalysis.canMove[0] == false) {
              target = shipAnalysis.closestEnemy.position;
            } else {
              action = Action.WAIT;
            }
          }
          actions.elements[i + s*DEPTH].action = action;
          actions.elements[i + s*DEPTH].target = target;
        } else {
          // other turns is more random
          action = ACTION_VALUES[rand.nextInt(ACTION_VALUES.length)];
          if (action == Action.FIRE) {
            action = Action.WAIT;
          }
          actions.elements[i + s*DEPTH].action = action;
          actions.elements[i + s*DEPTH].target = Coord.ZERO;
        }
      }
    }
  }
  
  public static AGSolution createFake() {
    AGSolution fake = new AGSolution();
    fake.energy = Double.NEGATIVE_INFINITY;
    return fake;
  }

  public void updateEnergyTurn(int turn, GameState state) {
    // ATM, the health with a patience coef is not a good result (really not !)
  }

  public void updateEnergyEnd(GameState state) {
    // feature after turn DEPTH, less precise, but more insight
    Feature feature = new Feature();
    feature.calculateFeatures(state);
    energy += feature.applyWeights(weights);
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
  public FastArray<AGAction> getActionsNew() {
    return actions;
  }

  @Override
  public Map<Ship, AGAction[]> getActions() {
    // TODO Auto-generated method stub
    return null;
  }
}
