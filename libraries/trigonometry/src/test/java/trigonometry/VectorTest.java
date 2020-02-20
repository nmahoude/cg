package trigonometry;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class VectorTest {

  @Test
  @Ignore
  public void getInertialPointsIntersection_zeroSolution() throws Exception {
    Vector speed = new Vector(0, 100);
    Vector desiredDirection = new Vector(100, 0);
    double maxForce =  99;
    
    Point[] solutions = Vector.getInertialPointsIntersection(speed, desiredDirection, maxForce);
    
    assertThat(solutions, is (nullValue()));
  }

  @Test
  @Ignore
  public void getInertialPointsIntersection_oneSolution() throws Exception {
    Vector speed = new Vector(0, 100);
    Vector desiredDirection = new Vector(100, 0);
    double maxForce =  100;
    
    Point[] solutions = Vector.getInertialPointsIntersection(speed, desiredDirection, maxForce);
    
    assertThat(solutions.length, is (1));
  }
  
  @Test
  @Ignore
  public void getInertialPointsIntersection() throws Exception {
    Vector speed = new Vector(0, 100);
    Vector desiredDirection = new Vector(100, 0);
    double maxForce =  141.421356237;
    
    Point[] solutions = Vector.getInertialPointsIntersection(speed, desiredDirection, maxForce);
    
    assertThat(solutions.length, is (2));
    assertThat(solutions[0], is (new Point(100, 0)));
    assertThat(solutions[1], is (new Point(-100, 0)));
  }


}
