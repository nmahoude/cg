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
      for (Buster buster : Player.hisTeam.elements) {
        if (buster.position.x == -1) continue;
        if (buster.state == 2) continue; // don't restun TODO maybe it would be good to restun ...
        if (buster.position.dist2(self.position) > Player.BUSTER_RANGE_2) continue;

        self.stun = 20;
        return "STUN "+buster.id;
      }
    }
    for (Ghost ghost : Player.ghosts.elements) {
      if (ghost.state.isOnMap() && ghost.position.dist2(self.position) < Player.GHOST_RANGE_2) {
        // TRY TO catch the ghost
        return "BUST "+ghost.id;
      }
    }
    if (self.position.equals(target)) {
      target = getNextTarget();
    }
    return "MOVE "+target.x+" "+target.y;
  }

  private P getNextTarget() {
    for (Ghost ghost : Player.ghosts.elements) {
      if (ghost.state == State.IN_FOG) {
        System.err.println("WANDERING TO A GHOST IN FOG");
        ghost.state = State.UNKNOWN; // reset la position pour pas qu'un autre vienne voir
        return new P(ghost.position.x, ghost.position.y);
      }
    }
    return new P(Player.rand.nextInt(16000), Player.rand.nextInt(9000));
  }
}
