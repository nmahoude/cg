package thales.ai;

import java.util.Arrays;
import java.util.Comparator;

import thales.Flag;
import thales.Player;
import thales.UFO;
import thales.physics.Simulation;

public class AI {
  Simulation simulation = new Simulation();
  
  public static final int POPULATION = 100;
  public static final int SURVIVOR_POP_SIZE = 20;
  public AGSolution[] population = new AGSolution[POPULATION];
  AGSolution best;
  TestEvaluator evaluator = new TestEvaluator();
  
  public AI() {
  }

    private long stopTime;

    public AGSolution evolve(long stop) {
      this.stopTime = stop;
      
      evaluator.initRound();
      // 1st step : simple random evolution
      best = new AGSolution();
      createPopulation();

      initFirstGeneration();
      int generations = 1;
      while (System.currentTimeMillis() < stopTime) {
        nextPopulation();
        generations++;
      }

      // System.err.printf("Final best is %.3f with %d generations\n",best.energy,generations);
      return best;
    }

    /** all random **/
    private void initFirstGeneration() {
      AGSolution localBest = null;

      for (int p = 0; p < POPULATION; p++) {
        AGSolution sol = population[p];
        
        evaluator.clear();
        for (int depth = 0; depth < AGSolution.DEPTH; depth++) {
          sol.randomize(depth);
          sol.apply(depth);
          dummyOpponent();
          simulation.move();
          
          evaluator.evaluate(sol, depth);
        }
        Player.restore();

        if (localBest == null || population[p].energy > localBest.energy) {
          localBest = population[p];
        }
      }
      best.copyFrom(localBest);
    }

    private void dummyOpponent() {
      UFO attacker = evaluator.hisAttacker;
      if (attacker.flag) {
        attacker.applyTarget(attacker.myTeam.depX, attacker.y, 100);
      } else {
        attacker.applyTarget(evaluator.myFlag.x, evaluator.myFlag.y, 100);
      }
      
      // TODO defense ?
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
    
    public void simulate(AGSolution sol) {
      evaluator.clear();
      for (int i = 0; i < AGSolution.DEPTH; i++) {
        simulate(sol, i);
        
        evaluator.evaluate(sol, i);
      }
      Player.restore();
    }

    public void simulate(AGSolution sol, int i) {
      Player.teams[0].ufos[0].apply(sol.getAngle(i), sol.getThrust(i));
      Player.teams[0].ufos[1].apply(sol.getAngle(AGSolution.DEPTH + i), sol.getThrust(AGSolution.DEPTH + i));
      // TODO dummy for opponents ?
      simulation.move();
    }

    private void createPopulation() {
      for (int i = 0; i < POPULATION; i++) {
        population[i] = new AGSolution();
      }
    }
  }

