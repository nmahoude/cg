package utg2019;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import trigonometryInt.Point;
import utg2019.ai4.AI;
import utg2019.sim.Action;
import utg2019.world.World;
import utg2019.world.entity.Robot;
import utg2019.world.maps.ActionMap;
import utg2019.world.maps.ExplosionMap;
import utg2019.world.maps.Oracle;
import utg2019.world.maps.TrapAdvisor;
import utg2019.world.maps.WallDetector;

public class Player {
  public static Random random = new Random(0);
  public static long start;

  public static boolean DEBUG_INPUT = false;
  public static boolean DEBUG_ORACLE = false;

  public static boolean DEBUG_AI = false;
  public static boolean DEBUG_AI_CHOICE = false;
  public static final boolean DEBUG_AI_CHOICE_BEST_CHANGE = false;
  
  public static boolean DEBUG_POTENTIAL_ORE_MAP = false;
  
  public static boolean DEBUG_POTENTIAL_TRAPS_MAP = false;
  public static boolean DEBUG_POTENTIAL_TRAPS_DANGER_ROBOT = false;
  public static final boolean DEBUG_POTENTIAL_TRAPS_REFLEXION = false;
  public static final boolean DEBUG_POTENTIAL_MY_TRAPS = false;
  
  
  public static boolean DEBUG_ORACLE_DENSITY_MAP = false;
  
  public static final boolean DEBUG_EXPLOSION_MAP = false;
  public static final boolean DEBUG_EXPLOSION_DECISION = false;


  public static final boolean DEBUG_DANGER_ZONE = false;

  public static final int TRAPS_TRIGGER_ZERO_TO_BE_CAUTIOUS_NEG_1_ELSE = 0;
  
  private static World old;
  private static World currentWorld;
  public static int turn = 0;
  
  public static Oracle oracle = new Oracle();
  public static TrapAdvisor trapAdvisor;
  public static ExplosionMap explosionMap;
  public static WallDetector wallDetector;
  private static AI ai = new AI();
  private static Action myPastActions[];

  public static RadarOptimizer radarOptimizer;
  public static boolean finalGambit = false;
  private static World currentWorldBackup;

  static {
    Point.init(30, 15);
  }

  public static void main(String[] args) {
    init();
    
    Scanner in = new Scanner(System.in);
    play(in);
  }

  public static void init() {
    ActionMap.init();
    trapAdvisor = new TrapAdvisor();
    explosionMap = new ExplosionMap();
    wallDetector = new WallDetector();
    old = new World();
    radarOptimizer = new RadarOptimizer();
    myPastActions = new Action[] { Action.doWait(), Action.doWait(), Action.doWait(), Action.doWait(), Action.doWait()};
    currentWorldBackup = new World();
  }

  static void play(Scanner in) {
    ai.init();
    oracle.init();

    readInit(in);
    while(true) {
      readTurn(in);
      
      
      System.err.println("Turn "+turn);
      if (turn > 150 && currentWorld.teams[0].score < currentWorld.teams[1].score) {
        finalGambit = true;
        System.err.println("********* MODE GAMBIT ***********");
      } else if (turn > 500) {
        break;
      }
      
      oracle.preTurn(currentWorld, myPastActions);
      if (old != null && myPastActions != null) {
        if (DEBUG_POTENTIAL_TRAPS_DANGER_ROBOT) {
          trapAdvisor.debugDangerousity();
        }
        
        trapAdvisor.update(old, currentWorld, myPastActions);
        if (DEBUG_POTENTIAL_TRAPS_DANGER_ROBOT) {
          trapAdvisor.debugDangerousity();
        }
        if (DEBUG_POTENTIAL_TRAPS_MAP) {
          trapAdvisor.debugPotentialTraps();
        }
      }
      currentWorldBackup.copyFrom(currentWorld);
      //oracle.removeOreObviouslyDiggedNextTurn(currentWorldBackup);

      radarOptimizer.update(currentWorld, oracle.potentialOre);
      explosionMap.update(currentWorld, trapAdvisor);
      wallDetector.update(trapAdvisor.potentialTraps);


      if (Player.DEBUG_EXPLOSION_MAP) explosionMap.debug();
      // detectDangerousSpot();
      myPastActions = ai.think(currentWorldBackup);

      // prepare for next turn, using the backup
      oracle.prepareForNextTurn(currentWorld, myPastActions);
      saveOldWorld(currentWorld);
      
      System.err.println("Time :"+(System.currentTimeMillis() - start));
      outputActions(myPastActions);
    }
  }
  
  private static void detectDangerousSpot() {
    Map<Integer, Set<Robot> > explosions = new HashMap<>();
    
    // WIP to detect when I take risks (ie : more of my robots in a explosionMap ...)
    for (int i=0;i<5;i++) {
      Robot robot;
      robot = currentWorld.teams[0].robots[i];
      if (!robot.isDead()) {
        addRobotToList(explosions, robot);
      }
      robot = currentWorld.teams[1].robots[i];
      if (!robot.isDead()) {
        addRobotToList(explosions, robot);
      }
      
    }
    
    debug(DEBUG_DANGER_ZONE, "Result : ");
    for (Entry<Integer, Set<Robot>> ex : explosions.entrySet()) {
      Set<Robot> robots = ex.getValue();
      int explosionId = ex.getKey();
      if (DEBUG_DANGER_ZONE) System.err.print("In explosion "+explosionId+" => ");
      int mine = 0, his = 0;
      for (Robot r : robots) {
        if (DEBUG_DANGER_ZONE) System.err.print(r.getId()+", ");
        if (r.owner == Owner.ME) {
          mine++;
        } else {
          his++;
        }
      }
      if (DEBUG_DANGER_ZONE) {
        System.err.println();
        System.err.println("So ... mine is "+mine+" and his is "+his);
        if (mine > his && his > 0) {
          System.err.println("Danger, Will Robinson");
        }
      }
    }
  }

  private static void addRobotToList(Map<Integer, Set<Robot>> explosions, Robot robot) {
    if (explosionMap.explosionPosIdsFE[robot.pos.offset] > 0) {
      if (DEBUG_DANGER_ZONE) {
        System.err.print("*Robot "+ robot.getId()+" in explosions maps : " );
      }
      for (int em=0;em<explosionMap.explosionPosIdsFE[robot.pos.offset];em++) {
        int id = explosionMap.explosionPosIds[robot.pos.offset][em];

        if (DEBUG_DANGER_ZONE) {
          System.err.print(""+id+", ");
        }
        
        Set<Robot> list = explosions.get(id);
        if (list == null) {
          list = new HashSet<>();
          explosions.put(id,  list);
        }
        list.add(robot);
      }
      System.err.println();
    } else {
      if (DEBUG_DANGER_ZONE) {
        System.err.println("Robot "+robot.getId()+" is safe");
      }
    }
  }

  private static void outputActions(Action[] actions) {
    for (int i=0;i<5;i++) {
      System.out.println(actions[i].output() + " "+ai.agents[i].debugString());
    }
  }

  private static void saveOldWorld(World world2) {
    old.copyFrom(currentWorld);
  }

  static void readTurn(Scanner in) {
    turn ++;
    currentWorld.read(in);
    if (turn == 1) {
      start = start +500; // more time on 1st turn
    }
  }

  static void readInit(Scanner in) {
    int width = in.nextInt();
    int height = in.nextInt();
    currentWorld = new World();
  }
  
  public static void debug(boolean debug, String string) {
    if (debug) {
      System.err.println(string);
    }
  }

}
