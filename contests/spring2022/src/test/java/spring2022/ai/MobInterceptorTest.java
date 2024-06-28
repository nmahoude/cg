
package spring2022.ai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fast.read.FastReader;
import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;

public class MobInterceptorTest {
  State state = new State();
  
  Hero hero = new Hero();
  Unit unit = new Unit();

  private MobInterceptor mobInterceptor;

  @BeforeEach
  public void setup() {
    MobInterceptor.CHECK_STEP = 40; 
    mobInterceptor = new MobInterceptor();
  }
  
  @Test
  void easyToFollow() throws Exception {
    hero.pos.copyFrom(0, 0);
    unit.pos.copyFrom(0, 0);
    unit.speed.set(400, 0);
    
    Pos pos = mobInterceptor.intercept(state, hero, unit);
    
    assertThat(pos.x).isEqualTo(400);
    assertThat(pos.y).isEqualTo(0);
  }

  @Test
  void easyToFollowButSecondUnitHitable() throws Exception {
    hero.pos.copyFrom(0, 0);
    unit.pos.copyFrom(1, 0);
    unit.speed.set(400, 0);
    
    Unit anotherMob = new Unit();
    anotherMob.health = 10;
    anotherMob.pos.copyFrom(0, 1000);
    anotherMob.speed.set(400, 0);
    state.addFogMob(anotherMob);
    
    Pos pos = mobInterceptor.intercept(state, hero, unit);
    
    assertThat(pos.x).isEqualTo(240);
    assertThat(pos.y).isEqualTo(240);
  }

  
  @Test
  void notInDirectHitBoxButAccessibleNextTurn() throws Exception {
    hero.pos.copyFrom(0, 0);
    unit.pos.copyFrom(1000, 0);
    unit.speed.set(400, 0);
    
    Pos pos = mobInterceptor.intercept(state, hero, unit);
    
    assertThat(pos.x).isEqualTo(800);
    assertThat(pos.y).isEqualTo(0);
    
    assertThat(mobInterceptor.realSteps).isEqualTo(1);
    
  }

  @Test
  void need2TurnsToReach() throws Exception {
    hero.pos.copyFrom(0, 0);
    unit.pos.copyFrom(1700, 0);
    unit.speed.set(400, 0);
    
    Pos pos = mobInterceptor.intercept(state, hero, unit);
    
    assertThat(pos.x).isEqualTo(800);
    assertThat(pos.y).isEqualTo(0);
    
    assertThat(mobInterceptor.realSteps).isEqualTo(2);
    
  }

  @Test
  void cantReachItBeforeIsOutsideOfMap() throws Exception {
    hero.pos.copyFrom(0, 0);
    unit.pos.copyFrom(17000, 0);
    unit.speed.set(400, 0);
    
    Pos pos = mobInterceptor.intercept(state, hero, unit);
    
    assertThat(pos).isEqualTo(Pos.VOID);
    assertThat(mobInterceptor.realSteps).isEqualTo(200);
    
  }

  
  @Test
  void need2TurnsToReachInCollisionCourse() throws Exception {
    hero.pos.copyFrom(3000, 0);
    unit.pos.copyFrom(1700, 0);
    unit.speed.set(400, 0);
    
    Pos pos = mobInterceptor.intercept(state, hero, unit);
    
    assertThat(pos.x).isEqualTo(3000 - 800);
    assertThat(pos.y).isEqualTo(0);
    
    assertThat(mobInterceptor.realSteps).isEqualTo(1);
    
  }
  
  @Test
  void hits2Mobs() throws Exception {
    MobInterceptor.DEBUG_STEPS = true;
    State state = new State();
    state.read(FastReader.fromString("""
        ^3 192 3 266
        ^5
        ^0 1 1888 5782 0 0 -1 -1 -1 -1 -1
        ^1 1 4680 2794 0 0 -1 -1 -1 -1 -1
        ^2 1 4190 6223 0 0 -1 -1 -1 -1 -1
        ^47 0 3795 6147 0 0 3 -359 -174 0 1
        ^61 0 3719 6723 0 0 12 -271 -293 0 1
        """.replace("^", "") ));
    
    Pos pos = mobInterceptor.intercept(state, state.myHeroes[2], state.findUnitById(47));
    
    assertThat(mobInterceptor.mobsCount).isEqualTo(2);
    
  }
  
}
