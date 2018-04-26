package coderoyale;

import coderoyale.sites.Site;

public class Pos {
  public int x;
  public int y;

  public Pos() {
  }

  public Pos(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  @Override
  public String toString() {
    return "" + x + " "+y;
  }
  public double dist(Pos pos) {
    return Math.sqrt(dist2(pos));
  }
  
  public double dist2(Pos pos) {
    return (pos.x-x)*(pos.x-x) + (pos.y-y)*(pos.y-y);
  }

  public CRVector direction(Pos pos) {
    return new CRVector(pos.x - this.x , pos.y - this.y);
  }

  public Pos add(CRVector v) {
    return new Pos((int)(x+v.x), (int)(y+v.y));
  }
}
