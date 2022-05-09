package spring2022.ai.micro;

import spring2022.Action;
import spring2022.Hero;
import spring2022.MobInterceptor;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.MicroAI;

public class AttackNearest implements MicroAI {
  static final MobInterceptor mobInterceptor = new MobInterceptor();
  
  public Action think(State state, Hero hero) {
    return think(state, hero, hero.pos, 100_000);
  }

  public Action think(State state, Hero hero, Pos nearestFrom, int maxDist) {
   
    double bestScore = Double.NEGATIVE_INFINITY;
    Unit best = null;
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.forbiddenToHit()) continue;
      if (!unit.isInRange(State.myBase, maxDist)) continue;
      
      mobInterceptor.stepsAndPosToIntercept(state, hero, unit);
      Pos interceptPos = mobInterceptor.interceptPosition;
      
      if (interceptPos.equals(Pos.VOID)) continue;
      
      State future = State.future.get(mobInterceptor.interceptSteps);
      int mobsCount = future.getUnitCountInRange(interceptPos, State.MONSTER_TARGET_KILL);
      
      // Player.draw.drawLine(Color.BLACK, hero.pos, interceptPos);
      // draw.drawText(Color.BLACK, interceptPos, ""+mobsCount);
      
      int dist = nearestFrom.fastDist(unit.pos);
      
      double score = -100*dist + 10 * ( 100 - mobInterceptor.interceptSteps) + mobsCount ; // low steps is better, then mobsCount is better
      if (unit.isInFog()) {
        score -= 5; // in fog is worst
      }
      if (score > bestScore) {
        bestScore = score;
        best = unit;
      }
    }
    if (best != null) {
      System.err.println("attacking "+best);
      Pos target = mobInterceptor.intercept(state, hero, best);
      System.err.println("Optimizing ... to "+target);
      
      return Action.doMove(target);
    } else {
      return Action.WAIT;
    }
  }

}
