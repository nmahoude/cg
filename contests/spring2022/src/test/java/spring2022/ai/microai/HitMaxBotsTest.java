package spring2022.ai.microai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spring2022.Action;
import spring2022.State;

public class HitMaxBotsTest {
  private State state;

  @BeforeEach
  public void setup() {
    state = State.fromInput( """
        ^1 122 3 122
        ^9
        ^0 1 3732 5747 0 0 -1 -1 -1 -1 -1
        ^1 1 4487 3482 0 0 -1 -1 -1 -1 -1
        ^2 1 11810 7212 0 0 -1 -1 -1 -1 -1
        ^3 2 5643 2002 0 0 -1 -1 -1 -1 -1
        ^4 2 12189 5135 0 0 -1 -1 -1 -1 -1
        ^5 2 6176 2113 0 0 -1 -1 -1 -1 -1
        ^58 0 10705 7370 0 0 10 90 389 0 0
        ^65 0 10975 8087 0 0 17 385 -107 0 2
        ^71 0 11321 8553 0 0 16 -342 -207 0 1
        ****************************
        UNITS IN FOG - debug 'input'
        ****************************
        ^0
        turn88
        *************************
        *     ATTACKER V2       *
        *************************
        ^turn 88
        ^mind 2
        """, null);
  }
  
  
  @Test
  void hit3bots() throws Exception {

    Action action = new HitMaxBots().think(state, state.myHeroes[2]);
    
    assertThat(state.getUnitCountInRange(action.target, State.MONSTER_TARGET_KILL)).isEqualTo(3);
  }
}
