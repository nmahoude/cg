package hypersonic;

import java.util.Scanner;

import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.entities.Item;
import hypersonic.montecarlo.MonteCarlo;
import hypersonic.utils.P;

public class PlayerMCTS {
  
  Board board = new Board();
  private long startTime;
  private static Scanner in;
  private static int myId;
  private int turn = 0;
  
  void play() {
    final Simulation sim = new Simulation();
    sim.board = board;
    final MonteCarlo mc = new MonteCarlo();
 
    while (true) {
      turn ++;
      getSimulationState();
      
      mc.root.retrocedRoot();
int maxTime;
      //      System.err.println("board cache is "+Board.cache.size());
//      System.err.println("node cache is "+Node.cache.size());
//      System.err.println("bomberman cache is "+Bomberman.cache.size());
//      System.err.println("bombs cache is "+Bomb.cache.size());
//      System.err.println("items cache is "+Item.cache.size());
      if (turn <= 2) {
        maxTime = 100_000_000;
      } else {
        maxTime = 30_000_000;
      }
      mc.simulate(maxTime, startTime, sim);
      final Move move = mc.findNextBestMove();
      //final Move move = mc.simulateBeam(sim);
//      System.err.println("After sim :");
//      System.err.println("board cache is "+Board.cache.size());
//      System.err.println("node cache is "+Node.cache.size());
//      System.err.println("bomberman cache is "+Bomberman.cache.size());
//      System.err.println("bombs cache is "+Bomb.cache.size());
//      System.err.println("items cache is "+Item.cache.size());

      outputMove(board.me, move);
    }
  }
  private void outputMove(final Bomberman me, final Move move) {
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
    final int bombsOnBoard[] = new int[4];
    
    final int entities = in.nextInt();
    startTime = System.nanoTime();
    for (int i = 0; i < entities; i++) {
      final int entityType = in.nextInt();
      final int owner = in.nextInt();
      final int x = in.nextInt();
      final int y = in.nextInt();
      final int param1 = in.nextInt();
      final int param2 = in.nextInt();
      if (entityType == 0) {
        final Bomberman player = new Bomberman(board, owner, P.get(x, y), param1, param2);
        board.addPlayer(player);
        if (player.owner == myId) {
          board.me = player;
        }
      } else if (entityType == 1) {
        final Bomb bomb = Bomb.create(board, owner, P.get(x, y), param1, param2);
        board.addBomb(bomb);
        bombsOnBoard[owner]+=1;
      } else if (entityType == 2) {
        final Item item = Item.create(board, owner, P.get(x, y), param1, param2);
        board.addItem(item);
      }
    }
    // update bombsCount
    for (final Bomberman b : board.players) {
      b.bombCount = b.bombsLeft + bombsOnBoard[b.owner];
    }
    //System.err.println("ME == pos: "+board.me.position+" bLeft: "+board.me.bombsLeft+ "/"+board.me.bombCount+" - range:"+board.me.currentRange);
    System.err.println(board.getDebugString());
  }

  private void initBoard() {
    board.init();
    for (int y = 0; y < 11; y++) {
      final String row = in.next();
      board.init(y, row);
    }
  }
  public static void main(final String args[]) {
    in = new Scanner(System.in);
    final int width = in.nextInt();
    final int height = in.nextInt();
    myId = in.nextInt();
    
    final PlayerMCTS p = new PlayerMCTS();
    p.play();
  }
}