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
    int simulations = 0;
    simulation = new Simulation(state);
    // Pre analyse
    StateAnalyser analyser = new StateAnalyser();
    analyser.analyse(state);
    analyser.debug();
    
    createPossibleActionsAtTurn0();
    
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
    System.err.println("Simulations "+simulations);
    return best;
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
