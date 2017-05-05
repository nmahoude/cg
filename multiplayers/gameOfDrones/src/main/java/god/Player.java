package god;

import java.util.ArrayList;
import java.util.List;

import cgutils.io.InputReader;
import god.algorithm.OptimalSubSet;
import god.algorithm.ZoneInfo;
import god.entities.Drone;
import god.entities.Zone;

public class Player {
  static GameState state = new GameState();
  static int turns = 0;
  private static List<Zone> bestZonesToCheck;
  
  public static void main(String args[]) {
    InputReader in = new InputReader(System.in);
    state.readInit(in);
    
    bestZonesToCheck = new ArrayList<>();
    for (Zone zone : state.zones) {
      bestZonesToCheck.add(zone);
    }
//    if (state.playerCount == 2) {
//    } else {
//      int zonesToCheck = 0;
//      if (state.playerCount == 3) {
//        zonesToCheck = 3;
//      } else if (state.playerCount == 4) {
//        zonesToCheck = 3;
//      }
//      double bestDist = Double.POSITIVE_INFINITY;
//      for (Zone z1 : state.zones) {
//        for (Zone z2 : state.zones) {
//          if (z1 == z2) continue;
//          for (Zone z3 : state.zones) {
//            if (z1 == z3 || z2 == z3) continue;
//            double dist = z1.position.distance(z2.position) 
//                + z1.position.distance(z3.position)
//                + z2.position.distance(z3.position);
//            if( dist < bestDist) {
//              bestDist = dist;
//              bestZonesToCheck = Arrays.asList(z1, z2, z3);
//            }
//          }
//        }
//      }
//    }
    
    
    System.err.println("Selection of zones to attack/defend : ");
    for (Zone zone : bestZonesToCheck) {
      System.err.print(""+zone.id+" ; ");
    }
    System.err.println();
    
    // game loop
    while (true) {
      turns++;
      state.readRound(in);

      // debug
//      for (Zone zone : state.zones) {
//      zone.debug(GameState.myId);
//      }
      // specialized debug
      //      Zone zoneUnderInvestigation = state.zones.get(2);
//      zoneUnderInvestigation.debug(GameState.myId);
//      Drone droneUnderInvestigation = state.drones.get(3);
//      System.err.println("Info about drone UI: "+droneUnderInvestigation.id);
//      System.err.println("Zone pos:  "+zoneUnderInvestigation.position);
//      System.err.println("Drone pos: "+droneUnderInvestigation.position);
//      System.err.println("dist2Zone : "+droneUnderInvestigation.position.dist2(zoneUnderInvestigation.position));
//      System.err.println("rapprcohement ? " + droneUnderInvestigation.lastPos.dist2(zoneUnderInvestigation.position));
      
      List<Zone> otherZones = new ArrayList<>();
      getNotOwnedZones(otherZones);
      
      
      
      List<Drone> spareDrones = new ArrayList<>();
      extractSparedDrones(spareDrones);

      System.err.println("Spared drones ");
      for (Drone drone : spareDrones) {
        System.err.print(""+drone.id+ ", ");
      }
      System.err.println();
      
      // check for zone in danger (zone is ours but incomming is overwhelming)
      for (Zone zone : state.zones) {
        if (!zone.isMine(GameState.myId)) continue;
        int spareDroneFutureFor = zone.spareDroneFutureFor(GameState.myId);
        if (spareDroneFutureFor < 0 && spareDrones.size() >= -spareDroneFutureFor) {
          int neededDrones = -spareDroneFutureFor;
          System.err.println("Zone "+zone.id+" in danger");
          // danger, get some spared drones back
          for (int i=0;i<neededDrones;i++) {
            Drone drone = zone.getClosest(spareDrones);
            drone.target = zone.position;
            spareDrones.remove(drone);
            System.err.println(" -->Sending back drone "+drone.id);
          }
        }
      }
      
      
      // do the knapsack problem with the spared drones
      List<Zone> optimalChoice = new ArrayList<>();
      KnapSack.fillPackage(spareDrones.size(), otherZones, optimalChoice, otherZones.size());

      if (optimalChoice.isEmpty()) {
        int maxPoint = 0;
        int maxId = -1;
        for (int i=0;i<4;i++) {
          if (i == state.myId) continue;
          if (state.turnPoints[i] > maxPoint) {
            maxPoint = state.turnPoints[i];
            maxId = i;
          }
        }
        if (GameState.myId != maxId) {
          spareDrones.clear();
          for (Drone d : state.myDrones) {
            if (d.inZone == null || !d.inZone.isMine(GameState.myId)) {
              spareDrones.add(d);
            }
          }
          KnapSack.fillPackage(spareDrones.size(), otherZones, optimalChoice, otherZones.size());
        }
      }
      if (optimalChoice.isEmpty()) {
        // ????
      } else {
        // happily affect the drones, no strategy
        affectSparedDronesToZones(spareDrones, optimalChoice);
      }
      for (Drone drone : state.myDrones) {
        if (drone.target == null) {
          Zone zone = state.findClosestZone(drone);
          drone.target = zone.position; // TODO better ? barycentre of Zones, my zones, other zones, ...
        }
        System.out.println(String.format("%d %d", drone.target.x, drone.target.y));
      }
      
      
      
    }
  }

  private static void affectSparedDronesToZones(List<Drone> spareDrones, List<Zone> zones) {
    OptimalSubSet algo = new OptimalSubSet();
    List<ZoneInfo> optimize = algo.optimize(zones, spareDrones, 200-turns);
    for (ZoneInfo info : optimize) {
      for (Drone drone : info.affectedDrones) {
        drone.target = info.zone.position;
      }
    }

    // old affectation
//    for (Zone zone : zones) {
//      int needed = zone.unitsToTake();
//      //TODO find the best reparition of closest drones for each zone!
//      int i=0;
//      while (i<needed) {
//        Drone drone = zone.getClosest(spareDrones);
//        drone.target = zone.position;
//        spareDrones.remove(drone);
//        i++;
//      }
//    }
  }

  private static void getNotOwnedZones(List<Zone> otherZones) {
    for (Zone zone : bestZonesToCheck) {
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
        int mesDrones = zone.incomming_drones[GameState.myId] + zone.drones[GameState.myId];
        int max = 0;
        for (int i=0;i<GameState.playerCount;i++) {
          if (i == GameState.myId) continue;
          int sesDrones = zone.incomming_drones[i] + zone.drones[i];
          if (sesDrones > max) {
            max = sesDrones;
          }
        }
        if (mesDrones < max || (mesDrones == max && !zone.isNeutral())) {
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
