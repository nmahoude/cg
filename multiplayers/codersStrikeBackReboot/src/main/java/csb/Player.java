package csb;

import java.util.Scanner;

import csb.ai.AG;
import csb.ai.AGParameters;
import csb.ai.Simulation;
import csb.game.PhysicsEngine;
import csb.simulation.AGSolution1;
import trigonometry.Vector;

public class Player {
  private static final Vector DIR_X = new Vector(1,0);
  static Map map = new Map();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int laps = in.nextInt();
    map.readCheckpoints(in);
    int round = 0;
    // game loop
    
    PhysicsEngine engine = new PhysicsEngine();
    engine.pods = map.pods;
    engine.checkPoints = map.checkPoints;
    
    AGParameters parameters = new AGParameters();
    Simulation simulation = new Simulation(engine);
    AG ag = new AG(simulation, parameters);
    
    while (true) {
      round++;

      for (int i = 0; i < 4; i++) {
        int x = in.nextInt(); // x position of your pod
        int y = in.nextInt(); // y position of your pod
        int vx = in.nextInt(); // x speed of your pod
        int vy = in.nextInt(); // y speed of your pod
        int angle = in.nextInt(); // angle of your pod
        int nextCheckPointId = in.nextInt(); // next check point id of your pod
        if (round == 1) {
          // get the angle as it pleases us, it's first turn
          Vector dir = new Vector(map.checkPoints[0].position.x - x, map.checkPoints[0].position.y - y).normalize();
          angle = (int) (180 * Math.signum(dir.ortho().dot(DIR_X)) * Math.acos(dir.dot(DIR_X)) / Math.PI);
        }
        map.pods[i].readInput(x, y, vx, vy, angle, nextCheckPointId);
      }

      AGSolution1 best = AGSolution1.getBest(map.pods, map.checkPoints);
      System.out.println(best.actionOutput(0));
      System.out.println(best.actionOutput(1));
      
      // update scorers (who is the runner / chaser pod ?)
      // simulation.scorer1 = new RunnerScorer();
      // simulation.scorer2 = new ZeroScorer();
      //AGSolution best = ag.evolution(System.nanoTime() + 100_000_000);
      //System.out.println(simulation.actionOutput(best, 0));
      //System.out.println(simulation.actionOutput(best, 1));
    }
  }
}
