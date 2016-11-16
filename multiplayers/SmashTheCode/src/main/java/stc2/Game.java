package stc2;

public class Game {
  public int nextBalls[] = new int[8];
  public int nextBalls2[] = new int[8];
  
  public BitBoard myBoard = new BitBoard();
  public BitBoard otherBoard = new BitBoard();
  
  
  public int lastSkullsCount;
  public int lastScore;
  
  public int myScore;
  public int otherScore;
  public long nanoStart;
  public long nanoEnd;
  
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
