package trigonometry;

public class Point {

  public static final Point ZERO = new Point(0,0);
  public final double x,y;

  public Point(double x, double y) {
    super();
    this.x = x;
    this.y = y;
  }
  public Point(Point position) {
    this(position.x, position.y);
  }
  
  public static Point from(double x, double y) {
    return new Point(x,y);
  }
  @Override
  public String toString() {
    return "P("+x+","+y+")";
  }
  
  /** move towards the target, but only radius length
   * 
   * @param target
   * @param radius
   * @return
   */
  public Point moveTowards(Point target, double radius) {
    Vector v = target.sub(this);
    
    return this.add(v.normalize().dot(radius));
  }
  
  public Point add(Point addedPoint) {
    return new Point(x+addedPoint.x, y+addedPoint.y);
  }
  public Point add(Vector vec) {
    return new Point(x+vec.vx, y+vec.vy);
  }
  
  public Point sub(Vector vec) {
    return new Point(x-vec.vx, y-vec.vy);
  }

  public double distTo(Point p) {
    return Math.sqrt( (p.x-x)*(p.x-x) + (p.y-y)*(p.y-y) );
  }

  public double distTo(Point p, Vector v) {
    Point p2 = p.add(v);
    return distTo(p, p2);
  }

  public double distTo(Point p1, Point p2) {
    return Math.abs( (p2.y-p1.y)*x - (p2.x-p1.x)*y + p2.x*p1.y - p2.y*p1.x) / 
      Math.sqrt((p2.y-p1.y)*(p2.y-p1.y) + (p2.x-p1.x)*(p2.x-p1.x));
  }
  public Vector sub(Point p2) {
    return new Vector(x-p2.x, y-p2.y);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Point other = (Point) obj;
    if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
      return false;
    if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
      return false;
    return true;
  }
  public double squareDistance(Point p) {
    return (x-p.x)*(x-p.x)+(y-p.y)*(y-p.y);
  }
  public boolean isAbove(Point p0, Point p1) {
    if (y < p0.y && y < p1.y) {
      return false;
    }
    Vector n= this.sub(p0);
    Vector v = p1.sub(p0);
    Vector result = n.sub(v.dot(v.dot(n))); 
    return result.vy > 0;
  }
  
  Point closest(Point origin, Vector v) {
    Point p2 = origin.add(v);
    return closest(origin, p2);
  }
  
  Point closest(Point a, Point b) {
    double da = b.y - a.y;
    double db = a.x - b.x;
    double c1 = da*a.x + db*a.y;
    double c2 = -db*this.x + da*this.y;
    double det = da*da + db*db;
    double cx = 0;
    double cy = 0;

    if (det != 0) {
        cx = (da*c1 - db*c2) / det;
        cy = (da*c2 + db*c1) / det;
    } else {
        // The point is already on the line
        cx = this.x;
        cy = this.y;
    }

    return new Point(cx, cy);
  }
}
