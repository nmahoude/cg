package hypersonic.utils;

public class P {
  public P(int x2, int y2) {
    x = x2;
    y = y2;
  }
  public P() {
    // TODO Auto-generated constructor stub
  }
  public int x;
  public int y;
  public int distTo(P p) {
    return (int)(Math.pow(p.x-x,2)+Math.pow(p.y-y, 2));
  }
}
