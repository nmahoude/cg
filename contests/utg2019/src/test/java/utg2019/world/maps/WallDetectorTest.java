package utg2019.world.maps;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import trigonometryInt.Point;
import utg2019.world.World;

public class WallDetectorTest {
  private int traps[];
  private WallDetector wallDetector;
  
  @BeforeClass
  public static void setupClass() {
    Point.init(30, 15);
  }

  @Before
  public void setup() {
    traps = new int[World.MAX_OFFSET];
    
    wallDetector = new WallDetector();
  }
  @Test
  public void noWall() throws Exception {
    wallDetector.update(traps);
  }
  
  @Test
  public void fullWall_onCol1() throws Exception {
    for (int y=0;y<World.HEIGHT;y++) {
      placetrap(1,y);
    }
    wallDetector.update(traps);
  }

  @Test
  public void twoWalls_onCol1() throws Exception {
    for (int y=0;y<World.HEIGHT;y++) {
      placetrap(1,y);
    }
    removeTrap(1,6);
    
    wallDetector.update(traps);
  }

  private void removeTrap(int x, int y) {
    Point pos = Point.get(x, y);
    traps[pos.offset] = 0;
  }

  private void placetrap(int x, int y) {
    Point pos = Point.get(x, y);
    traps[pos.offset] = 1;
  }
}
