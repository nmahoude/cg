package csb.ai;

import trigonometry.Point;

public class DummySolution implements AISolution {

  public Point target1;
  public int thrust1;
  public Point target2;
  public int thrust2;

  @Override
  public String[] output() {
    return new String[] {
        ""+(int)(target1.x)+" "+(int)(target1.y)+" "+thrust1,
        ""+(int)(target2.x)+" "+(int)(target2.y)+" "+thrust2
    };
  }

}
