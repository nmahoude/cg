package spring2022.ai.microai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spring2022.Action;
import spring2022.State;

public class ControlMobToOppBaseTest {
  private State state;

  @BeforeEach
  public void setup() {
    
  }
  
  
  @Test
  void dontUseSpellWhenBotIsShielded() throws Exception {
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
    Action action = new ControlMobToOppBase().think(state, state.myHeroes[2]);
    
    assertThat(action).isEqualTo(Action.WAIT);
  }

  @Test
  void sendMobRandomly() throws Exception {
    state = State.fromInput( """
        ^3 76 2 348
        ^11
        ^0 1 4223 4088 0 0 -1 -1 -1 -1 -1
        ^1 1 4250 585 0 0 -1 -1 -1 -1 -1
        ^2 1 12210 5797 0 0 -1 -1 -1 -1 -1
        ^5 2 5726 1456 0 0 -1 -1 -1 -1 -1
        ^112 0 6077 272 0 0 5 -368 -154 0 0
        ^116 0 4147 3859 0 0 15 -385 108 0 1
        ^123 0 3530 4276 0 0 14 -309 -253 0 1
        ^124 0 13642 6379 12 0 22 334 219 1 2
        ^125 0 4161 524 8 0 14 -396 -49 1 1
        ^129 0 11470 6844 0 1 25 -351 -191 0 1
        ^131 0 11210 6157 0 0 25 365 162 0 2
        ****************************
        UNITS IN FOG - debug 'input'
        ****************************
        ^4
        ^130 0 7540 5051 0 0 25 -85 390 0 0
        ^135 0 12665 8749 0 0 26 399 20 0 1
        ^126 0 7455 7081 0 0 25 -68 394 0 0
        ^127 0 10175 1919 0 0 13 68 -394 0 0
        turn 172
        *************************
        *  ATTACKER V2     *
        *************************
        ^turn 172
        ^mind 2
        """, null);
    
    Action action = new ControlMobToOppBase().think(state, state.myHeroes[2]);
    
    assertThat(action.type).isEqualTo(Action.TYPE_CONTROL);
    
  }
  
  
  
}
