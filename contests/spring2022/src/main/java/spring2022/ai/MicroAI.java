package spring2022.ai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.State;

public interface MicroAI {
  public Action think(State state, Hero hero);
}