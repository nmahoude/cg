package weightsOptimizer.optimizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GameBatch {
  private static Random random = ThreadLocalRandom.current();
  int p1Score;
  int p2Score;
  
  
  public Result start(Individu p1, Individu p2, int N, boolean swap) throws Exception {
    String gameCommand = "java -cp target\\weightsOptimizer-0.1-SNAPSHOT-jar-with-dependencies.jar weightsOptimizer.OneGame";
    String agent1 = p1.getCommandLine();
    String agent2 = p2.getCommandLine();
    
    
    System.out.println("Game :"+p1+" vs "+p2);
    System.out.println(agent1);
    System.out.println(agent2);
    for (int i=0;i<N;i++) {
      long seed = Math.abs(random.nextInt());
      //System.out.println("Starting game "+i + " with seed "+seed);
      String result = doGame(gameCommand, agent1, agent2, seed);
      //System.out.println("Result "+result);
      String[] scoresStr = result.split(" ");
      int win1 = Integer.parseInt(scoresStr[0]);
      int win2 = Integer.parseInt(scoresStr[1]);
      p1Score+= win1 == 1 ? 1 : 0;
      p2Score+= win2 == 1 ? 1 : 0;
      
      if (swap) {
//        System.out.println("Starting swap game "+i);
        
        result = doGame(gameCommand, agent2, agent1, seed);
        scoresStr = result.split(" ");
        win1 = Integer.parseInt(scoresStr[0]);
        win2 = Integer.parseInt(scoresStr[1]);
        p2Score+= win1 == 1 ? 1 : 0;
        p1Score+= win2 == 1 ? 1 : 0;
      }
    }
    
    return new Result(p1Score, p2Score);
  }


  private String doGame(String gameCommand, String agent1, String agent2, long seed) throws IOException, InterruptedException {
    String commandLine = gameCommand+" -p1 \""+agent1+"\" -p2 \""+agent2+"\" -seed "+seed;
    Process process = Runtime.getRuntime().exec(commandLine);
    BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
    process.waitFor();
    process.waitFor();
    
    String result = output.readLine();
    if (result == null) {
      System.out.println("Problem with game seed = "+seed);
      System.out.println(gameCommand);
      System.out.println(agent1);
      System.out.println(agent2);
      return "-1 -1";
    }
    return result;
  }
}
