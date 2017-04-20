package cotc.tests.agweights;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cotc.ai.AI;
import cotc.ai.DummyAI;
import cotc.ai.ag.AG;
import cotc.tests.Controller;

public class WeightEvaluator {
  static Random rand = new Random(System.currentTimeMillis());

  static AG ai1;
  static AG ai2;
  static List<AI> ais = Arrays.asList(ai1, ai2);

  public static void main(String[] args) {
    WeightEvaluator we = new WeightEvaluator();
    
    while(true) {
      int scores[] = we.oneGame();
      System.out.println("Result : "+scores[0]+" / "+scores[1]);
      if (scores[1] > 100) {
        ai2.standardWeights.output();
      }
      if (scores[1] > scores[0]) {
        System.out.println("Candidate ! ");
        System.out.println("*********************");
        ai2.standardWeights.output();
        System.out.println("*********************");
      }
    }
  }

  private int[] oneGame() {
    ai1 = new AG();
    ai2 = new AG();

    //ai1.weights.output();
    
    ai2.standardWeights.soundValues();
    ai2.standardWeights.mutate();
    //ai2.weights.output();
    
    int scores[] = Controller.doMatches(500, ai1, ai2);
    //System.out.println("team0 : "+scores[0]);
    //System.out.println("team1 : "+scores[1]);
    return scores;
  }
}
