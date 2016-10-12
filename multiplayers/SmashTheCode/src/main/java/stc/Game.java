package stc;

public class Game {
  int nextBalls[] = new int[8];
  int nextBalls2[] = new int[8];
  
  Board myBoard = new Board();
  Board otherBoard = new Board();
  public int myScore;
  public int otherScore;
  final public void prepare() {
    myBoard.prepare();
    otherBoard.prepare();
  }

  public final void debug() {
    String blocks = "GameTest.setNextBlocks(game,\n";
    for (int i=0;i<8;i++) {
      blocks += "\""+nextBalls[i]+""+nextBalls2[i]+"\"";
      if (i != 7) {
        blocks +=",\n";
      }
    }
    blocks+="\n);";
    System.err.println(blocks);
  }
}
