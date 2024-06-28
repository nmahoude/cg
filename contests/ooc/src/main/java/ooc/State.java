package ooc;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;

import ooc.orders.Order;
import ooc.orders.Orders;
import ooc.trailmapper.FastDetector;
import ooc.trailmapper.TorpedoHitStat;

public class State {

  public static final int MAX_LIFE = 6;
  public static List<P> myTrueMinesPos = new ArrayList<>();

  
  public P myPos = P.I;
  public int myLife = 6;
  public int oppLife = 6;
  
  SonarResult sonarResult;
  Orders oppOrders;

  public Cooldown cooldowns = new Cooldown();
  
  public OOCMap map;
  public BitSet visitedCells = new BitSet(OOCMap.S2);
  public BitSet myMines = new BitSet(OOCMap.S2);
  public FastDetector fastDetector = new FastDetector();


  public TorpedoHitStat torpedoHitStat = new TorpedoHitStat();

  public void attachMap(OOCMap map) {
    this.map = map;
  }
  
  public void read(Scanner in) {
    myPos = P.get(in.nextInt(), in.nextInt());
    System.err.println("My pos : "+myPos);

    myLife = in.nextInt();
    oppLife = in.nextInt();
    
    cooldowns.read(in);
    sonarResult = SonarResult.fromString(in.next());
    if (in.hasNextLine()) {
      in.nextLine();
    }
    oppOrders = Order.parse(in.nextLine());
    
    setVisitedCell(myPos);
    torpedoHitStat.reset();
  }

  public void setVisitedCell(P pos) {
    visitedCells.set(pos.o);
  }

  
  public void clearVisitedCells() {
    visitedCells.clear();
  }

  public void debugVisitedCells() {
    map.debugMap("Visited cells", (P pos) -> {
      if (pos == myPos) return "o";
      else if (visitedCells.get(pos.o) == false) return " ";
      else return "X";
      
    });
  }

  public void resetVisitedCells() {
    clearVisitedCells();
  }

  public void teardown(Orders orders) {
    
  }

	public void copyFrom(State model) {
		this.map = model.map;
		this.myPos = model.myPos;
		
    myLife = model.myLife;
    oppLife = model.oppLife;
    
    cooldowns.copyFrom(model.cooldowns);
    sonarResult = model.sonarResult;
    
    visitedCells.clear();
    visitedCells.or(model.visitedCells);

    myMines.clear();
    myMines.or(model.myMines);
    
    fastDetector.copyFrom(model, model.fastDetector);
    torpedoHitStat.copyFrom(model.torpedoHitStat);
	}

  public boolean isVisitedCells(P pos) {
    return visitedCells.get(pos.o);
  }

	public double expectedOppLife() {
		return oppLife - torpedoHitStat.potentialDamage;
	}

	public int maximalOppLife() {
		return oppLife - torpedoHitStat.minimalDamage;
	}
}
