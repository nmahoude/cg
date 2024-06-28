package fall2023;

import java.util.ArrayList;
import java.util.List;

public class AI implements AIInterface {
  private static final MoveOptimizer optimizer = new MoveOptimizer();
  private static final ScoreMinimax scoreMinimax = new ScoreMinimax();

  static final int[] topSpeed = new int[] { Drone.MOVE_SPEED };
  static final int[] uglySpeeds = new int[] { 540 };
  public static int[] fearFish = new int[] { Drone.MOVE_SPEED };
  
  

  List<Drone> forceUp = new ArrayList<>();
  Action[] actions = new Action[] { new Action(), new Action(), new Action(), new Action() };
  private State state;

  int[] currentTarget = new int[] { -1, -1 , -1, -1};
  public Action[] think(State state) {
    this.state = state;
    for (int i=0;i<4;i++) {
      currentTarget[i] = -1;
    }
    
    System.err.println(" ------------------------ AI -----------------------");

    if (Player.DEBUG_SURFACE_ESTIMATE) {
      for (Drone drone : state.dronesById) {
        System.err.println("estimated turns to surface for " + drone + " = " + drone.estimateTurnsToSurface());
      }
    }

    // do minimax to know which drones need to go up
    forceUp.clear();
    forceUp.addAll(scoreMinimax.think(state));
    

    for (int i = 0; i < 2; i++) {
      Drone drone = state.myDrones[i];
      System.err.println("------------ thinking for " + drone);
      Action a = actions[drone.id];

      
      if (isDead(drone, a)) {
        System.err.println("   [EMERGENCY] Nothing to do, think about the sea ....");
        continue;
      }
      
      if (precalculatedMove(drone, a)) {
        System.err.println("   [Precalculated] No time to think ");
        continue;
      }

      if (scoreMinimax.willLoseWithThisState) {
        System.err.println("  Derniers secours ....");
        if (denyFish(drone, a)) {
          System.err.println("  [Denying Fish] to opp");
          continue;
        }
        if (lureUgly(drone, a)) {
          System.err.println("  [Luring evil] towards opp");
          continue;
        }
      }
      
      if (shouldGoUp(drone, a)) {
        System.err.println("   [GoUp] or wander ? ");
        continue;
      }

      if (killFish(drone, a)) {
        System.err.println("   [KILL] fish get out ");
        continue;
      }

      if (captureFish(drone, a)) {
        System.err.println("   [Capture] time to get some fish "); 
        continue;
      }

      if (scanningToBase(drone, a)) {
        System.err.println("   [Scanning to base] I don't have anything special to do and fish to scan ... going up "); 
        continue;
      }
      
      if (denyFish(drone, a)) {
        System.err.println("  [Denying Fish] to opp");
        continue;
      }
      
      if (lureUgly(drone, a)) {
        System.err.println("  [Luring evil] towards opp");
        continue;
      }
      
      
      
      System.err.println("    [LAST CCOMMAND] GO UP because what else ?");
      goUp(state, drone, a);
    }

    System.err.println("---------------- RESULT --------------------");
    for (Drone d : state.myDrones) {
      System.err.println("A[" + d.id + "] : " + actions[d.id]);
      System.err.println("--------------------------------------------");
    }
    return actions;
  }

  private boolean lureUgly(Drone drone, Action a) {
    
    a.lamp = false;
    
    
    int bestDist2 = Integer.MAX_VALUE;
    int bestUgly = -1;
    for (int f=16;f<16+State.uglyCount;f++) {
      if (currentTarget[otherDrone(state, drone).id] == f) continue;
      
      Pos center = state.getBestTriangulation(f).center();
      int dist2 = center.dist2(drone.pos);
      if (dist2 < bestDist2) {
        bestDist2 = dist2;
        bestUgly = f;
      }
    }

    currentTarget[drone.id] = bestUgly;
    
    boolean alreadyLuring = false;
    if (bestDist2 < Drone.NORMAL_LIGHT_RADIUS2) {
      alreadyLuring = true;
    }
    
    
    if (alreadyLuring) {
      // go to opp (with ugly)
      // TODO which opp ?? 
      
      double bestScore = Double.NEGATIVE_INFINITY;
      Drone bestOppDrone = null;
      for (Drone opp : state.oppDrones) {
        double score = 0.0;
        
        if (opp.currentScans.isEmpty()) score -= 1_000_000;  
        
        int dist2 = drone.pos.dist2(opp.pos);
        score -= 0.000001 * dist2;

        if (score > bestScore) {
          bestScore = score;
          bestOppDrone = opp;
        }
      }
      
      System.err.println(" Already luring, sending fish onto top of "+bestOppDrone);
      Pos target = new Pos(bestOppDrone.pos);
      target.y -= Drone.MOVE_SPEED;
      chaseTarget(state, drone, a, uglySpeeds, bestOppDrone.pos);

      
    } else {
      // go to nearest ugly !
      System.err.println("Need to go to ugly first");
      chaseTarget(state, drone, a, topSpeed, state.getBestTriangulation(bestUgly).center());
    }
    
    return true;
  }

  private boolean killFish(Drone drone, Action a) {
//    if (Player.KILL_FISH_DISABLED) {
//      return false;
//    }
    
    
    System.err.println("   Check if we can eject a fish with a gentle push");
    List<Fish> goodToEjects = new ArrayList<>();
    for (Fish fish : state.fishes) {
      int l = fish.id;
      if (state.oppScans.contains(l)) continue;
      if (state.oppDrones[0].currentScans.contains(l)) continue;
      if (state.oppDrones[1].currentScans.contains(l)) continue;
      
      if (!state.myScans.contains(l) 
          && !state.myDrones[0].currentScans.contains(l) 
          && !state.myDrones[1].currentScans.contains(l)) continue;
      
      goodToEjects.add(fish);
    }
    System.err.println("Fish I can eject : "+goodToEjects);
    if (goodToEjects.isEmpty()) return false;
    
    Pos target = new Pos(0, 0);
    Pos nextPos = new Pos(0,0);
    for (Fish f : goodToEjects) {
      
      if (f.pos.dist2(drone.pos) <= Fish.FISH_HEARING_RANGE2 || 
          (f.pos.dist2(drone.pos) <= 2 * Fish.FISH_HEARING_RANGE2 && f.pos.distToBorder() < 400)) {
      } else {
        continue; // il n'est pas dans ma range (donc pas assez vite)
      }
      
      // check the future pos of fish (maybe out !)
      nextPos.copyFrom(f.pos);
      nextPos.add(f.speed);
      if (nextPos.outOfGame()) {
        System.err.println("Fish "+f+" will be out next turn ! Doing nothing for it");
        continue; // do nothing
      }
      if (nextPos.distToBorder() > 800) continue; // pas assez pret du bord 
      
      if (nextPos.x < 600 ) {
        target.set(nextPos.x + 1000, nextPos.y);
      } else {
        target.set(nextPos.x - 1000, nextPos.y);
      }
      
      System.err.println(" Ejecting "+f);
      chaseTarget(state, drone, a, topSpeed, target);
      a.lamp = shouldLightOn(state, drone, a, -1);
      return true;
    }
    
    System.err.println(" No eligible fish to eject");
    return false;
  }

  private boolean denyFish(Drone drone, Action a) {
    // for each fish he has not (not scanned, not in inventory)
    // take the closest and (try) deny it
    currentTarget[drone.id] = -1;
    
    int bestFish = -1;
    int bestDist2 = Integer.MAX_VALUE;
    for (int f=4;f<16;f++) {
      if (!state.fishPresent[f]) continue;
      if (state.oppScans.contains(f)) continue;
      if (state.oppDrones[0].currentScans.contains(f)) continue;
      if (state.oppDrones[1].currentScans.contains(f)) continue;
      
      System.err.println("opp does not have "+f);
      
      int dist2 = drone.pos.dist2(state.getBestTriangulation(f).center());
      
      if (currentTarget[otherDrone(state, drone).id] == f) {
        dist2 += 100_000; // TODO passer sur un score !
      }
      if( dist2 < bestDist2) {
        bestDist2 = dist2;
        bestFish = f;
      }
    }

    if (bestFish != -1) {
      System.err.println("Deny fish : "+bestFish );
      currentTarget[drone.id] = bestFish;
      AIChaseFishes chaseAI = new AIChaseFishes();
      chaseAI.chaseFish(state, drone, state.getBestTriangulation(bestFish).center(), Vec.ZERO, a);
      a.lamp = shouldLightOn(state, drone,a, -1);
      return true;
    }
    
    return false;
  }

  private boolean scanningToBase(Drone drone, Action a) {
    if (!drone.currentScans.isEmpty()) {
      goUp(state, drone, a);
      return true;
    }
    return false;
  }

  private boolean isDead(Drone drone, Action a) {
    if (drone.emergency) {
      a.dx = 0;
      a.dy = -300;
      a.lamp =false;
      
      return true;
    }
    return false;
  }

  private boolean precalculatedMove(Drone drone, Action a) {
    
//    if (State.turn > 6)  return false;
//    
//    for (int ll=4;ll<16;ll++) {
//      if (State.fType[ll] != 0) continue;
//      if (state.myScans.contains(ll)) continue;
//      if (!state.fishPresent[ll]) continue;
//      if (drone.currentScans.contains(ll)) continue;
//
//      Pos center = state.getBestTriangulation(ll).center();
//      
//      int borderLimit = 1000;
//      if ((drone.pos.x < 5000 && center.x <= borderLimit) || (drone.pos.x > 5000 && center.x >= 10000-borderLimit)) {
//        chaseTarget(state, drone, a, topSpeed, center);
//        a.lamp = shouldLightOn(state, drone,a, -1);
//        return true;
//      }
//    }
    
    if (State.turn < 4 && drone.id > 1) {
      if (drone.id == 2) {
        a.dx = 7000 - drone.pos.x;
        a.dy = 2685 - drone.pos.y;
        a.lamp = shouldLightOn(state, drone,a, -1);
        return true;
      }
      if (drone.id == 3) {
        a.dx = 3000 - drone.pos.x;
        a.dy = 2685 - drone.pos.y;
        a.lamp = shouldLightOn(state, drone,a, -1);
        return true;
      }
    }
    return false;
  
  }

  private boolean shouldGoUp(Drone drone, Action a) {
    if (drone.currentScans.isEmpty()) return false;
    if (drone.pos.y <= 500) return false; // can't go any higher :)
    
    
    
    // si on a assez pour gagner, on remonte !
    if (scoreMinimax.willWinIfGoUp) {
      goUp(state, drone, a);
      return true;
    }
    
    
    // TODO use "minimax"
    if (forceUp.contains(drone)) {
      goUp(state, drone, a);
      return true;
    }
    
    Scan all = new Scan(state.myScans);
    all.append(state.myDrones[0].currentScans);
    all.append(state.myDrones[1].currentScans);
    if (all.isFull(state)) {
      System.err.println(" Got evertyhing ! going up");
      goUp(state, drone, a);
      return true;
    }

    if (!drone.currentScans.hasLevel(2)) return false;
    
    if (drone.currentScans.hasLevel(2)) {
      for (int l = 4; l < 16; l++) {
        if (!state.fishPresent[l])
          continue;
        if (State.fType[l] != 2)
          continue;
        if (!drone.currentScans.contains(l))
          continue;
        for (Drone oppDrone : state.oppDrones) {
          if (oppDrone.currentScans.contains(l) && oppDrone.estimateTurnsToSurface() < drone.estimateTurnsToSurface()) {
            System.err.println("Got a big fish, but opp " + oppDrone + " has it too, continuing ... ");
            return false;
          }
        }
      }
    }

    // if wingman has the same and is higher, dont go up !
    if (drone.currentScans.hasLevel(2) && otherDrone(state, drone).pos.y > drone.pos.y) {
      boolean hasDifferent = false;
      for (int l=4;l<16;l++) {
        if (!state.fishPresent[l]) continue;
        if (State.fType[l] != 2) continue; // only big fishes

        if (!otherDrone(state, drone).currentScans.contains(l)) {
          hasDifferent = true;
          break;
        }
      }
      if (!hasDifferent) return false;
    }

    
    // si on a de la marge pour remonter, on peut faire un "detour" pour chopper un poisson manquant
    int myTurnToSurface = drone.estimateTurnsToSurface();
    int oppTurnToSurface = 100;
    for (int o = 0; o < 2; o++) {
      if (state.oppDrones[o].estimateTurnsToSurface() >= myTurnToSurface) {
        oppTurnToSurface = Math.min(oppTurnToSurface, state.oppDrones[o].estimateTurnsToSurface());
      }
    }

    if (myTurnToSurface <= oppTurnToSurface) {
      if (chaseAndGoUp(drone, a, myTurnToSurface, oppTurnToSurface)) {
        return true;
      }
    }

    goUp(state, drone, a);
    return true;
  }

  private boolean chaseAndGoUp(Drone drone, Action a, int myTurnToSurface, int oppTurnToSurface) {
    if (myTurnToSurface + 1 < oppTurnToSurface) {
      if (oppTurnToSurface != 100) {
        System.err.println("     J'ai le temps de chase ! " + (oppTurnToSurface - myTurnToSurface) + " turn(s)");
      } else {
        System.err.println("     J'ai le temps de chase, je suis à la bourre :/");
      }
      // check les fish que j'ai pas et qui sont plus haut
      int bestFishToChase = -1;
      int bestDist = Integer.MAX_VALUE;
      Pos bestTarget = new Pos(-1, -1);
      
      for (int ll = 4; ll < 16; ll++) {
        if (!state.fishPresent[ll]) continue;
        if (state.myScans.contains(ll)) continue; // already have it
        if (drone.currentScans.contains(ll)) continue; // already have it inside
        Drone wingman = otherDrone(state, drone);
        
        // TODO for this 2 criteria, check if we can get it first and go up ????
        if (wingman.currentScans.contains(ll)) continue; // my wingman has it 
        if (currentTarget[wingman.id] == ll) continue; // my wingman chase it

        Pos center = state.getBestTriangulation(ll).center();
        int max600Delta = (oppTurnToSurface - myTurnToSurface) / 2;
        if (center.y - 300 * max600Delta > drone.pos.y) continue; // really too low to surface first

        int dist2 = center.dist2(drone.pos);
        if (dist2 < bestDist) {
          bestDist = dist2;
          bestFishToChase = ll;
          bestTarget.copyFrom(center);
        }
      }

      if (bestFishToChase != -1) {
        currentTarget[drone.id] = bestFishToChase;
        System.err.println("    best fish to chase is " + bestFishToChase);
        chaseTarget(state, drone, a, topSpeed, bestTarget);
        a.lamp = shouldLightOn(state, drone, a,bestFishToChase);
        return true; // technically it's still an up
      }
    }
    return false;
  }

  private boolean captureFish(Drone drone, Action a) {
    double bestScore = Double.NEGATIVE_INFINITY;
    Pos bestTarget = new Pos(drone.pos.x, 0);
    int bestFishId = -1;
    for (int fishId = 4; fishId < 16; fishId++) {
      if (!state.fishPresent[fishId]) continue;

      if (state.myScans.contains(fishId) 
          || state.myDrones[0].hasScan(fishId) 
          || state.myDrones[1].hasScan(fishId)) continue;

      Zone bestTriangulation = state.getBestTriangulation(fishId);
      Pos target = new Pos(bestTriangulation.center());

      double score = 0.0;

      if (bestFishId != -1) {
        Scan potentialScan = new Scan();
        potentialScan.copyFrom(drone.currentScans);
        potentialScan.scan(bestFishId);
        // int points = Points.deltaPoints(potentialScan, state.myScans, state.oppScans);
        // score += 1.0 * points;
      }

      score += 1_000_000 * State.fType[fishId];
      score += -0.0000001 * target.dist2(drone.pos);
      
      if (target.distToBorder() < 2000) {
        score += 500;
      }
      
      
      // check with other drone ...
      if ((drone.pos.x < 5000 && target.x > 5000) || (drone.pos.x > 5000 && target.x < 5000)) {
        score += -1000.0;
      }

      if (currentTarget[otherDrone(state, drone).id] == fishId) {
        score -= 10_000_000; // don't go after the same fish
      }
      if (score > bestScore) {
        bestScore = score;
        bestTarget.copyFrom(target);
        bestFishId = fishId;
      }
    }

    if (bestFishId != -1) {
      currentTarget[drone.id] = bestFishId;
      System.err.println("Looking for fish " + bestFishId + " with radar : " + state.getBestTriangulation(bestFishId));

      // now that we have the fish, find the best way to go to him
      chaseTarget(state, drone, a, topSpeed, bestTarget);
      // On allume la lampe si un radius 2000 a une intersection avec any cible
      a.lamp = shouldLightOn(state, drone,a, bestFishId);
      return true;
    }

    // no fish to fish anymore
    return false;
  }

  private void chaseTarget(State state, Drone drone, Action resultAction, int[] speeds, Pos bestTarget) {
    
    Vec bestSpeed = optimizer.optimize(state, drone, speeds, (tentativeState, tentativeDrone, action) -> {
      double score = 0.0;
      score += - tentativeDrone.pos.dist(bestTarget);  // dist to target
      
      if (currentTarget[drone.id] != -1 && tentativeState.fishPresent[currentTarget[drone.id]] == false) {
        return Double.NEGATIVE_INFINITY; // don't chase our own target !
      }
      
      for (Fish f : state.fishes) {
        Fish newFish = tentativeState.getFishById(f.id);
        if (newFish == null || newFish.pos.x < -500) {
          score += 1_000_000; // too big ? :p
        }
        if (State.fType[f.id] == Fish.UGLY) {
          score += Math.min(1000, tentativeDrone.pos.dist(bestTarget));
        }
        
      }
      
      return score;
    });

    System.err.println("Best speed= " + bestSpeed);
    resultAction.dx = bestSpeed.vx;
    resultAction.dy = bestSpeed.vy;
  }

  private Drone otherDrone(State state, Drone drone) {
    if (state.myDrones[0] == drone)
      return state.myDrones[1];
    else
      return state.myDrones[0];
  }

  private boolean shouldLightOn(State state, Drone drone, Action a, int bestFishId) {
    boolean shouldLightOn = false;
    System.err.println("Should light ? for drone " + drone.id);
    
    
    if (State.turn % 2 == 0) {
      System.err.println(" tour impair ...");
      return false;
    }

    
    
    Pos nextPos = new Pos(drone.pos.x, drone.pos.y);
    Vec dir = new Vec(a.dx, a.dy);
    dir.limitTo(Drone.MOVE_SPEED);
    nextPos.add(dir);
    
    
    if (drone.canTurnLight()) {

      int potentialMatches = 0;
      double totalMatch = 0.0;
      for (int l = 4; l < 16+State.uglyCount; l++) {

        if (!state.fishPresent[l]) continue; // fish does not exists anymore 
        if (state.myScans.contains(l)) continue; // I have scanned it
        if (drone.currentScans.contains(l)) continue; // Ialready have it (this turn or previously)
        if (otherDrone(state, drone).currentScans.contains(l)) continue;  

        Rectangle currentTriangulation = (Rectangle)state.getBestTriangulation(l);
        
        int viewRadiusNormal = State.fType[l] == Fish.UGLY ? 1100 : 800;
        int viewRadiusNormal2 = viewRadiusNormal*viewRadiusNormal;
        int viewRadiusBig = State.fType[l] == Fish.UGLY ? 2300 : 2000;

        Fish fish = state.getFishById(l); 
        if (fish != null) {
          if (fish.pos.dist2(nextPos) < viewRadiusNormal2) continue; // on le voit ! (pour les ugly)
          
          if (State.fType[l] == Fish.UGLY && currentTriangulation.surface() == 1) continue; // on sait où est le ugly, pas besoin d'éclairer
        }
        
        
        if(!currentTriangulation.hasPotentialIntersectionWithCircle(nextPos.x, nextPos.y, viewRadiusBig)) continue;
        double intersectionRatio = currentTriangulation.surfaceRatio(nextPos, viewRadiusBig);
        
        System.err.println("   Ratio with fish " + l + " => " + intersectionRatio);
        System.err.println("   "+l+" rect is " + (Rectangle)state.getBestTriangulation(l));
        
        // target is 0.2 if battery is 30 and 0.6 if battery is 5
        // TODO review this choice
        // double targetRatio = 0.6 - 0.4 * (drone.battery - 5) / (30 - 5);

        totalMatch += intersectionRatio;
        // TODO ne pas utiliser la light ?? si l'autre a déjà le poisson
//        if (bestFishId != l && intersectionRatio > 0.5) {
//          System.err.println("   Potential match between " + drone.id + " and any fish " + l);
//          potentialMatches++;
//        }
//        
//        if (bestFishId == l) {
//          System.err.println("   Potential match between " + drone.id + " and Target FISH " + l);
//          if ((drone.battery > 10 && intersectionRatio > 0.1) || intersectionRatio > 0.2) {
//            potentialMatches++;
//          }
//        }
      }

      if (totalMatch >= 0.5) {
        System.err.println("   Drone " + drone.id + " found " + potentialMatches + " potential matches with total ratio : "+totalMatch+" , turning the light ON");
        shouldLightOn = true;
      } else {
        System.err.println("   Drone " + drone.id + " found  no potential matches, light off");
      }
    }
    return shouldLightOn;
  }

  private void goUp(State state, Drone drone, Action finalAction) {
    // go up
    System.err.println("Going up ... looking for best angle");

    Vec bestSpeed = optimizer.optimize(state, drone, topSpeed, (tentativeState, tentativeDrone, action) -> {
      double score = 10_000 - action.dy;
      if ((action.dx > 0 && tentativeDrone.pos.x > 9500) || (action.dx < 0 && tentativeDrone.pos.x < 500)) {
        score -= 1000;
      }
      return score;
    });

    System.err.println("Best speed: " + bestSpeed);
    finalAction.dx = bestSpeed.vx;

    if (drone.pos.y + bestSpeed.vy < 500) {
      // TODO prepare for next descent if possible ? by reducing Y, we can influence X!
      System.err.println("Go just @ 500. TODO : can we decal x too to prepare for descent ?");
      finalAction.dy = 500 - drone.pos.y;
    } else {
      finalAction.dy = bestSpeed.vy;
    }

    finalAction.lamp = shouldLightOn(state, drone, finalAction, -1 /* we go up, no best fish anymore */);

    if (!finalAction.lamp && drone.canTurnLight()) {
      // check if we may encounter a ugly fish
      Rectangle r = new Rectangle(drone.pos.x - 300, drone.pos.y - 600 * 2, 2 * 300, 600);
      boolean caution = false;
      for (int i = 16; i < 16 + State.uglyCount; i++) {
        Fish ugly = state.getFishById(i);
        if ( ugly == null && r.hasIntersection((Rectangle) state.getBestTriangulation(i))) {
          System.err.println("Going up, may encounter " + i);
          caution = true;
        }
      }
      if (caution) {
        System.err.println("   need to be cautious, turning light on");
        finalAction.lamp = true;
      }
    }
  }
}
