package hypersonic;

import java.util.Scanner;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.entities.Item;
import hypersonic.montecarlo.MonteCarlo;
import hypersonic.utils.P;

public class Player {
  
  Board board = new Board();
  private static Scanner in;
  private static int myId;

  void play() {
    Simulation sim = new Simulation();
    sim.board = board;
    MonteCarlo mc = new MonteCarlo();

    while (true) {
      getSimulationState();
      
      mc.root.retrocedBoards();
      System.err.println("Current board cache is "+Board.availableBoards.size());
      mc.simulate(sim);
      System.err.println("(after sim) board cache is "+Board.availableBoards.size());
      Move move = mc.findNextBestMove();
      outputMove(board.me, move);
    }
  }
  private void outputMove(Bomberman me, Move move) {
    int newX = board.me.position.x;
    int newY = board.me.position.y;
    boolean dropBomb = false;

    switch(move) {
      case DOWN_BOMB:
        dropBomb = true;
      case DOWN:
        newY+=1;
        break;
      case LEFT_BOMB:
        dropBomb = true;
      case LEFT:
        newX-=1;
        break;
      case RIGHT_BOMB:
        dropBomb = true;
      case RIGHT:
        newX+=1;
        break;
      case STAY_BOMB:
        dropBomb = true;
      case STAY:
        break;
      case UP_BOMB:
        dropBomb = true;
      case UP:
        newY-=1;
    }
    if (dropBomb) {
      System.out.println("BOMB "+newX+" "+newY);
    } else {
      System.out.println("MOVE "+newX+" "+newY);
    }
  }
  private void getSimulationState() {
    initBoard();
    initEntities();
  }
  private void initEntities() {
    int bombsOnBoard[] = new int[4];
    
    int entities = in.nextInt();
    for (int i = 0; i < entities; i++) {
      int entityType = in.nextInt();
      int owner = in.nextInt();
      int x = in.nextInt();
      int y = in.nextInt();
      int param1 = in.nextInt();
      int param2 = in.nextInt();
      if (entityType == 0) {
        Bomberman player = new Bomberman(board, owner, P.get(x, y), param1, param2);
        board.addPlayer(player);
        if (player.owner == myId) {
          board.me = player;
        }
      } else if (entityType == 1) {
        Bomb bomb = new Bomb(board, owner, P.get(x, y), param1, param2);
        board.addBomb(bomb);
        bombsOnBoard[owner]+=1;
      } else if (entityType == 2) {
        Item item = new Item(board, owner, P.get(x, y), param1, param2);
        board.addItem(item);
      }
    }
    // update bombsCount
    for (Bomberman b : board.players) {
      b.bombCount = b.bombsLeft + bombsOnBoard[b.owner];
    }
    System.err.println("ME == pos: "+board.me.position+" bLeft: "+board.me.bombsLeft+ "/"+board.me.bombCount+" - range:"+board.me.currentRange);
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