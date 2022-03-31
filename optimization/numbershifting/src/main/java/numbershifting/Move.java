package numbershifting;

import java.util.ArrayList;
import java.util.List;

public class Move {
  Pos from;
  Pos to;
  int fromValue;
  boolean addition;
  int toValue;

  Move parent;
  List<Move> children = new ArrayList<>();
  
  
  public Move(Pos from, int fromValue, Pos to, int toValue, boolean addition ) {
    this.from = from;
    this.fromValue = fromValue;
    this.to = to;
    this.toValue = toValue;
    this.addition = addition;
  }

  public String output() {
    String output = from.x+" "+from.y+" ";
    if (to.x > from.x) output+="R";
    if (to.x < from.x) output+="L";
    if (to.y < from.y) output+="U";
    if (to.y > from.y) output+="D";

    output+=" " + (addition ? "+" : "-");
    return output;
  }

  
  public int value() {
    if (addition) {
      return fromValue + toValue;
    } else {
      return Math.abs(fromValue - toValue);
    }
  }
}
