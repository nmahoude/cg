package dab;

public class Edge {
  public static final int TOP = 0;
  public static final int RIGHT = 1;
  public static final int BOTTOM = 2;
  public static final int LEFT = 3;
  
  Box boxes[] = new Box[2];
  int boxesFE = 0;
  private boolean isSet = false;
  
  public final String type;
  
  public Edge(String type) {
    this.type = type;
  }
  public void set() {
    isSet  = true;
  }

  public boolean isSet() {
    return isSet;
  }

  public void unset() {
    isSet = false;
  }
  
  @Override
  public String toString() {
    return "E ("+type+")["+(boxes[0] != null ? boxes[0].name : "Huh?")+ (boxes[1] != null ? "," + boxes[1].name : "")+"]";
  }
}
