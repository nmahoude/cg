package csb.ai.ag;

import java.util.Arrays;
import java.util.Comparator;

import csb.GameState;
import csb.Player;
import csb.ai.AI;
import csb.ai.AISolution;
import csb.game.PhysicsEngine;
import trigonometry.Point;

public class AGAI implements AI {

  private GameState state;
  private PhysicsEngine engine = new PhysicsEngine();

  public static final int POPULATION = 100;
  public static final int SURVIVOR_POP_SIZE = 20;
  public AGSolution[] population = new AGSolution[POPULATION];
  AGSolution best;
  AGEvaluator evaluator = new AGEvaluator(state);

  private long stopTime;

  public AGAI(long stopTime) {
    this.stopTime = stopTime;
  }

  @Override
  public AISolution evolve() {
    // setup physics engine
    engine.pods = state.pods;
    engine.checkPoints = state.checkPoints;

    // 1st step : simple random evolution, fixed iterations
    best = new AGSolution(state);
    createPopulation(state);

    initFirstGeneration();
    int generations = 1;
    while (System.currentTimeMillis() < stopTime) {
      nextPopulation();
      generations++;
    }

    System.err.printf("Final best is %.3f with %d generations\n",best.energy,generations);
    return best;
  }

  /** all random **/
  private void initFirstGeneration() {
    AGSolution localBest = null;

    for (int i = 0; i < POPULATION; i++) {
      population[i].randomize();
      simulate(population[i]);
      if (localBest == null || population[i].energy > localBest.energy) {
        localBest = population[i];
      }
    }
    best.copyFrom(localBest);
  }

  public void nextPopulation() {
    int pop=SURVIVOR_POP_SIZE;
    
    sortPopulation(population);
    
    AGSolution localBest = best;
    while(pop < POPULATION && System.currentTimeMillis() < stopTime) {
      int firstIndex = findIndex(population, SURVIVOR_POP_SIZE, -1);
      int secondIndex = findIndex(population, SURVIVOR_POP_SIZE, firstIndex);
      
      AGSolution solution1 = population[pop++];
      AGSolution solution2 = population[pop++];
      solution1.clear();
      solution2.clear();
      
      AGSolution.crossOver(solution1, solution2, population[firstIndex], population[secondIndex]);
      solution1.mutate();
      solution2.mutate();
      simulate(solution1);
      if (solution1.energy > localBest.energy) {
        localBest = solution1;
      }
      simulate(solution2);
      if (solution2.energy > localBest.energy) {
        localBest = solution2;
      }
    }
    if (localBest.energy > best.energy) {
      best.copyFrom(localBest);
    }
  }

  static void sortPopulation(AGSolution[] population) {
    Arrays.sort(population, new Comparator<AGSolution>() {
      @Override
      public int compare(AGSolution o2, AGSolution o1) {
        return Double.compare(o1.energy, o2.energy);
      }
    });
  }
  private static int findIndex(AGSolution[] pool, int max, int otherThanIndex) {
    int aIndex, bIndex;
    do {
      aIndex = Player.rand.nextInt(max);
    } while (aIndex == otherThanIndex);

    do {
      bIndex = Player.rand.nextInt(max);
    } while (bIndex == aIndex || bIndex == otherThanIndex);

    return pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
  }
  
  private void simulate(AGSolution sol) {
    evaluator.clear();
    for (int i = 0; i < AGSolution.DEPTH; i++) {
      state.pods[0].apply(sol.getAngle(i), sol.getThrust(i));
      state.pods[1].apply(sol.getAngle(AGSolution.DEPTH + i), sol.getThrust(AGSolution.DEPTH + i));
      engine.simulate();
      
      evaluator.evaluate(sol, i);
    }
    sol.finalPosition0 = new Point(state.pods[0].x, state.pods[0].y);
    sol.finalPosition1 = new Point(state.pods[1].x, state.pods[1].y);
    state.restore();
  }

  @Override
  public void setState(GameState state) {
    this.state = state;
    evaluator = new AGEvaluator(state);
  }

  private void createPopulation(GameState state) {
    for (int i = 0; i < POPULATION; i++) {
      population[i] = new AGSolution(state);
    }
  }
}
