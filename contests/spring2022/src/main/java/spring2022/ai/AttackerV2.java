package spring2022.ai;

import java.util.concurrent.ThreadLocalRandom;

import fast.read.FastReader;
import spring2022.Action;
import spring2022.Hero;
import spring2022.Player;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;
import spring2022.ai.microai.AttackNearest;
import spring2022.ai.microai.ControlMobToOppBase;
import spring2022.ai.microai.FollowMobIntoBse;
import spring2022.ai.microai.HitMaxBots;
import spring2022.ai.microai.InitPatrol;
import spring2022.ai.microai.ShieldAttack;
import spring2022.ai.microai.TakeOpportunity;
import spring2022.ai.microai.UseWindTowardsOppBase;

public class AttackerV2 {
  private static final ShieldAttack SHIELD_ATTACK = new ShieldAttack();
  private static final HitMaxBots HIT_MAX_BOTS = new HitMaxBots();
  private static final InitPatrol INIT_PATROL = new InitPatrol();
  private static final AttackNearest ATTACK_NEAREST = new AttackNearest();
  
  private static final Pos CENTER = new Pos(17630 /2 , 9000/2);
  private static final MobInterceptor mobInterceptor = new MobInterceptor();
  private static ThreadLocalRandom random = ThreadLocalRandom.current();
  
  public final static int NEED_MANA_THRESHOLD = 60;
  public final static int NO_NEED_MANA_THRESHOLD = 100;
  
  
  public final static int MIND_EARLYGAME = 0;
  public final static int MIND_NEED_MANA = 1;
  public final static int MIND_AGGRO = 2;

  private State state;
  private Hero hero;
  int mind = MIND_EARLYGAME;
  //PatrolOppBaseNeighborood patrolOppBaseNeighborood = new PatrolOppBaseNeighborood();
  
  public void outputForDebug() {
    if (State.DEBUG_INPUTS) {
      System.err.println("^turn "+Player.turn);
      System.err.println("^mind "+mind);
    }
  }

  public void read(FastReader in) {
    in.nextString();
    Player.turn = in.nextInt();
    
    in.nextString();
    mind = in.nextInt();
    
  }
  
  public void updateTurn(State state) {
    state = state;
  }

  int sawCenterAt;
  
  public Action think(State state, Hero hero) {
    this.state = state;
    this.hero = hero;
    
    System.err.println("*************************");
    System.err.println("*     ATTACKER V2       *");
    System.err.println("*************************");
    outputForDebug();
    
    
    final int ATTACK_TURN = 100;

    // histeresis for mana
    if (mind == MIND_AGGRO || mind == MIND_NEED_MANA) {
      if (state.mana[0] < NEED_MANA_THRESHOLD) mind = MIND_NEED_MANA;
      if (state.mana[0] > NO_NEED_MANA_THRESHOLD) mind = MIND_AGGRO;
    } else {
      if (Player.turn > ATTACK_TURN || state.mana[0] > 200) {
        mind = MIND_AGGRO;
      } else {
        mind = MIND_EARLYGAME;
      }
    }
    
    Action action;

    
    if ((action = TakeOpportunity.i.think(state, hero)) != Action.WAIT) return action;
    
    
    if (mind == MIND_NEED_MANA) {
      if ((action = UseWindTowardsOppBase.i.think(state, hero)) != Action.WAIT) return action;
      if ((action = shouldGetBackSomeMana()) != Action.WAIT) return action;
      if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT) return action;
      //if ((action = patrolOppBaseNeighborood.think(state, hero)) != Action.WAIT) return action;
      
      return patrol();
    }
    if (mind == MIND_AGGRO) {
      if ((action = SHIELD_ATTACK.think(state, hero)) != Action.WAIT) return action;
      if ((action = UseWindTowardsOppBase.i.think(state, hero)) != Action.WAIT) return action;
      if ((action = FollowMobIntoBse.i.think(state, hero)) != Action.WAIT) return action;
      
      if ((action = ControlMobToOppBase.i.think(state, hero)) != Action.WAIT) return action;
      if ((action = sendOppsFurther()) != Action.WAIT) return action;

      if ((action = attackOppBase()) != Action.WAIT) return action;
      
      if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT) return action;
      //if ((action = patrolOppBaseNeighborood.think(state, hero)) != Action.WAIT) return action;

      return patrol();
      
    } else if (mind == MIND_EARLYGAME) {
      if (Player.turn < 10) {
        if ((action = INIT_PATROL.think(state, hero)) != Action.WAIT) return action;
      }
      if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT) return action;
      return patrol();
    } else {
      System.out.println("Unknwon mind "+mind);
      return Action.WAIT;
    }
    
  }

  private Action shouldGetBackSomeMana() {
    if (mind != MIND_NEED_MANA) return Action.WAIT;
    
    System.err.println("In need of mana");
    Action action;
    
    if ((action = HIT_MAX_BOTS.think(state, hero)) != Action.WAIT) return action;
    if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT) return action;

    return patrol();
  }

  private Action sendOppsFurther() {
    if (state.mana[0] < 10) return Action.WAIT;
    if (!hero.isInRange(State.oppBase, State.BASE_TARGET_DIST + 2000)) return Action.WAIT;
    
    boolean unitInOppBaseRadius = false;
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.isInRange(State.oppBase, State.BASE_TARGET_DIST)) {
        unitInOppBaseRadius = true;
        break;
      }
    }
    if (!unitInOppBaseRadius) return Action.WAIT; // do send him back if there is no mobs in his base
    // TODO au contraire ???
    
    
 // then try to control
    for (Hero opp : state.oppHeroes) {
      if (opp.hasShield()) continue;
      
      if (opp.isInRange(hero, State.CONTROL_RANGE)) {
        return Action.doControl(opp.id, State.myBase); // TODO better angle ?
      }
    }
    
    
    // 1st wind
    int windCount = 0;
    for (Hero opp : state.oppHeroes) {
      if (opp.hasShield()) continue;
      if (opp.isInRange(hero, State.WIND_RANGE)) {
        windCount++;
      }
    }
    if (windCount > 0) {
      // opposé du centre TODO better to do ?
      return Action.doWind(hero.pos.x - State.oppBase.x, hero.pos.y - State.oppBase.y);
    }
    
    
    
    return Action.WAIT;
  }


  private Action attackOppBase() {
    if (hero.pos.isInRange(State.oppBase, State.BASE_TARGET_DIST + 1000)) return Action.WAIT;
    
    return Action.doMove(State.oppBase); // just out of range
  }

  static Pos patrols[] = new Pos[]{ new Pos(7500, 7000), new Pos(12000, 2000), new Pos(13000, 4975) };
  private Action patrol() {
    if (hero.isInRange(CENTER, 200)) {
      sawCenterAt = Player.turn;
    }
    
    if (sawCenterAt < Player.turn - 15) {
      System.err.println("Moving to center");
      return Action.doMove(CENTER);
    } else {
      System.err.println("Patrolling ");
      
      return Action.doMove(patrols[random.nextInt(patrols.length)]);
    }
    
  }
}
