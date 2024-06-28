package spring2022.ai.microai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spring2022.Action;
import spring2022.State;

public class UseWindTowardsOppBaseTest {
  private State state;

  @BeforeEach
  public void setup() {
    
  }
  
  
  @Test
  void dontWindShieldBots() throws Exception {
    state = State.fromInput( """
        ^3 126 3 240
        ^5
        ^0 1 3334 4654 0 0 -1 -1 -1 -1 -1
        ^1 1 5937 2162 0 0 -1 -1 -1 -1 -1
        ^2 1 12968 6549 0 0 -1 -1 -1 -1 -1
        ^3 2 14536 7030 0 0 -1 -1 -1 -1 -1
        ^83 0 12059 7300 12 0 19 -399 23 0 0
        ****************************
        UNITS IN FOG - debug 'input'
        ****************************
        ^0
        turn 110
        *************************
        *  ATTACKER V2     *
        *************************
        ^turn 110
        ^mind 2
        """, null);
    Action action = new UseWindTowardsOppBase().think(state, state.myHeroes[2]);
    
    assertThat(action).isEqualTo(Action.WAIT);
  }

  @Test
  void windFromBotNotFromMe() throws Exception {
    state = State.fromInput( """
        ^2 78 2 34
        ^10
        ^0 1 5238 3690 0 2 -1 -1 -1 -1 -1
        ^1 1 4830 1544 0 2 -1 -1 -1 -1 -1
        ^2 1 11144 7814 0 2 -1 -1 -1 -1 -1
        ^4 2 6200 950 0 2 -1 -1 -1 -1 -1
        ^5 2 6200 950 0 2 -1 -1 -1 -1 -1
        ^79 0 4746 3157 0 0 13 -297 -266 0 1
        ^82 0 11215 8901 0 0 17 96 388 0 0
        ^83 0 6502 537 0 0 11 -96 -388 0 0
        ^85 0 5427 1570 0 0 17 -62 -395 0 0
        ^86 0 5605 4193 0 0 10 -66 394 0 0
        ****************************
        UNITS IN FOG - debug 'input'
        ****************************
        ^2
        ^71 0 6964 2224 0 0 4 -291 -274 0 1
        ^78 0 16929 7856 3 0 19 209 341 1 2
        turn 122
        *************************
        *  ATTACKER V2     *
        *************************
        ^turn 122
        ^mind 2        """, null);
    
    Action action = new UseWindTowardsOppBase().think(state, state.myHeroes[2]);
    
    assertThat(action.type).isEqualTo(Action.TYPE_WIND);
    assertThat(action.target).isNotEqualTo(State.oppBase);
  }

  @Test
  void useDifferenteBaseTargetForWinds() throws Exception {
    state = State.fromInput( """
^3 228 2 18
^8
^0 1 787 983 0 0 -1 -1 -1 -1 -1
^1 1 1239 214 0 0 -1 -1 -1 -1 -1
^2 1 13207 4753 0 0 -1 -1 -1 -1 -1
^3 2 2983 4518 0 0 -1 -1 -1 -1 -1
^158 0 3901 0 0 0 3 -400 0 1 1
^161 0 13212 4730 0 0 27 287 278 0 2
^166 0 13286 4301 0 0 28 263 300 0 2
^171 0 11611 5899 0 0 30 233 -325 0 0
****************************
UNITS IN FOG - debug 'input'
****************************
^5
^150 0 16899 8399 6 0 18 309 254 1 2
^151 0 6306 1211 0 0 20 316 244 0 2
^157 0 16569 8398 7 0 18 348 197 1 2
^164 0 10725 7737 0 0 29 -95 388 0 0
^116 0 9666 1650 0 0 1 391 -83 0 0
turn 219
*************************
*  ATTACKER V2     *
*************************
^turn 219
^mind 2
        """, null);
    Action action = new UseWindTowardsOppBase().think(state, state.myHeroes[2]);
    
    assertThat(action.type).isEqualTo(Action.TYPE_WIND);
  }
}
