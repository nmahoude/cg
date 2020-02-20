package stc3.ai;

public class Move {
  String comment;
  public int column;
  public int rotation;
  
  public Move(int column, int rotation, String comment) {
    super();
    this.column = column;
    this.rotation = rotation;
    this.comment = comment;
  }
  
  @Override
  public String toString() {
    return ""+column+" "+rotation+" "+comment;
  }
}
