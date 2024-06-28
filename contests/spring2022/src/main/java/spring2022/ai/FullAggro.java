package spring2022.ai;

import java.util.concurrent.ThreadLocalRandom;

import fast.read.FastReader;
import spring2022.Action;
import spring2022.Hero;
import spring2022.Player;
import spring2022.Pos;
import spring2022.State;
import spring2022.ai.microai.AttackNearest;
import spring2022.ai.microai.ControlMobToOppBase;
import spring2022.ai.microai.HitMaxBots;
import spring2022.ai.microai.InitPatrol;
import spring2022.ai.microai.PatrolOppBaseNeighborood;
import spring2022.ai.microai.ShieldAttack;
import spring2022.ai.microai.TakeOpportunity;
import spring2022.ai.microai.UseWindTowardsOppBase;

public class FullAggro {
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
  PatrolOppBaseNeighborood patrolOppBaseNeighborood = new PatrolOppBaseNeighborood();
  
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
  
  public Action attack(State state, Hero hero) {
    this.state = state;
    this.hero = hero;
    
    System.err.println("*************************");
    System.err.println("*     FULL AGGRO        *");
    System.err.println("*************************");
    outputForDebug();
    
    
    Action action;
    
    if ((action = TakeOpportunity.i.think(state, hero)) != Action.WAIT) return action;
    if ((action = UseWindTowardsOppBase.i.think(state, hero)) != Action.WAIT) return action;
    if ((action = ControlMobToOppBase.i.think(state, hero)) != Action.WAIT) return action;
    if ((action = SHIELD_ATTACK.think(state, hero)) != Action.WAIT) return action;
    return patrolOppBaseNeighborood.think(state, hero);
    
  }

}
