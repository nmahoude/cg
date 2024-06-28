package sg22._posminimax;

public class Node {

  public Node(int myPos, int hisPos) {
    this.myPos = myPos;
    this.hisPos = hisPos;
    turn = true; // me
  }

  int myPos;
  int hisPos;
  
  boolean turn;
}
