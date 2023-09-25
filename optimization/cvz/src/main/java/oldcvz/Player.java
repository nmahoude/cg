package oldcvz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Player {

  private static long start;

  private static Random random = ThreadLocalRandom.current();

  
  public static void main(String args[]) {
    List<Point> actions = new ArrayList<>();
    actions.add(new Point(0,0));
    actions.add(new Point(0,0));
    actions.add(new Point(0,0));

    
    Scanner in = new Scanner(System.in);
    State state = new State();
    Simulation simulation = new Simulation();
    AGSolution solution = new AGSolution();
    while (true) {
      state.read(in);
      state.debugDistances();
      
      start = System.currentTimeMillis();
      solution.setup(state);
      Point bestAction = new Point(0,0);
      double bestScore = -1_000_000_000.0;
      Point tmp = new Point(0,0);
      
      
      // for each human, test straight line
      Point directAction = actions.get(0);
      List<Point> directActions = Arrays.asList(directAction);
      for (Human h : state.humans) {
        if (h.dead) continue;
        
        state.restore();
        double dist = state.humans[0].p.distTo(state.ash.p);
        directAction.x = state.ash.p.x + Simulation.ASH_MOVE * (state.humans[0].p.x - state.ash.p.x) / dist; //state.ash.p.x;
        directAction.y= state.ash.p.y + Simulation.ASH_MOVE * (state.humans[0].p.y - state.ash.p.y) / dist; //state.ash.p.x;
        
        solution.reset();
        simulation.simulate(state, solution, directActions);
        if (solution.energy > bestScore) {
          bestScore = solution.energy;
          bestAction.x = directAction.x;
          bestAction.y = directAction.y;
          
          
          System.err.println("New best (direct human), Ash @"+bestAction + " dist = "+state.b_ash.p.distTo(bestAction));
          System.err.println("Actions : "+directActions);
          System.err.println("nrj: "+solution.energy+", score : "+simulation.score+" DZ: "+(state.b_aliveZombies - state.aliveZombies));
          System.err.println("H: "+state.aliveHumans+" Z:"+state.aliveZombies);
          System.err.println("@ " + (int) bestAction.x + " " + (int) bestAction.y);
          System.err.println("HvsZ : "+state.aliveHumans +" / " +state.aliveZombies);
        }
        
      }
      
      
      int slice = 360;
      int sim = 0;
      for (; ; ) {
        state.restore();
        
        sim++;
        if ((sim & (1024 - 1)) == 0) {
          if (System.currentTimeMillis() - start > 90)
            break;
        }
        
        Point current = state.ash.p;
        for (int r=0;r<3;r++) {
          double i = random.nextInt(360);
          int range = Math.min(1000, random.nextInt(2000));
          double x = current.x + (int) (range * Math.cos(i * 2 * Math.PI / slice));
          double y = current.y + (int) (range * Math.sin(i * 2 * Math.PI / slice));

          tmp.x = x;
          tmp.y = y;
          if (r > 3 || !isInLimit(tmp)) {
            tmp.x = current.x;
            tmp.y = current.y;
          }
          current = actions.get(r);
          current.x = tmp.x;
          current.y = tmp.y;
        }
      
        
        solution.reset();
        simulation.simulate(state, solution, actions);
        if (solution.energy > bestScore) {
          bestScore = solution.energy;
          bestAction.x = actions.get(0).x;
          bestAction.y = actions.get(0).y;
          
          
          System.err.println("New best, Ash @"+bestAction +" dist = "+state.b_ash.p.distTo(bestAction));
          System.err.println("Actions : "+actions);
          System.err.println("nrj: "+solution.energy+", score : "+simulation.score+" DZ: "+(state.b_aliveZombies - state.aliveZombies));
          System.err.println("H: "+state.aliveHumans+" Z:"+state.aliveZombies);
          System.err.println("@ " + (int) bestAction.x + " " + (int) bestAction.y);
          System.err.println("HvsZ : "+state.aliveHumans +" / " +state.aliveZombies);
        }
      }
      
      // replay best action
      if (true) {
        System.err.println("Sims : "+sim);
        state.restore();
        solution.reset();
        simulation.enableDebug();
        simulation.simulate(state, solution, Arrays.asList(bestAction));
        simulation.resetDebug();
        
        System.err.println("** Result of bestAction simulation **");
        System.err.println("Action : "+bestAction);
        System.err.println("Ash : "+state.ash.p);

        System.err.println("Humans: ");
        for (Human h:  state.humans) {
          if (h.dead) continue;
          System.err.println("  h:"+h.id+" "+h.p);
        }
        
        System.err.println("Zombies: ");
        for (Zombie z:  state.zombies) {
          if (z.dead) continue;
          System.err.println("  z:"+z.id+" "+z.p);
        }
      
      }
      
      System.out.println("" + (int) bestAction.x + " " + (int) bestAction.y);
    }
  }

  private static boolean isInLimit(Point p) {
    if (p.x < 0 || p.y < 0) {
      return false;
    }
    if (p.x > 16000 || p.y > 9000) {
      return false;
    }
    return true;
  }
}

