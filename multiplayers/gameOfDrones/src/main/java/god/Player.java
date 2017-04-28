package god;

import java.util.ArrayList;
import java.util.List;

import cgutils.io.InputReader;
import god.entities.Drone;
import god.entities.Zone;

public class Player {
  static GameState state = new GameState();
  
  public static void main(String args[]) {
    InputReader in = new InputReader(System.in);
    state.readInit(in);
    
    // game loop
    while (true) {
      state.readRound(in);

      List<Zone> otherZones = new ArrayList<>();
      getNotOwnedZones(otherZones);
      
      List<Drone> spareDrones = new ArrayList<>();
      extractSparedDrones(spareDrones);

      // check for zone in danger (zone is ours but incomming is overwhelming)
      for (Zone zone : state.zones) {
        if (!zone.isMine(GameState.myId)) continue;
        int spareDroneFutureFor = zone.spareDroneFutureFor(GameState.myId);
        if (spareDroneFutureFor < 0) {
          // danger, get some spared drones back
          for (int i=0;i<spareDroneFutureFor && !spareDrones.isEmpty();i++) {
            Drone drone = zone.getClosest(spareDrones);
            drone.target = zone.position;
            spareDrones.remove(drone);
          }
        }
      }
      
      
      // to better choose the zones, we add the distance (drone,zone) for each zone, sparedDrones
      for (Zone zone : otherZones) {
        zone.value = 0;
        for (Drone drone : spareDrones) {
          zone.value += 1.0 / drone.position.dist2(zone.position);
        }
      }
      
      
      // do the knapsack problem with the spared drones
      List<Zone> optimalChoice = new ArrayList<>();
      KnapSack.fillPackage(spareDrones.size(), otherZones, optimalChoice, otherZones.size());

      if (optimalChoice.isEmpty()) {
        // arggg stuck in a checkmate position
        
      } else {
        // happily affect the drones, no strategy
        affectSparedDronesToZones(spareDrones, optimalChoice);
      }
      
      for (Drone drone : state.myDrones) {
        if (drone.target == null) {
          Zone zone = state.findClosestZone(drone);
          drone.target = GameState.CENTER; // TODO better ? barycentre of Zones, my zones, other zones, ...
        }
        System.out.println(String.format("%d %d", drone.target.x, drone.target.y));
      }
    }
  }

  private static void affectSparedDronesToZones(List<Drone> spareDrones, List<Zone> optimalChoice) {
    for (Zone zone : optimalChoice) {
      int needed = zone.unitsToTake();
      //TODO find the best reparition of closest drones for each zone!
      int i=0;
      while (i<needed) {
        Drone drone = zone.getClosest(spareDrones);
        drone.target = zone.position;
        spareDrones.remove(drone);
        i++;
      }
    }
  }

  private static void getNotOwnedZones(List<Zone> otherZones) {
    for (Zone zone : state.zones) {
      zone.debug(GameState.myId);
      if ( zone.owner != GameState.myId) {
        otherZones.add(zone);
      }
    }
  }

  private static void extractSparedDrones(List<Drone> spareDrones) {
    // check for drone already in zone, that can be spared
    for (Drone drone : state.myDrones) {
      if (drone.inZone == null) {
        spareDrones.add(drone);
      } else {
        drone.target = drone.inZone.position; // stay in da zone
      }
    }

    // find worthless drones in zones
    for (Zone zone : state.zones) {
      if (zone.isMine(GameState.myId)) {
        int spareInFuture = zone.spareDroneFutureFor(GameState.myId);
        int spare = zone.spareDroneFor(GameState.myId);
        if (spare > 0 && spareInFuture > 0) {
          for (int i=0;i<Math.min(spare, spareInFuture);i++) {
            Drone sparedDrone = zone.removeOneDrone();
            sparedDrone.target = null;
            spareDrones.add(sparedDrone);
          }
        }
      } else {
        int playersAtMax = 0;
        for (int i=0;i<GameState.playerCount;i++) {
          if (zone.drones[i] > zone.drones[GameState.myId]) {
            playersAtMax++;
          }
        }
        if (playersAtMax > 1) {
          while (!zone.myDrones.isEmpty()) {
            // find another target
            Drone drone = zone.removeOneDrone();
            drone.target = null;
            spareDrones.add(drone);
          }
        }
      }
    }
  }
}
