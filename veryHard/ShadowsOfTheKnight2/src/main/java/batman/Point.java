package batman;

public class Point {
  public int x;
  public int y;
  
  public Point() {
    x = 0;
    y = 0;
  }

  public Point(int w, int h) {
    x = w;
    y = h;
  }

  public String debug() {
    return "("+x+","+y+")";
  }
}
