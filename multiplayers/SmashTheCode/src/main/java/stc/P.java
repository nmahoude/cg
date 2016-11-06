package stc;


public class P {
  public final static P ps[] = new P[6*12];
  static {
    for (int y = 0; y < 12; y++) {
      for (int x = 0; x < 6; x++) {
        ps[x+6*y] = new P(x, y);
      }
    }
  }

  public P(int x2, int y2) {
    x = x2;
    y = y2;
  }

  int x, y;
}
