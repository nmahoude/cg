package hypersonic;

import java.util.Scanner;

import hypersonic.utils.P;

public class Player {

  Board board = new Board();
  private static Scanner in;
  private static int myId;

  void play() {

    while (true) {
      initBoard();
      initEntities();
      
      P p = board.findClosestBox();
      if (p != null) {
        if (p.distTo(board.player1) == 1) {
          System.out.println("BOMB "+p.x+" "+p.y);
        } else {
          System.out.println("MOVE "+p.x+" "+p.y);
        }
      } else {
        System.out.println("MOVE 0 0");
      }
    }
  }
  private void initEntities() {
    int entities = in.nextInt();
    for (int i = 0; i < entities; i++) {
      int entityType = in.nextInt();
      int owner = in.nextInt();
      int x = in.nextInt();
      int y = in.nextInt();
      int param1 = in.nextInt();
      int param2 = in.nextInt();
      if (entityType == 1) {
        Bomb bomb = new Bomb();
        bomb.p = new P(x,y);
        bomb.timer = param1;
        bomb.range = param2;
        board.addBomb(bomb);
        
      } else if (entityType == 0 && owner == myId) {
        board.player1 = new P(x,y);
      }
    }
  }
  private void initBoard() {
    board.init();
    for (int y = 0; y < 11; y++) {
      String row = in.next();
      board.init(y, row);
    }
  }
  public static void main(String args[]) {
    in = new Scanner(System.in);
    int width = in.nextInt();
    int height = in.nextInt();
    myId = in.nextInt();
    
    Player p = new Player();
    p.play();
  }
}