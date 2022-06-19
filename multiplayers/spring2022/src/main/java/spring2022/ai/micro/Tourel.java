package spring2022.ai.micro;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.ai.MicroAI;

public class Tourel implements MicroAI{

  @Override
  public Action think(State state, Hero hero) {
    Pos tourels[] = new Pos[] { Pos.get(5500, 2000), Pos.get(3000, 4500) };

    if (hero.pos.isInRange(tourels[hero.index()], 400)) {
      return Action.WAIT;
    } else {
      return Action.doMove(tourels[hero.index()]);
    }
  }

}
