package pac.ai;

import java.util.Arrays;

import pac.Player;
import pac.State;
import pac.map.Path;
import pac.map.PathResolver;
import pac.map.Pos;

public class AGPelletOptimizer {

  private static final int POPULATION_SIZE = 100;

  private PathValueCalculator pathValueCalculator = new PathValueCalculator();
  private AGPaths population[] = new AGPaths[POPULATION_SIZE];
  private Pos[][] best = new Pos[5][3]; // current pos @ 0

  private State state;
  
  {
    for (int i=0;i<POPULATION_SIZE;i++) {
      population[i] = new AGPaths();
    }
  }
  
  public Pos[][] optimize(State state) {
    this.state = state;
    
    
    
    pathValueCalculator.init(state, getMaximumDepthAcceptable());
    pathValueCalculator.sort();
    
    initInitialPopulation();
    
    int iteration = 0;
    while (true) {
      if ((iteration & (16-1)) == 0) {
//        System.err.println("AG iter = "+iteration);
//        System.err.println("Actual best is "+population[0].score);
        if (System.currentTimeMillis() - Player.start > 40) {
          break;
        }
      }
      
      buildNextGeneration();
      iteration++;
    }
    
    //System.err.println("AG iterations "+iteration+ " "+(System.currentTimeMillis() - Player.start)+" ms");
    
    for (int i=0;i<5;i++) {
      if (state.pacmen[i].pos == Pos.INVALID ) continue;
      
      best[i][0] = population[0].paths[i].positions[0];
      best[i][1] = population[0].paths[i].positions[1];
      best[i][2] = population[0].paths[i].positions[2];
    }
    return best;
  }
  
  
  
  private void buildNextGeneration() {
    for (int pop=10;pop<POPULATION_SIZE;pop+=2) {
      int index1 = Player.random.nextInt(10);
      int index2 = Player.random.nextInt(10);
      if (index2 == index1) { index2 = Player.random.nextInt(10); }
      if (index2 == index1) { index2 = Player.random.nextInt(10); }

      // crossover
      for (int i=0;i<5;i++) {
        if (state.pacmen[i].pos == Pos.INVALID || state.pacmen[i].isDead()) continue;
        
        if (Player.random.nextBoolean()) {
          Path tmp = population[pop].paths[i];
          population[pop].paths[i] = population[pop+1].paths[i];
          population[pop+1].paths[i] = tmp;
          
        }
      }
      
      // mutation
      for (int i=0;i<5;i++) {
        if (state.pacmen[i].pos == Pos.INVALID || state.pacmen[i].isDead()) continue;
        
        if (Player.random.nextInt(100) < 10) {
          population[pop].paths[i] = randomPathFor(i);
        }
        if (Player.random.nextInt(100) < 10) {
          population[pop+1].paths[i] = randomPathFor(i);
        }
      }
      // rescore
      population[pop].score = PathScorer.scorePaths(state, population[pop].paths, getMaximumDepthAcceptable());
      population[pop+1].score = PathScorer.scorePaths(state, population[pop+1].paths, getMaximumDepthAcceptable());
    }
    
    Arrays.sort(population, (p1, p2) -> Double.compare(p2.score, p1.score));
  }



  private void initInitialPopulation() {
    for (int pop=0;pop<POPULATION_SIZE;pop++) {
      for (int i=0;i<5;i++) {
        if (state.pacmen[i].pos == Pos.INVALID || state.pacmen[i].isDead()) continue;
        population[pop].paths[i] = randomPathFor(i);
      }
      
      population[pop].score = PathScorer.scorePaths(state, population[pop].paths, getMaximumDepthAcceptable());
    }
    Arrays.sort(population, (p1, p2) -> Double.compare(p2.score, p1.score));
  }



  private Path randomPathFor(int i) {
    return pathValueCalculator.bestPaths[i][Player.random.nextInt(pathValueCalculator.bestPathsFE[i])];
  }



  private int getMaximumDepthAcceptable() {
    return Math.min(200-Player.turn, PathResolver.DEPTH);
  }
}
