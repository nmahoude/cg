package ag;

import java.util.Arrays;
import java.util.Comparator;

import stc2.BitBoard;
import stc2.Game;
import stc2.Simulation;

public class AG {
  private static final int POPULATION_COUNT = 100;
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
  private int bestKey;
  String info = "";
  private int generations;
  private int simulatedPopulation;
  
  public void simulate(Game game, AGSolution lastBestSolution) {
    randomPopulate(populations);
    if (lastBestSolution != null) {
      populations[0].copyFromLastBestSolution(lastBestSolution);
    }

    double bestScore = Double.NEGATIVE_INFINITY;
    generations = 0;
    simulatedPopulation = 0;
    do {
      generatePopulation(game);

      Arrays.sort(populations, new Comparator<AGSolution>() {
        @Override
        public int compare(AGSolution o1, AGSolution o2) {
          return Double.compare(o2.energy,o1.energy);
        }
      });
      
//      if (populations[0].points > 420*4) {
//        System.err.println("Find population with enough points "+populations[0].points);
//        bestKey = populations[0].keys[0];
//        bestScore = populations[0].score;
//        return;
//      }
      if (populations[0].energy > bestScore) {
        bestKey = populations[0].keys[0];
        bestScore = populations[0].energy;
        bestSolution.copyFrom(populations[0]);
      }
      
      mutateAndCrossOver();
      
    } while (System.nanoTime() - game.nanoStart < 95_000_000);

    System.err.println("AG in "+(System.nanoTime() - game.nanoStart)/1_000_000);
    System.err.println("gen("+generations+") Better pop with score "+bestSolution.energy+" / pts="+bestSolution.points);
    System.err.println("pop : "+bestSolution);
    //retraceBestSolution(game, bestSolution);
  }

  private void retraceBestSolution(Game game, AGSolution bestSolution2) {
    Simulation simulation = new Simulation();
    BitBoard board = new BitBoard();
    board.copyFrom(game.myBoard);
    simulation.board = board;
    
    for (int i=0;i<8;i++) {
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

  private void mutateAndCrossOver() {
    for (int i=0;i<50;i++) {
      AGSolution.mutate(populations2[i], populations[i % 10]);
    }
    for (int i=51;i<POPULATION_COUNT;i++) {
      AGSolution.crossover(populations2[i], populations[rand.fastRandInt(10)], populations[rand.fastRandInt(10)]);
    }
    swapPopulations();
  }


  private void generatePopulation(Game game) {
    generations++;
    
    Simulation simulation = new Simulation();
    BitBoard board = new BitBoard();
    simulation.board = board;
    
    for (AGSolution population : populations) {
      simulatedPopulation++;
      simulation.clear();
      board.copyFrom(game.myBoard);
      boolean didOne = false;
      double patiencePoints = 0;
      for (int t=0;t<8;t++) {
        simulation.clear();
        int rotation = AGSolution.keyToRotation(population.keys[t]);
        int column = AGSolution.keyToColumn(population.keys[t]);
        if (!simulation.putBalls(game.nextBalls[t], game.nextBalls2[t], rotation, column)) {
          break;
        }
        patiencePoints = 1.25*patiencePoints  + simulation.points;
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
    String info = "gen("+generations+") popSim("+simulatedPopulation+")";
    return ""+AGSolution.keyToColumn(bestKey)+" "+AGSolution.keyToRotation(bestKey)+" "+info;
  }

  public void feed(AGSolution bestSolution2) {
    
  }

}
