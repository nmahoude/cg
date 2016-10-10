package stc;

public class Ai {
  private Game game;
  String command = "";
  public Ai(Game game) {
    this.game = game;
  }
  
  public void think() {
    int color1 = game.nextBalls[0];
    int color2 = game.nextBalls2[0];
    
    Board boardCopy = new Board();
    
    int bestRot = 0;
    int bestX = 0;
    int bestScore = 0;
    
    for (int rot = 0;rot<4;rot++) {
      for (int x=0;x<6;x++) {
        if ((x== 0 && rot == 2) || (x==5 && rot==0)) {
          continue;
        }
        game.myBoard.copy(boardCopy);
        boardCopy.putBlocks(color1, color2, rot, x);
        int maxHeight = 0;
        for (int i=0;i<6;i++) {
          maxHeight=Math.max(boardCopy.heights[i], maxHeight);
        }
        int score = boardCopy.points-maxHeight;
        if (score > bestScore) {
          bestScore = maxHeight;
          bestRot = rot;
          bestX = x;
        }
      }
    }
    System.err.println("best : "+bestX+" / "+bestRot);
    command = ""+bestX+" "+bestRot;
  }
  public final String output() {
    return command;
  }
}
