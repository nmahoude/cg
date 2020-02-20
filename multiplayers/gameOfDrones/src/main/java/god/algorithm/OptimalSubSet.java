package god.algorithm;

import java.util.ArrayList;
import java.util.List;

import god.entities.Drone;
import god.entities.Zone;

/**
 * From a list of Zones
 * From a list of Drones
 * 
 * find the best subset repartition to optimize points gained
 * 
 * @author nmahoude
 *
 */
public class OptimalSubSet {

  public List<ZoneInfo> optimize(List<Zone> zones, List<Drone> drones, int turnLeft) {
    List<ZoneInfo> best = null;
    int bestPoints = 0;
    
    for (Zone zone : zones) {
      List<ZoneInfo> infoList = new ArrayList<>();
      ZoneInfo info = new ZoneInfo();
      info.zone = zone;
      
      List<Zone> remainingZones = new ArrayList<>();
      remainingZones.addAll(zones);
      remainingZones.remove(zone);
      
      List<Drone> remainingDrones = new ArrayList<>();
      remainingDrones.addAll(drones);
      
      // affect closest drones to this zone, TODO find better or do it recursively
      int needed = zone.unitsToTake();
      int i=0;
      double longestTTA = 0;
      while (i<needed) {
        Drone drone = zone.getClosest(remainingDrones);
        double thisTTA = Math.ceil(drone.position.distance(zone.position) / 100);
        if (thisTTA > longestTTA) {
          longestTTA = thisTTA;
        }
        remainingDrones.remove(drone);
        info.affectedDrones.add(drone);
        i++;
      }
      info.tta = (int)longestTTA;
      info.points = (turnLeft*turnLeft - info.tta*info.tta) / 2;
      
      infoList.add(info);

      if (remainingZones.isEmpty()) {
        return infoList;
      } else {
        List<ZoneInfo> bestSub = optimize(remainingZones, remainingDrones, turnLeft);
        infoList.addAll(bestSub);
        // compare bestSub points + my points !
        if (best == null) {
          best = infoList;
        } else {
          int points = 0;
          for (ZoneInfo zi : infoList) {
            points+=zi.points;
          }
          if (points > bestPoints) {
            bestPoints = points;
            best = infoList;
          }
        }
      }
    }
    return best;
  }
  
}
