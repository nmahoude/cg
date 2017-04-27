package god.entities;

import java.util.ArrayList;
import java.util.List;

import god.GameState;

public class Zone extends Entity {
  public int id;
  int radius = 100;
  public int drones[] = new int[4]; // drone count inside for each player
  public int incomming_drones[] = new int[4]; // incomming drones comming to this zone
  public int owner; // controlled by id
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
      if (drone.lastPos.dist2(position) < drone.position.dist2(position)) {
        if (drone.position.dist2(position) < 500*500) { //TODO better than that
          incomming_drones[drone.owner] ++;
        }
      }
      
    });
  }
  
  public void debug(int myId) {
    System.err.println("Drones in zone "+id+" :");
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
    int spare = drones[id] + incomming_drones[id];
    for (int i=0;i<GameState.playerCount;i++) {
      if (i == id) continue;
      spare = Math.min(spare, (drones[id]+incomming_drones[i]) - (drones[i]-incomming_drones[i]));
    }
    return spare;
  }

  public Drone removeOneDrone() {
    Drone drone = myDrones.remove(0);
    allDronesInOrder.remove(drone);
    drones[drone.owner]--;
    return drone;
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
  
  
}
