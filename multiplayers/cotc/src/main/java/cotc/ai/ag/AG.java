package cotc.ai.ag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import cgcollections.arrays.FastArray;
import cotc.GameState;
import cotc.Player;
import cotc.ai.AI;
import cotc.ai.AISolution;
import cotc.entities.Action;
import cotc.entities.Ship;
import cotc.game.Simulation;
import cotc.utils.Coord;

public class AG implements AI {
  private static AGSolution fake = AGSolution.createFake();

  protected GameState state;
  protected Simulation simulator;
  public FeatureWeight weights = new FeatureWeight();
  public static GameStatus status = GameStatus.STANDARD;
  
  // Possible moves at turns 0 for each ships (the same for all simulations)
  public List<FastArray<Action> > turn0PossibleActions = new ArrayList<>();
  public static Coord[] possibleTargetsPerShips = new Coord[6];
  public static double[] possibleTargetsPerShipsPercent = new double[6];

  int positionsFE;
  Coord possiblePositions[] = new Coord[256];
  int possiblePositionsValues[] = new int[256];

  public static final int POPULATION = 50;
  public static final int SURVIVOR_POPULATION = 10;
  private AGSolution[] population;
  
  public AISolution evolve(long stopTime) {
    status = GameStatus.STANDARD;
    
    updateChampions();
    int simulations = 0;
    simulator = new Simulation(state);
    
    // Pre analyse
    StateAnalyser analyser = new StateAnalyser();
    analyser.analyse(state);
    
    resetShipsActions();
    for (int s=0;s<state.teams[0].ships.length;s++) {
      Ship ship = state.teams[0].ships.elements[s];
      if (ship.cannonCooldown == 0) { 
        analyseFireTargets(ship);
      }
    }
    createPossibleActionsAtTurn0();
    
    updateWeightsForEndGame();
    
    initGeneration0(analyser);
    
    int bestGeneration = 0;
    while (System.currentTimeMillis() < stopTime) {
      Arrays.sort(population, new Comparator<AGSolution>() {
        @Override
        public int compare(AGSolution o2, AGSolution o1) {
          return Double.compare(o1.energy, o2.energy);
        }
      });

      int pop=SURVIVOR_POPULATION;
      AGSolution bestLocal = fake;
      while (pop<POPULATION) {
        int firstIndex = findIndex(population, SURVIVOR_POPULATION, -1);
        int secondIndex = findIndex(population, SURVIVOR_POPULATION, firstIndex);
        
        AGSolution solution1 = population[pop++];
        AGSolution solution2 = population[pop++];
        solution1.clear();
        solution2.clear();
        
        AGSolution.crossOver(solution1, solution2, population[firstIndex], population[secondIndex]);
        solution1.mutate();
        solution2.mutate();

        simulator.simulateNew(solution1);
        simulations++;
        simulator.simulateNew(solution2);
        simulations++;
      }
    }
    AGSolution best = fake;
    for (int i=0;i<POPULATION;i++) {
      if (population[i].energy > best.energy) {
        best = population[i];
      }
    }
    System.err.println("Simulations "+simulations+ " at "+bestGeneration+" with "+best.energy);
    return best;
  }

  private static int findIndex(AGSolution[] pool, int max, int otherThanIndex) {
    int aIndex, bIndex;
    do {
      aIndex = Player.rand.nextInt(max);
    } while (aIndex == otherThanIndex);

    do {
      bIndex = Player.rand.nextInt(max);
    } while (bIndex == aIndex && bIndex != otherThanIndex);

    return pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
  }
  
  private void initGeneration0(StateAnalyser analyser) {
    population = new AGSolution[POPULATION];
    for (int i=0;i<POPULATION;i++) {
      AGSolution sol = new AGSolution(state, weights);
      population[i] = sol;
      sol.randomize(state, analyser, turn0PossibleActions);
      
      simulator.simulateNew(sol);
    }
  }

  private void resetShipsActions() {
    for (int s=0;s<state.ships.length;s++) {
      Ship ship = state.ships.elements[s];
      ship.action = Action.WAIT;
      ship.target = Coord.ZERO;
    }
  }
  
  
  private void analyseFireTargets(Ship ship) {
    possibleTargetsPerShips[ship.id] = null;
    possibleTargetsPerShipsPercent[ship.id] = 0.0;
    positionsFE = 0;
    
    Action[] actions = new Action[] { Action.WAIT, Action.FASTER, Action.SLOWER, Action.PORT, Action.STARBOARD };

    Ship other = state.getClosestEnnemy(ship);

    // only check if close enough TODO 4 is good ?
    if (ship.position.distanceTo(other.position) < 4) {
      // I wait and enemy do something -> get where it will be
      ship.action = Action.WAIT;

      for (Action action : actions) {
        // eliminate impossible actions
        if (other.speed == 2 && action == Action.FASTER) continue;
        if (other.speed == 0 && action == Action.SLOWER) continue;
        
        other.action = action;
        // simulate one turn
        simulator.playOneTurn();
        
        // update the hasmap
        updatePositionMap(other.position, 2);
        updatePositionMap(other.bow(), 1);
        updatePositionMap(other.stern(), 1);
        state.restore();
      }
      // get the coord wit maximum chance (number of times)
      Coord bestCoord = null;
      int bestCount = 0;
      int totalCount = 0;
      for (int i=0;i<positionsFE;i++) {
        int count = possiblePositionsValues[i];
        totalCount += count;
        if (count > bestCount) {
          bestCoord = possiblePositions[i];
          bestCount = count;
        }
      }
      // if we fire, it is here
      possibleTargetsPerShips[ship.id] = bestCoord;
      possibleTargetsPerShipsPercent[ship.id] = 1.0 * bestCount / totalCount;
    }
  }

  private void updatePositionMap(Coord coord, int times) {
    for (int i=0;i<positionsFE;i++) {
      if (possiblePositions[i] == coord) {
        possiblePositionsValues[i] += times;
        return;
      }
    }
    possiblePositions[positionsFE] = coord;
    possiblePositionsValues[positionsFE++] = times;
  }

  private void updateWeightsForEndGame() {
    // end game is when there is no barrels, check the scores & the max ship energy
      if (state.barrels.length == 0) {
        // win/loss ?

        int survivorHealth_0 = 0;
        int survivorHealth_1 = 0;
        int bestHealth = 0;
        int bestTeam = 0;
        for (int s=0;s<state.ships.length;s++) {
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
          status = GameStatus.ENDGAME_WINNING;
        } else {
          updateWeightsForLosingState();
          status = GameStatus.ENDGAME_LOSING;
        }
      } else {
        status = GameStatus.STANDARD;
        updateWeightsForStandardState();
      }
  }

  private void updateWeightsForStandardState() {
    // don't do anything
  }

  private void updateWeightsForLosingState() {
    // don't do anything 
    //TODO find something interesting
    weights.weights[Feature.DISTANCE_TO_CLOSEST_ENEMY_BOW2_FEATURE] = -1.0;
  }

  private void updateWeightsForWinningState() {
    weights.weights[Feature.DISTANCE_TO_ALL_ENEMY_FEATURE] = 1.0;
    weights.weights[Feature.DISTANCE_TO_CENTER_FEATURE] = 1.0;
  }

  private void createPossibleActionsAtTurn0() {
    turn0PossibleActions.clear();
    for (int s=0;s<state.teams[0].ships.length;s++) {
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
    for (int s=0;s<state.teams[0].ships.length;s++) {
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
    simulator = new Simulation(state);
    // Pre analyse
    StateAnalyser analyser = new StateAnalyser();
    analyser.analyse(state);
    
    resetShipsActions();
    for (int s=0;s<state.teams[0].ships.length;s++) {
      Ship ship = state.teams[0].ships.elements[s];
      if (ship.cannonCooldown == 0) { 
        analyseFireTargets(ship);
      }
    }

    AGSolution best = fake;
    for (int i =0;i<iteration;i++) {
      AGSolution sol = new AGSolution(state,weights);
      createPossibleActionsAtTurn0();

      sol.randomize(state, analyser, turn0PossibleActions);

      simulator.simulateNew(sol);
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
