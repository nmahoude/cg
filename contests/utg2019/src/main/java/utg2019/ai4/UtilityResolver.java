package utg2019.ai4;

import trigonometryInt.Point;
import utg2019.Player;
import utg2019.RoutePlanner;
import utg2019.world.World;
import utg2019.world.entity.Robot;

public class UtilityResolver {

  public static double utilityToPlaceRadar(World currentWorld, Robot robot, Point target) {
    int eta = RoutePlanner.etaToBeInVicinity(robot.pos, target, robot.hasRadar(), currentWorld.teams[0].radarCooldown);
  
    double pRadarScore = 100.0; // big utility to place radar, TODO maybe not always the case
    pRadarScore += (robot.hasOre() ? 100 : 0);
    pRadarScore += 1.0 * Player.radarOptimizer.density[target.offset] * Player.radarOptimizer.density[target.offset] / (eta + 1);
  
    if (Player.trapAdvisor.isDangerous(target)) {
      // big malus for potentially trap cell
      pRadarScore -= 1000;
    }
    
    return pRadarScore; 
  }

  public static double utilityToDigOn(World currentWorld, Robot robot, Point target) {
    double distToTargetUtility;
    double rOre;
    
    int eta = GoalInfo.etaToDigOnAndComeBackToBase(currentWorld, robot, target);
    
    distToTargetUtility = 1.0 / (eta+1);
    // TODO un tiens vaut mieux que 2 tu l'auras ? quel repartition entre ore potentiel et prêt & plus loin, mais sûr ?
    
    if (currentWorld.isCurrentlyKnown(target)) {
      if (currentWorld.getOre(target) > 0) {
        rOre = 50 + distToTargetUtility;
        if (Player.turn < 180 && Player.trapAdvisor.myPotentialTraps[target.offset] != 0) {
          rOre-=1;// pour le meme ETA, on prefere dig sur ceux qui sont unsafe
        }
      } else {
        rOre = 0; // no utility if we dig nothing
      }
    } else {
      if (Player.oracle.potentialOre[target.offset] != 0) {
        rOre = 25 + /*Player.oracle.potentialOre[target.offset] +*/
                    distToTargetUtility;
      } else {
        rOre = 0;
      }
    }
    if (Player.trapAdvisor.isDangerous(target)) {
      rOre -= 1000; // big malus for potentially trap cell
    }
    // ne pas dig les ores qui sont "cachées"
    if (Player.turn < 100 && Player.trapAdvisor.myPotentialTraps[target.offset] != 0) {
      rOre -=2000;
    }
    return rOre;
  }

}
