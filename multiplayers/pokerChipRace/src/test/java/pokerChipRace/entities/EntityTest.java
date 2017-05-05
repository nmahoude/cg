package pokerChipRace.entities;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import pokerChipRace.simulate.Collision;

public class EntityTest {

  private static final double EPSILON = 0.0001;

  @Test
  public void bounceHorizontalAxis() throws Exception {
    Entity a = new Entity(0, 0);
    a.update(0, 0, 0, 100, 100, 0);
    
    Entity b = new Entity(1,1);
    b.update(1, 200, 0, 100, 0, 0);
    
    a.bounce(b);
    
    assertThat(a.x, closeTo(0.0, EPSILON));
    assertThat(a.y, closeTo(0.0, EPSILON));
    assertThat(a.vx, closeTo(-50.0, EPSILON));
    assertThat(a.vy, closeTo(0.0, EPSILON));
    
    assertThat(b.x, closeTo(200.0, EPSILON));
    assertThat(b.y, closeTo(0.0, EPSILON));
    assertThat(b.vx, closeTo(50.0, EPSILON));
    assertThat(b.vy, closeTo(0.0, EPSILON));
  }
  @Test
  public void bounceHorizontalAxisWithSameSpeed() throws Exception {
    Entity a = new Entity(0, 0);
    a.update(0, 0, 0, 100, 100, 0);
    
    Entity b = new Entity(1,1);
    b.update(1, 200, 0, 100, -100, 0);
    
    a.bounce(b);
    
    assertThat(a.x, closeTo(0.0, EPSILON));
    assertThat(a.y, closeTo(0.0, EPSILON));
    assertThat(a.vx, closeTo(-100.0, EPSILON));
    assertThat(a.vy, closeTo(0.0, EPSILON));
    
    assertThat(b.x, closeTo(200.0, EPSILON));
    assertThat(b.y, closeTo(0.0, EPSILON));
    assertThat(b.vx, closeTo(100.0, EPSILON));
    assertThat(b.vy, closeTo(0.0, EPSILON));
  }
  
  @Test
  public void bounceAnyAxisWithSameSpeed() throws Exception {
    Entity a = new Entity(0, 0);
    a.update(0, 0, 0, 100, 100, 100);
    
    Entity b = new Entity(1,1);
    b.update(1, 142, 142, 100, -100, -100);
    
    a.bounce(b);
    
    assertThat(a.x, closeTo(0.0, EPSILON));
    assertThat(a.y, closeTo(0.0, EPSILON));
    assertThat(a.vx, closeTo(-100.0, EPSILON));
    assertThat(a.vy, closeTo(-100.0, EPSILON));
    
    assertThat(b.x, closeTo(142.0, EPSILON));
    assertThat(b.y, closeTo(142.0, EPSILON));
    assertThat(b.vx, closeTo(100.0, EPSILON));
    assertThat(b.vy, closeTo(100.0, EPSILON));
  }
  
  @Test
  public void bounceReal() throws Exception {
    Entity a = new Entity(0, 0);
    a.update(0, 35.678554336827624, 59.831848004842804, 21.46666717529297, 10.885801216893281, 23.347908542443296);
    
    Entity b = new Entity(1,1);
    b.update(1, 27.626590054295832, 42.5619638768994, 21.46666717529297, -10.885801216893281, -23.347908542443296);
    
    Collision init = new Collision();
    init = a.collision(b, 0.0, init);
    
    assertThat(init.t, is(0.0));
    a.bounce(b);
    
    Collision col = new Collision();
    col = a.collision(b, 0.0, col);
    
    assertThat(col, is(nullValue()));
  }
  
}
