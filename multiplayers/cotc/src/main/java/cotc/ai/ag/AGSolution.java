package cotc.ai.ag;

import java.util.List;
import java.util.Map;

import cgcollections.arrays.FastArray;
import cotc.GameState;
import cotc.Player;
import cotc.ai.AISolution;
import cotc.entities.Action;
import cotc.entities.Ship;
import cotc.utils.Coord;

public class AGSolution implements AISolution{
  private static final int MUTATION_RATE = 5;

  public final static int AGACTION_SIZE = 1000;
  private static final Action[] ACTION_VALUES = Action.values();
  public static int DEPTH = 5;

  public static double patience[] = new double[]{ 1.0, 0.8, 0.6, 0.4, 0.2, 0.1};
  
  public FastArray<AGAction> actions = new FastArray<AGAction>(AGAction.class, 3 * DEPTH);
  protected int shipCount;
  
  protected GameState state;
  public double energy;
  private FeatureWeight weights;
  public Feature  feature = new Feature();
 
 

  protected AGSolution() {
    for (int i=0;i<3*DEPTH;i++) {
      actions.add(new AGAction());
    }
  }
  
  public AGSolution(GameState state, FeatureWeight weights) {
    this.state = state;
    this.weights = weights;
    this.shipCount = state.teams[0].shipsAlive.size();
    for (int i=0;i<3*DEPTH;i++) {
      actions.add(new AGAction());
    }
  }

  public void copyFrom(AGSolution from) {
    this.state = from.state;
    this.shipCount = from.shipCount;
    
    this.energy = from.energy;
    for (int i=0;i<3*DEPTH;i++) {
      this.actions.elements[i].copyFrom(from.actions.elements[i]);
    }
  }

  public void clear() {
    energy = 0;
    actions.clear();
  }

  @Override
  public String[] output() {
    String[] output = new String[shipCount];
    for (int s=0;s<state.teams[0].shipsAlive.length;s++) {
      output[s] = actions.elements[0 + s*DEPTH].toString();
    }
    return output;
  }

  public static void crossOver(AGSolution solution1, AGSolution solution2, AGSolution parent1, AGSolution parent2) {
    int cut = Player.rand.nextInt(parent1.shipCount * DEPTH);
    for (int i=0;i<3 * DEPTH;i++) {
      if (i<cut) {
        solution1.actions.elements[i].copyFrom(parent1.actions.elements[i]);
        solution2.actions.elements[i].copyFrom(parent2.actions.elements[i]);
      } else {
        solution1.actions.elements[i].copyFrom(parent2.actions.elements[i]);
        solution2.actions.elements[i].copyFrom(parent1.actions.elements[i]);
      }
    }
  }

  public void mutate() {
    if (Player.rand.nextInt(100) < MUTATION_RATE) {
      int mutateIndex = Player.rand.nextInt(shipCount*DEPTH);
      Action action = ACTION_VALUES[Player.rand.nextInt(ACTION_VALUES.length)];
      if (action == Action.FIRE) {
        action = Action.WAIT;
      }
      actions.elements[mutateIndex].action = action;
      actions.elements[mutateIndex].target = Coord.ZERO;
      
    }
  }

  public void randomize(GameState state, StateAnalyser analyser, List<FastArray<Action>> turn0PossibleActions) {
    weights.weights[Feature.HIS_DELTA_HEALTH_FEATURE] = 0.0; // don't care about its health
    for (int s=0;s<state.teams[0].shipsAlive.length;s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];
      ShipStateAnalysis shipAnalysis = analyser.analyse.get(ship);
      FastArray<Action> possibleActions = turn0PossibleActions.get(s);
      
      for (int i=0;i<DEPTH;i++) {
        Action action;
      
        if (i == 0) {
          action = possibleActions.elements[Player.rand.nextInt(possibleActions.length)];
          Coord target = Coord.ZERO;
          
          if (action == Action.MINE) {
            if (shipAnalysis.enemyAtStern[0] == true) {
              action = Action.MINE;
              weights.weights[Feature.HIS_DELTA_HEALTH_FEATURE] = -1.0; // don't care about its health
            } else if (shipAnalysis.enemyAtStern[1] == true && shipAnalysis.closestEnemy.speed == 2) {
              weights.weights[Feature.HIS_DELTA_HEALTH_FEATURE] = -0.5; // don't care about its health
              action = Action.MINE;
            } else {
              if (weights.weights[Feature.MINE_DROPPED_FEATURE] > 0) {
                action = Action.MINE;
              } else {
                action = Action.WAIT;
              }
            }
          }
          if (action == Action.FIRE) {
            target = AG.possibleTargetsPerShips[ship.id];
            if (target != null && 
                // don't fire if it's the endgame and we are winning unless there is 40%+ chance to hit
                (AG.status != GameStatus.ENDGAME_WINNING || AG.possibleTargetsPerShipsPercent[ship.id] > 0.4)) {
              weights.weights[Feature.CANNONBALL_FIRED_FEATURE] = 1.0;// 1 cannonball equivalent 5 pts de vie ?
            } else {
              action = Action.WAIT;
              target = Coord.ZERO;
            }
          }
          
          actions.elements[i + s*DEPTH].action = action;
          actions.elements[i + s*DEPTH].target = target;
        } else {
          // other turns is more random
          action = ACTION_VALUES[Player.rand.nextInt(ACTION_VALUES.length)];
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
    for (int s=0;s<state.teams[0].shipsAlive.length;s++) {
      Ship ship = state.teams[0].shipsAlive.elements[s];
      
      // greedy health
      energy += (ship.health-ship.b_health);
      // greddy speed
      energy += Feature.speeds [ship.speed];

      Ship closestShip = state.getClosestEnnemy(ship);
      if (closestShip != null) {
        feature.features[Feature.DISTANCE_TO_CLOSEST_ENEMY_BOW2_FEATURE] += patience[turn] * closestShip.bow().neighborsCache[closestShip.orientation].distanceTo(ship.bow());
      }

      
      // don't go at ship stern-1 to avoid mines
      if (turn < 2) {
        double coeff = turn == 0 ? 1.0 : 0.3;
        for (int s2=0;s2<state.teams[1].shipsAlive.length;s2++) {
          Ship other = state.teams[1].shipsAlive.elements[s2];
          if (other.health <= 0 || ship.mineCooldown > 0) continue;
          if (ship.at(other.stern().neighborsCache[(other.orientation + 3) %6])) {
            // not good !
            energy -= 10 * coeff;
          }
        }
      }
    }
  }

  public void updateEnergyEnd(GameState state) {
    feature.calculateFeaturesFinal(state);
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
