package ag;

import java.util.Arrays;
import java.util.Comparator;

import stc2.BitBoard;
import stc2.Game;
import stc2.Simulation;

public class AG {
  private static final int POPULATION_COUNT = 100;
  AGSolution bestSolution = new AGSolution();
  int bestsolutionAtGeneration = 0;
  
  AGSolution populations[] = new AGSolution[POPULATION_COUNT];
  AGSolution populations2[] = new AGSolution[POPULATION_COUNT];
  {
    for (int i=0;i<POPULATION_COUNT;i++) {
      populations[i] = new AGSolution();
      populations2[i] = new AGSolution();
    }
  }
  FastRand rand = new FastRand(213135453);
  private int bestKey;
  String info = "";
  private int currentGeneration;
  private int simulatedPopulation;
  private double bestScore;
  private int maxDepth = 8;
  private BitBoard boardModel;
  
  public void simulate(Game game, BitBoard board, int maxDepth, long duration, AGSolution lastBestSolution) {
    this.maxDepth = maxDepth;
    this.boardModel = board;
    randomPopulate(populations);
    if (lastBestSolution != null) {
      populations[0].copyFromLastBestSolution(lastBestSolution);
    }

    bestScore = Double.NEGATIVE_INFINITY;
    currentGeneration = 0;
    simulatedPopulation = 0;
    do {
      scorePopulation(game);
      sortPopulationOnEnergy();
      
      newChampionFightAgainstElder();
      
      mutateAndCrossOverPopulation(bestSolution);
      
    } while (System.nanoTime() - game.nanoStart < duration);

    System.err.println("AG in "+(System.nanoTime() - game.nanoStart)/1_000_000);
    System.err.println("Generations  :"+currentGeneration);
    System.err.println("gen("+bestsolutionAtGeneration+") Better solution with score "+bestSolution.energy+" / pts="+bestSolution.points);
    System.err.println("pop : "+bestSolution);
    //retraceBestSolution(game, bestSolution);
  }

  private void newChampionFightAgainstElder() {
    if (populations[0].energy > bestScore) {
      bestKey = populations[0].keys[0];
      bestScore = populations[0].energy;
      bestSolution.copyFrom(populations[0]);
      bestsolutionAtGeneration = currentGeneration;
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

  private void retraceBestSolution(Game game, AGSolution bestSolution2) {
    Simulation simulation = new Simulation();
    BitBoard board = new BitBoard();
    board.copyFrom(boardModel);
    simulation.board = board;
    
    for (int i=0;i<maxDepth;i++) {
      simulation.clear();
      simulation.putBalls(
          game.nextBalls[i], 
          game.nextBalls2[i], 
          AGSolution.keyToRotation(bestSolution.keys[i]), 
          AGSolution.keyToColumn(bestSolution.keys[i]));
      System.err.println("Put ["+game.nextBalls[i]+","+game.nextBalls2[i]+"]"); 
      System.err.println(board.getDebugString());
      System.err.println("Points : "+simulation.points);
    }
  }

  private void swapPopulations() {
    AGSolution[] temp = populations2;
    populations2 = populations;
    populations = temp;
  }

  final static int eliteCount = 10;
  private void mutateAndCrossOverPopulation(AGSolution champion) {
    replaceLastOfElitePopulationByLastChampion(champion, POPULATION_COUNT);
    
    for (int i=0;i<POPULATION_COUNT;i++) {
      int individu1 = rand.fastRandInt(POPULATION_COUNT);
      for (int p=0;p<3;p++) {
        int pop = rand.fastRandInt(POPULATION_COUNT);
        if (populations[pop].energy > populations[individu1].energy) {
          individu1 = pop;
        }
      }
      int individu2 = rand.fastRandInt(POPULATION_COUNT);
      for (int p=0;p<3;p++) {
        int pop = rand.fastRandInt(POPULATION_COUNT);
        if (populations[pop].energy > populations[individu2].energy) {
          individu2 = pop;
        }
      }
      AGSolution.crossover(populations2[i], 
          populations[individu1], 
          populations[individu2], 10);
    }
    swapPopulations();
  }

  private void replaceLastOfElitePopulationByLastChampion(AGSolution champion, int eliteCount) {
    populations[eliteCount-1].copyFrom(champion); 
    populations[eliteCount-1].resetSolution();
  }

  private void scorePopulation(Game game) {
    currentGeneration++;
    
    Simulation simulation = new Simulation();
    BitBoard board = new BitBoard();
    simulation.board = board;
    
    for (AGSolution population : populations) {
      simulatedPopulation++;
      simulation.clear();
      board.copyFrom(boardModel);
      boolean didOne = false;
      double patiencePoints = 0;
      for (int t=0;t<maxDepth;t++) {
        simulation.clear();
        int rotation = AGSolution.keyToRotation(population.keys[t]);
        int column = AGSolution.keyToColumn(population.keys[t]);
        if (!simulation.putBalls(game.nextBalls[t], game.nextBalls2[t], rotation, column)) {
          break;
        }
        if (simulation.points > 5040) {
          patiencePoints = 1.25*patiencePoints  + 5040*0.9; // malus si on dépasse 5048 points
        } else {
          patiencePoints = 1.25*patiencePoints  + simulation.points;
        }
        didOne = true;
      }
      if (didOne) {
        population.energy = fitness(simulation, patiencePoints);
        population.points = simulation.points;
      }
    }
  }

  private double fitness(Simulation simulation, double patiencePoints) {
    return patiencePoints
        +getSkullsScore(simulation)
        +getColorGroupScore(simulation)
        +getColumnScore(simulation)
        ;
  }

  public int getSkullsScore(Simulation simulation) {
    return 0; //-2*simulation.board.layers[BitBoard.SKULL_LAYER].bitCount();
  }

  public double getColorGroupScore(Simulation simulation) {
        return 
            -10*simulation.groupsCount[1] 
            +10*simulation.groupsCount[2]
            +40*simulation.groupsCount[3];
  }


  public double getColumnScore(Simulation simulation) {
    return 
        -1*simulation.board.getColHeight(0)
        -0*simulation.board.getColHeight(1)
        +1*simulation.board.getColHeight(2)
        +1*simulation.board.getColHeight(3)
        -0*simulation.board.getColHeight(4)
        -1*simulation.board.getColHeight(5);
  }

  
  private void randomPopulate(AGSolution[] populations) {
    for (int i=POPULATION_COUNT;--i>=0;) {
      populations[i].randomize();
    }
  }

  public String output() {
    String info = "gen("+currentGeneration+") popSim("+simulatedPopulation+")";
    return ""+AGSolution.keyToColumn(bestKey)+" "+AGSolution.keyToRotation(bestKey)+" "+info;
  }

  public void feed(AGSolution bestSolution2) {
    
  }

}
