package trigonometry;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class LineTest {

  public static class LineToCircle {
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

  public static class LineToPoint {
    @Test
    public void onLine() throws Exception {
      Line line = new Line(new Point(0,0), new Point(1,0));
      Point p = new Point(0.5,0);
      
      double length = line.distanceTo(p);
      
      assertThat(length, is(0.0));
    } 
    @Test
    public void orthogonal_Y() throws Exception {
      Line line = new Line(new Point(0,0), new Point(1,0));
      Point p = new Point(0,1);
      
      double length = line.distanceTo(p);
      
      assertThat(length, is(1.0));
    }
    
    @Test
    public void orthogonal_minusY() throws Exception {
      Line line = new Line(new Point(0,0), new Point(10,0));
      Point p = new Point(0,-3);
      
      double length = line.distanceTo(p);
      
      assertThat(length, is(3.0));
    }
    
    @Test
    public void orthogonal_X() throws Exception {
      Line line = new Line(new Point(4,0), new Point(4,-1));
      Point p = new Point(5,0);
      
      double length = line.distanceTo(p);
      
      assertThat(length, is(1.0));
    }
  }
}
