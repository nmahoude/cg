package trigonometry;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class OBB2DTest {

  @Test
  public void horizontal_collisionPointVector() throws Exception {
    OBB2D rect1 = new OBB2D(new Point(0,50), new Vector(100,0), 50);
    OBB2D rect2 = new OBB2D(new Point(50,50), new Vector(100,0), 50);
    
    assertThat(rect1.overlaps(rect2), is(true));
  }
  
  @Test
  public void horizontal_collision() throws Exception {
    OBB2D rect1 = new OBB2D(new Vector[] {
        new Vector(0,0),
        new Vector(100,0),
        new Vector(100,100),
        new Vector(0,100)
    });

    OBB2D rect2 = new OBB2D(new Vector[] {
        new Vector(50,0),
        new Vector(150,0),
        new Vector(150,100),
        new Vector(50,100)
    });
    
    assertThat(rect1.overlaps(rect2), is(true));
  }
  
  @Test
  public void horizontal_NO_collision() throws Exception {
    OBB2D rect1 = new OBB2D(new Vector[] {
        new Vector(0,0),
        new Vector(100,0),
        new Vector(100,100),
        new Vector(0,100)
    });

    OBB2D rect2 = new OBB2D(new Vector[] {
        new Vector(101,0),
        new Vector(201,0),
        new Vector(201,100),
        new Vector(0,100)
    });
    
    assertThat(rect1.overlaps(rect2), is(false));
  }
  
}
