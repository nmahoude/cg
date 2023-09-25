package oldcvz;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class StateTest {

  
  @Test
  void loadState() throws Exception {
    String input = """
9321 3245
2
0 3647 384
2 2391 1601
8
0 3685 385 3647 384
1 5022 404 4622 398
5 11578 1742 11245 1963
16 60 3262 385 3029
17 2363 3422 2369 3022
23 52 3998 331 3711
24 2275 3918 2295 3518
25 3279 5241 3184 4852
        """;
    
    State state = new State();
    state.read(new Scanner(input));
    
    
    Simulation sim = new Simulation();
    AGSolution solution = new AGSolution();
    
    Point action = new Point(0,0);
    List<Point> actions = Arrays.asList(action);

    double dist = state.humans[0].p.distTo(state.ash.p);
    System.err.println("Current dist : "+dist);
    
    //action.p.x = state.ash.p.x + ASH_MOVE * (state.humans[0].p.x - state.ash.p.x) / dist; //state.ash.p.x;
    //action.p.y= state.ash.p.y + ASH_MOVE * (state.humans[0].p.y - state.ash.p.y) / dist; //state.ash.p.x;

    action = new Point(7788.0,2423.0);
    
    
    double dist2 = state.humans[0].p.distTo(action);
    System.err.println("New dist : "+dist2);

    sim.simulate(state, solution, Arrays.asList(P(8427,2793)));
    //sim.simulate(state, solution, Arrays.asList(P(7788.0,2423.0), P(7339.0,3201.0), P(6383.0,3493.0)));
    
    System.err.println("Human alive = "+state.aliveHumans);
    System.err.println("Zombie alive = "+state.aliveZombies);
    
  }

  private Point P(double x, double y) {
    return new Point(x, y);
  }
}
