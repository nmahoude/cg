package calm;

import java.util.Scanner;

import calm.ai.Order;
import calm.ai.OrderTag;
import calm.aifsm.FSMAI;
import calm.desertmaker.DMNode;

public class Player {
  public static final boolean DEBUG = true;
  public static final boolean DEBUG_PICKING = DEBUG && true;
  public static final boolean DEBUG_ASTAR = DEBUG && false;
  public static final boolean DEBUG_TABLES = DEBUG && true;
  
  public static final Map map = new Map();
  
  FSMAI ai = new FSMAI();
  public static State state = new State();


  public static int turnsRemaining;
  public static long start;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    Player player = new Player();
    player.play(in);
  }
  
  public void play(Scanner in) {
    DMNode.resetCache();
    state.readInit(in);

    while (true) {
      readTurn(in);
      System.err.println("Turn remaining: "+turnsRemaining);
      System.err.println("My current pos "+state.me.pos+" holding "+state.me.hands);
      start = System.currentTimeMillis();

      Order order = ai.think(state);
      
      if (order.tag == OrderTag.USE && order.pos == map.ovenAsEquipment.pos) {
        System.err.println("use oven");
        if (state.ovenContents.isEmpty() && state.me.pos.neighbor(map.ovenAsEquipment.pos) <= 1) {
          System.err.println("I put in oven");
          state.ovenIsMine = true;
        }
      }
      
      long end = System.currentTimeMillis();
      System.err.println("duration "+(end-start)+" ms");
      System.out.println(order.output());
    }
  }

  private void readTurn(Scanner in) {
    turnsRemaining = in.nextInt();
    
    state.read(in);
  }
}
