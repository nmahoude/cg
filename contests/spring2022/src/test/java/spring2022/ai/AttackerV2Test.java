package spring2022.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spring2022.Action;
import spring2022.Pos;
import spring2022.State;
import spring2022.ai.microai.HitMaxBots;

public class AttackerV2Test {
  AttackerV2 attacker = new AttackerV2();
  
  private State state;

  @BeforeEach
  public void setup() {
    state = State.fromInput( """
^3 64 3 290
^9
^0 1 2385 1708 0 0 -1 -1 -1 -1 -1
^1 1 4379 1651 0 0 -1 -1 -1 -1 -1
^2 1 10740 4455 0 0 -1 -1 -1 -1 -1
^5 2 3159 3781 0 0 -1 -1 -1 -1 -1
^111 0 2301 1762 6 0 7 -317 -243 1 1
^125 0 5042 3251 0 0 22 -351 -190 0 1
^130 0 11093 5593 0 0 15 134 376 0 0
^132 0 10367 5542 0 0 15 -144 373 0 0
^136 0 9923 3017 0 0 26 -241 318 0 0
****************************
UNITS IN FOG - debug 'input'
****************************
^4
^129 0 8611 6077 0 1 25 -346 -199 0 1
^110 0 2422 6891 0 0 5 -328 228 0 0
^118 0 7736 6638 0 0 2 -294 270 0 0
^126 0 10597 7803 0 0 21 81 391 0 0
turn 174
*************************
*  ATTACKER V2     *
*************************
^turn 174
^mind 1
        """, null);
  }
  
  
  @Test
  void hit3bots() throws Exception {
    MobInterceptor.DEBUG_STEPS = true;
    
    Action action = new HitMaxBots().think(state, state.myHeroes[2]);

    System.out.println("Hero dist "+state.myHeroes[2].pos.dist(State.oppBase));
    
    
    assertThat(state.getUnitCountInRange(action.target, State.MONSTER_TARGET_KILL)).isEqualTo(2);
    assertThat(action.target).isEqualTo(new Pos(10690,4905));
  }
}
