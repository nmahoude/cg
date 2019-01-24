package hypersonic;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import hypersonic.ai.HeatMap;
import hypersonic.ai.MC;
import hypersonic.entities.Bomb;
import hypersonic.entities.Bomberman;
import hypersonic.entities.Item;
import hypersonic.simulation.Simulation;
import hypersonic.utils.P;

public class Player {
  public static int NUMBER_OF_PLAYER = 4;
  public static boolean DEBUG_INPUT = true;
  public static boolean DEBUG_AI = false;
  public static Random rand = ThreadLocalRandom.current(); //new Random(0);
  
  public static long startTime;
  public static int myId;

  
  public State state = new State();
  private int turn = 0;
  private Scanner in;
  public static P goal;
  
  public Player(Scanner in) {
    this.in = in;
  }

  void play() {
    readInitialData();
    MC ai = new MC();
    
    while (true) {
      turn++;
      readGameState();
      if (turn == 1) {
        startTime+= 500;
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
      System.err.println(""+width+" "+height+" "+myId);
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
        System.err.println(""+entityType + " "+owner+" "+x+" "+y+" "+param1+" "+param2);
      }
      if (entityType == 0) {
        Bomberman player = state.getBomberman(owner);
        player.owner = owner;
        NUMBER_OF_PLAYER = owner+1;
        player.position = P.get(x, y);
        player.bombsLeft =  param1;
        player.currentRange = param2;
        player.isDead = false;
      } else if (entityType == 1) {
        int turnAtExplosion = turn + param1;
        final Bomb bomb = Cache.popBomb(owner, P.get(x, y), turnAtExplosion, param2);
        state.addBomb(bomb);
        bombCountOnTheBoard[owner]+=1;
      } else if (entityType == 2) {
        final Item item = Item.create(state, owner, P.get(x, y), param1, param2);
        state.addItem(item);
      }
    }
    // update bombsCount
    for (int p=0;p<state.playersFE;p++) {
      Bomberman b = state.players[p];
      b.bombCount = b.bombsLeft + bombCountOnTheBoard[b.owner];
    }
    //System.err.println("ME == pos: "+board.me.position+" bLeft: "+board.me.bombsLeft+ "/"+board.me.bombCount+" - range:"+board.me.currentRange);
  }

  private void initState() {
    state.turn = this.turn;
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
    
    final Player p = new Player(in);
    p.play();
  }
}