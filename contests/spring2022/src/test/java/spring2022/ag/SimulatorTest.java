package spring2022.ag;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fast.read.FastReader;
import spring2022.Action;
import spring2022.Pos;
import spring2022.State;
import spring2022.Vec;
import spring2022.ai.TriAction;

public class SimulatorTest {
  private static final Action WAIT = Action.WAIT;
  private static final Action WIND = Action.doWind(State.oppBase);

  State state = new State();
  AGEvaluator evaluator = new AGEvaluator();
  LightState current = new LightState();

  @BeforeEach 
  public void setup() {
    state = new State();
    evaluator = new AGEvaluator();
    current = new LightState();

    State.DEBUG_INPUTS = false;

    // always in topleft corner
    state.readGlobal(FastReader.fromString("0 0 3 "));
  }

  private TriAction tri(Action a1, Action a2) {
    TriAction tmp = new TriAction();
    tmp.actions[0].copyFrom(a1);
    tmp.actions[1].copyFrom(a2);
    tmp.actions[2].copyFrom(Action.WAIT);
    return tmp;
  }

  public TriAction tri(Action a1, Action a2, Action a3) {
    TriAction tmp = new TriAction();
    tmp.actions[0].copyFrom(a1);
    tmp.actions[1].copyFrom(a2);
    tmp.actions[2].copyFrom(a3);
    return tmp;
  }

  
  @Test
  void deals1damageWhenDoNothing() throws Exception {
    state.read(FastReader.fromString("""
        ^3 270 3 424
        ^4
        ^0 1 66 316 0 0 -1 -1 -1 -1 -1
        ^1 1 54 435 0 0 -1 -1 -1 -1 -1
        ^2 1 13000 5000 0 0 -1 -1 -1 -1 -1
        ^93 0 84 431 0 0 12 -76 -392 1 1
        """.replace("^", "") ));

    current.createFrom(state);

    Simulator.apply(current, 0, tri(WAIT, WAIT));

    assertThat(current.health).isEqualTo(2);
    assertThat(current.units[0].health).isEqualTo(0);
  }

  @Nested
  class UnitMove {
    
    @Test
    void snapBecauseInBase() throws Exception {
      state.read(FastReader.fromString("""
      ^1 42 1 68
      ^5
      ^0 1 1422 3603 0 0 -1 -1 -1 -1 -1
      ^1 1 3044 3414 0 0 -1 -1 -1 -1 -1
      ^2 1 14126 3770 0 0 -1 -1 -1 -1 -1
      ^5 2 3722 4869 0 0 -1 -1 -1 -1 -1
      ^121 0 0 5195 0 0 22 -200 -200 1 2
        """.replace("^", "") ));

      current.createFrom(state);
  
      Simulator.apply(current, 0, tri(WAIT, WAIT));
  
      assertThat(current.units[0].health > 0).isTrue();
      assertThat(current.units[0].speed).isEqualTo(new Vec(0, -400));
    }

    @Test
    void deadBecauseOutOfBase() throws Exception {
      state.read(FastReader.fromString("""
      ^1 42 1 68
      ^5
      ^0 1 1422 3603 0 0 -1 -1 -1 -1 -1
      ^1 1 3044 3414 0 0 -1 -1 -1 -1 -1
      ^2 1 14126 3770 0 0 -1 -1 -1 -1 -1
      ^5 2 3722 4869 0 0 -1 -1 -1 -1 -1
      ^121 0 0 5205 0 0 22 -200 -200 1 2
        """.replace("^", "") ));

      current.createFrom(state);
  
      Simulator.apply(current, 0, tri(WAIT, WAIT));
  
      assertThat(current.units[0].isDead()).isTrue();
    }

    
  }

  @Nested
  class Winds {

    @Test
    void windPushInCorrectDestination() throws Exception {
      state.read(FastReader.fromString("""
          ^3 212 3 412
          ^4
          ^0 1 1167 936 0 0 -1 -1 -1 -1 -1
          ^1 1 1043 45 0 0 -1 -1 -1 -1 -1
          ^2 1 13000 5000 0 0 -1 -1 -1 -1 -1
          ^113 0 770 1169 0 0 19 -220 -334 1 1
          """.replace("^", "") ));

      current.createFrom(state);
      Simulator.apply(current, 0, tri(WAIT, WIND));

      assertThat(current.health).isEqualTo(3);
      assertThat(current.units[0].health).isEqualTo(17);
      assertThat(current.units[0].pos).isEqualTo(Pos.get(2705, 2214));
    }

    @Test
    void windPushOppHeroInCorrectDestination() throws Exception {
      state.read(FastReader.fromString("""
          ^3 212 3 412
          ^5
          ^0 1 1167 936 0 0 -1 -1 -1 -1 -1
          ^1 1 1043 45 0 0 -1 -1 -1 -1 -1
          ^2 1 13000 5000 0 0 -1 -1 -1 -1 -1
          ^3 2 770 1169 0 0 19 -220 -334 1 1
          ^113 0 770 1169 0 0 19 -220 -334 1 1
          """.replace("^", "") ));

      current.createFrom(state);
      Simulator.apply(current, 0, tri(WAIT, WIND));

      assertThat(current.health).isEqualTo(3);
      assertThat(current.oppHeroes[0].pos).isEqualTo(Pos.get(2705, 2214));
    }

    @Test
    void windWONTPushOppHeroIfShielded() throws Exception {
      state.read(FastReader.fromString("""
          ^3 212 3 412
          ^5
          ^0 1 1167 936 0 0 -1 -1 -1 -1 -1
          ^1 1 1043 45 0 0 -1 -1 -1 -1 -1
          ^2 1 13000 5000 0 0 -1 -1 -1 -1 -1
          ^3 2 770 1169 0 0 19 -220 -334 1 1
          ^113 0 770 1169 0 0 19 -220 -334 1 1
          """.replace("^", "") ));

      state.oppHeroes[0].shieldLife = 1;

      current.createFrom(state);
      Simulator.apply(current, 0, tri(WAIT, WIND));

      assertThat(current.health).isEqualTo(3);
      assertThat(current.oppHeroes[0].pos).isEqualTo(Pos.get(770, 1169));
    }


    @Test
    void windPushIsWorthLessOnShieldedUnit() throws Exception {
      state.read(FastReader.fromString("""
          ^3 212 3 412
          ^4
          ^0 1 1167 936 0 0 -1 -1 -1 -1 -1
          ^1 1 1043 45 0 0 -1 -1 -1 -1 -1
          ^2 1 13000 5000 0 0 -1 -1 -1 -1 -1
          ^113 0 770 1169 0 0 19 -220 -334 1 1
          """.replace("^", "") ));

      state.fastUnits[0].shieldLife = 5;

      current.createFrom(state);
      Simulator.apply(current, 0, tri(WAIT, WIND));

      assertThat(current.health).isEqualTo(3);
      assertThat(current.units[0].health).isEqualTo(17);
      assertThat(current.units[0].pos).isEqualTo(Pos.get(770 -220, 1169 -334));
    }


    @Test
    void doubleWindPushInCorrectDestination() throws Exception {
      state.read(FastReader.fromString("""
          ^3 212 3 412
          ^4
          ^0 1 1043 45 0 0 -1 -1 -1 -1 -1
          ^1 1 1043 45 0 0 -1 -1 -1 -1 -1
          ^2 1 13000 5000 0 0 -1 -1 -1 -1 -1
          ^113 0 770 1169 0 0 19 -220 -334 1 1
          """.replace("^", "") ));

      current.createFrom(state);
      Simulator.apply(current, 0, tri(WIND, WIND));

      assertThat(current.health).isEqualTo(3);
      assertThat(current.units[0].health).isEqualTo(19);
      assertThat(current.units[0].pos).isEqualTo(Pos.get(4640, 3259));

    }


    @Test
    void noDamageWhenWindByPlayer1() throws Exception {
      state.read(FastReader.fromString("""
          ^3 246 3 350
          ^4
          ^0 1 1174 1064 0 0 -1 -1 -1 -1 -1
          ^1 1 814 1121 0 0 -1 -1 -1 -1 -1
          ^2 1 13000 5000 0 0 -1 -1 -1 -1 -1
          ^141 0 744 1171 0 0 20 -214 -337 1 1
          """.replace("^", "") ));

      current.createFrom(state);
      Simulator.apply(current, 0, tri(WIND, WAIT));

      assertThat(current.health).isEqualTo(3);
      assertThat(current.units[0].health).isEqualTo(16);

    }

    @Test
    void noDamageWhenWindByPlayer2() throws Exception {
      state.read(FastReader.fromString("""
          ^3 246 3 350
          ^4
          ^0 1 1174 1064 0 0 -1 -1 -1 -1 -1
          ^1 1 814 1121 0 0 -1 -1 -1 -1 -1
          ^2 1 13000 5000 0 0 -1 -1 -1 -1 -1
          ^141 0 744 1171 0 0 20 -214 -337 1 1
          """.replace("^", "") ));

      current.createFrom(state);
      Simulator.apply(current, 0, tri(WAIT, WIND) );

      assertThat(current.health).isEqualTo(3);
      assertThat(current.units[0].health).isEqualTo(16);

    }

    @Test
    void wildManaGainOutsideOfBaseRange() throws Exception {
      state.read(FastReader.fromString("""
          ^3 14 3 0
          ^4
          ^0 1 5491 2102 0 0 -1 -1 -1 -1 -1
          ^1 1 2499 4927 0 0 -1 -1 -1 -1 -1
          ^2 1 9344 3062 0 0 -1 -1 -1 -1 -1
          ^10 0 6403 1865 0 0 10 -268 296 0 0
          """.replace("^", "") ));

      current.createFrom(state);
      Simulator.apply(current, 0, tri(Action.doMove(6403, 1865), WAIT));

      assertThat(current.wildMana).isEqualTo(2);

    }
    
    @Test
    void oppCanDoWind() throws Exception {
      state.read(FastReader.fromString("""
          ^3 72 2 286
            ^5
            ^0 1 2351 5592 0 0 -1 -1 -1 -1 -1
            ^1 1 4744 1730 0 0 -1 -1 -1 -1 -1
            ^2 1 15185 407 0 0 -1 -1 -1 -1 -1
            ^5 2 5322 697 0 0 -1 -1 -1 -1 -1
            ^90 0 5427 1767 0 0 16 -395 58 0 1
          """.replace("^", "") ));
      
      current.createFrom(state);
      Simulator.apply(current, 0, tri(WAIT, Action.doMove(5155, 2296)), tri(WAIT, WAIT, Action.doWind(0, 0)));

      assertThat(current.findPosById(90)).isEqualTo(Pos.get(3246, 1482));
    }

    @Test
    void oppCanDoDoubleWindIntoBase__ouch__() throws Exception {
      state.read(FastReader.fromString("""
          ^3 174 3 118
          ^10
          ^0 1 3563 3546 0 0 -1 -1 -1 -1 -1
          ^1 1 5876 3396 0 0 -1 -1 -1 -1 -1
          ^2 1 10227 3627 0 0 -1 -1 -1 -1 -1
          ^3 2 2352 3246 0 0 -1 -1 -1 -1 -1
          ^4 2 2352 3246 0 0 -1 -1 -1 -1 -1
          ^31 0 6421 1756 0 0 9 -114 -383 0 0
          ^33 0 10359 3499 0 0 3 264 -300 0 0
          ^35 0 9503 3447 0 0 5 43 -397 0 0
          ^37 0 2331 3921 0 0 13 -204 -343 1 1
          ^41 0 3550 5597 0 0 14 -115 -382 0 1
          """.replace("^", "") ));
      
      current.createFrom(state);
      Simulator.apply(current, 0, tri(Action.doMove(2763, 3546), Action.doMove(5410, 3773)), tri(Action.doWind(1698, 2146), Action.doWind(1698, 2146), WAIT));
      
      assertThat(current.health).isEqualTo(2);
      assertThat(current.findPosById(37)).isEqualTo(Pos.VOID);
    }

    
    
    
    @Test
    void oppCannotDoWindAfterDepth0() throws Exception {
      state.read(FastReader.fromString("""
          ^3 72 2 286
          ^5
          ^0 1 2351 5592 0 0 -1 -1 -1 -1 -1
          ^1 1 4744 1730 0 0 -1 -1 -1 -1 -1
          ^2 1 15185 407 0 0 -1 -1 -1 -1 -1
          ^5 2 5322 697 0 0 -1 -1 -1 -1 -1
          ^90 0 5427 1767 0 0 16 -395 58 0 1
          """.replace("^", "") ));
      
      current.createFrom(state);
      Simulator.apply(current, 1, tri(WAIT, Action.doMove(5155, 2296)), tri(WAIT, WAIT, Action.doWind(0, 0)));
      
      assertThat(current.findPosById(90)).isEqualTo(Pos.get(5032, 1825));
    }

    @Test
    void oppCanDoWindOnHeroes() throws Exception {
      state.read(FastReader.fromString("""
          ^3 86 1 254
          ^9
          ^0 1 3702 5346 0 0 -1 -1 -1 -1 -1
          ^1 1 4515 1473 0 0 -1 -1 -1 -1 -1
          ^2 1 15033 610 0 0 -1 -1 -1 -1 -1
          ^5 2 5328 1866 0 0 -1 -1 -1 -1 -1
          ^87 0 5771 1425 9 0 12 -98 -387 0 0
          ^90 0 6375 1987 0 0 12 100 -387 0 0
          ^94 0 15619 1505 0 0 9 378 128 0 0
          ^97 0 2748 5142 0 0 13 -244 -316 0 1
          ^101 0 3957 4677 0 0 13 -66 -394 0 1
          """.replace("^", "") ));
      
      current.createFrom(state);
      Simulator.apply(current, 0, tri(Action.doMove(3464, 5209), Action.doMove(5028, 1582)), tri(WAIT, WAIT, Action.doWind(0, 0)));
      
      assertThat(current.hero[0].pos).isEqualTo(Pos.get(3464, 5209));
      assertThat(current.hero[1].pos).isEqualTo(Pos.get(2952, 855));
      assertThat(current.findPosById(87)).isEqualTo(Pos.get(5673, 1038)); // shield
      assertThat(current.findPosById(90)).isEqualTo(Pos.get(4299, 1260)); // no shield
    }

  }

  @Nested
  class Controls {
    @Test
    void simpleControlOnHero() throws Exception {
      state.read(FastReader.fromString("""
          ^3 114 3 82
          ^4
          ^0 1 2179 4590 0 0 -1 -1 -1 -1 -1
          ^1 1 2292 5572 0 0 -1 -1 -1 -1 -1
          ^2 1 9895 3170 0 0 -1 -1 -1 -1 -1
          ^3 2 3550 4610 0 1 -1 -1 -1 -1 -1
          """.replace("^", "") ));

      current.createFrom(state);
      Simulator.apply(current, 0, tri(Action.doControl(3, State.oppBase), WAIT));

      assertThat(current.oppHeroes[0].pos).isEqualTo(Pos.get(4314, 4849));
    }

    @Test
    void simpleControlOnUnit() throws Exception {
      state.read(FastReader.fromString("""
          ^3 12 3 2
          ^4
          ^0 1 3349 6723 0 0 -1 -1 -1 -1 -1
          ^1 1 5605 2280 0 0 -1 -1 -1 -1 -1
          ^2 1 3838 7251 0 0 -1 -1 -1 -1 -1
          ^37 0 4423 7034 0 0 11 -56 -395 0 1
          """.replace("^", "") ));

      current.createFrom(state);
      Simulator.apply(current, 0, tri(Action.doControl(37, State.oppBase), WAIT));
      assertThat(current.units[0].pos).isEqualTo(Pos.get(4367, 6639));

      
      Simulator.apply(current, 0, tri(WAIT, WAIT));

      assertThat(current.units[0].pos).isEqualTo(Pos.get(4760, 6709));
      assertThat(current.units[0].speed).isEqualTo(new Vec(393, 70));
    }

  }


  // --------

}
