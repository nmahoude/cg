package cotc.ai.ag;

import java.util.List;
import java.util.Map;
import java.util.Random;

import cotc.GameState;
import cotc.ai.AISolution;
import cotc.ai.ag.features.Feature;
import cotc.ai.ag.features.FeatureWeight;
import cotc.entities.Action;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;
import cotc.utils.FastArray;

public class AGSolution implements AISolution{
  public final static int AGACTION_SIZE = 1000;
  private static final Action[] ACTION_VALUES = {Action.WAIT, Action.PORT, Action.STARBOARD, Action.SLOWER, Action.FASTER};
  public static int DEPTH = 5;
  public static Random rand = new Random();

  public static double patience[] = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0};
  
  public FastArray<AGAction> actions = new FastArray<AGAction>(AGAction.class, 3 * DEPTH);
  protected int shipCount;
  
  protected GameState state;
  public double energy;
  private FeatureWeight weights;
  public Feature feature = new Feature();
 

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
    String mainStrat = "S";
    if (weights == AG.endGameLosingWeights) {
      mainStrat = "A";
    } else if (weights == AG.endGameWinningWeights) {
      mainStrat = "D";
    }
    
    
    String[] output = new String[shipCount];
    for (int s=0;s<state.teams[0].shipsAlive.FE;s++) {
      output[s] = actions.elements[0 + s*DEPTH].toString()+ " "+mainStrat+" "+weights.shipWeights[s].output();
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
              action = Action.MINE;
            }
          }
          if (action == Action.FIRE) {
            Ship other = shipAnalysis.closestEnemy;
            int distanceToOther = other.position.distanceTo(ship.position);
            int timeToHit = Simulation.travelTimeCache[distanceToOther];
            if (timeToHit <= 2) {
              // target the next position of the ship
              Coord c1 = other.position.neighborsCache[other.orientation];
              Coord c2 = c1.neighborsCache[other.orientation];
              Coord c3 = c2.neighborsCache[other.orientation];
              Coord c4 = c3.neighborsCache[other.orientation];
              if (other.speed == 0 ) {
                if (timeToHit <= 1) {
                  target = other.position;
                } else {
                  target = c1; // it will FASTER to avoir the ball, so we hit the stern
                }
              } else if (other.speed == 1) { 
                target = AOE(c1, 2*timeToHit); 
              } else if (other.speed == 2 ) { 
                action = Action.WAIT; // better opportunities later
              } 
            } else {
              // too far, don't shoot
              action = Action.WAIT;
            }
          }
          actions.elements[i + s*DEPTH].action = action;
          actions.elements[i + s*DEPTH].target = target;
        } else {
          // other turns is more random (but no fire or mine)
          action = ACTION_VALUES[rand.nextInt(ACTION_VALUES.length)];
          actions.elements[i + s*DEPTH].action = action;
          actions.elements[i + s*DEPTH].target = Coord.ZERO;
        }
      }
    }
  }
  
  /** area of effect */
  private Coord AOE(Coord coord, int radius) {
    return Coord.get(
        coord.x + (radius-rand.nextInt(2*radius)),
        coord.y + (radius-rand.nextInt(2*radius))
        );
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
