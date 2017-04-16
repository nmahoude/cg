package cotc.entities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class MineTest {

  @Test
  public void mineExplodeAtPositionProximity() throws Exception {
    Mine mine = new Mine(0, 5, 5);
    
    Ship detonatorShip = new Ship(1, 5, 5, 0, 0);
    Ship proximityShip = new Ship(1, 5, 6, 0, 0);
    
    boolean exploded = mine.explode(Arrays.asList(detonatorShip, proximityShip), false);
    
    assertThat(exploded, is(true));
    
    assertThat(detonatorShip.health, is(75));
    assertThat(proximityShip.health, is(90));
  }

  @Test
  public void mineExplodeAtSternProximity() throws Exception {
    Mine mine = new Mine(0, 5, 5);
    
    Ship detonatorShip = new Ship(1, 5, 5, 0, 0);
    Ship proximityShip = new Ship(1, 7, 6, 0, 0);
    
    boolean exploded = mine.explode(Arrays.asList(detonatorShip, proximityShip), false);
    
    assertThat(exploded, is(true));
    
    assertThat(detonatorShip.health, is(75));
    assertThat(proximityShip.health, is(90));
  }

  @Test
  public void mineExplodeAtBowProximity() throws Exception {
    Mine mine = new Mine(0, 5, 5);
    
    Ship detonatorShip = new Ship(1, 5, 5, 0, 0);
    Ship proximityShip = new Ship(1, 4, 6, 0, 0);
    
    boolean exploded = mine.explode(Arrays.asList(detonatorShip, proximityShip), false);
    
    assertThat(exploded, is(true));
    
    assertThat(detonatorShip.health, is(75));
    assertThat(proximityShip.health, is(90));
  }
}
