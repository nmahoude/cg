package utg2019.world;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import trigonometryInt.Point;

public class WorldTest {

  private World world;


  @BeforeClass
  public static void setupClass() {
    Point.init(30, 15);
  }
  
  @Before
  public void setup() {
    world = new World();
    
  }
  
  
  @Test
  public void whenSetHole_worldHasAnHole() throws Exception {
    world.setHole(Point.get(3, 7));
    
    assertThat(world.hasHole(Point.get(3, 7))).isTrue();
  }
  
  @Test
  public void putRadar_hasRadarAndAHole() throws Exception {
    world.putRadar(Point.get(17, 13));
    
    assertThat(world.hasRadar(Point.get(17, 13))).isTrue();
    assertThat(world.hasHole(Point.get(17, 13))).isTrue();
  }
  
  @Test
  public void putRadarThenRemove_noRadarStillAHole() throws Exception {
    world.putRadar(Point.get(17, 13));
    world.removeRadar(Point.get(17, 13));
    
    assertThat(world.hasRadar(Point.get(17, 13))).isFalse();
    assertThat(world.hasHole(Point.get(17, 13))).isTrue();
  }
  
  @Test
  public void putTrap_hasTrapAndAHole() throws Exception {
    world.putTrap(Point.get(13, 7));
    
    assertThat(world.hasTrap(Point.get(13, 7))).isTrue();
    assertThat(world.hasHole(Point.get(13, 7))).isTrue();
  }

  @Test
  public void putTrapAndThenRemove_hasTrapAndAHole() throws Exception {
    world.putTrap(Point.get(13, 7));
    world.removeTrap(Point.get(13, 7));
    
    assertThat(world.hasTrap(Point.get(13, 7))).isFalse();
    assertThat(world.hasHole(Point.get(13, 7))).isTrue();
  }
}
