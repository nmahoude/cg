package god.entities;

import java.util.ArrayList;
import java.util.List;

import god.GameState;
import god.KnapsackUnit;

public class Zone extends Entity implements KnapsackUnit {
  public static final int TURNS_IN_FUTURE = 20;
  
  public int id;
  int radius = 100;
  public int drones[] = new int[4]; // drone count inside for each player
  public int incomming_drones[] = new int[4]; // incomming drones comming to this zone
  public int owner = -1; // controlled by id
  public int futureOwner[] = new int[TURNS_IN_FUTURE];
  
  public List<Drone> allDronesInOrder = new ArrayList<>();
  public List<Drone> myDrones = new ArrayList<>();
  public double value;
  
  public Zone(int x, int y) {
    super(x,y);
  }

  public void clearDrones() {
    for (int i=0;i<4;i++) {
      drones[i] = 0;
      incomming_drones[i] = 0;
    }
    allDronesInOrder.clear();
    myDrones.clear();
  }

  public void updateDrones() {
    allDronesInOrder.sort((d1, d2) -> { return Integer.compare(d1.position.dist2(this.position) , d2.position.dist2(this.position));} );
    allDronesInOrder.forEach(drone -> {
      if (drone.inZone != null) return;
      if (drone.position.dist2(position) < 300*300) {
        incomming_drones[drone.owner] ++;
      } else {
        if (drone.lastPos.dist2(position) > drone.position.dist2(position)) {
          if (drone.position.dist2(position) < 1000*1000) { //TODO better than that
            incomming_drones[drone.owner] ++;
          }
        }
      }
    });
    
    updateFutureOwners();
  }

  // update futureOwner, it's a worst case (all drones move to this zone) of the future
  private void updateFutureOwners() {
    futureOwner[0] = owner;
    
    for (int turn=1;turn<TURNS_IN_FUTURE;turn++) {
      int drones[] = new int[4];
      int maxDist = radius * radius + turn * Drone.speed * Drone.speed;
      for (Drone drone : allDronesInOrder) {
        if (drone.position.dist2(this.position) > maxDist) {
          break; // all later drones are too far
        } else {
          drones[drone.owner]++;
        }
      }
      int turnOwner = futureOwner[turn-1];
      for (int i=0;i<4;i++) {
        // TODO bug here, if we are equals, player 0 is advantaged
        if (turnOwner == -1) {
          if (drones[i]> 0) {
            turnOwner = i;
          }
        } else if (drones[turnOwner] < drones[i]) {
          turnOwner = i;
        } else if (drones[turnOwner] == drones[i]) {
          int pastOwner = futureOwner[turn-1];
          if (pastOwner == i) {
            turnOwner = i;
          }
        }
      }
      futureOwner[turn] = turnOwner;
    }
  }
  
  public void debug(int myId) {
    System.err.println("***** Zone "+id+" ***************");
    for (int i=0;i<GameState.playerCount;i++) {
      System.err.print(" p"+i+"=>"+drones[i]);
    }
    System.err.println();
    
    System.err.println("Drones comming to zone "+id+" :");
    for (int i=0;i<GameState.playerCount;i++) {
      System.err.print(" p"+i+"=>"+incomming_drones[i]);
    }
    System.err.println();

    System.err.println("Zone value (previous): "+value);
    System.err.println("Is mine ? "+ (isMine(myId) ? "true" : "false"));
    System.err.println("spared : "+ spareDroneFor(myId) );
    System.err.println("spared Future : "+ spareDroneFutureFor(myId) );
    
    System.err.print("List : ");
    for (Drone d : allDronesInOrder) {
      System.err.print((d.id + 100*d.owner)+" < ");
    }
    System.err.println();
  }

  public boolean isMine(int myId) {
    return owner == myId;
  }

  /** 
   * how much drone to keep the zone
   * 
   * can be negative
   * @param id
   * @return
   */
  public int spareDroneFor(int id) {
    int spare = drones[id] ;
    for (int i=0;i<GameState.playerCount;i++) {
      if (i == id) continue;
      spare = Math.min(spare, drones[id] - drones[i]);
    }
    return spare;
  }

  public int spareDroneFutureFor(int id) {
    int spare = drones[id] ;
    for (int i=0;i<GameState.playerCount;i++) {
      if (i == id) continue;
      spare = Math.min(spare, (drones[id]) - (drones[i]+incomming_drones[i]));
    }
    return spare;
  }

  public Drone removeOneDrone() {
    Drone drone = myDrones.remove(0);
    allDronesInOrder.remove(drone);
    drones[drone.owner]--;
    return drone;
  }

  @Override
  public double getWeight() {
    return unitsToTake();
  }
  
  @Override
  public double getReward() {
    return 1 + value;
  }
  
  public int unitsToTake() {
    if (owner == -1) {
      return 1;
    }
    if (owner == GameState.myId) {
      return 0;
    }
    
    int max = 0;
    for (int i=0;i<GameState.playerCount;i++) {
      if (i == GameState.myId) continue;
      max = Math.max(max,  drones[i]);
    }
    return max - drones[GameState.myId]+1;
  }

  public Drone getClosest(List<Drone> spareDrones) {
    int bestDist = Integer.MAX_VALUE;
    Drone bestDrone= null;
    
    for (Drone drone : spareDrones) {
      int dist = drone.position.dist2(position);
      if (dist < bestDist) {
        bestDist = dist;
        bestDrone = drone; 
      }
    }
    return bestDrone;
  }

  public boolean isNeutral() {
    return owner == -1;
  }
  
  
}
