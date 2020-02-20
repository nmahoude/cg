package thales;

import java.util.Random;
import java.util.Scanner;

import thales.ai.AGSolution;
import thales.ai.AI;
import thales.ai.TestEvaluator;

public class Player {
  public static final double WIDTH = 10000;
  public static final double HEIGHT = 8000;

  public static boolean DEBUG_AG = true;
  public static boolean DEBUG_SIMULATION = false;
  public static boolean DEBUG_OUTPUT = true;
  
  public static int turn = 0;
  public static Team teams[] = new Team[2];

  public static Random rand = new Random(0);

  private static long start;
  
  public static Entity entities[] = new Entity[6];
  static {
    teams[0] = new Team();
    teams[1] = new Team();
    
    teams[0].create(teams[1]);
    teams[1].create(teams[0]);
    
    int id = 0;
    entities[id++] = teams[0].ufos[0];
    entities[id++] = teams[0].ufos[1];
    entities[id++] = teams[1].ufos[0];
    entities[id++] = teams[1].ufos[1];
    entities[id++] = teams[0].flag;
    entities[id++] = teams[1].flag;
  }

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    AI ai = new AI();
  
    // game loop
    while (true) {
      readWorld(in);
      
      
      AGSolution evolve = ai.evolve(start + 30);
      
      String message1 = " 1- ("+(teams[0].ufos[0] == TestEvaluator.attacker ? "ATT" : "DEF")+") ";
      String message2 = " 2- ("+(teams[0].ufos[1] == TestEvaluator.attacker ? "ATT" : "DEF")+") ";

      
      if (Player.DEBUG_SIMULATION) {
          restore();
          ai.simulate(evolve, 0);

          // evolve.debug();
          debugSimulation();

          restore(); // DONT REMOVE IT
      }
      String[] output = evolve.output();
      
      if (output[0].contains("BOOST")) {
        teams[0].ufos[0].prepareBoostTimer();
        message1 += "BOOST";
      }
      if (output[1].contains("BOOST")) {
        teams[0].ufos[1].prepareBoostTimer();
        message2 += "BOOST";
      }
      
      System.out.println(output[0] + message1);
      System.out.println(output[1] + message2);
    }
  }

  private static void debugSimulation() {
    System.err.println("WAITED VALUES : ");
    for (int i=0;i<2;i++) {
      UFO ufo = teams[0].ufos[i];
      System.err.print("ufo "+i+": ");
      ufo.debug();
    }
    System.err.println("flags : ");
    System.err.print("flag 0: ");
    teams[0].flag.debug();
    System.err.print("flag 1: ");
    teams[1].flag.debug();

  }

  public static void readWorld(Scanner in) {
    
    teams[0].flag.update(in.nextInt(), in.nextInt());
    teams[1].flag.update(in.nextInt(), in.nextInt());
    start = System.currentTimeMillis();
    
    updateUFOs();
    
    if (turn == 0) {
      teams[0].init();
      teams[1].init();
    }
    turn++;
    
    for (int i = 0; i < 2; i++) {
      int x = in.nextInt();
      int y = in.nextInt();
      int vx = in.nextInt();
      int vy = in.nextInt();
      int flag = in.nextInt();
      teams[0].ufos[i].update(x, y, vx, vy, flag == 1);
    }

    for (int i = 0; i < 2; i++) {
      int x = in.nextInt();
      int y = in.nextInt();
      int vx = in.nextInt();
      int vy = in.nextInt();
      int flag = in.nextInt();
      teams[1].ufos[i].update(x, y, vx, vy, flag == 1);
    }
    
    backup();
  }

  public static void backup() {
    teams[0].backup();
    teams[1].backup();
  }
  
  public static void restore() {
    teams[0].restore();
    teams[1].restore();
  }
  
  public static void updateUFOs() {
    for (int i=0;i<4;i++) {
      ((UFO)Player.entities[i]).decreaseBoostTimer();
    }
  }

  public static void initTeamRegularDirection() {
    teams[0].flag.x = 1000;
    teams[1].flag.x = 9000;
    
    teams[0].init();
    teams[1].init();
  }
}
