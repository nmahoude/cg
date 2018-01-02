package codeBusters.entities.som;

import codeBusters.Player;
import codeBusters.entities.Buster;

public class ReturnToBase extends StateOfMind {
  public ReturnToBase(Buster self) {
    super(self);
  }
  
  @Override
  public String output() {
    if (self.position.dist2(Player.myBase) < Player.BASE_RANGE_2) {
      self.stateOfMind = new Wander(self);
      Player.myScore++;
      return "RELEASE";
    } else {
      return "MOVE "+Player.myBase.x +" " + Player.myBase.y;
    }
  }
}
