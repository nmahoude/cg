package numbershifting;

public class Move {
  Pos from;
  Pos to;
  boolean addition;

  public Move(Pos from, Pos to ) {
    this.from = from;
    this.to = to;
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

}
