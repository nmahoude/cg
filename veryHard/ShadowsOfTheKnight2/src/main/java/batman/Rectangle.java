package batman;

public class Rectangle {
  public Point p0,p1;
  
  public Rectangle() {
    p0 = new Point();
    p1 = new Point();
  }

  public int width() {
    return p1.x - p0.x;
  }

  public int middleX() {
    return (p0.x+p1.x) / 2;
  }

  public String debug() {
    return p0.debug()+"->"+p1.debug();
  }
}
