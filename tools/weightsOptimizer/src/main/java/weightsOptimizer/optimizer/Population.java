package weightsOptimizer.optimizer;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Population {
  private static final int GAME_COUNT = 5;
  private static Random random = ThreadLocalRandom.current();
  private int populationSize;
  private int weightsCount;

  Individu individus[];
  
  public Population(int populationSize, int weightsCount) {
    this.populationSize = populationSize;
    this.weightsCount = weightsCount;

    individus = new Individu[populationSize];
    for (int i=0;i<populationSize;i++) {
      double[] weights = new double[weightsCount];
      for (int w=0;w<weightsCount;w++) {
        weights[w] = 1.0 - random.nextDouble() * 2.0; 
      }
      individus[i] = new Individu(i, weights);
    }
  }
  
  public void start() throws Exception {
    for (int p1=0;p1<populationSize-1;p1++) {
      for (int p2=p1+1;p2<populationSize;p2++) {
        GameBatch gameBatch = new GameBatch();
        Individu player1 = individus[p1];
        Individu player2 = individus[p2];
        Result result = gameBatch.start(player1, player2, GAME_COUNT, true);
        player1.addResult(player2, result);
        player2.addResult(player1, result.inv());
        System.out.println(""+player1 +" vs "+player2+" => "+result);
      }
    }
    
    Arrays.sort(individus, (p1, p2) -> - Integer.compare(p1.totalWins(), p2.totalWins()));
    for (Individu i : individus) {
      System.out.println(""+i+" wins : "+i.totalWins());
    }
  }
}
