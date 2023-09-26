package oldcvz;

import java.util.Arrays;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class StateTest {

  
  @Test
  void loadState() throws Exception {
    String input = """
6362 3917
2
0 0 4500
1 15999 4500
17
0 340 3936 133 4278
1 620 3940 323 4208
2 1163 3887 809 4073
5 7939 7603 7781 7235
6 7956 7582 7796 7215
7 8351 7397 8152 7049
8 9075 7386 8828 7070
9 15152 5431 15421 5135
10 15457 5552 15640 5196
11 15456 3440 15638 3796
12 15152 3568 15421 3864
17 7667 7215 7519 6843
18 7327 7148 7212 6764
19 6715 7130 6671 6732
20 616 5056 319 4787
21 336 5056 129 4713
22 203 5296 104 4908
        """;
    
    State state = new State();
    state.read(new Scanner(input));
    
    
    Simulation sim = new Simulation();
    AGSolution solution = new AGSolution();
    
    
    sim.simulate(state, solution, Arrays.asList(   ));
    //sim.simulate(state, solution, Arrays.asList(P(5366.17248723263,4008.2554919747527)   ));
    //sim.simulate(state, solution, Arrays.asList(P(7788.0,2423.0), P(7339.0,3201.0), P(6383.0,3493.0)));
    
    System.err.println("Next pos : "+sim.nextPos);
    System.err.println("Final pos of ash : "+state.ash.p);
    state.debugDistances();
    System.err.println("Human alive = "+state.aliveHumans);
    System.err.println("Zombie alive = "+state.aliveZombies);
    
  }

  @SuppressWarnings("unused")
  private Point P(double x, double y) {
    return new Point(x, y);
  }
}
