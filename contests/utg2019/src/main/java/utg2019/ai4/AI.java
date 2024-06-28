package utg2019.ai4;

import java.util.Arrays;

import trigonometryInt.Point;
import utg2019.Player;
import utg2019.sim.Action;
import utg2019.world.World;
import utg2019.world.entity.Robot;

public class AI {
  int[] infosFE = new int[5];
  GoalInfo infos[][] = new GoalInfo[5][100];
  Action microActions[][] = new Action[5][100];
    
  double bestUtility;
  GoalInfo bestGoals[] = new GoalInfo[5];
  
  
  public Agent agents[] = new Agent[5];
  GoalInfo seletectedGoals[] = new GoalInfo[5];
  Action selectedMicroActions[] = new Action[5];
  World worlds[] = new World[5];
  private boolean needRadar;
  World world;
  private int[] diggedAt = new int[World.MAX_OFFSET];
  private boolean backupNeedForRadar;
  private int sims;
  
  public void init() {
    for (int i=0;i<5;i++) {
      agents[i] = new Agent(i);
      worlds[i] = new World();
      for (int j=0;j<100;j++) {
        infos[i][j] = new GoalInfo();
      }
    }
  }

  public Action[] think(World currentWorld) {
    sims = 0;
    
    if (Player.DEBUG_AI_CHOICE) {
      System.err.println("Current Goals");
      for (int i=0;i<5;i++) {
        System.err.println("  Robot "+i+" "+agents[i].currentGoal);
      }
    }
    
    this.world = currentWorld;
    for (int i=0;i<5;i++) {
      infosFE[i] = agents[i].getMacroGoals(currentWorld, infos[i]);

      if (Player.DEBUG_AI_CHOICE) {
        System.err.println("Propositions for roboto "+i);
        for (int p=0;p<infosFE[i];p++) {
          System.err.println("   "+infos[i][p].toString());
        }
      }
    }
    
    calculateNeedForRadar(currentWorld);
    Player.debug(Player.DEBUG_AI_CHOICE, "Need Radar ? " + needRadar);
   
    bestUtility = Double.NEGATIVE_INFINITY;
    World parent = prepareWorldForGoalSelection(currentWorld);
    boolean stop = selectGoalRecursive(0, 0.0, parent);
    
    if (Player.DEBUG_AI_CHOICE) {
      System.err.println("Selected Goals : (result was stopped ?? : "+stop+")");
      for (int i=0;i<5;i++) {
        System.err.println("  Robot "+i+" "+bestGoals[i].toString());
      }
    }
    
    selectMicroGoals(currentWorld);
    
    Player.debug(Player.DEBUG_AI_CHOICE, "Simulations : "+sims);
    return selectedMicroActions;
  }

  World prepareWorldForGoalSelection(World currentWorld) {
    World parent = new World();
    parent.copyFrom(currentWorld);
    // copy all potential ore to the world we will use as base
    for (int i=0;i<World.MAX_OFFSET;i++) {
      if (!currentWorld.isCurrentlyKnown(i)) {
        
        if (Player.oracle.potentialOre[i] != 0) {
          if (Player.oracle.potentialOre[i] > 0) {
            parent.setCurrentlyKnown(Point.getFromOffset(i));
            parent.setOre(Point.getFromOffset(i), Player.oracle.potentialOre[i]);
          }
        } 
      }
    }
    return parent;
  }

  private void selectMicroGoals(World currentWorld) {
    World current = worlds[0];
    
    // get micro actions to fullfill robot GOAL
    for (int i=0;i<5;i++) {
      worlds[i].copyFrom(current);
      current = worlds[i];
      GoalInfo robotGoal = bestGoals[i];
      
      agents[i].setSelectedGoal(robotGoal);
      Robot robot = current.teams[0].robots[i];
      robotGoal.applyMacroGoalResult(current, robot);
      
      int actionsFE = agents[i].getMicroActions(currentWorld, microActions[i]);
      
      // trier les actions 
      Arrays.sort(microActions[i],0, actionsFE, 
          (a,b) -> Double.compare(robotGoal.microActionUtility(currentWorld, robot, b), robotGoal.microActionUtility(currentWorld, robot, a)));
    }
    
    // take best actions of each robot 
    // TODO check compatibility (explosionMap)
    for (int i=0;i<5;i++) {
      Robot robot = current.teams[0].robots[i];
      if (robot.isDead()) {
        selectedMicroActions[i] = Action.doWait();
      } else {
        Action chosenAction = microActions[i][0];
        selectedMicroActions[i] = chosenAction != null ? chosenAction : Action.doWait();
        agents[i].setSelectedMicroAction(currentWorld, chosenAction);
      }
    }
    checkTradeOfExplosions(currentWorld);
    
  }

  private void calculateNeedForRadar(World currentWorld) {
    needRadar = true;
    int totalKnownOre = 0;
    for (int i=0;i<World.MAX_OFFSET;i++) {
      if (currentWorld.isCurrentlyKnown(i) && currentWorld.getOre(i)>0) {
        if (Player.trapAdvisor.isDangerous(Point.getFromOffset(i))) {
          // TODO ajouter un phénomene de risque ...
        } else {
          totalKnownOre+= world.getOre(i);
        }
      }
    }
    if (totalKnownOre > 15) {
      needRadar = false;
    } else if (totalKnownOre == 0) {
      needRadar = true;
    } else {
      for (int i=0;i<5;i++) {
        if (currentWorld.teams[0].robots[i].hasRadar()) {
          needRadar = false;
        }
      }
      if (Player.turn > 190) {
        needRadar = false;
      }
    }
  }

  boolean selectGoalRecursive(int robot, double ancestorsUtility, World parent) {
    if (robot == 5) {
      sims++;
      if (ancestorsUtility > bestUtility) {
        
        bestUtility = ancestorsUtility;
        for (int i=0;i<5;i++) {
          bestGoals[i] = seletectedGoals[i];
        }
      }
      if (System.currentTimeMillis() - Player.start > 40) {
        return false; // need to stop 
      }
      return true;
    }
    
    if (robot == 0) {
      backupNeedForRadar = needRadar;
    }
    boolean continu = true;
    for (int i=0;i<infosFE[robot];i++) {
      
      GoalInfo goalInfo = infos[robot][i];
      if (!isCompatible(goalInfo, world.teams[0].robots[robot], robot)) continue;

      worlds[robot].copyFrom(parent);
      double thisUtility = goalInfo.applyMacroGoalResult(worlds[robot], world.teams[0].robots[robot]);
      
      // TODO oups, hack, should be a global representation of the world ...
      if (goalInfo.goal == Goal.PLACE_RADAR) {
        needRadar = false;
      }
      
      seletectedGoals[robot] = goalInfo;
      continu = selectGoalRecursive(robot+1, ancestorsUtility+thisUtility, worlds[robot]);
      if (!continu) return false; // stop
      
      if (robot == 0) {
        needRadar = backupNeedForRadar;
      }
    }
    return continu;
  }

  private boolean isCompatible(GoalInfo goalInfo, Robot robot, int index) {
    if (goalInfo.goal == Goal.PLACE_RADAR) {
      if (needRadar ||  robot.hasRadar()) {
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  private static Point checkTradeOfExplosions(World current) {
    Player.debug(Player.DEBUG_EXPLOSION_DECISION, "    Found "+Player.explosionMap.explosionIdsFE+" explosions " );
    
    Point posToTrigger = Point.Invalid;
    int bestCount = 0; // Don't trade one for one
    for (int explosionId=0;explosionId<Player.explosionMap.explosionIdsFE;explosionId++) {
      int explosionMap[] = Player.explosionMap.explosionsIdsMap[explosionId];
      int explosionPointInMapCount = Player.explosionMap.explosionsIdsMapFE[explosionId];
      
      int myRobotsInExplosion = 0;
      int hisRobotsInExplosion = 0;
      
      for (int p=0;p<explosionPointInMapCount;p++) {
        Point pos = Point.getFromOffset(explosionMap[p]);
        for (int r=0;r<5;r++) {
          myRobotsInExplosion += (current.teams[0].robots[r].pos == pos ? 1 : 0); 
          hisRobotsInExplosion += (current.teams[1].robots[r].pos == pos ? 1 : 0); 
        }
      }
      if (myRobotsInExplosion > 0) {
        int count = hisRobotsInExplosion - myRobotsInExplosion;
        Player.debug(Player.DEBUG_EXPLOSION_DECISION, "    Found explosion "+(char)('A'+explosionId)+" with "+hisRobotsInExplosion+" ennemies... and mine : "+myRobotsInExplosion );
        if (count > 0) {
          posToTrigger = Player.explosionMap.getPointToTrigger(current, explosionId);
          Player.debug(Player.DEBUG_EXPLOSION_DECISION, "    /!\\ Need to trigger!  @ "+posToTrigger); 
        }
      }
    }
    return posToTrigger;
  }
}
