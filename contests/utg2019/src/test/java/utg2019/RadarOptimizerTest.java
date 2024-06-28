package utg2019;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import trigonometryInt.Point;
import utg2019.world.World;
import utg2019.world.maps.Oracle;

public class RadarOptimizerTest {

  private World world;
  private RadarOptimizer sut;
  private Oracle oracle;

  @BeforeClass
  public static void setupClass() {
    Point.init(30, 15);
  }
  
  @Before
  public void setup() {
    world = new World();
    oracle = new Oracle();
    sut = new RadarOptimizer();
  }
  
  @Test
  public void radarToNearCol0_isbad() throws Exception {
    sut.updateDensityOfCellCoverage(world, oracle.potentialOre);
    
    double nearBorder = sut.getRadarScore(world, oracle.potentialOre, Point.get(1,7));
    double littleFarAway = sut.getRadarScore(world, oracle.potentialOre, Point.get(2,7));
    
    Assertions.assertThat(littleFarAway).isGreaterThan(nearBorder);
  }
  
  @Test
  public void noSeamRadars() throws Exception {
    putRadarAndUpdate(Point.get(5, 3));
    sut.updateDensityOfCellCoverage(world, oracle.potentialOre);
    
    double scoreOfAllEmpties = sut.getRadarScore(world, oracle.potentialOre, Point.get(5,11));
    double scoreOfRecovering = sut.getRadarScore(world, oracle.potentialOre, Point.get(5,10));
    
    assertThat(scoreOfAllEmpties).isGreaterThan(scoreOfRecovering);
  }

  @Test
  public void _21_7_is_better_than_22_7() throws Exception {
    double score21_7 = sut.getRadarScore(world, oracle.potentialOre, Point.get(21, 7));
    double score22_7 = sut.getRadarScore(world, oracle.potentialOre, Point.get(22, 7));
    System.err.println(score21_7+" vs "+score22_7);
    assertThat(score21_7).isGreaterThan(score22_7);
  }
  
  @Test
  public void _21_6_is_better_than_22_7() throws Exception {
    double score21_6 = sut.getRadarScore(world, oracle.potentialOre, Point.get(21, 6));
    double score22_7 = sut.getRadarScore(world, oracle.potentialOre, Point.get(22, 7));
    System.err.println(score21_6+" vs "+score22_7);
    assertThat(score21_6).isGreaterThan(score22_7);
  }
  
  private void putRadarAndUpdate(Point center) {
    world.putRadar(center);
    int radius = 4;
    for (int dy = -radius; dy <= radius; dy++) {
      for (int dx = -radius; dx <= radius; dx++) {
        Point pos = Point.getSecured(center.x+dx, center.y+dy);
        if (pos != Point.Invalid) {
          if (pos.x == 0) continue; 
          world.setCurrentlyKnown(pos);
        }
      }
    }    
  }
  
}
