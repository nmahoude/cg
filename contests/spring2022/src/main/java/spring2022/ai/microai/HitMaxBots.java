package spring2022.ai.microai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.ai.MicroAI;
import spring2022.ai.MobInterceptor;

/**
 * no target, but should hit the maximum of bots
 * @author nmahoude
 *
 */
public class HitMaxBots implements MicroAI {
  public static final HitMaxBots i = new HitMaxBots();
  private static final MobInterceptor mobInterceptor = new MobInterceptor();

  @Override
  public Action think(State state, Hero hero) {
    System.err.println("Move for max mobs !");
    Pos intercept = mobInterceptor.bestPosFor(state, hero.pos, null, null);
    if (intercept.equals(Pos.VOID)) return Action.WAIT;
    
    System.err.println("Found "+mobInterceptor.mobsCount+" @ "+intercept);
    return Action.doMove(intercept);
  }

}
