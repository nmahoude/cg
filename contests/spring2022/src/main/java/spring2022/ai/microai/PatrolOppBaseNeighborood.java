package spring2022.ai.microai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.ai.MicroAI;

/**
 * Statefull :/
 *
 */
public class PatrolOppBaseNeighborood implements MicroAI {
  private static final Pos[] points = new Pos[] { new Pos(12000, 8000), new Pos(16000, 3500) }; 
  
  
  int nextPoint = -1;
  Pos tmp = new Pos();
  
  @Override
  public Action think(State state, Hero hero) {
    if (nextPoint == -1) {
      if (hero.pos.dist2(points[0]) < hero.pos.dist2(points[1])) {
        nextPoint = 0;
      } else {
        nextPoint = 1;
      }
    }

    if (hero.isInRange(points[nextPoint], 200)) {
      nextPoint = (nextPoint + 1) % points.length;
    }
  
    tmp.copyFrom(hero.pos);
    tmp.moveToward(points[nextPoint], State.HERO_MAX_MOVE);
    return Action.doMove(tmp);
  }
  
}
