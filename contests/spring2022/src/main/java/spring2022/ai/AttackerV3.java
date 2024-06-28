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
import spring2022.ai.microai.HitMaxBots;
import spring2022.ai.microai.TakeOpportunity;
import spring2022.ai.microai.UseWindTowardsOppBase;

public class AttackerV3 {
  private static final HitMaxBots HIT_MAX_BOTS = new HitMaxBots();

  private static final AttackNearest ATTACK_NEAREST = new AttackNearest();
  
  private static final Pos CENTER = new Pos(17630 /2 , 9000/2);
  private static final MobInterceptor mobInterceptor = new MobInterceptor();
  private static ThreadLocalRandom random = ThreadLocalRandom.current();

  
  public final static int NEED_MANA_THRESHOLD = 60;
  public final static int NO_NEED_MANA_THRESHOLD = 100;
  
  public final static int AGGRO_MANA_THRESHOLD = 200;

   
  public final static int MIND_EARLYGAME = 0;
  public final static int MIND_NEED_MANA = 1;
  public final static int MIND_AGGRO = 2;

  private State state;
  private Hero hero;
  int mind = MIND_EARLYGAME;

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
  
  int sawCenterAt;
  
  public Action attack(State state, Hero hero) {
    this.state = state;
    this.hero = hero;
    
    System.err.println("*************************");
    System.err.println("*     ATTACKER V3       *");
    System.err.println("*************************");
    outputForDebug();
    
    
    switch(mind) {
    case MIND_EARLYGAME:
      changeMindFromEarlyGame();break;
    case MIND_NEED_MANA:
      changeMindFromNeedMana();break;
    case MIND_AGGRO:
      changeMindFromAggro();break;
    default:
      changeMindFromEarlyGame();break;
    }
    
    
    Action action;
    if ((action = TakeOpportunity.i.think(state, hero)) != Action.WAIT) return action;

    if (mind == MIND_NEED_MANA) {
      if ((action = shouldGetBackSomeMana()) != Action.WAIT) return action;
      return patrol();
      
    } else if (mind == MIND_AGGRO) {

      if ((action = UseWindTowardsOppBase.i.think(state, hero)) != Action.WAIT) return action;
      if ((action = sendMobToOppBase()) != Action.WAIT) return action;
      if ((action = sendOppsFurther()) != Action.WAIT) return action;
      
      return attackOppBase();
      
    } else {
      if ((action = ATTACK_NEAREST.think(state, hero)) != Action.WAIT) return action;

      return patrol();
    }
    
  }

  private void changeMindFromEarlyGame() {
    if (Player.turn >= 100 || state.mana[0] > 200) {
      System.err.println("Changing mind to AGGRO");
      mind = MIND_AGGRO;
    }
  }
  
  
  private void changeMindFromAggro() {
    if (state.mana[0] < NEED_MANA_THRESHOLD) {
      System.err.println("Changing mind to MANA");
      mind = MIND_NEED_MANA;
    }
  }

  private void changeMindFromNeedMana() {
    if (state.mana[0] > NO_NEED_MANA_THRESHOLD) {
      System.err.println("Changing mind to AGGRO");
      mind = MIND_AGGRO;
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
    
    // 1st wind
    // TODO check units ?
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
    
    
    // then control
    for (Hero opp : state.oppHeroes) {
      if (opp.hasShield()) continue;
      
      if (opp.isInRange(hero, State.CONTROL_RANGE)) {
        return Action.doControl(opp.id, State.myBase); // TODO better angle ?
      }
    }
    
    return Action.WAIT;
  }


  private static Action attackOppBase() {
    return Action.doMove(13000, 4975); // just out of range
  }


  /**
   * check if we can 'control' a mob and send it towards opp base
   * @return
   */
  private Action sendMobToOppBase() {
    if (state.mana[0] < 10) return Action.WAIT;
    
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.isDead()) continue;
      if (!unit.isInRange(hero, State.CONTROL_RANGE)) continue;
      if (unit.health < 20) continue;   // pas assez de vie
      if (unit.isInRange(State.oppBase, State.BASE_TARGET_DIST + 1000)) continue; // trop pret de la base
      if (!unit.isInRange(State.oppBase, State.BASE_TARGET_DIST + 4000)) continue; // trop loin de la base
      if (unit.nearBase || unit.threatFor == 2) continue; // pas besoin si elle va déjà dans la direction
      
      //TODO check current dir
      
      
      Pos target = State.oppBase;
      // System.err.println(" Mind control "+unit.id+" into "+target);
      return Action.doControl(unit.id, target);
    
    }
    return Action.WAIT;
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
