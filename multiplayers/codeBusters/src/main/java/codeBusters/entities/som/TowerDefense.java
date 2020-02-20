package codeBusters.entities.som;

import codeBusters.P;
import codeBusters.Player;
import codeBusters.entities.Buster;
import codeBusters.entities.Ghost;

/**
 * Go to enemy pool to try to get a ghost back
 *
 */
public class TowerDefense extends StateOfMind {
  P target = new P(0, 0);

  public TowerDefense(Buster self) {
    super(self);

    if (Player.myBase.x == 0) {
      target.x = Player.WIDTH-1000;
      target.y = Player.HEIGHT-1000;
    } else {
      target.x = 1000;
      target.y = 1000;
    }

  }


  @Override
  public String output() {
    if (self.position.dist2(target) < 100) {
      // check for buster with ghost
      for (Buster buster: Player.hisTeam) {
        if (self.stun == 0 && buster.carried != Ghost.noGhost && buster.position.dist2(self.position) < Player.STUN_RANGE_2) {
          return "STUN "+buster.id;
        }
      }
      Ghost g = Wander.chooseGhost(self);
      if (g != null && g.position.dist2(self.position) < Player.BUSTER_RANGE_2) {
        done = true;
        return "BUST "+g.id;
      }
    }
    return "MOVE "+target.x+" "+target.y+" TOWDEF";
  }
}
