package ooc;

import java.util.Random;
import java.util.Scanner;

import ooc.ai.deathBlow.DeathBlowAI;
import ooc.ai.search.Search;
import ooc.ai.search.SearchAI;
import ooc.ai.start.PatternStartPositionAI;
import ooc.minimax.Minimax;
import ooc.orders.Order;
import ooc.orders.OrderTag;
import ooc.orders.Orders;
import ooc.path.PathFinder;
import ooc.sim.Simulator;

public class Player {
  public static boolean PROD = false;
  
  public static boolean D_ORDERS = !PROD & false;
  public static final boolean D_PERF = !PROD & false;
  public static boolean D_SEARCH = !PROD & true;
  public static int D_SEARCH_TURN = 0;
  public static final boolean D_TOPERDOES = !PROD & false;
  public static final boolean D_FASTDETECTOR = !PROD & false;
  public static final boolean D_SCORE = !PROD & false;
  public static final boolean D_HEATMAP = !PROD & false;
  public static final boolean D_MINES = !PROD & false;
  public static final boolean D_OPP_MAPPER = !PROD & false;
  public static final boolean D_MY_MAPPER = !PROD & false;
  public static final boolean D_TRIGGERS = !PROD & false;

  
  public static int turn = 0;
  private static final Simulator SIMULATOR = new Simulator(true);
	public static Search search = new Search();
	public static Minimax minimax = new Minimax();
  public static Random random = /*PROD ? ThreadLocalRandom.current() :*/ new Random(0);
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    new Player().play(in);
  }
  public static final OOCMap map = new OOCMap();
  public static Oracle oracle;
  public static long start;
  State deathBlowState = new State();
  private DeathBlowAI deathBlow = new DeathBlowAI();
  private State currentState = new State();
  private SearchAI standardAI = new SearchAI();
  private PatternStartPositionAI startAI = new PatternStartPositionAI();
  
  public static PathFinder finder = new PathFinder();
  public static String MSG = null;
  
  private void play(Scanner in) {
    map.read(in);
    currentState.attachMap(map);
    oracle = new Oracle(map);

    /*
    int best = 0;
    for (int i=0;i<OOCMap.S2;i++) {
      if (map.size[i] > map.size[best]) {
        best = i;
      }
    }
    P startPos = P.getFromOffset(best);
    System.err.println("Best is "+startPos+" with "+map.size[best]);
    System.out.println(String.format("%d %d", startPos.x, startPos.y));
    */

    startAI.outputStartingPos(currentState);
    
    while (true) {
      MSG = null;
      currentState.read(in);

      turn++;
      System.err.println("Turn = "+turn);
      //finder.next(currentState.myPos, currentState.visitedCells);
      
      start = System.currentTimeMillis();
      FreeCellsDetector2.resetCache();
      
      oracle.preTurnInfo(currentState);

      initFastDetector();
      
      Orders orders = null;
      
      /* death blow - preuve que ca marche ?*/ 
      if (canTryToDeathBlow()) {
        long dbStart = System.currentTimeMillis();
        deathBlowState.copyFrom(currentState);
        orders = deathBlow.think(deathBlowState, oracle);
        long dbStop = System.currentTimeMillis();
        System.err.println("DB in "+(dbStop - dbStart)+" ms");
        if (!orders.isEmpty()) {
          System.err.println("DeathBlow");
          MSG = "DB ";
          orders.debug();
        }
      }
      
      if (orders == null || orders.isEmpty()) {
        orders = standardAI.think(currentState, oracle);
      }

      applyOrderToState(orders);
      oracle.postTurnInfo(currentState, orders);
      currentState.teardown(orders);

    	long end = System.currentTimeMillis();
    	
      orders.print((MSG != null ? MSG : "")+""+turn
              +" - "
              +"o:"+oracle.oppMapper.potentialPositions.size()+" "
              +""+oracle.oppMapper.bestRealityPos+" "+(int)(oracle.oppMapper.bestPot * 100)+"% "
              +"m:"+oracle.myMapper.potentialPositions.size()+" "
              +(end-start)+"ms"
              );
    }
  }

	public static void time(String msg ) {
	  if (Player.D_PERF) {
	    System.err.println(msg+" "+(System.currentTimeMillis() - start)+"ms");
	  }
  }

  private boolean canTryToDeathBlow() {
		/* torpedo & silence can be charged in this turn (after a move)
		    and there is not a lot of possibilities
		*/
		return currentState.cooldowns.torpedoCooldown() + currentState.cooldowns.silenceCooldown() <= 1
					 && oracle.oppMapper.potentialPositions.size() < 10
					 && oracle.closestOppPos() < 10
					 && currentState.oppLife <= 2;
	}

  private void initFastDetector() {
    currentState.fastDetector.init(currentState, oracle.myMapper);
    
    for (Order order : currentState.oppOrders.getOrders()) {
      if (order.tag == OrderTag.TRIGGER || order.tag == OrderTag.TORPEDO) {
        int damage = currentState.myPos == order.pos ? 2 : currentState.myPos.blastDistance(order.pos) == 1 ? 1 : 0;
        currentState.fastDetector.oppHasHit(damage, order.pos);
      }
    }
    if (Player.D_FASTDETECTOR) {
      currentState.fastDetector.debug();
    }
  }

  private void applyOrderToState(Orders orders) {
    for (Order order : orders.getOrders()) {
      SIMULATOR.applySimulation(currentState, oracle.myMapper, order);
    }
    standardAI.chargeAttackAI.updateReal(currentState);
  }
}