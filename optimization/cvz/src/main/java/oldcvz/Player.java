package oldcvz;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Player {

  private static long start;

  private static Random random = ThreadLocalRandom.current();

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    GameState state = new GameState();
    Simulation simulation = new Simulation();
    AGSolution solution = new AGSolution();
    while (true) {
      state.read(in);
      start = System.currentTimeMillis();
      solution.setup(state);
      Action bestAction = null;
      double bestScore = -1_000_000_000.0;
      int slice = 360;
      int sim = 0;
      for (; ; ) {
        sim++;
        if ((sim & (1024 - 1)) == 0) {
          if (System.currentTimeMillis() - start > 90)
            break;
        }
        double i = random.nextInt(360);
        int range = Math.max(1000, random.nextInt(2000));
        double x = state.ash.p.x + (int) (range * Math.cos(i * 2 * Math.PI / slice));
        double y = state.ash.p.y + (int) (range * Math.sin(i * 2 * Math.PI / slice));
        Point p = new Point(x, y);
        if (isInLimit(p)) {
          solution.reset();
          Action a = new Action(p);
          simulation.simulate(state, solution, Arrays.asList(a));
          if (solution.energy > bestScore) {
            bestScore = solution.energy;
            bestAction = a;
          }
        }
      }
      System.out.println("" + (int) bestAction.p.x + " " + (int) bestAction.p.y);
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

