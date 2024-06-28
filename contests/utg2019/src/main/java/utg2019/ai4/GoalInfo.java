package utg2019.ai4;

import trigonometryInt.Point;
import utg2019.Player;
import utg2019.RoutePlanner;
import utg2019.sim.Action;
import utg2019.sim.Item;
import utg2019.sim.Order;
import utg2019.world.MapCell;
import utg2019.world.World;
import utg2019.world.entity.Robot;

public class GoalInfo {
  
  Goal goal = Goal.WAIT;
  int eta;
  double utility;
  Point target = Point.Invalid;
  
  public GoalInfo() {
  }

  @Override
  public String toString() {
    return String.format("%s, %f %s eta:%d",goal.toString(), utility, target.toString(), eta);
  }
  
  public void update(Goal goal, Point target, int eta, double utility) {
    this.goal = goal;
    this.eta = eta;
    this.utility = utility;
    this.target = target;
  }

  public int getMicroActions(World current, Robot robot, Action[] actions) {
    int actionsFE = 0;
    
    switch (goal) {
    case DIG_RAND:
    case DIG:
    case FOLLOW_RADAR:
      actionsFE = actionsDig(current, robot, actions, actionsFE);
      return actionsFE;
    case PLACE_RADAR:
      if (robot.hasRadar()) {
        actionsFE = actionsDig(current, robot, actions, actionsFE);
      } else if (robot.pos.x == 0) {
        actions[actionsFE++] = Action.request(Item.RADAR); 
      } else {
        actionsFE = actionsToMoveToBase2(robot, target, actions, actionsFE); 
      }
      return actionsFE;
    case MOVE_TO_BASE:
      Point next = Point.Invalid;
      actionsFE = actionsToMoveToBase2(robot, next, actions, actionsFE);
      return actionsFE;
    case WAIT:
    default:
      actions[0] = Action.doWait();
      return 0;
    }
  }

  private Point getNextPotentialTarget(World currentWorld, Robot robot) {
    Point target = Point.Invalid;
    int best = Integer.MAX_VALUE;
    for (int y = 0; y < 15; y++) {
      for (int x = 0; x < 30; x++) {
        Point pos = Point.get(x, y);
        if (x == 0) {
          continue;
        }
        if (!currentWorld.isCurrentlyKnown(pos.offset)) continue;
        if (robot.pos.distance(pos) < best) {
          best = robot.pos.distance(pos);
          target = pos;
        }
      }
    }
    return target;
  }

  static int actionsToMoveToBase2(Robot robot, Point nextTarget, Action actions[], int actionsFE) {
    int coef = 1; // savoir si il est important de viser la prochaine case ...
    if (nextTarget == Point.Invalid) {
      nextTarget = Point.get(0, robot.pos.y);
      coef = 0; // renvoit toutes les possibilités
    }
    int actionsFEBackup = actionsFE;
    int bestScore = Integer.MAX_VALUE;
    for (MapCell mc : World.mapCells[robot.pos.offset].neighborsRadius4) {
      int score = mc.pos.x + coef * Math.abs(mc.pos.y - nextTarget.y);
      if (mc.pos.x == 0) {
        // on base, mega score et on peut regarder si on peut se rapprocher de la prochaine target!
        score -= 1000 ; 
      }
      if (score < bestScore) {
        bestScore = score;
        actionsFE = actionsFEBackup; // reset actions, we found a better one
        actions[actionsFE++] = Action.move(mc.pos);
      } else if (score == bestScore) {
        // add one action, same score
        actions[actionsFE++] = Action.move(mc.pos);
      }
    }

    return actionsFE;
  }
  
  private int actionsDig(World current, Robot robot, Action actions[], int actionsFE) {
    if (robot.pos.distance(target) <=1 ) {
      // TODO what of danger ?
      actions[actionsFE++] = Action.dig(target);
    } else {
      actionsFE = actionsToDigOnTarget(robot, target, actions, actionsFE);
    }
    return actionsFE;
  }

  static int actionsToDigOnTarget(Robot robot, Point nextTarget, Action[] actions, int actionsFE) {
    int actionsFEBackup = actionsFE;
    int bestScore = Integer.MAX_VALUE;
    for (MapCell mc : World.mapCells[robot.pos.offset].neighborsRadius4) {
      int score = mc.pos.distance(nextTarget);
      if (score == 1) score = 0; // being at 1 cell is sufficiant to dig
      if (score < bestScore) {
        bestScore = score;
        actionsFE = actionsFEBackup; // reset actions, we found a better one
        actions[actionsFE++] = Action.move(mc.pos);
      } else if (score == bestScore) {
        // add one action, same score
        actions[actionsFE++] = Action.move(mc.pos);
      }
    }      
    return actionsFE;
  }

  public void copyFrom(GoalInfo model) {
    this.goal = model.goal;
    this.eta = model.eta;
    this.utility = model.utility;
    this.target = model.target;
  }

  /** is the next actions finishing the goal ? */
  public boolean isFinishingAction(World currentWorld, Robot robot, Action chosenAction) {
    switch (goal) {
    case DIG_RAND:
    case DIG:
    case FOLLOW_RADAR:
      return chosenAction.order == Order.DIG;
    case PLACE_RADAR:
      return robot.hasRadar() && chosenAction.order == Order.DIG;
    case MOVE_TO_BASE:
      return chosenAction.order == Order.MOVE && chosenAction.pos.x == 0;
    case WAIT:
    default:
      return true;
    }
  }

  public void createDigRandom(World world, Robot robot) {
    this.goal = Goal.DIG_RAND;
    this.target = Point.get(Player.random.nextInt(29)+1, Player.random.nextInt(15));
    updateUtility(world, robot);
  }

  static Point[] listForRandomRadars = new Point[50];
  public void createFollowRadar(World world, Robot robot) {
    int lfrrFE = 0;
    Point nextTargetForRadar = Player.radarOptimizer.findSpot2(robot, world);
    if (nextTargetForRadar == Point.Invalid) {
      this.goal = Goal.WAIT;
      this.utility = Double.NEGATIVE_INFINITY;
      return;
    }
    
    
    MapCell mc = World.mapCells[nextTargetForRadar.offset];
    for (MapCell n : mc.neighborsRadius4) {
      if (Player.trapAdvisor.isDangerous(n.pos)) continue;
      if (Player.oracle.potentialOre[n.pos.offset] == 0) continue;
      listForRandomRadars[lfrrFE++] = n.pos;
    }
    
    this.goal = Goal.FOLLOW_RADAR;
    if (lfrrFE > 0) {
      this.target = listForRandomRadars[Player.random.nextInt(lfrrFE)];
    } else {
      this.target = nextTargetForRadar;
    }
    updateUtility(world, robot);
  }


  public void createPlaceRadar(Point target, World world, Robot robot) {
    this.goal = Goal.PLACE_RADAR;
    this.target = target;
    updateUtility(world, robot);
  }

  public void createDig(World world, Robot robot, Point target) {
    this.goal = Goal.DIG;
    this.target = target;
    updateUtility(world, robot);
  }

  public void updateUtility(World currentWorld, Robot robot) {
    utility = calculateCurrentUtility(currentWorld, robot);
  }

  private double calculateCurrentUtility(World currentWorld, Robot robot) {
    switch (goal) {
    case DIG:
      eta = etaToDigOnAndComeBackToBase(currentWorld, robot, target);
      return UtilityResolver.utilityToDigOn(currentWorld, robot, target);
    case FOLLOW_RADAR:
      eta = etaToDigOnAndComeBackToBase(currentWorld, robot, target);
      return 0.5 * UtilityResolver.utilityToDigOn(currentWorld, robot, target);
    case DIG_RAND:
      eta = etaToDigOnAndComeBackToBase(currentWorld, robot, target);
      return 0.1 * UtilityResolver.utilityToDigOn(currentWorld, robot, target);
    case MOVE_TO_BASE:
      eta = RoutePlanner.etaToBeOn(robot.pos, target);
      return 100.0;
    case PLACE_RADAR:
      eta = RoutePlanner.etaToBeInVicinity(robot.pos, target, robot.hasRadar(), currentWorld.teams[0].radarCooldown);
      return UtilityResolver.utilityToPlaceRadar(currentWorld, robot, target);
    case WAIT:
    default:
      return -1.0;
    }
  }

  static int etaToDigOnAndComeBackToBase(World currentWorld, Robot robot, Point target) {
    return RoutePlanner.etaToBeInVicinity(robot.pos, target, true, -1)
        + RoutePlanner.etaToBeOn(target, Point.get(0, robot.pos.y));
  }
  /**
   * Apply the result of the goal when it will be finished
   * Allow robots to know which ores will be dig eventually ...
   */
  public double applyMacroGoalResult(World world, Robot robot) {
    double currentUtility = calculateCurrentUtility(world, robot);
    if (goal == Goal.DIG || goal == Goal.DIG_RAND) {
      if (world.isCurrentlyKnown(target)) {
        world.setOre(target, Math.max(world.getOre(target)-1, 0));
        world.setHole(target);
        world.teams[0].score++;
      }
    }
    return currentUtility;
  }

  public double microActionUtility(World current, Robot robot, Action action) {
    switch(goal) {
    case DIG:
    case DIG_RAND:
    case FOLLOW_RADAR:
      return 1000 * (1 - RoutePlanner.etaToBeInVicinity(robot.pos, target, true, 0))
            - action.pos.x // prefer position that are near the QG
            - 0.1 * (Math.abs(target.y - 7)) // prefer position near the middle
          ;
    case PLACE_RADAR:
      return 1 - RoutePlanner.etaToBeInVicinity(robot.pos, target, robot.hasRadar(), current.teams[0].radarCooldown);
    case MOVE_TO_BASE:
      return 100 * action.pos.x - Math.abs(action.pos.y - robot.pos.y);
    case WAIT:
    default:
      return 0.0;
    }
  }

}
