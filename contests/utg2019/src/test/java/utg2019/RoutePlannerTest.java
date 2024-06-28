package utg2019;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import trigonometryInt.Point;

public class RoutePlannerTest {
  @BeforeClass
  public static void setupClass() {
    Point.init(30, 15);
  }
  
  @Test
  public void etaToBeON_alreadyOnIt_etaIs0() throws Exception {
    int eta = RoutePlanner.etaToBeOn(Point.get(10,10), Point.get(10,10));
    Assertions.assertThat(eta).isEqualTo(0);
  }

  @Test
  public void etaToBeON_neighbor_etaIs1() throws Exception {
    int eta = RoutePlanner.etaToBeOn(Point.get(10,10), Point.get(11,10));
    Assertions.assertThat(eta).isEqualTo(1);
  }

  @Test
  public void etaToBeON_exactly4_etaIs1() throws Exception {
    int eta = RoutePlanner.etaToBeOn(Point.get(10,10), Point.get(14,10));
    Assertions.assertThat(eta).isEqualTo(1);
  }

  @Test
  public void etaToBeON_5cells_etaIs2() throws Exception {
    int eta = RoutePlanner.etaToBeOn(Point.get(10,10), Point.get(15,10));
    Assertions.assertThat(eta).isEqualTo(2);
  }
  
  @Test
  public void etaInVicinity_On_etaIs0() throws Exception {
    int eta = RoutePlanner.etaToBeInVicinity(Point.get(10,10), Point.get(10,10), true, -1);
    Assertions.assertThat(eta).isEqualTo(0);
  }

  @Test
  public void etaInVicinity_neighbors_etaIs0() throws Exception {
    int eta = RoutePlanner.etaToBeInVicinity(Point.get(11,10), Point.get(10,10), true, -1);
    Assertions.assertThat(eta).isEqualTo(0);
  }

  @Test
  public void etaInVicinity512_612_neighbors_etaIs0() throws Exception {
    int eta = RoutePlanner.etaToBeInVicinity(Point.get(5,12), Point.get(6,12), true, -1);
    Assertions.assertThat(eta).isEqualTo(0);
  }
  @Test
  public void etaInVicinity_2cells_etaIs1() throws Exception {
    int eta = RoutePlanner.etaToBeInVicinity(Point.get(12,10), Point.get(10,10), true, -1);
    Assertions.assertThat(eta).isEqualTo(1);
  }
  
  
}
