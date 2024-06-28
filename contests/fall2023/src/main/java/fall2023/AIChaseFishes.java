package fall2023;

public class AIChaseFishes implements AIInterface {
  Action[] actions = new Action[] { new Action(), new Action(), new Action(), new Action() };
  int[] blipTarget = new int[] { -1, -1 };
  private State state;

  public Action[] think(State state) {
    this.state = state;

    for (int i=0;i<2;i++) {
      Drone drone = state.myDrones[i];
      if (i == 1) break; // only one drone for now
      
      
      if (state.fishes.isEmpty()) {
        actions[drone.id].dx = 0;
        actions[drone.id].dy = 600;
        actions[drone.id].lamp = true;
        continue;
      }
      
      // chase the fish !
      Fish fish = null;
      int bestDist2 = Integer.MAX_VALUE;
      for (Fish f : state.fishes) { 
        if (State.fType[f.id] == Fish.UGLY) continue;
        
        int turns = (int)(Math.sqrt(f.pos.dist2(drone.pos)) / Drone.MOVE_SPEED); // steps to go to fish
        if (f.pos.x < 5000) {
          turns += (f.pos.x / 300);
        } else {
          turns += (Simulator.WIDTH - f.pos.x) / 300;
        }
        if (fish == null || turns < bestDist2) {
          fish = f;
          bestDist2 = turns;
        }
      }
      
      if (fish != null) {
        chaseFish(state, drone, fish.pos, fish.speed, actions[drone.id]);
      }      
    }
    
    return actions;
  }

  public void chaseFish(State state, Drone drone, Pos fishPos, Vec fishSpeed, Action a) {
    int bestDist2;
    System.err.println(" chasing fish @ "+fishPos);
    Vec target = new Vec(fishPos.x - drone.pos.x, fishPos.y - drone.pos.y);
    target.limitTo(400);
    
    int distTurns;
    if (target.vx != 0) {
      if (fishPos.x < drone.pos.x) {
        distTurns = (int)Math.abs(Math.round(fishPos.x / target.vx));
      } else {
        distTurns = (int)Math.abs(Math.round((Simulator.WIDTH - fishPos.x) / target.vx));
      }
      System.err.println("Estimated turns to reach border : " + distTurns);
    }
    

    Pos nextFishPos = new Pos(fishPos);
    nextFishPos.add(fishSpeed);
    System.err.println("   next fish pos : "+nextFishPos);
    
    
    final Vec testSpeed = new Vec(0,0);
    final MoveOptimizer moves = new MoveOptimizer();
    
    Vec bestMove = moves.optimize(state, drone, AI.fearFish, (tentativeState, tentativeDrone, action) -> {
      
      int dist2 = tentativeDrone.pos.dist2(nextFishPos);
      if (dist2 > Fish.FISH_HEARING_RANGE2) {
        return -dist2; // always < 0 but closer is better
      }
      
      double score;
      testSpeed.set(nextFishPos.x - tentativeDrone.pos.x, nextFishPos.y - tentativeDrone.pos.y);
      testSpeed.normalize(Fish.FISH_FLEE_SPEED);
      if (nextFishPos.x < 5000) {
        score = 1000 * -testSpeed.vx; 
      } else {
        score = 1000 * +testSpeed.vx;
      }
      
      score -= Math.sqrt(Math.max(300, dist2));
      return score;
    });
    
    
    a.dx = bestMove.vx;
    a.dy = bestMove.vy;
    a.lamp = false;
  }
}
