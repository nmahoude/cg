package lib.trigo;

public class VectorLib {
  public static double length(double vx, double vy) {
    return Math.sqrt(vx*vx+vy*vy);
  }

  public static double distance2(double vx, double vy) {
    return vx*vx+vy*vy;
  }
}
