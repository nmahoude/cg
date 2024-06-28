package spring2022.ai.microai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import spring2022.Action;
import spring2022.State;

public class TakeOpportunityTest {

  private State state;

  @BeforeEach
  public void setup() {
    
  }

  @Nested
  class Allin {
    private State buildAllIn() {
      return State.fromInput(true, """
          ^3 98 3 108
          ^10
          ^0 2 11630 7500 0 0 -1 -1 -1 -1 -1
          ^1 2 11630 7500 0 0 -1 -1 -1 -1 -1
          ^2 2 11630 7500 0 0 -1 -1 -1 -1 -1
          ^3 1 12492 4151 0 0 -1 -1 -1 -1 -1
          ^4 1 11738 6504 0 0 -1 -1 -1 -1 -1
          ^5 1 6828 3992 0 0 -1 -1 -1 -1 -1
          ^36 0 12002 6704 0 0 1 178 358 0 1
          ^38 0 12725 4743 0 0 8 230 326 0 1
          ^40 0 6916 2550 0 0 14 -347 197 0 0
          ^42 0 6763 3533 0 0 14 -171 361 0 0
          ****************************
          UNITS IN FOG - debug 'input'
          ****************************
          ^0
          turn 59
          *************************
          * ATTACKER V2 *
          *************************
          ^turn 59
          ^mind 0
          """, null);
    }
    
    @Test
    @Disabled
    void takeOpportunity() throws Exception {
      state = buildAllIn();
      
      TakeOpportunity sut = new TakeOpportunity();
      Action action = sut.think(state, state.myHeroes[2]);

      assertThat(sut.allin).isTrue();
      
    }
    
  }

  
  @Nested
  class Wind {
    @Test
    void takeOpportunity() throws Exception {
      /**
       * 88 is too near to not take the shot
       */
      state = buildOpportunity();
      
      
      Action action = new TakeOpportunity().think(state, state.myHeroes[2]);
      
      assertThat(action.type).isEqualTo(Action.TYPE_WIND);
    }
  
    @Test
    void notIfNotEnoughHealth() throws Exception {
      state = buildOpportunity();
      state.findUnitById(88).health = 13; // there is 7 steps
      
      
      Action action = new TakeOpportunity().think(state, state.myHeroes[2]);
      
      assertThat(action.type).isEqualTo(Action.TYPE_WIND);
    }
  
    @Test
    void windIfNotEnoughHealthButReallyNear() throws Exception {
      state = buildOpportunity();
      state.myHeroes[2].pos.set(State.WIDTH - 600,State.HEIGHT - 600); // need to be near him
      state.findUnitById(88).pos.set(State.WIDTH -400,State.HEIGHT - 400); // near the base
      state.findUnitById(88).health = 4;  // almost dead
      
      
      Action action = new TakeOpportunity().think(state, state.myHeroes[2]);
      
      assertThat(action.type).isEqualTo(Action.TYPE_WIND);
    }
  
    private State buildOpportunity() {
      return State.fromInput( """
          ^3 60 3 152
          ^5
          ^0 1 2999 1506 0 0 -1 -1 -1 -1 -1
          ^1 1 2186 5641 0 0 -1 -1 -1 -1 -1
          ^2 1 14486 6776 0 0 -1 -1 -1 -1 -1
          ^4 2 15297 8461 0 0 -1 -1 -1 -1 -1
          ^88 0 15532 6877 0 0 18 281 284 1 2
          ****************************
          UNITS IN FOG - debug 'input'
          ****************************
          ^0
          turn 123
          *************************
          * ATTACKER V2 *
          *************************
          ^turn 123
          ^mind 1
          """, null);
    }
  
    @Test
    void dontWindShieldBots() throws Exception {
      state = buildOpportunity();
  
      state.findUnitById(88).shieldLife = 5;
      
      Action action = new TakeOpportunity().think(state, state.myHeroes[2]);
      
      assertThat(action).isEqualTo(Action.WAIT);
    }
    
    @Test
    void dontWindTooFar() throws Exception {
      state = State.fromInput( """
          ^3 152 3 258
          ^5
          ^0 1 3689 5555 0 0 -1 -1 -1 -1 -1
          ^1 1 5931 625 0 0 -1 -1 -1 -1 -1
          ^2 1 13872 5891 0 0 -1 -1 -1 -1 -1
          ^3 2 15403 6585 0 0 -1 -1 -1 -1 -1
          ^52 0 15403 6585 0 0 5 271 294 1 2
          ****************************
          UNITS IN FOG - debug 'input'
          ****************************
          ^0
          turn 77
          *************************
          *  ATTACKER V2     *
          *************************
          ^turn 77
          ^mind 0        
          """, null);
      
      Action action = new TakeOpportunity().think(state, state.myHeroes[2]);
      
      assertThat(action).isEqualTo(Action.WAIT);
    }
  }
  
}
