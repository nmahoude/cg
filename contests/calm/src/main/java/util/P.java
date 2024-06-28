package util;

public class P {
  static P[] pos = new P[11*7];
  static {
    for (int y=0;y<7;y++) {
      for (int x=0;x<11;x++) {
        pos[x+11*y] = new P(x,y);
      }
    }
  }
  
  public final int x;
  public final int y;
  public final int index;
  private P(int x, int y) {
    this.x = x;
    this.y = y;
    this.index = x + 11 * y;
  }
  
  public static P get(int x,int y) {
    return pos[x+11*y];
  }
  
  @Override
  public String toString() {
    return "("+x+","+y+")";
  }

  public int manhattanDistance(P pos) {
    return Math.abs(this.x-pos.x)+Math.abs(this.y-pos.y);
  }

  public int neighborDistance(P pos) {
    return Math.max(Math.abs(this.x-pos.x),Math.abs(this.y-pos.y));
  }

  public static P get(int index) {
    return pos[index];
  }

  public P getUp() {
    return pos[this.index - 11];
  }
  public P getDown() {
    return pos[this.index + 11];
  }
  public P getRight() {
    return pos[this.index + 1];
  }
  public P getLeft() {
    return pos[this.index - 1];
  }

  public String out() {
    return ""+x+" "+y+" ";
  }
}
