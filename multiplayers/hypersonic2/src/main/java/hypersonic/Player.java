package hypersonic;

import hypersonic.ai.HeatMap;
import hypersonic.ai.search.Search;
import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.entities.Item;
import hypersonic.utils.P;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Player {
  public static final int DEPTH = 20;
  public static final boolean DEBUG_SCORE = false;
  public static int NUMBER_OF_PLAYER = 4;
  public static boolean DEBUG_INPUT = true;
  public static boolean DEBUG_AI = false;
  public static boolean DEBUG_OPTIMIZE = false;
  public static boolean DEBUG_LASTBEST = false;
  public static Random rand = ThreadLocalRandom.current(); // new Random(0);

  public static long startTime;
  public static int myId;
  Board copyOfBoard = new Board();

  public State state = new State();
  private int turn = 0;
  private Scanner in;
  public static P goal;
  public static int KILLERBOMB_BONUS;

  public Player(Scanner in) {
    this.in = in;
  }

  void play() {
    readInitialData();
    Search ai = new Search();

    ai.reset();

    while (true) {
      turn++;
      ai.reset();

      readGameState();

      state.hash = 0;
      if (turn == 1) {
        startTime += 500;
      }
      Player.KILLERBOMB_BONUS = 0;
      if (state.players[myId].bombsLeft > 0) {
        copyOfBoard.copyFrom(state.board);
        Bomberman me = state.players[myId];
        copyOfBoard.addBomb(Cache.popBomb(myId, me.position, Bomb.DEFAULT_TIMER, me.currentRange));

        for (int i = 0; i < NUMBER_OF_PLAYER; i++) {
          if (i == myId)
            continue;
          Bomberman bomberman = state.players[i];
          if (bomberman.isDead)
            continue;
          int cells = new BoardBFS().movements(state.board, state.players[i].position);
          int cellsWithBombs = new BoardBFS().movements(copyOfBoard, state.players[i].position);
          if (cellsWithBombs < 5 && cells > 5) {
            Player.KILLERBOMB_BONUS = 20_000;
            System.err.println("Can reduce player " + i + " cells !!!! ");
          } else {
            System.err.println("Player " + i + "reduction => " + cells + " => " + cellsWithBombs);
          }
        }
      }

      // now look what I can do !
      ai.think(state);

      ai.ouput(state);
    }
  }

  private void readInitialData() {
    int width = in.nextInt();
    int height = in.nextInt();
    myId = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.println("" + width + " " + height + " " + myId);
    }
  }

  public void readGameState() {
    initState();
    initEntities();

    HeatMap.calculate(this.state);
  }

  private void initEntities() {
    final int bombCountOnTheBoard[] = new int[4];

    final int entitiesCount = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.println(entitiesCount);
    }
    startTime = System.currentTimeMillis();
    for (int i = 0; i < entitiesCount; i++) {
      final int entityType = in.nextInt();
      final int owner = in.nextInt();
      final int x = in.nextInt();
      final int y = in.nextInt();
      final int param1 = in.nextInt();
      final int param2 = in.nextInt();
      if (Player.DEBUG_INPUT) {
        System.err.println("" + entityType + " " + owner + " " + x + " " + y + " " + param1 + " " + param2);
      }
      if (entityType == 0) {
        Bomberman player = state.getBomberman(owner);
        player.owner = owner;
        NUMBER_OF_PLAYER = owner + 1;
        player.position = P.get(x, y);
        player.bombsLeft = param1;
        player.currentRange = param2;
        player.isDead = false;
      } else if (entityType == 1) {
        int turnAtExplosion = param1;
        final Bomb bomb = Cache.popBomb(owner, P.get(x, y), turnAtExplosion, param2);
        state.addBomb(bomb);
        bombCountOnTheBoard[owner] += 1;
      } else if (entityType == 2) {
        final Item item = Item.create(state, owner, P.get(x, y), param1, param2);
        state.addItem(item);
      }
    }
    // update bombsCount
    for (int p = 0; p < state.playersFE; p++) {
      Bomberman b = state.players[p];
      b.bombCount = b.bombsLeft + bombCountOnTheBoard[b.owner];
    }
    // System.err.println("ME == pos: "+board.me.position+" bLeft:
    // "+board.me.bombsLeft+ "/"+board.me.bombCount+" -
    // range:"+board.me.currentRange);
  }

  private void initState() {
    state.turn = 0;
    state.init();
    for (int y = 0; y < Board.HEIGHT; y++) {
      final String row = in.next();
      if (Player.DEBUG_INPUT) {
        System.err.println(row);
      }
      state.init(y, row);
    }
  }

  public static void main(final String args[]) {
    Scanner in = new Scanner(System.in);
    try {
      Player p = new Player(in);
      p.play();
    } catch (Error er) {
      System.err.println(er);
    }
  }
}