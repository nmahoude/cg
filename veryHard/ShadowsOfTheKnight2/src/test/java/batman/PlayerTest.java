package batman;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class PlayerTest {
  private static Point batman;
  private static Rectangle r;


  public static class Reduction {
    @Before
    public void setup() {
      r = new Rectangle();
    }
    
    @Test
    public void nominal_X() throws Exception {
      setRectangle(new Point(0,0), new Point(10,20));
      Point oldBatman = new Point(0,0);
      Point newBatman = new Point(10,0);
      
      Point position = Player.reduction("WARMER", oldBatman, newBatman, r);
      
      assertThat(r.p0.x, is(5));
      assertThat(r.p0.y, is(0));
      assertThat(r.p1.x, is(10));
      assertThat(r.p1.y, is(20));
    }
  }
  
  public static class Symmetry {
    
    @Before
    public void setup() {
      r = new Rectangle();
    }
    
    @Test
    public void nominal_X() throws Exception {
      setRectangle(new Point(0,0), new Point(10,20));
      batman = new Point(0,0);
      
      Point position = Player.get_X_SymmetryProjection(batman, r);
      
      assertThat(position.x, is(10));
      assertThat(position.y, is(0));
    }
  
    @Test
    public void batman_decal_X() throws Exception {
      setRectangle(new Point(0,0), new Point(10,20));
      batman = new Point(3,0);
      
      Point position = Player.get_X_SymmetryProjection(batman, r);
      
      assertThat(position.x, is(7));
      assertThat(position.y, is(0));
    }
  
    @Test
    public void rectangle_offset_X() throws Exception {
      setRectangle(new Point(10,0), new Point(40,20));
      batman = new Point(13,0);
      
      Point position = Player.get_X_SymmetryProjection(batman, r);
      
      assertThat(position.x, is(37));
      assertThat(position.y, is(0));
    }
    
    @Test
    public void rectangle_offset_batman_other_side_X() throws Exception {
      setRectangle(new Point(10,0), new Point(40,20));
      batman = new Point(39,0);
      
      Point position = Player.get_X_SymmetryProjection(batman, r);
      
      assertThat(position.x, is(11));
      assertThat(position.y, is(0));
    }
  
  }
  
  static void setRectangle(Point point, Point point2) {
    r.p0 = point;
    r.p1 = point2;
  }
}
