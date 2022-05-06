package spring2022.ai.micro;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.ai.MicroAI;

public class Tourel implements MicroAI{

  @Override
  public Action think(State state, Hero hero) {
    Pos target = Pos.get(5000, 3000);

    if (hero.pos.isInRange(target, 400)) {
      return Action.WAIT;
    } else {
      return Action.doMove(target);
    }
  }

}
