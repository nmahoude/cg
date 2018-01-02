package codeBusters.entities.som;

import codeBusters.P;
import codeBusters.Player;
import codeBusters.entities.Buster;
import codeBusters.entities.Ghost;
import codeBusters.entities.State;

public class Wander extends StateOfMind {
  public Wander(Buster self) {
    super(self);
    target = getNextTarget();
  }
  
  public P target;
  
  @Override
  public String output() {
    if (self.carried == Ghost.noGhost && self.stun==0) {
      Buster bestToBurst = null;
      double bestScoreToBurst = -1000;
      
      for (Buster buster : Player.hisTeam.elements) {
        if (buster.position.x == -1) continue;
        if (buster.state == 2) continue; // don't restun TODO maybe it would be good to restun ...
        if (buster.position.dist2(self.position) > Player.BUSTER_RANGE_2) continue;

        double score = 0.0;
        if (buster.carried != Ghost.noGhost) {
          score = 1.0;
        }
        if (score > bestScoreToBurst) {
          bestScoreToBurst = score;
          bestToBurst = buster;
        }
      }
      if (bestToBurst != null) {
        self.stun = 20;
        return "STUN "+bestToBurst.id;
      }
    }
    
    Ghost ghost = chooseGhost(self);
    if (ghost != null && ghost.state.isOnMap() && ghost.position.dist2(self.position) < Player.RANGE_TO_BUST_GHOST_2) {
      System.err.println(self.id + " bestghost is "+ghost.id);
      // TRY TO catch the ghost
      return "BUST "+ghost.id;
    }
    
    if (self.position.dist2(target) < 5) {
      target = getNextTarget();
    }
    return "MOVE "+target.x+" "+target.y;
  }

  private P getNextTarget() {
    Ghost bestGhost = chooseGhost(self);
    if (bestGhost != null) {
      System.err.println(self.id +" wandering to ghost "+bestGhost.id+" in fog");
      return new P(bestGhost.position.x, bestGhost.position.y);
    } else {
      return new P(Player.rand.nextInt(16000), Player.rand.nextInt(9000));
    }
  }

  public static Ghost chooseGhost(Buster self) {
    Ghost bestGhost = null;
    double bestScore = -1000.0;
    
    for (Ghost ghost : Player.ghosts.elements) {
      if (ghost.state == State.FREE || ghost.state == State.IN_FOG) {
        double score = 1.0 / (1.0 * ghost.energy * ghost.position.dist2(self.position));
        if (score > bestScore) {
          bestScore = score;
          bestGhost = ghost;
        }
      }
    }
    return bestGhost;
  }
}
