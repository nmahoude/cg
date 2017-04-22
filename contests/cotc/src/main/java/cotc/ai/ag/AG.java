package cotc.ai.ag;

import java.util.ArrayList;
import java.util.List;

import cotc.GameState;
import cotc.ai.AI;
import cotc.ai.AISolution;
import cotc.entities.Action;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.FastArray;

public class AG implements AI {
  private static AGSolution fake = AGSolution.createFake();

  protected GameState state;
  protected Simulation simulation;
  public FeatureWeight weights = new FeatureWeight();

  
  // Possible moves at turns 0 for each ships (the same for all simulations)
  public List<FastArray<Action> > turn0PossibleActions = new ArrayList<>();
  

  public AISolution evolve(long stopTime) {
    updateChampions();
    int simulations = 0;
    simulation = new Simulation(state);
    // Pre analyse
    StateAnalyser analyser = new StateAnalyser();
    analyser.analyse(state);
    
    createPossibleActionsAtTurn0();
    
    updateWeightsForEndGame();
    
    AGSolution best = fake;
    int bestGeneration = 0;
    while (System.currentTimeMillis() < stopTime) {
      AGSolution sol = new AGSolution(state, weights);
      sol.randomize(state, analyser, turn0PossibleActions);
      
      simulation.simulateNew(sol);
      simulations++;
      if (sol.energy > best.energy) {
        best = sol;
        bestGeneration = simulations;
      }
    }
    System.err.println("Simulations "+simulations+ " at "+bestGeneration+" with "+best.energy);
    return best;
  }

  private void updateWeightsForEndGame() {
    // end game is when there is no barrels, check the scores & the max ship energy
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
          updateWeightsForWinningState();
        } else {
          updateWeightsForLosingState();
        }
      } else {
        updateWeightsForStandardState();
      }
  }

  private void updateWeightsForStandardState() {
    // don't do anything
  }

  private void updateWeightsForLosingState() {
    // don't do anything 
    //TODO find something interesting
  }

  private void updateWeightsForWinningState() {
    weights.weights[Feature.DISTANCE_TO_ALL_ENEMY_FEATURE] = 1.0;
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

  
  private void updateChampions() {
    Ship best = null;
    int bestHealth = 0;
    for (int s=0;s<state.teams[0].ships.FE;s++) {
      Ship ship = state.teams[0].ships.elements[s];
      ship.champion = false;
      if (ship.health > bestHealth) {
        bestHealth = ship.health;
        best = ship;
      }
    }
    best.champion = true;
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
      AGSolution sol = new AGSolution(state,weights);
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
