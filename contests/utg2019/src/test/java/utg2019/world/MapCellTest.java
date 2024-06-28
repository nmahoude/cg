package utg2019.world;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import trigonometryInt.Point;

public class MapCellTest {

  
  @Test
  public void neighborsat4_10() throws Exception {
    Point.init(30, 15);
    World world = new World();
    
    MapCell mc = World.mapCells[Point.get(4, 10).offset];
    
    Assertions.assertThat(mc.pos).isEqualTo(Point.get(4, 10));
    for (MapCell n : mc.neighborsAndSelf) {
      System.err.println(n.pos);
    }
  }
}
