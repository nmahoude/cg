package trigonometry;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class LineTest {

  @Test
  public void lineToCircle() throws Exception {
    Line line = new Line(new Point(0,0), new Point(100,0));
    Circle circle = new Circle(new Point(0,0), 500);
    
    List<Point> intersection = line.intersection(circle);
    
    assertThat(intersection.size(), is(2));
    assertThat(intersection, hasItem(new Point(500,0)));
    assertThat(intersection, hasItem(new Point(-500,0)));
  }
  
  @Test
  public void lineToCircle_tangent() throws Exception {
    Line line = new Line(new Point(0,500), new Point(100,500));
    Circle circle = new Circle(new Point(0,0), 500);
    
    List<Point> intersection = line.intersection(circle);
    
    assertThat(intersection.size(), is(1));
    assertThat(intersection, hasItem(new Point(-0.0,500.0)));
  }
  
}
