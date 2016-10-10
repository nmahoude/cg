package trigonometry;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class CirclesIntersectionTest {

  @Test
  public void simpleOne() throws Exception {
    Circle agent = new Circle(new Point(0,0), 1000);
    Circle enemy = new Circle(new Point(1000,0), 500);
    
    List<Point> potentialPoints = CirclesIntersection.getPotentialPoints(agent, Arrays.asList(enemy));
    assertThat(potentialPoints.size(), is(360));
    assertThat(potentialPoints.get(0).x, is(500.0));
  }
  
  @Test
  public void TotallyEclipseAPart() throws Exception {
    Circle agent = new Circle(new Point(0,0), 1000);
    Circle enemy = new Circle(new Point(1000,0), 1000);
    
    List<Point> potentialPoints = CirclesIntersection.getPotentialPoints(agent, Arrays.asList(enemy));
    assertThat(potentialPoints.size(), is(360));
    assertThat(potentialPoints.get(0), is(nullValue()));
  }
  
  @Test
  public void twoEnemies() throws Exception {
    Circle agent = new Circle(new Point(0,0), 1000);
    Circle enemy = new Circle(new Point(1000,0), 500);
    Circle enemy2 = new Circle(new Point(-1000,0), 500);
    
    List<Point> potentialPoints = CirclesIntersection.getPotentialPoints(agent, Arrays.asList(enemy, enemy2));
    assertThat(potentialPoints.size(), is(360));
    assertThat(potentialPoints.get(0).x, is(500.0));
    assertThat(potentialPoints.get(180).x, is(-500.0));
  }
}
