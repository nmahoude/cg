package marsLander;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import marsLander.ai.AG;
import marsLander.sim.Simulation;

public class Player {
  public static boolean DEBUG_OUTPUT = true;

  // TODO optimization : user ThreadLocalRandom
  public static Random rand = ThreadLocalRandom.current();

  public static long start;

  public static double maxFuel;

  public static int turn = 0;
  
  Mars mars = new Mars();
  MarsLander lander = new MarsLander();
  Simulation simulation = new Simulation(mars, lander);
  
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    new Player().play(in);
  }

  private void play(Scanner in) {
    mars.readInput(in);
    
    // game loop
    AG ag = new AG(mars, lander);
    while (true) {
      lander.readInput(in);
      start = System.currentTimeMillis();

      ag.think();
      
      System.out.println(""+(lander.angle + ag.solutions[0].values[0][0])+" "+(lander.thrust + ag.solutions[0].values[0][1]));
      ag.prepareNextSolution();
      turn++;
    }
  }
}
