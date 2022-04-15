package marslander3;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import cgutils.logger.Logger;

public class Player {
    public static Logger inputLogger = Logger.inputLogger();
    
    public static boolean DEBUG_OUTPUT = true;

    // TODO optimization : user ThreadLocalRandom
    public static Random rand = ThreadLocalRandom.current();

    public static long start;

    public static double maxFuel;

    public static int turn = 0;

    public static Mars mars = new Mars();

    public static MarsLander lander = new MarsLander();

    Simulation simulation = new Simulation(mars, lander);

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        new Player().play(in);
    }

    private void play(Scanner in) {
      readGlobal(in);
      
      // game loop
        AG ag = new AG(mars, lander);
        while (true) {

            readTurn(in);
            TrajectoryOptimizer to = new TrajectoryOptimizer();
            to.calculate(mars, lander);
            start = System.currentTimeMillis();
            ag.think(to);
            System.out.println("" + (lander.angle + ag.solutions[0].values[0][0]) + " " + (lander.thrust + ag.solutions[0].values[0][1]));
            ag.prepareNextSolution();
            turn++;
        }
    }

    public void readTurn(Scanner in) {
      lander.readInput(in);
    }

    public void readGlobal(Scanner in) {
      mars.readInput(in);
    }
}
