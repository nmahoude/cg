package calm;

import util.P;

public class Action {
  private static Action moveCache[] = new Action[77];
  private static Action useCache[] = new Action[77];
  static {
    for (int index=0;index<77;index++) {
        P pos = P.get(index);
        moveCache[index] = new Action(false,pos);
        useCache[index] = new Action(true,pos);
    }
  }
  
  public final boolean use; // or move ?
  public final P pos;
  
  public Action(boolean b, P pos) {
    this.use = b;
    this.pos = pos;
  }

  public static Action getMove(P p) {
    return moveCache[p.index];
  }

  public static Action getUse(P p) {
    return useCache[p.index];
  }

  public String output() {
    if (use) {
      return "USE "+pos.out();
    } else {
      return "MOVE "+pos.out();
    }
  }
  
  @Override
  public String toString() {
    return "A: "+output();
  }
}
