package weightsOptimizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExternalGame {

  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("Starting a process for N game");
    
    ExecutorService executorService = Executors.newFixedThreadPool(8);
    
    Set<Callable<String>> callables = new HashSet<Callable<String>>();

    
    String gameCommand = "-cp target\\weightsOptimizer-0.1-SNAPSHOT-jar-with-dependencies.jar weightsOptimizer.OneGame";
    String agent1 = "java -classpath 'D:\\\\Workspace\\\\competitive programming\\\\cg\\\\codingame\\\\multiplayers\\\\lcm\\\\target\\\\classes\\\\' Player";
    String agent2 = "java -classpath 'D:\\\\Workspace\\\\competitive programming\\\\cg\\\\codingame\\\\multiplayers\\\\lcm\\\\target\\\\classes\\\\' Player";
    
    String commandLine = "java "+gameCommand+" -p1 \""+agent1+"\" -p2 \""+agent2+"\" -seed 1";
    System.out.println("Full command is "+commandLine);

    for (int i=0;i<50;i++) {
      Process process = Runtime.getRuntime().exec(commandLine);
      BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
      process.waitFor();
      
      String result = output.readLine();
      String[] scoresStr = result.split(" ");
      int p1 = Integer.parseInt(scoresStr[0]);
      int p2 = Integer.parseInt(scoresStr[1]);
      System.out.println("p1 = "+p1+" , p2 = "+p2);
    }
    
  }
}
