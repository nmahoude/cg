package fantasticBitsMulti;

import java.util.Scanner;

import fantasticBitsMulti.ag.AG;
import fantasticBitsMulti.ag.AGSolution;
import fantasticBitsMulti.simulation.Action;
import fantasticBitsMulti.simulation.Simulation;
import fantasticBitsMulti.units.Pole;
import random.FastRand;
import trigonometry.Point;

public class Player {
  public static final long TIME_LIMIT = 85;

  public static final int MAP_WIDTH  = 16000;
  public static final int MAP_HEIGHT = 7500;
  public static final int MAP_MAX_DISTANCE_2 = MAP_WIDTH * MAP_WIDTH + MAP_HEIGHT * MAP_HEIGHT;
  public static final int MAP_MAX_DISTANCE = (int)Math.sqrt(MAP_MAX_DISTANCE_2);
  
  static final double TO_RAD = Math.PI / 180.0;
  public static final int ANGLES_LENGTH = 36;
  static final double ANGLES[] = new double[]{0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0, 150.0, 160.0, 170.0, 180.0, 190.0, 200.0, 210.0, 220.0, 230.0, 240.0, 250.0, 260.0, 270.0, 280.0, 290.0, 300.0, 310.0, 320.0, 330.0, 340.0, 350.0};
  public static final double E = 0.001;

  public static boolean DEBUG_SIM = false;
  public static double cosAngles[] = new double[ANGLES_LENGTH];
  public static double sinAngles[] = new double[ANGLES_LENGTH];
  
  public static FastRand rand;
  
  public static Point mid = new Point(8000, 3750);
  public static Point myGoal;
  public static Point hisGoal;
  public static Pole[] poles = new Pole[4];
  static {
    poles[0] = new Pole(20, 0, 1750);
    poles[1] = new Pole(21, 0, 5750);
    poles[2] = new Pole(22, 16000, 1750);
    poles[3] = new Pole(23, 16000, 5750);
  }

  public static State state;
  
  
  
  public static long start;
  public static int turn = 0;
  public static int victory;
  
  
  static {
    initConstants();
    rand = new FastRand(73);
  }
  
  
  public static void init(int myTeam) {
    if (myTeam == 0) {
      myGoal = new Point(16000, 3750);
      hisGoal = new Point(0, 3750);
    } else {
      myGoal = new Point(0, 3750);
      hisGoal = new Point(16000, 3750);
    }
    state = new State(myTeam);
  }

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);

    init(in.nextInt());
    
    while (true) {
      state.read(in);
      
      if (turn == 9 || turn == 10) {
        System.err.println("-------------------------------");
        System.err.println("Next turn sim if nothing change");
        Player.DEBUG_SIM = true;
  
        Action action0 = new Action();
        action0.type = Action.TYPE_WAIT;
        action0.angle = 18;
        action0.thrust = 500;
        
        new Simulation().simulate(action0, Action.WAIT, Action.WAIT, Action.WAIT);
        
        System.err.println("My score "+state.teamInfos[0].score);
        System.err.println("Opp score "+state.teamInfos[1].score);
        for (int i=0;i<state.unitsFE;i++) {
          System.err.println(""+state.units[i]);
        }
        Player.DEBUG_SIM = false;
        state.restoreState();
        System.err.println("-------------------------------");
      }
      
      AGSolution solution = AG.evolution();

      Action action0 = solution.actions0[0];
      Action action1 = solution.actions0[1];
      state.wizards[0].output(action0);
      state.wizards[1].output(action0);

      Player.turn += 1;
      state.unitsFE = 10; // 4 poles, 4 wizards & 2 bludgers

    }
  }

  static void updateStartAfter1stRead() {
    start = System.currentTimeMillis();
    if (turn == 0) {
      start += 800;
    }
  }


  private static void initConstants() {
    for (int i = 0; i < ANGLES_LENGTH; ++i) {
      cosAngles[i] = Math.cos(ANGLES[i] * TO_RAD);
      sinAngles[i] = Math.sin(ANGLES[i] * TO_RAD);
    }
  }

}
