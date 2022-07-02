package connect4;

public class Node {
  // state
  public long me;
  public long opp;
  
  // score
  public double score;
  
  @Override
  public boolean equals(Object obj) {
    Node other = (Node)obj;
    return other.me == this.me && other.opp == this.opp;
  }
  
}
