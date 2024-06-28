package spring2022.ai.microai;

import spring2022.Action;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.MicroAI;
import spring2022.ai.MobInterceptor;

public class AttackNearest implements MicroAI {
  static final MobInterceptor mobInterceptor = new MobInterceptor();
  
  public Action think(State state, Hero hero) {
   
    double bestScore = Double.NEGATIVE_INFINITY;
    Unit best = null;
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.forbiddenToHit()) continue;
      
      mobInterceptor.stepsAndPosToIntercept(state, hero, unit);
      Pos interceptPos = mobInterceptor.interceptPosition;
      // System.err.println("Checking Unit "+unit);
      // System.err.println( "  intercept going to "+interceptPos);
      
      if (interceptPos.equals(Pos.VOID)) continue;
      if (interceptPos.isInRange(State.oppBase, State.BASE_TARGET_DIST + 1000)) continue; // dont follow (and so hit) if near opp base  
      if (interceptPos.isInRange(State.myBase, State.BASE_TARGET_DIST + 2000)) continue; // defending base is not my job 
      
      State future = State.future.get(mobInterceptor.interceptSteps);
      int mobsCount = future.getUnitCountInRange(interceptPos, State.MONSTER_TARGET_KILL);
      
      // Player.draw.drawLine(Color.BLACK, hero.pos, interceptPos);
      // draw.drawText(Color.BLACK, interceptPos, ""+mobsCount);
      
      double score =  10 * ( 100 - mobInterceptor.interceptSteps) + mobsCount ; // low steps is better, then mobsCount is better
      // System.err.println("     score of unit "+unit+" is "+score + "( "+mobInterceptor.interceptSteps+" / future : "+mobsCount+")");
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
