package ooc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ooc.charge.ChargeDetector;
import ooc.orders.Order;
import ooc.orders.OrderTag;
import ooc.orders.Orders;
import ooc.trailmapper.TrailMapper;

public class Oracle {
  private static final int MAX_TURN_TO_EVADE = 2;


  private List<Orders> opponentOrdersHistory = new ArrayList<>();

  
  public TrailMapper oppMapper;
  public TrailMapper myMapper;
  public int oppMineCount = 0;
  public int myNextTurnExpectedLife = State.MAX_LIFE;
  public int oppNextTurnExpectedLife = State.MAX_LIFE;

  public P lastTorpedo = P.I;
  public P lastTrigger = P.I;
  private int lastSonarSector;


  public ChargeDetector chargeDetector = new ChargeDetector();


  private P lastOppTorpedo;

  public int dangerTimer = -1;
  
  private P lastOppTrigger;


  private int closestOppPos;

  public Oracle(OOCMap map) {
    oppMapper = new TrailMapper(map);
    myMapper = new TrailMapper(map);
  }
  
  public void preTurnInfo(State state) {
    chargeDetector.setupTurn();
    myMapper.setupTurn();
    oppMapper.setupTurn();
    
    opponentOrdersHistory.add(state.oppOrders);
  
    dangerTimer = Math.max(-1, dangerTimer - 1);

    if (myNextTurnExpectedLife != state.myLife) {
      int delta = myNextTurnExpectedLife - state.myLife;
      if (delta > 0) {
        dangerTimer = MAX_TURN_TO_EVADE;
        if (state.oppOrders.isTorpedoAfterMove()) {
          dangerTimer+=1;
        }
      }
    }
    if (state.oppOrders.hasSurface()) {
      oppNextTurnExpectedLife--;
    }

    if (lastSonarSector != 0) {
      oppMapper.handleSonarResult(lastSonarSector, state.sonarResult);
    }
    
    oppMapper.applyOppDamage(lastTorpedo);
    oppMapper.applyOppDamage(lastTrigger);
    
    lastOppTorpedo = P.I;
    lastOppTrigger = P.I;
    for (Order order : state.oppOrders.getOrders()) {
      order.accept(oppMapper);
      order.accept(chargeDetector);

      if (order.tag == OrderTag.TORPEDO) {
        lastOppTorpedo = order.pos;
      } else if (order.tag == OrderTag.MINE) {
        oppMineCount++;
      } else if (order.tag == OrderTag.TRIGGER) {
        oppMineCount--;
        lastOppTrigger = order.pos;
      } else if (order.tag == OrderTag.SONAR) {
        myMapper.handleSonarResult(order.value, state.myPos.sector == order.value ? SonarResult.PRESENT : SonarResult.ABSENT);
      }
    }
    
    oppMapper.filterRealityBasedOnDamages(oppNextTurnExpectedLife - state.oppLife);
    
    chargeDetector.teardownTurn();
    oppMapper.teardownTurn();
    myMapper.teardownTurn();
    
    updateClosestOppPos(state);
    
    if (Player.D_OPP_MAPPER) {
      oppMapper.debug();
      oppMapper.debugPotentiality();
    }
    if (Player.D_MINES) {
      oppMapper.debugMinesHeat2();
    }
    oppNextTurnExpectedLife = state.oppLife;
  }

  private void updateClosestOppPos(State state) {
    closestOppPos = Integer.MAX_VALUE;
    for (P p: oppMapper.potentialPositions) {
      if (Player.map.distances(p, state.myPos) < closestOppPos) {
        closestOppPos = Player.map.distances(p, state.myPos);
      }
    }
  }

  public boolean isInDanger() {
    return (closestOppPos < 5 && myMapper.potentialPositions.size() == 1) /*|| (dangerTimer >= 0 )*/;
  }
  
  public void postTurnInfo(State state, Orders myOrders) {
    
    this.lastTorpedo = P.I;
    this.lastTrigger = P.I;
    this.lastSonarSector = 0;
    
    int myDeltaLife = myNextTurnExpectedLife - state.myLife;
    myNextTurnExpectedLife = state.myLife;

    myMapper.applyOppDamage(lastOppTorpedo);
    myMapper.applyOppDamage(lastOppTrigger);
    
    
    for (Order order : myOrders.getOrders()) {
      order.accept(myMapper);
      if (order.tag == OrderTag.TORPEDO) {
        this.lastTorpedo = order.pos;
      } else if (order.tag == OrderTag.SURFACE) {
        myDeltaLife -= 1; // take surface into account this turn
      } else if (order.tag == OrderTag.MOVE) {
      } else if (order.tag == OrderTag.SILENCE) {
      } else if (order.tag == OrderTag.SONAR) {
        this.lastSonarSector = order.value;
      } else if (order.tag == OrderTag.TRIGGER) {
        this.lastTrigger = order.pos;
      } else if (order.tag == OrderTag.MINE) {
      }
    } 

    myMapper.filterRealityBasedOnDamages(myDeltaLife);
    myMapper.teardownTurn();
    
    System.err.println("Opp Potential positions (tot="+oppMapper.potentialPositions.size()+" nodes="+oppMapper.currentLayerFE+" / "+oppMapper.realityCountTotal+")");
    System.err.println("My Potential positions (tot="+myMapper.potentialPositions.size()+" nodes="+myMapper.currentLayerFE+" / "+myMapper.realityCountTotal+")");
    if (Player.D_MY_MAPPER) {
      System.err.println("My mapper size : "+myMapper.potentialPositions.size());
      myMapper.debug();
    }
  }

  public Set<P> oppActualPotentialPositions() {
    return oppMapper.potentialPositions;
  }

  public Set<P> myPotentialPositions() {
    return myMapper.potentialPositions;
  }

  public String debugString() {
    return "(d:"+dangerTimer+")";
  }

  public int closestOppPos() {
    return closestOppPos;
  }

}
