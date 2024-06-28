package fall2023;

public class Simulator {
  
  public static final int WIDTH = 10000;
  public static final int HEIGHT = 10000;
  public static final int UGLY_UPPER_Y_LIMIT = 2500;

  public static int DARK_SCAN_RANGE_2 = 800 * 800;
  public static int LIGHT_SCAN_RANGE_2 = 2000 * 2000;
  
  public static final int UGLY_ATTACK_SPEED = 540;
  public static final int UGLY_ATTACK_SPEED_2 = 540*540;
  
  public static final int UGLY_SEARCH_SPEED = 270;
  public static final int UGLY_SEARCH_SPEED_2 = 270*270;
  
  
  public static final int DRONE_EMERGENCY_SPEED = 300;
  public static final int DRONE_UPPER_Y_LIMIT = 0;
  public static final int FISH_FLEE_SPEED = 400;
  
  public static final int[] lowY = new int[] { 2500, 5000, 7500, 2500};
  public static final int[] highY = new int[] { 5000, 7500, 10000, 10000};

  
  Pos nextPos = new Pos(0,0);
  private State state;
  private Action[] actions;
  private Pos[] uglyTargets = new Pos[22];
  {
    for (int i=0;i<22;i++) {
      uglyTargets[i] = new Pos(-1, -1);
    }
  }
  
  
  
  public void applyJustMe(State state, Action[] actions) {
    // don't attract ugly fishes TODO do better !
    state.oppDrones[0].pos.set(-1000, -1000);
    state.oppDrones[1].pos.set(-1000, -1000);
    
    apply(state, actions);
  }

  public void apply(State state, Action[] actions) {
    this.state = state;
    this.actions = actions;
    
    
    
    doBatteries();
    moveEntities();
    updateUglyTarget();

    doScan();
    doReport();
    
    upkeepDrones();
    
    updateFish();
    updateUglySpeed();
    
  }

  private void upkeepDrones() {
    // TODO Auto-generated method stub
    
  }

  private void doScan() {

//    for (Drone drone : state.myDrones) {
//      for (int i=4;i<16;i++) {
//        if (!state.fishPresent[i]) continue;
//        
//        Pos center = state.getBestTriangulation(i).center();
//        int radius2 = actions[drone.id].lamp ? Drone.BIG_LIGHT_RADIUS2 : Drone.NORMAL_LIGHT_RADIUS2; 
//        if (drone.pos.dist2(center) <= radius2) {
//          drone.currentScans.scan(i);
//        }
//      }
//    }
  }

  private void updateFish() {
 // check if we flee
    
    for (Fish fish : state.fishes) {
      if (State.fType[fish.id] == Fish.UGLY) continue;
      
      Drone closestDrone = null;
      int bestDist2 = Integer.MAX_VALUE;
      for (Drone d: state.dronesById) {
        if (d.emergency) continue;
        if (wasEngineOff(d)) continue;
        
        int dist2 = d.pos.dist2(fish.pos);
        if (dist2 < bestDist2 && dist2 <= Fish.FISH_HEARING_RANGE2) {
          bestDist2 = dist2;
          closestDrone = d;
        }
      }
      
      if (closestDrone != null) {
        // flee from drone
        Vec fleeVec = new Vec(fish.pos, closestDrone.pos);
        fleeVec.normalize(Fish.FISH_FLEE_SPEED);
        fish.speed.copyFrom(fleeVec);
        
        if (fish.pos.x < 0 || fish.pos.x > Simulator.WIDTH -1 ) {
          fish.pos.set(-1000, -1000);
          state.fishPresent[fish.id] = false;
        }
      } else {
      
        Vec swimVec = new Vec(fish.speed);
        swimVec.normalize(Fish.FISH_SWIM_SPEED);
        
        nextPos = new Pos(fish.pos);
        nextPos.add(swimVec);
        
        if (nextPos.x < 0 && nextPos.x < fish.pos.x ||
                nextPos.x > Simulator.WIDTH - 1 && nextPos.x > fish.pos.x
        ) {
            swimVec.hsymmetric();
        }
        
        int lowY = Simulator.lowY[State.fType[fish.id]];
        int highY = Simulator.highY[State.fType[fish.id]];
        if (
            nextPos.y < lowY && nextPos.y < fish.pos.y ||
                nextPos.y > highY && nextPos.y > fish.pos.y
        ) {
            swimVec.vsymmetric();
        }
        fish.speed.copyFrom(swimVec);
      }
    }
  }

  private boolean wasEngineOff(Drone d) {
    return actions[d.id].dx == 0 && actions[d.id].dy == +300; 
  }
  
  private void doReport() {
//    for (Drone drone : state.myDrones) {
//      if (drone.pos.y <= 500) {
//        state.myScans.append(drone.currentScans);
//        state.myScans.updateFirsts(state.oppScans);
//        state.myScore = state.myScans.score();
//        drone.currentScans.clear();
//      }
//    }
    
    
  }

  private void updateUglyTarget() {
    for (Fish ugly : state.fishes) {
      if (State.fType[ugly.id] != Fish.UGLY) continue;
      
      uglyTargets[ugly.id].set(-1, -1);; 
      int bestDist2 = Integer.MAX_VALUE;
      for (int i=0;i<4;i++) {
        if (state.dronesById[i].emergency) continue;
        
        int dist2 = state.dronesById[i].pos.dist2(ugly.pos);
        if (actions[i].lamp && dist2 > LIGHT_SCAN_RANGE_2) continue;
        if (!actions[i].lamp && dist2 > DARK_SCAN_RANGE_2) continue;

        if (dist2 < bestDist2) {
          bestDist2 = dist2;
          uglyTargets[ugly.id].set(state.dronesById[i].pos);
        }
      }
    }
  }

  Vec speed = new Vec(0,0);
  private void moveEntities() {
    for (int i=0;i<4;i++) {
      Drone drone = state.dronesById[i];
      
      if (drone.emergency) {
        drone.pos.y -= DRONE_EMERGENCY_SPEED;
      } else {
        
        speed.set(actions[i].dx, actions[i].dy);
        speed.limitTo(Drone.MOVE_SPEED);
        for (Fish ugly : state.fishes) {
          if (State.fType[ugly.id] != Fish.UGLY) continue;
          
          if (Collision.happened(drone, speed, ugly)) {
            drone.emergency = true;
            drone.currentScans.clear();
            break;
          }
        }
        
        drone.pos.x += actions[i].dx;
        drone.pos.y += actions[i].dy;
      }
      snapToDroneZone(drone);
    }
    
    
    for (Fish f : state.fishes) {

      f.pos.add(f.speed);
      if (State.fType[f.id] != Fish.UGLY) {
        snapToFishZone(f);
      } else {
        snapToUglyZone(f);
      }
    }
  }

  private void snapToDroneZone(Drone drone) {
    if (drone.pos.y > HEIGHT - 1) {
      drone.pos.set(drone.pos.x, HEIGHT - 1);
    } else if (drone.pos.y < DRONE_UPPER_Y_LIMIT) {
        drone.pos.set(drone.pos.x, DRONE_UPPER_Y_LIMIT);
    }
    if (drone.pos.x < 0) {
        drone.pos.set(0, drone.pos.y);
    } else if (drone.pos.x >= WIDTH) {
        drone.pos.set(WIDTH - 1, drone.pos.y);
    }    
  }

  private void doBatteries() {
    for (int i=0;i<4;i++) {
      Drone drone = state.dronesById[i];
      if (actions[i].lamp) {
        drone.battery-= 5;
      } else {
        drone.battery+= 1;
      }
    }
  }

  Vec attack = new Vec(0,0);
  private void updateUglySpeed() {
    for (Fish ugly : state.fishes) {
      if (State.fType[ugly.id] != Fish.UGLY) continue;

      Pos targetPos = uglyTargets[ugly.id];
      if (targetPos.x != -1) {
        attack.vx = targetPos.x - ugly.pos.x;
        attack.vy = targetPos.y - ugly.pos.y;
        attack.clampTo(UGLY_ATTACK_SPEED);
        ugly.speed.set(attack);
      } else {
        ugly.speed.clampTo(UGLY_SEARCH_SPEED);
        
        Fish fleeFrom = null;
        int bestDist2 = Integer.MAX_VALUE;
        for (Fish ugly2 : state.fishes) {
          if (ugly == ugly2) continue;
          if (State.fType[ugly2.id] != Fish.UGLY) continue;
          
          int dist2 = ugly.pos.dist2(ugly2.pos);
          if (dist2 < bestDist2 && dist2 < 600 * 600) {
            bestDist2 = dist2;
            fleeFrom = ugly2;
          }
        }
        
        if (fleeFrom != null) {
          Vec newSpeed = new Vec(ugly.pos, fleeFrom.pos);
          newSpeed.normalize(Fish.FISH_SWIM_SPEED);
          if (!newSpeed.isZero()) {
            ugly.speed.copyFrom(newSpeed);
          }
        } 
        
        nextPos.set(ugly.pos);
        nextPos.add(ugly.speed);
  
        if (nextPos.x < 0 && nextPos.x < ugly.pos.x || nextPos.x > WIDTH - 1 && nextPos.x > ugly.pos.x) {
            ugly.speed.hsymmetric();
        }
  
        if (nextPos.y < UGLY_UPPER_Y_LIMIT && nextPos.y < ugly.pos.y || nextPos.y > HEIGHT - 1 && nextPos.y > ugly.pos.y) {
            ugly.speed.vsymmetric();
        }
      }
    }
  }

  public static void snapToFishZone(Fish fish) {
    
    if (fish.pos.y > HEIGHT - 1) {
        fish.pos.set(fish.pos.x, HEIGHT - 1);
    } else if (fish.pos.y > highY[State.fType[fish.id]]) {
        fish.pos.set(fish.pos.x, highY[State.fType[fish.id]]);
    } else if (fish.pos.y < lowY[State.fType[fish.id]]) {
        fish.pos.set(fish.pos.x, lowY[State.fType[fish.id]]);
    }

  } 
  public static void snapToUglyZone(Fish ugly) {
    if (ugly.pos.y > HEIGHT - 1) {
      ugly.pos.set(ugly.pos.x, HEIGHT - 1);
    } else if (ugly.pos.y < UGLY_UPPER_Y_LIMIT) {
      ugly.pos.set(ugly.pos.x, UGLY_UPPER_Y_LIMIT);
    }
  }

}
