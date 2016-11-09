package stc2;

public class Game {
  int nextBalls[] = new int[8];
  int nextBalls2[] = new int[8];
  
  BitBoard myBoard = new BitBoard();
  BitBoard otherBoard = new BitBoard();
  
  public int myScore;
  public int otherScore;
  
  public void prepare() {
    myBoard.clear();
    otherBoard.clear();
  }

  public String debugPairs() {
    StringBuffer sb = new StringBuffer(3*8);
    for (int i=0;i<8;i++) {
      sb.append(nextBalls[i]);
      sb.append(nextBalls2[i]);
      sb.append(" ");
    }
    return sb.toString();
  }
}
