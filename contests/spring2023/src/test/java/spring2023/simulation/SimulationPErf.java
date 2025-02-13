package spring2023.simulation;

import java.util.Random;

import spring2023.State;
import spring2023.StateTest;
import spring2023.map.Cell;

public class SimulationPErf {

  
  
  public static void main(String[] args) {
    State state = StateTest.buildFromInput("""
        *** INIT ***
^61
^ 0 0 1 3 -1 2 4 -1 
^ 0 0 5 -1 3 0 -1 14 0 0 0 -1 13 6 -1 4 0 0 -1 7 9 -1 0 1 0 0 -1 0 2 -1 8 10 1 0 15 17 -1 1 14 28 1 0 2 13 27 16 18 -1 2 0 -1 19 21 9 3 -1 2 0 10 4 -1 -1 20 22 2 0 7 21 23 11 -1 3 2 0 12 -1 4 8 22 24 
^ 0 0 9 23 25 -1 13 -1 0 0 -1 14 -1 10 24 26 0 0 -1 11 -1 27 6 2 0 0 28 5 1 -1 12 -1 0 0 -1 29 17 5 28 40 0 0 6 27 39 -1 30 18 1 0 29 31 -1 -1 5 15 1 0 -1 6 16 30 32 -1 0 0 33 -1 -1 21 7 -1 0 0 22 8 -1 34 -1 -1 
^ 0 0 19 -1 -1 23 9 7 0 0 24 10 8 20 -1 -1 0 0 21 -1 -1 25 11 9 0 0 26 12 10 22 -1 -1 0 0 23 -1 -1 35 -1 11 0 0 36 -1 12 24 -1 -1 0 0 13 -1 37 39 16 6 0 0 40 15 5 14 -1 38 0 0 43 45 31 17 15 -1 0 0 18 16 -1 44 46 32 
^ 1 0 45 -1 33 -1 17 29 1 0 -1 18 30 46 -1 34 0 0 -1 -1 -1 19 -1 31 0 0 20 -1 32 -1 -1 -1 0 0 25 -1 -1 47 37 -1 0 0 48 38 -1 26 -1 -1 2 0 -1 35 47 -1 39 27 2 0 -1 40 28 -1 36 48 0 0 27 37 -1 49 -1 16 0 0 50 -1 15 28 38 -1 
^ 0 0 51 -1 43 -1 50 60 0 0 -1 49 59 52 -1 44 0 0 -1 53 45 29 -1 41 0 0 30 -1 42 -1 54 46 0 0 53 55 -1 31 29 43 0 0 32 30 44 54 56 -1 0 0 35 -1 -1 57 -1 37 0 0 58 -1 38 36 -1 -1 0 0 39 -1 -1 59 42 -1 0 0 60 41 -1 40 -1 -1 
^ 1 0 -1 -1 -1 41 60 -1 1 0 42 59 -1 -1 -1 -1 0 0 -1 -1 55 45 43 -1 0 0 46 44 -1 -1 -1 56 0 0 -1 -1 -1 -1 45 53 0 0 -1 46 54 -1 -1 -1 0 0 47 -1 -1 -1 -1 -1 0 0 -1 -1 -1 48 -1 -1 0 0 49 -1 -1 -1 52 42 0 0 -1 51 41 50 -1 -1 
^ 
^1
^ 20 19 
*** OPTIONAL ***
^11
*** TURN
^ 0 0 

^ 202000 
^ 105000 102000 100000 100000 7000 102029 3000 200000 18 500006 0 100000 0 100000 0 0 7000 300004 21000 700000 
^ 0 0 0 100000 0 0 0 0 4000 100000 17000 300007 7000 200000 0 0 55 55 0 0 
^ 0 0 2000 0 0 0 0 0 0 0 19 19 0 0 0 0 0 0 0 0 
^ 
*** END
        """);
    
   Random random = new Random();
   
   Simulation sim = new Simulation();

   int maxIterIn100ms = 20000;

   for (int iter=0;iter<1_000;iter++) {
     long start = System.currentTimeMillis();
    for (int i=0;i<maxIterIn100ms;i++) {
       for (Cell c : state.cells) {
         c.beacon = random.nextInt(4);
       }
       
       sim.simulate(state);
       
     }
     long end = System.currentTimeMillis();
     System.err.println(""+(end-start)+"ms");
   }
  }
}
