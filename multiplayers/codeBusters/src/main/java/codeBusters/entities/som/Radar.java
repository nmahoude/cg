package codeBusters.entities.som;


import codeBusters.P;
import codeBusters.Player;
import codeBusters.entities.Buster;

/**
 * Go to specify target and use radar
 * @author nmahoude
 *
 */
public class Radar extends StateOfMind {
  public P target = new P(Player.WIDTH/2, Player.HEIGHT/2);
  
  public Radar(Buster self) {
    super(self);
  }
  
  @Override
  public String output() {
    if (self.position.dist2(target) > 100) {
      return "MOVE "+target.x+" "+target.y+" going radar";
    } else {
      done = true;
      return "RADAR";
    }
  }

}
