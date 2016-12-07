package trigonometry;

public class MathUtil {

  public static double[] resolve2ndDegree(double a, double b, double c) {
    double root = b*b-4*a*c;
    if (root < 0) {
      return null;
    } 
    
    double sqRoot = Math.sqrt(root);
    if (root == 0) {
      return new double[] { -b / (2*a) };
    } else {
      return new double[] { (-b +sqRoot) / (2*a), (-b -sqRoot) / (2*a)};
    }
  }
}
