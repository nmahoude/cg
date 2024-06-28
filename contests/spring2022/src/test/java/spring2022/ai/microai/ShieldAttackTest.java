package spring2022.ai.microai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spring2022.Action;
import spring2022.State;

public class ShieldAttackTest {

  private State state;

  @BeforeEach
  public void setup() {
    state = State.fromInput("""
        ^3 120 3 206
        ^8
        ^0 1 4730 3518 0 0 -1 -1 -1 -1 -1
        ^1 1 1130 3781 0 0 -1 -1 -1 -1 -1
        ^2 1 12737 5263 0 0 -1 -1 -1 -1 -1
        ^3 2 11860 4289 0 0 -1 -1 -1 -1 -1
        ^51 0 13335 3535 0 0 13 396 53 0 2
        ^69 0 2355 4152 0 0 17 -197 -347 1 1
        ^72 0 14413 6521 0 0 12 316 244 1 2
        ^73 0 4975 3415 0 0 12 10 -399 0 0
        ****************************
        UNITS IN FOG - debug 'input'
        ****************************
        ^0
        turn98
        *************************
        *     ATTACKER V2       *
        *************************
        ^turn 98
        ^mind 2
                """, null);
  }
  
  @Test
  void putShield() throws Exception {
    
    Action action = new ShieldAttack().think(state, state.myHeroes[2]);
    assertThat(action.type).isEqualTo(Action.TYPE_SHIELD);
    assertThat(action.targetEntity).isEqualTo(72);
    
  }
  
  @Test
  void notEnoughMana() throws Exception {
    state.mana[0] = 9;
    
    Action action = new ShieldAttack().think(state, state.myHeroes[2]);
    assertThat(action).isEqualTo(Action.WAIT);
  }

  @Test
  void notEnoughHealth() throws Exception {
    state.findUnitById(72).health = 11;
    
    Action action = new ShieldAttack().think(state, state.myHeroes[2]);
    assertThat(action).isEqualTo(Action.WAIT);
  }

  @Test
  void alreadyHasShield() throws Exception {
    state.findUnitById(72).shieldLife = 1;
    
    Action action = new ShieldAttack().think(state, state.myHeroes[2]);
    assertThat(action).isEqualTo(Action.WAIT);
  }

}
