package weightsOptimizer;

import java.util.Properties;
import java.util.Random;

import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.simulate.GameResult;

public class WeightsOptimizer {
  static Random random = new Random(System.currentTimeMillis());

  
  
  
  public static void main(String[] args) {
    // 1. start a LOCAM game
    // 2. send weights to agent
    
    double[] weights1 = new double[] { 1.0, 1.0, 1.0, 1.0 ,1.0 ,1.0 ,1.0 ,1.0 };
    double[] weights2 = new double[] { 1.0, 1.0, 1.0, 1.0 ,1.0 ,1.0 ,1.0 ,1.0 };
    
    String weights1AsString = "";
    String weights2AsString = "";
    for (int i=0;i<weights1.length;i++) {
      weights1AsString+=weights1[i]+";";
      weights2AsString+=weights2[i]+";";
    }
    
    Properties gameParameters = new Properties();
    String agent1 = "java -Dweights="+weights1AsString+" -classpath \"d:\\\\Workspace\\\\competitive programming\\\\cg\\\\codingame\\\\multiplayers\\\\lcm\\\\target\\\\classes\\\\\" Player";
    String agent2 = "java -Dweights="+weights2AsString+" -classpath \"D:\\\\Workspace\\\\competitive programming\\\\cg\\\\codingame\\\\multiplayers\\\\lcm\\\\target\\\\classes\\\\\" Player";

    int win0=0,win1=0;
    
    long start = System.currentTimeMillis();
    for (int pass=0;pass<2;pass++) {
      for (int i=0;i<15;i++) {
        System.out.println("Games "+(2*i)+" and "+(2*i+1));
        GameResult result = doOneGame(gameParameters, agent1, agent2);
        System.out.println("Result (1 vs 2) "+result.scores);
        if (result.scores.get(0) == -1 || result.scores.get(1) == -1) {
          System.out.println(result.summaries);
          System.err.println("Error ! ");
          System.err.println(result.errors);
          return;
        }
        if (result.scores.get(0) == 1) win0++;
        if (result.scores.get(1) == 1) win1++;
  
        GameResult resultRetour = doOneGame(gameParameters, agent2, agent1);
        System.out.println("Result (2 vs 1) "+resultRetour.scores);
        if (resultRetour.scores.get(0) == -1 || resultRetour.scores.get(1) == -1) {
          System.out.println(resultRetour.summaries);
          System.err.println("Error ! ");
          System.err.println(resultRetour.errors);
          return;
        }
        if (resultRetour.scores.get(0) == 1) win1++;
        if (resultRetour.scores.get(1) == 1) win0++;
      }
    }
    long end = System.currentTimeMillis();
    System.out.println("Result "+win0+" / "+win1 + " in "+(end-start)+" ms");
  }

  private static GameResult doOneGame(Properties gameParameters, String agent1, String agent2) {
    MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
    gameRunner.setGameParameters(gameParameters);
    gameRunner.setSeed(random.nextLong());
    
    gameRunner.addAgent(agent1);
    gameRunner.addAgent(agent2);

    //set ruleset here
    System.setProperty("league.level", "6");
    
    GameResult result = gameRunner.simulate();
    return result;
  }
}
