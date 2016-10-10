package stc;

public class Game {
  int nextBalls[] = new int[8];
  int nextBalls2[] = new int[8];
  
  Board myBoard;
  Board otherBoard;
  public int myScore;
  public int otherScore;
  final public void prepare() {
    myBoard.prepare();
    otherBoard.prepare();
  }

  
}
