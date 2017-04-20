package cotc.ai.ag;

import java.util.ArrayList;
import java.util.List;

import cotc.GameState;
import cotc.ai.AI;
import cotc.ai.AISolution;
import cotc.ai.ag.features.Feature;
import cotc.ai.ag.features.FeatureWeight;
import cotc.ai.ag.features.ShipWeight;
import cotc.entities.Action;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.FastArray;

public class AG implements AI {
  private static AGSolution fake = AGSolution.createFake();

  protected GameState state;
  protected Simulation simulation;
  
  // weights
  public FeatureWeight standardWeights = new FeatureWeight();
  static public FeatureWeight endGameLosingWeights = new FeatureWeight();
  static public FeatureWeight endGameWinningWeights = new FeatureWeight();
  
  static {
    // prepare a endGameLosingWeights
    endGameLosingWeights.weights[Feature.MINE_DROPPED_FEATURE] = 0.5; // drop mine,but less than cannonball
    endGameLosingWeights.weights[Feature.HIS_HEALTH_FEATURE] = 0.0; // we want to fire anywhere
    endGameLosingWeights.weights[Feature.CANNONBALL_FIRED_FEATURE] = 1.0; // fire as much as possible
    endGameLosingWeights.weights[Feature.MINE_COUNT_FEATURE] = -1.0; // try to destroy mines with AOE balls
    endGameLosingWeights.shipWeights[0] = ShipWeight.FULL_ATTACK;
    endGameLosingWeights.shipWeights[1] = ShipWeight.FULL_ATTACK;
    endGameLosingWeights.shipWeights[2] = ShipWeight.FULL_ATTACK;
    
    endGameWinningWeights.weights[Feature.MINE_DROPPED_FEATURE] = -10_000.0; // DONT DROP MINES
    endGameWinningWeights.weights[Feature.HIS_HEALTH_FEATURE] = 0.0; // we want to fire anywhere
    endGameWinningWeights.weights[Feature.CANNONBALL_FIRED_FEATURE] = 0.01; // fire as much as possible
    endGameWinningWeights.weights[Feature.MINE_COUNT_FEATURE] = -1.0; // fire as much as possible
    endGameWinningWeights.shipWeights[0] = ShipWeight.FULL_RETREAT;
    endGameWinningWeights.shipWeights[1] = ShipWeight.FULL_RETREAT;
    endGameWinningWeights.shipWeights[2] = ShipWeight.FULL_RETREAT;
  }
  
  // Possible moves at turns 0 for each ships (the same for all simulations)
  public List<FastArray<Action> > turn0PossibleActions = new ArrayList<>();

  public AISolution evolve(long stopTime) {
    int simulations = 0;
    simulation = new Simulation(state);

    // Pre analyse
    StateAnalyser analyser = new StateAnalyser();
    analyser.analyse(state);
    //analyser.debug();
    
    createPossibleActionsAtTurn0();
    FeatureWeight weights = chooseWeightStrategy();
    
    AGSolution best = fake;
    while (System.currentTimeMillis() < stopTime) {
      AGSolution sol = new AGSolution(state, weights);
      sol.randomize(state, analyser, turn0PossibleActions);
      
      simulation.simulateNew(sol);
      simulations++;
      if (sol.energy > best.energy) {
        best = sol;
      }
    }
    //best.feature.debugFeature(weights);
    System.err.println("Simulations "+simulations + " , bestE= "+best.energy);
    return best;
  }

  private FeatureWeight chooseWeightStrategy() {
      FeatureWeight weights;
      if (state.barrels.FE == 0) {
        // win/loss ?

        int survivorHealth_0 = 0;
        int survivorHealth_1 = 0;
        int bestHealth = 0;
        int bestTeam = 0;
        for (int s=0;s<state.ships.FE;s++) {
          Ship ship = state.ships.elements[s];
          if (ship.health > 200-state.rounds) {
            if (ship.owner == 0) {
              survivorHealth_0 += ship.health;
            } else {
              survivorHealth_1 += ship.health;
            }
          }
          if (bestHealth < ship.health) {
            bestHealth = ship.health;
            bestTeam = ship.owner;
          }
        }

        boolean winning = 
            (bestHealth > 0 && bestTeam == 0)
            || (bestHealth == 0 && survivorHealth_0 > survivorHealth_1);
        if (winning) {
          weights = standardWeights;
        } else {
          weights = endGameLosingWeights;
        }
      } else {
        weights = standardWeights;
      }
      return weights;
  }

  private void createPossibleActionsAtTurn0() {
    turn0PossibleActions.clear();
    for (int s=0;s<state.teams[0].ships.FE;s++) {
      Ship ship = state.teams[0].ships.elements[s];
      FastArray<Action> actions = new FastArray<Action>(Action.class, 20);
      getPossibleActions(actions, state, ship);
      turn0PossibleActions.add(actions);
    }
  }
  
  private void getPossibleActions(FastArray<Action> actions, GameState state, Ship ship) {
    actions.clear();
    actions.add(Action.WAIT);
    if (ship.speed < 2) actions.add(Action.FASTER);
    if (ship.speed > 0) actions.add(Action.SLOWER);
    actions.add(Action.PORT);
    actions.add(Action.STARBOARD);
    if (ship.mineCooldown == 0) actions.add(Action.MINE);
    if (ship.cannonCooldown == 0) actions.add(Action.FIRE);
  }

  
  @Override
  public AISolution evolve() {
    return evolve(100);
  }

  public AISolution evolve(int iteration) {
    simulation = new Simulation(state);
    // Pre analyse
    StateAnalyser analyser = new StateAnalyser();
    analyser.analyse(state);
    
    AGSolution best = fake;
    for (int i =0;i<iteration;i++) {
      AGSolution sol = new AGSolution(state,standardWeights);
      createPossibleActionsAtTurn0();

      sol.randomize(state, analyser, turn0PossibleActions);

      simulation.simulateNew(sol);
      if (sol.energy > best.energy) {
        best = sol;
      }
    }
    return best;
  }
  
  @Override
  public void setState(GameState state) {
    this.state = state;
  }

}
