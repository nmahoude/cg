package utg2019.ai4;

import java.util.Arrays;
import java.util.PriorityQueue;

import trigonometryInt.Point;
import utg2019.Player;
import utg2019.RadarOptimizer;
import utg2019.sim.Action;
import utg2019.world.MapCell;
import utg2019.world.World;
import utg2019.world.entity.Robot;

public class Agent {

  final int robotIndex;
  
  // 
  Robot robot;
  private int infosFE;

  GoalInfo currentGoal = new GoalInfo();

  private World world;
  
  
  Agent(int robotIndex) {
    this.robotIndex = robotIndex;
  }
  /**
   * return macro tasks
   * @param currentWorld
   * @param goalInfos
   * @return
   */
  public int getMacroGoals(World currentWorld, GoalInfo[] goalInfos) {
    this.world = currentWorld;
    robot = world.teams[0].robots[robotIndex];
    
    int goalsFE = 0;
    if (robot.isDead()) {
      goalInfos[goalsFE++].update(Goal.WAIT, Point.Invalid, 0, 0.0);
      return goalsFE;
    }
    
    // Step 1 : only one cell away explosions
    Point bestToTrigger = triggerTrapForGoodTrade(currentWorld);
    if (bestToTrigger != Point.Invalid) {
      System.err.println("TIME TO TRIGGERRRRRR on " + bestToTrigger);
      goalInfos[goalsFE++].update(Goal.DIG, bestToTrigger, 0, 10_000);
      goalsFE = placeBestRadar(currentWorld, goalInfos, goalsFE);
      return goalsFE;
    }
    if (robot.hasOre()) {
        // only one solution
        goalInfos[goalsFE++].update(Goal.MOVE_TO_BASE, Point.Invalid, 0, 1.0);
        goalsFE = placeBestRadar(currentWorld, goalInfos, goalsFE);
        return goalsFE;
    } else {
      int currentGoalsFE = goalsFE;
      if (Player.turn > 3) {
        goalsFE = copyOldGoal(currentWorld, goalInfos, goalsFE++);
        //goalsFE = digAroundIfInteressant(currentWorld, goalInfos, goalsFE);
        goalsFE = digknownOres(currentWorld, goalInfos, goalsFE);
        goalsFE = digPastOreCellsOrRandom(currentWorld, goalInfos, goalsFE);
        if (goalsFE == currentGoalsFE) {
          // no movement provided
          goalsFE = digRandomCells(currentWorld, goalInfos, goalsFE);
        }
        goalsFE = followRadar(currentWorld, goalInfos, goalsFE);
      } else {
        if (Player.turn == 1) {
          goalInfos[goalsFE++].update(Goal.WAIT, Point.Invalid, 0, 0.0);
        } else if (Player.turn == 2 ) {
          goalInfos[goalsFE++].update(Goal.DIG, Point.get(5, robot.pos.y), 0, 0.0);
        } else if (Player.turn == 3 ) {
          goalInfos[goalsFE++].update(Goal.DIG, Point.get(9, robot.pos.y), 0, 0.0);
        }
      }
      goalsFE = placeBestRadar(currentWorld, goalInfos, goalsFE);
      if (currentGoalsFE == goalsFE) {
        // avoid NPE ...
        goalInfos[goalsFE++].update(Goal.WAIT, Point.Invalid, 0, 0);
      }
    }

    Arrays.sort(goalInfos, 0, goalsFE, (o1, o2) -> Double.compare(o2.utility, o1.utility));
    return goalsFE;
  }
  
  private int followRadar(World currentWorld, GoalInfo[] goalInfos, int goalsFE) {
    goalInfos[goalsFE++].createFollowRadar(currentWorld, robot);
    
    return goalsFE;
  }
  
  
  private int digAroundIfInteressant(World currentWorld, GoalInfo[] goalInfos, int goalsFE) {
    for (MapCell mc : World.mapCells[robot.pos.offset].neighborsAndSelf) {
        if (mc.pos.x > 3 && Player.oracle.potentialOre[mc.pos.offset] != 0) {
            goalInfos[goalsFE].createDig(currentWorld, robot, mc.pos);
        } else {
        }
    }
    return goalsFE;
  }
  
  private int digRandomCells(World currentWorld, GoalInfo[] goalInfos, int goalsFE) {
      for (int i = 0; i < 3; i++) {
          goalInfos[goalsFE++].createDigRandom(currentWorld, robot);
      }
      return goalsFE;
  }

  private Point triggerTrapForGoodTrade(World currentWorld) {
    Robot robot = currentWorld.teams[0].robots[robotIndex];
    
    int best = Player.TRAPS_TRIGGER_ZERO_TO_BE_CAUTIOUS_NEG_1_ELSE;
    if (robot.pos.x < 3) {
      best = -1; // take the even trades against WALLs
    }
    Point bestToTrigger= Point.Invalid;
    for (MapCell mc : World.mapCells[robot.pos.offset].neighborsAndSelf) {
      if (! Player.trapAdvisor.isDangerous(mc.pos)) continue;
      
      int myKilled = 0, hisKilled = 0;
      Player.debug(Player.DEBUG_EXPLOSION_DECISION, "  try cell "+mc.pos);
      // count the robots in the neighbor of the cell mc
      for (MapCell mc2 : World.mapCells[mc.pos.offset].neighborsAndSelf) {
        for (int i=0;i<5;i++) {
          Robot mine = currentWorld.teams[0].robots[i];
          if (! mine.isDead() && mine.pos == mc2.pos) {
            Player.debug(Player.DEBUG_EXPLOSION_DECISION, "     Killing #"+i+" of mine @ "+mine.pos);
            myKilled++;
          }
          Robot his = currentWorld.teams[1].robots[i];
          if (! his.isDead() && his.pos == mc2.pos) {
            Player.debug(Player.DEBUG_EXPLOSION_DECISION, "     Killing #"+i+" of his @ "+his.pos);
            hisKilled++;
          }
        }
      }
      int score = hisKilled - myKilled;
      if (score > best) {
        Player.debug(Player.DEBUG_EXPLOSION_DECISION, " => Find a way to explode "+hisKilled+" vs me :"+myKilled);
        best = score;
        bestToTrigger = mc.pos;
      }
    }
    return bestToTrigger;
  }
  
  private int digPastOreCellsOrRandom(World currentWorld, GoalInfo[] goalInfos, int goalsFE) {
    PriorityQueue<DigLocation> bestDigLocations = new PriorityQueue<>();

    for (int y = 0; y < 15; y++) {
      for (int x = 0; x < 30; x++) {
        Point pos = Point.get(x, y);
        if (x == 0) {
          continue;
        }
        // we only want potential (no known, no unknown, just past with ore)
        if (currentWorld.isCurrentlyKnown(pos.offset) 
            || Player.oracle.potentialOre[pos.offset] <= 0) continue;
        
        bestDigLocations.add(new DigLocation(pos, UtilityResolver.utilityToDigOn(currentWorld, robot, pos)));
      }
    }
    
    int past = 0;
    for (int i=0;i<5 && !bestDigLocations.isEmpty();i++) {
      DigLocation dl = bestDigLocations.poll();
      if (dl.score <=0.000001) break; // don't dig random, not our role :)
      goalInfos[goalsFE++].createDig(world, robot, dl.pos);
      past++;
    }
    
    // finish with unknownrandoms if not enough past
    for (;past<5;past++) {
      goalInfos[goalsFE].createDigRandom(currentWorld, robot);
      if (Player.oracle.potentialOre[goalInfos[goalsFE].target.offset] == -1) {
        // true unknwon
        goalsFE++;
      }
    }
    
    
    return goalsFE;
  }
  
  
  private int placeRandomRadar(World currentWorld, GoalInfo[] goalInfos, int goalsFE) {
    for (int i=0;i<5;i++) {
      Point target = Point.get(Player.random.nextInt(29)+1, Player.random.nextInt(15));
      goalInfos[goalsFE++].createPlaceRadar(target, currentWorld, robot);
    }
    return goalsFE;
  }

  private int placeBestRadarBasedOnScore(World currentWorld, GoalInfo[] goalInfos, int goalsFE) {
    Point target = Point.Invalid;
    double best = 0;
    for (int y = 0; y < 15; y++) {
      for (int x = 0; x < 30; x++) {
        Point p = Point.get(x, y);
        double score = Player.radarOptimizer.getRadarScore(currentWorld, Player.oracle.potentialOre, p);
        if (score > best) {
          best = score;
          target = p;
        }
      }
    }
    goalInfos[goalsFE++].createPlaceRadar(target, currentWorld, robot);
    return goalsFE;
  }
  
  private int placeBestRadar(World currentWorld, GoalInfo[] goalInfos, int goalsFE) {
    Point optimizerSpot = new RadarOptimizer().findSpot2(robot, currentWorld);
    if (optimizerSpot != Point.Invalid 
        && Player.trapAdvisor.isDangerous(optimizerSpot) == false) {
      goalInfos[goalsFE++].createPlaceRadar(optimizerSpot, currentWorld, robot);
    } else {
      double bestScore = Double.NEGATIVE_INFINITY;
      Point bestTarget = Point.Invalid;
      for (int x = 4; x < 30; x++) {
        for (int y = 0; y < 15; y++) {
          double score = 0.0;
          Point target = Point.get(x, y);

          score = UtilityResolver.utilityToPlaceRadar(currentWorld, robot, target);
          score -= 0.5 * x;
          
          if (target.x < 4) {
            score -= 100;
          }
          if (score > bestScore) {
            bestScore = score;
            bestTarget = target;
          }
        }
      }
      if (bestTarget != Point.Invalid) {
        goalInfos[goalsFE++].createPlaceRadar(bestTarget, currentWorld, robot);
      }
    }
    return goalsFE;
  }

  private int copyOldGoal(World currentWorld, GoalInfo[] goalInfos, int goalsFE) {
    if (currentGoal.goal != Goal.WAIT) {
      goalInfos[goalsFE].copyFrom(currentGoal);
      goalInfos[goalsFE++].updateUtility(currentWorld, robot);
    }
    return goalsFE;
  }
  
  public void setSelectedGoal(GoalInfo goalInfo) {
    currentGoal.copyFrom(goalInfo);
  }
  public int getMicroActions(World currentWorld, Action actions[]) {
    return currentGoal.getMicroActions(currentWorld, robot, actions);
  }
  
  public void setSelectedMicroAction(World currentWorld, Action chosenAction) {
    if (currentGoal.isFinishingAction(currentWorld, robot, chosenAction)) {
      currentGoal.update(Goal.WAIT, Point.Invalid, 0, -1.0);
    }
  }

  static class DigLocation implements Comparable<DigLocation>{
    Point pos;
    double score;
    
    
    public DigLocation(Point pos, double score) {
      super();
      this.pos = pos;
      this.score = score;
    }


    @Override
    public int compareTo(DigLocation other) {
      return Double.compare(other.score, score);
    }

  }
  
  private int digknownOres(World currentWorld, GoalInfo[] goalInfos, int goalsFE) {
    PriorityQueue<DigLocation> bestDigLocations = new PriorityQueue<>();

    for (int y = 0; y < 15; y++) {
      for (int x = 0; x < 30; x++) {
        Point pos = Point.get(x, y);
        if (x == 0) {
          continue;
        }
        if (!currentWorld.isCurrentlyKnown(pos.offset) 
            || currentWorld.getOre(pos.offset)==0) continue;
        
        bestDigLocations.add(new DigLocation(pos, UtilityResolver.utilityToDigOn(currentWorld, robot, pos)));
      }
    }
    
    for (int i=0;i<5 && !bestDigLocations.isEmpty();i++) {
      DigLocation dl = bestDigLocations.poll();
      if (dl.score > 0.0001 || Player.finalGambit) {
        goalInfos[goalsFE++].createDig(world, robot, dl.pos);
      }
    }
    return goalsFE;
  }
  public String debugString() {
    return ""; //+currentGoal.goal+" E:"+currentGoal.eta;
  }

}
