package weightsOptimizer;

import org.junit.jupiter.api.Test;

public class OneGameTest {

  
  @Test
  public void manuelGame() throws Exception {
    String gameCommand = "-cp target\\\\weightsOptimizer-0.1-SNAPSHOT-jar-with-dependencies.jar weightsOptimizer.OneGame";
    String agent1 = "java -classpath 'd:\\\\Workspace\\\\competitive programming\\\\cg\\\\codingame\\\\multiplayers\\\\lcm\\\\target\\\\classes\\\\' Player";
    String agent2 = "java -classpath \"D:\\\\Workspace\\\\competitive programming\\\\cg\\\\codingame\\\\multiplayers\\\\lcm\\\\target\\\\classes\\\\\" Player";

    for (int i=0;i<1;i++) {
      String result = new OneGame().doOneGame(agent1, agent2, 0);
      System.out.println(result);
    }
  }
}
