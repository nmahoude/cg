package weightsOptimizer.optimizer;

import java.util.HashMap;
import java.util.Map;

public class Individu {
  double[] weights;
  Map<Individu, Result> results = new HashMap<>();
  private int id;

  public Individu(int id, double[] weights) {
    this.id = id;
    this.weights = weights;
  }

  public String getCommandLine() {
    String weightsAsString="";
    for (int i=0;i<weights.length;i++) {
      weightsAsString+=weights[i]+";";
    }
    return "java -Dweights="+weightsAsString+" -classpath 'D:\\\\Workspace\\\\competitive programming\\\\cg\\\\codingame\\\\multiplayers\\\\lcm\\\\target\\\\classes\\\\' Player ";
  }

 @Override
  public String toString() {
    return "P"+id;
  }

  public void addResult(Individu player2, Result result) {
    results.put(player2, result);
  }

  public int totalWins() {
    return results.values().stream().mapToInt(r -> r.wins).sum();
  } 
}
