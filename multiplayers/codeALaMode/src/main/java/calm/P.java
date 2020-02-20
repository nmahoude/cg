package calm;

public class P {
  final public int x;
  final public int y;
  final public int index;
  
  static P ps[] = new P[11*7];
  static {
    for (int y=0;y<7;y++) {
      for (int x=0;x<11;x++) {
        ps[x+y*11] = new P(x,y);
      }
    }
  }
  
  private P (int x, int y) {
    this.x = x;
    this.y = y;
    this.index = x+y*11;
  }
  
  static P get(int x, int y) {
    return ps[x+y*11];
  }
}
