package cgfx;

public class Inset {
  public static final Inset NO = new Inset(0);
  public final int l;
  
  public Inset(int intset) {
    this.l = intset;
  }

  public static Inset of(int l) {
    return new Inset(l);
  }
}
