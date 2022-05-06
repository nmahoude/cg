package spring2022;

import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;

public class MobInterceptor {

  public static final boolean DEBUG = false;
  public static int CHECK_STEP = 50;
  public static boolean DEBUG_STEPS = false;

  public final static int MAX_STEPS_TO_INTERCEPT = 10;

  
  final Pos initialTarget =  new Pos();
  int initialSteps;
  
  final Pos realTarget = new Pos();
  int realSteps;
  public int mobsCount;

  
  // intercept evaluation result
  public int interceptSteps = 0;
  public final Pos interceptPosition = new Pos();

  
  
  final Pos nextTurnTargetPos = new Pos();
  public Pos intercept(State state, Hero hero, Unit target) {
    initialTarget.copyFrom(target.pos);
    initialSteps = (Math.max(0, (hero.pos.dist(target.pos)) / State.HERO_MAX_MOVE));
    
    
    if (DEBUG) System.err.println("*** DEBUG MobInterceptor *** ");
    if (DEBUG) System.err.println("NAIVE : " + hero.id + " @ " + hero.pos + " need " + initialSteps + " steps to reach " + target.id + " @ " + target.pos);

    if (initialSteps <= 1) {
      // ici : on est sur de le taper au prochain coup, mais on peut optimiser pour le tapper, rester dans la hitbox  ET tapper un autre!
      
      nextTurnTargetPos.copyFrom(target.pos);
      nextTurnTargetPos.add(target.speed); // next turn pos to glue on target as a best solution
      
      
      if (DEBUG) System.err.println("Can optimize interceptor by hitting another mob ? ");
      
      Pos bestPos = bestPosFor(state, hero.pos, target.pos, nextTurnTargetPos);

      realSteps = 1;
      realTarget.copyFrom(bestPos);
      return bestPos;

    } else {
      if (DEBUG) System.err.println("Need more than one steps, so let's intercept on correct trajectory");
      
      stepsAndPosToIntercept(state, hero, target);
      
      realSteps = interceptSteps;
      if (interceptSteps == 200) {
        System.err.println("Can't reach it !");
        realSteps = 200;
        realTarget.copyFrom(Pos.VOID);
        return realTarget;
      }
      realTarget.copyFrom(interceptPosition);
      
      // TODO check if we can find a near pos where i hit a monster too, keeping the number of steps 
      if (DEBUG) System.err.println("targeting " + realTarget+" in "+interceptSteps);
      if (DEBUG) System.err.println("TODO : can we find a better pos for the route to hit some monster ? ");
      return interceptPosition.stepFrom(hero.pos, State.HERO_MAX_MOVE);
      
    }
  }
  
  
  final Pos bestPosToIntercept = new Pos();
  final Pos mobPos = new Pos();
  public void stepsAndPosToIntercept(State state, Hero hero, Unit unit) {
    int bestSteps = Integer.MAX_VALUE;
    int bestStepAt = 0;
    mobPos.copyFrom(unit.pos);
    
    for (int i=0;i<MAX_STEPS_TO_INTERCEPT;i++) {
      if (!mobPos.insideOfMap()) {
        bestPosToIntercept.copyFrom(Pos.VOID);
        bestStepAt = 200;
        break;
      }
      int stepToNewPos = Math.max(0, (int)(0.5 + (1.0 * hero.pos.dist(mobPos) - State.MONSTER_TARGET_KILL) / State.HERO_MAX_MOVE));
      int diff = Math.abs(stepToNewPos - i);
      if (diff < bestSteps) {
        bestSteps = diff;
        bestStepAt = i;
        bestPosToIntercept.copyFrom(mobPos);
      } else if (diff > bestSteps) {
        break;
      }
      mobPos.add(unit.speed);
    }
    
    interceptSteps = bestStepAt;
    interceptPosition.copyFrom(bestPosToIntercept);
  }
  
  final Pos myNextPos = new Pos();
  final Pos bestPos = new Pos();
  final boolean[] forbidenUnits = new boolean[200];
  public Pos bestPosFor(State state, Pos startPos, Pos currentTarget, Pos currentTargetNextPos) {
    int bestMobsCount = 0;
    bestPos.copyFrom(Pos.VOID);
    
    // precalculates unit i should not try to hit (if i do, tant pis)
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];
      if (unit.isDead()) { forbidenUnits[u] = true; }
      else if (unit.forbiddenToHit()) { forbidenUnits[u] = true; }
      else { forbidenUnits[u] = false; }
    }    
    
    for (int dy = -800; dy <= 800; dy += CHECK_STEP) {
      for (int dx = -800; dx <= 800; dx += CHECK_STEP) {
        if (dx * dx + dy * dy > 800 * 800) {
          if (DEBUG_STEPS) System.err.print(" ");
          continue;
        }
        myNextPos.copyFrom(startPos);
        myNextPos.addAndSnap(dx, dy);

        // we need to hit the chosen mob ! we will move but not him before the combat resolution
        if (currentTarget != null && myNextPos.dist2(currentTarget) > State.MONSTER_TARGET_KILL2) {
          if (DEBUG_STEPS) System.err.print("X");
          continue;
        }
        
        // calculer le nombre de mobs qu'on peut hit sur ce tour (les mobs n'auront pas bouger avant la résolution du combat)
        int mobsCount = 0;
        for (int u= 0;u<state.unitsFE;u++) {
          Unit unit = state.fastUnits[u];
          if (forbidenUnits[u]) continue;

          if (myNextPos.dist2(unit.pos) < State.MONSTER_TARGET_KILL2)
            mobsCount++;
        }

        if (DEBUG_STEPS) System.err.print(mobsCount > 9 ? "+" : mobsCount);

        if (mobsCount > bestMobsCount) {
          bestMobsCount = mobsCount;
          bestPos.copyFrom(myNextPos);
        } else if (mobsCount == bestMobsCount) {
          // meme nombre de mob, mais on est plus pret de la prochaine position de notre target, donc on le choisi
          if (currentTargetNextPos != null &&  myNextPos.dist2(currentTargetNextPos) < bestPos.dist2(currentTargetNextPos)) {
            bestPos.copyFrom(myNextPos);
          }
        }
      }
      if (DEBUG_STEPS) System.err.println();
    }
    
    if (bestMobsCount == 1) {
      if (DEBUG) System.err.println("targeting only target @" + currentTargetNextPos+" by going to closest pos : "+bestPos);
    } else {
      if (DEBUG) System.err.println("targeting multiple targets " +bestMobsCount + " by going to closest pos : "+bestPos);
    }
    
    mobsCount = bestMobsCount;
        
    return bestPos;
  }
  
}
