package csb.ai.ag;

import java.util.Random;

import csb.GameState;
import csb.ai.AI;
import csb.ai.AISolution;
import csb.game.PhysicsEngine;
import trigonometry.Point;

public class AGAI implements AI {
  private static final Random rand= new Random();
  
  private GameState state;
  private PhysicsEngine engine = new PhysicsEngine();
  private AGEvaluator evaluator;
  
  public static final int OCCURENCES = 1000;
  public static final int POPULATION = 20;
  
  public AGSolution[] pool = new AGSolution[POPULATION];
  public AGSolution[] newPool = new AGSolution[POPULATION];
  
  @Override
  public AISolution evolve() {
    // setup physics engine
    engine.pods = state.pods;
    engine.checkPoints = state.checkPoints;
    
    // 1st step : simple random evolution, fixed iterations
    AGSolution best = new AGSolution(state);
    int bestGeneration = 0;
    // 1st population on random
    for (int i=0;i<POPULATION;i++) {
      AGSolution sol = pool[i];
      sol.randomize();
      simulate(sol);
      evaluator.evaluate(sol);
      if (sol.energy > best.energy) {
        best = sol;
      }
      state.restore();
    }
    
    for (int occ=0;occ<OCCURENCES-1;occ++) {
      for (int i=0;i<POPULATION;i++) {
        AGSolution sol = newPool[i];
        merge2Solutions(sol, pool);
        
        simulate(sol);
        evaluator.evaluate(sol);
        if (sol.energy > best.energy) {
          best = sol;
          bestGeneration = occ+1;
        }
        state.restore();
      }
      //System.err.println("Best of generation "+occ+" is "+best.energy);
      swapPools();
    }
    
    //System.err.println("Final best is "+best.energy+" at generation "+bestGeneration);
    return best;
  }

  private void swapPools() {
    AGSolution[] temp = newPool;
    newPool = pool;
    pool = temp;
  }

  private static void merge2Solutions(AGSolution futureChild, AGSolution[] pool) {
    int firstIndex = findIndex(pool, -1);
    int secondIndex = findIndex(pool, firstIndex);

    futureChild.cross(pool[firstIndex], pool[secondIndex]);
    futureChild.mutate();
  }

  private static int findIndex(AGSolution[] pool, int otherThanIndex) {
    int aIndex, bIndex;
    do {
      aIndex = rand.nextInt(POPULATION);
    } while (aIndex == otherThanIndex);

    do {
      bIndex = rand.nextInt(POPULATION);
    } while (bIndex == aIndex && bIndex != otherThanIndex);

    return pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
  }

  private void simulate(AGSolution sol) {
    final int depth = AGSolution.DEPTH / 2;
    for (int i=0;i<depth;i++) {
      state.pods[0].apply(sol.getAngle(i), sol.getThrust(i));
      state.pods[1].apply(sol.getAngle(depth+i), sol.getThrust(depth+i));
      engine.simulate();
    }
    sol.finalPosition0 = new Point(state.pods[0].x,state.pods[0].y);
    sol.finalPosition1 = new Point(state.pods[1].x,state.pods[1].y);
  }

  @Override
  public void setState(GameState state) {
    this.state = state;
    evaluator = new AGEvaluator(state);
    preparePopulations(state);
  }

  private void preparePopulations(GameState state) {
    for (int i=0;i<POPULATION;i++) {
      pool[i] = new AGSolution(state);
      newPool[i] = new AGSolution(state);
    }
  }
}
