package ooc.orders;

public enum Charge {
  
  TORPEDO(0),
  SILENCE(1),
  SONAR(2),
  MINE(3),
  UNKNWON(4);
  
  public final int index;

  Charge(int index) {
    this.index = index;
  }
  
  public static Charge fromIndex(int i) {
    switch(i) {
    case 0: return TORPEDO;
    case 1: return SILENCE;
    case 2: return SONAR;
    case 3: return MINE;
    }
    return TORPEDO;
  }
}
