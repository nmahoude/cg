package fall2023;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AIMonteCarlo implements AIInterface {
  private static final Random random = ThreadLocalRandom.current();
  private static final Simulator SIM = new Simulator();
  private static final ScoreMinimax scoreMinimax = new ScoreMinimax();
  
  private State work = new State();
  
  @Override
  public Action[] think(State state) {
    Action[] bestActions = new Action[] { new Action(), new Action(), new Action(), new Action() };
    Action[] actions = new Action[] { new Action(), new Action(), new Action(), new Action() };

    
    scoreMinimax.think(state);
    
    double bestScore = Double.NEGATIVE_INFINITY;
    for (int sims =0;sims<1000;sims++) {
    
      for (Drone drone : state.myDrones) {
        Action a = actions[drone.id];
        
        double angle = random.nextDouble(1);
        int speed = 600;
        a.dx = (int)(speed * Math.cos(angle * 2 * Math.PI ));
        a.dy = - (int)(speed * Math.sin(angle * 2 * Math.PI ));
        if (drone.canTurnLight()) {
          a.lamp = random.nextBoolean();
        } else {
          a.lamp = false;
        }
      }
      
      
      work.copyFrom(state);
      SIM.applyJustMe(work, actions);
      
      double score = eval(work, actions);
      if (score > bestScore) {
        bestScore = score;
        for (Drone drone : state.myDrones) {
          bestActions[drone.id].copyFrom(actions[drone.id]);
        }        
      }
    }
    
    return bestActions;
  }

  Scan wScan = new Scan();
  Rectangle wRect = new Rectangle();
  private double eval(State current, Action[] actions) {
    double score = 0.0;
    score += 10_000 * current.myScore;

    for (Drone d : current.myDrones) {
      if (d.emergency) score -= 10_000_000;
      Action a = actions[d.id];
      
      int lightRadius = a.lamp ? Drone.BIG_LIGHT_RADIUS : Drone.NORMAL_LIGHT_RADIUS;
      
      Pos best = new Pos(0,0);
      int bestFish = -1;
      int bestDist2 = Integer.MAX_VALUE;
      for (int i=4;i<16;i++) {
        if (!current.fishPresent[i]) {
          continue;
        }
        if (d.currentScans.contains(i)) continue;
        
        Zone triangulation = current.getBestTriangulation(i);
        
        double ratio = triangulation.surfaceRatio(d.pos, lightRadius);
        score += 0.001 * ratio;
        
        int dist2 = d.pos.dist2(triangulation.center());
        if (dist2 < bestDist2) {
          bestFish = i;
          bestDist2 = dist2;
          best.copyFrom(triangulation.center());
        }
      }
      
      if (bestDist2 != Integer.MAX_VALUE) {
        score += State.fType[bestFish];
        score -= Math.sqrt(bestDist2);
      }
      
    
      score += 0.0001 * d.battery;
      if (d.pos.y < 490) score -= 1000; // keep on the limit, not higher 
      
      if (scoreMinimax.willLoseIfNotUp) {
        score -= d.pos.y;
      } else {
        score += d.pos.y;
      }
    }
    
    
    return score;
  }

  
}
