package trigonometry;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.*;

import org.junit.Test;

import trigonometry.Trigonometry.Circle;
import trigonometry.Trigonometry.FrictionEngine;
import trigonometry.Trigonometry.Point;
import trigonometry.Trigonometry.Trajectory;
import trigonometry.Trigonometry.Vector;



public class TrigonometryTest {

  @Test
  public void createPoint() throws Exception {
    Point p = new Point(1,2);
    assertThat(p.x, is(1.0));
    assertThat(p.y, is(2.0));
  }
  
  @Test
  public void addPoint() throws Exception {
    Point p1 = new Point(1,2);
    Point p2 = new Point(7,5);
    Point exceptedPoint = new Point(8,7);
    
    Point result = p1.add(p2);
    
    assertThat(result, is (exceptedPoint));
  }

  @Test
  public void point_sub() {
    Point p1 = new Point(1,2);
    Point p2 = new Point(7,5);
    
    assertThat(p2.sub(p1), is(new Vector(6,3)));
  }
  @Test
  public void point_hash() throws Exception {
    assertThat(new Point(0,0).hashCode(), is(new Point(0,0).hashCode()));
  }
  
  @Test
  public void vector_hash() throws Exception {
    assertThat(new Vector(0,0).hashCode(), is(new Vector(0,0).hashCode()));
  }
  
  @Test
  public void addVector() throws Exception {
    Point p1 = new Point(1,2);
    Vector v2 = new Vector(3,4);
    Point exceptedPoint = new Point(4,6);
    
    Point result = p1.add(v2);
    
    assertThat(result, is (exceptedPoint));
  }
  
  @Test
  public void distTo_horizontal() throws Exception {
    Point p1 = new Point(1,2);
    Point p2 = new Point(2,2);

    assertThat(p1.distTo(p2), is(1.0));
  }

  @Test
  public void distTo_vertical() throws Exception {
    Point p1 = new Point(1,2);
    Point p2 = new Point(1,3);

    assertThat(p1.distTo(p2), is(1.0));
  }

  @Test
  public void distTo_diagonal() throws Exception {
    Point p1 = new Point(10,10);
    Point p2 = new Point(20,20);

    assertThat(p1.distTo(p2), closeTo(14.142, 0.001));
  }

  @Test
  public void distTo_random() throws Exception {
    Point p1 = new Point(5,17);
    Point p2 = new Point(13,97);

    assertThat(p1.distTo(p2), closeTo(80.399, 0.001));
  }

  @Test
  public void distTo_mutative() throws Exception {
    Point p1 = new Point(5,17);
    Point p2 = new Point(13,97);

    assertThat(p1.distTo(p2), is(p2.distTo(p1)));
  }
  
  @Test
  public void distToALineFrom2points() throws Exception {
    Point p0 = new Point(0,0);
    Point p1 = new Point(1,0);
    Point p2 = new Point(1,1);
    
    assertThat(p0.distTo(p1, p2), is(1.0));
  }
  
  @Test
  public void distToALineFromPointAndVector() throws Exception {
    Point p0 = new Point(0,0);
    
    Point p1 = new Point(1,0);
    Vector v1 = new Vector(0,1);
    
    assertThat(p0.distTo(p1, v1), is(1.0));
  }
  
  @Test
  public void lengthOfVector() throws Exception {
    assertThat(new Vector(3,4).length(), is(5.0));
  }
  
  @Test
  public void vector_dot() throws Exception {
    Vector v1 = new Vector(1,2);
    Vector v2 = new Vector(3,1);
    
    assertThat(v1.dot(v2), is(5.0));
  }
  
  @Test
  public void vector_add() throws Exception {
    Vector v1 = new Vector(1,2);
    Vector v2 = new Vector(3,1);
    
    assertThat(v1.add(v2), is(new Vector(4,3)));
  }
  @Test
  public void vector_alignAngles() throws Exception {
    Vector v1 = new Vector(1,0);
    Vector v2 = new Vector(1,0);
    
    assertThat(v1.angle(v2), is(0.0));
  }

  @Test
  public void vector_perpendicularsAngles() throws Exception {
    Vector v1 = new Vector(1,0);
    Vector v2 = new Vector(0,1);
    
    assertThat(v1.angle(v2), is(Math.PI / 2.0));
  }

  @Test
  public void vector_opposedAngles() throws Exception {
    Vector v1 = new Vector(1,0);
    Vector v2 = new Vector(-1,0);
    
    assertThat(v1.angle(v2), is(Math.PI));
  }

  @Test
  public void vector_someAngles() throws Exception {
    Vector v1 = new Vector(3,0);
    Vector v2 = new Vector(5,5);
    
    assertThat(v1.angle(v2), closeTo(Math.PI/4, 0.001));
  }
  
  @Test
  public void trajectory_simpleCase() throws Exception {
    Point p1 = new Point(0,0);
    Vector s1 = new Vector(10,0);

    Trajectory trajectory = new Trajectory(p1, s1);
    trajectory.simulate();
    
    assertThat(trajectory.position, is(new Point(10,0)));
    assertThat(trajectory.speed, is(s1));
  }

  @Test
  public void trajectory_frictionCase() throws Exception {
    Point p1 = new Point(0,0);
    Vector s1 = new Vector(10,0);

    Trajectory trajectory = new Trajectory(p1, s1);
    trajectory.engine = new FrictionEngine(0.85);
    trajectory.simulate();
    
    assertThat(trajectory.position.equals(new Point(8.5,0)), is(true));
    assertThat(trajectory.speed.equals(s1.dot(0.85)), is(true));
  }
  
  @Test
  public void circle_isIn() throws Exception {
    Circle c = new Circle(new Point(0,0), 10);
    
    assertThat(c.isIn(new Point(5,5)), is(true));
  }
  
  @Test
  public void circle_isIn_not() throws Exception {
    Circle c = new Circle(new Point(0,0), 10);
    
    assertThat(c.isIn(new Point(10,1)), is(false));
  }
  
  @Test
  public void circle_isOn() throws Exception {
    Circle c = new Circle(new Point(0,0), 10);
    
    assertThat(c.isOn(new Point(0,10)), is(true));
  }
  
  @Test
  public void circle_isOn_not_inside() throws Exception {
    Circle c = new Circle(new Point(0,0), 10);
    
    assertThat(c.isOn(new Point(0,9.9)), is(false));
  }
  @Test
  public void circle_isOn_not_outside() throws Exception {
    Circle c = new Circle(new Point(0,0), 10);
    
    assertThat(c.isOn(new Point(0,10.1)), is(false));
  }
}
