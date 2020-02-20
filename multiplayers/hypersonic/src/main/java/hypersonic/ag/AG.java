package hypersonic.ag;

import java.util.Arrays;
import java.util.Comparator;

import hypersonic.Board;
import hypersonic.Move;
import hypersonic.Simulation;
import random.FastRand;

public class AG {
  private static final int POPULATION_COUNT = 200;
  AGSolution bestSolution = new AGSolution();
  
  AGSolution populations[] = new AGSolution[POPULATION_COUNT];
  AGSolution populations2[] = new AGSolution[POPULATION_COUNT];
  {
    for (int i=0;i<POPULATION_COUNT;i++) {
      populations[i] = new AGSolution();
      populations2[i] = new AGSolution();
    }
  }
  FastRand rand = new FastRand(213135453);
  private int generations;
  private double bestScore;
  private Board originalBoard;
  private int bestGen;
  
  public void simulate(long start, Board board, AGSolution lastBestSolution) {
    originalBoard = board;
    
    bestScore = Double.NEGATIVE_INFINITY;
    generations = 0;
    generationInitialPopulation();
    do {
      playPopulation();
      sortPopulationOnEnergy();
      
      newChampionFightAgainstElder();
      
      mutateAndCrossOverPopulation(bestSolution);
      
    } while (System.nanoTime() - start < 90_000_000);
    System.err.println("AG-generations : "+generations);
    System.err.println("AG-best from "+bestGen+" generation");
  }

  private void generationInitialPopulation() {
  }

  private void mutateAndCrossOverPopulation(AGSolution bestSolution2) {
    for (int i=0;i<POPULATION_COUNT;i++) {
      int individu1 = rand.fastRandInt(POPULATION_COUNT);
      for (int p=0;p<3;p++) {
        int pop = rand.fastRandInt(POPULATION_COUNT);
        if (populations[pop].energy > populations[individu1].energy) {
          individu1 = pop;
        }
      }
      AGSolution.mutate(populations2[i], populations[individu1]);
    }
    swapPopulations();
  }
  
  private void swapPopulations() {
    AGSolution[] temp = populations2;
    populations2 = populations;
    populations = temp;
  }

  private void randomPopulate(AGSolution[] populations3) {
    for (int i=0;i<POPULATION_COUNT;i++) {
      populations[i].reset();
    }
  }

  private void playPopulation() {
    generations++;
    //System.err.println("New generation  : "+generations);
    
    Simulation simulation = new Simulation();
    for (int i=0;i<POPULATION_COUNT;i++) {
      simulation.board = originalBoard.duplicate();
      populations[i].play(simulation);
    }
  }

  private void sortPopulationOnEnergy() {
    Arrays.sort(populations, new Comparator<AGSolution>() {
      @Override
      public int compare(AGSolution o1, AGSolution o2) {
        return Double.compare(o2.energy,o1.energy);
      }
    });
  }
  
  private void newChampionFightAgainstElder() {
    if (populations[0].energy > bestScore) {
      bestGen = generations;
      bestScore = populations[0].energy;
      bestSolution.copyFrom(populations[0]);
    }
  }

  public Move findNextBestMove() {
    return bestSolution.keys[0];
  }
}
