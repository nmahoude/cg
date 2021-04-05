package weightsOptimizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExternalGameWithExecutorService {

  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("Starting a process for N game");
    
    ExecutorService executorService = Executors.newFixedThreadPool(8);
    
    Set<Callable<String>> callables = new HashSet<Callable<String>>();

    
    String gameCommand = "-cp target\\weightsOptimizer-0.1-SNAPSHOT-jar-with-dependencies.jar weightsOptimizer.OneGame";
    String agent1 = "java -classpath 'D:\\\\Workspace\\\\competitive programming\\\\cg\\\\codingame\\\\multiplayers\\\\lcm\\\\target\\\\classes\\\\' Player";
    String agent2 = "java -classpath 'D:\\\\Workspace\\\\competitive programming\\\\cg\\\\codingame\\\\multiplayers\\\\lcm\\\\target\\\\classes\\\\' Player";
    
    String commandLine = "java "+gameCommand+" -p1 \""+agent1+"\" -p2 \""+agent2+"\"";
    System.out.println("Full command is "+commandLine);

    for (int i=0;i<10;i++) {
//      callables.add(new Callable<String>() {
//        public String call() throws Exception {
//          Process process = Runtime.getRuntime().exec(commandLine);
//          BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
//          process.waitFor();
//          
//          return output.readLine();
//        }
//      });
      
      Process process = Runtime.getRuntime().exec(commandLine);
      BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
      process.waitFor();
      
      System.out.println(output.readLine());
    }

    try {
      
      List<Future<String>> futures = executorService.invokeAll(callables);
 
      executorService.shutdown();
      executorService.awaitTermination(1, TimeUnit.HOURS);
 
      for (Future<String> future : futures) {
        System.out.println("resultat = " + future.get());
      }
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    } catch (ExecutionException ee) {
      ee.printStackTrace();
    }
    
    
    
  }
}
