package ooc.ai.search;

import java.util.ArrayList;
import java.util.List;

import ooc.OOCMap;
import ooc.Oracle;
import ooc.Player;
import ooc.State;
import ooc.ai.AI;
import ooc.ai.search.scorers.LastChanceScorer;
import ooc.ai.search.scorers.Scorer;
import ooc.ai.search.thinkers.MineThinker;
import ooc.ai.search.thinkers.MoveThinker;
import ooc.ai.search.thinkers.SilenceThinker;
import ooc.ai.search.thinkers.SonarThinker;
import ooc.ai.search.thinkers.TorpedoThinker;
import ooc.ai.search.thinkers.TriggerThinker;
import ooc.charge.PB4ChargeAI;
import ooc.orders.Order;
import ooc.orders.Orders;
import ooc.trailmapper.TrailMapper;

public class SearchAI implements AI {

  private Oracle oracle;
  
  Orders orders = new Orders();
  
  public PB4ChargeAI chargeAttackAI = new PB4ChargeAI();
  
  TorpedoThinker torpedoThinker = new TorpedoThinker();
  SilenceThinker silenceThinker = new SilenceThinker();
  MineThinker mineThinker = new MineThinker();
  TriggerThinker triggerThinker = new TriggerThinker();
  MoveThinker moveThinker = new MoveThinker();
  SonarThinker sonarThinker = new SonarThinker();

  private boolean isSurfacePossible;

  public static Scorer scorer = new LastChanceScorer(OOCMap.CENTER);

  private State state;
  
  public SearchAI(State state, Oracle oracle) {
    this.state = state;
    this.oracle = oracle;
    moveThinker.setChargeAI(chargeAttackAI);
  }
  public SearchAI() {
    moveThinker.setChargeAI(chargeAttackAI);
  }
  
  public Orders think(State state, Oracle oracle) {
    //Player.time("search.think ");
    this.state = state;
    this.oracle = oracle;
    
    orders.clear();
    isSurfacePossible = true;
    
    // clear memoisations
    scorer.reset();
    torpedoThinker.reset();
    silenceThinker.reset();
    mineThinker.reset();
    triggerThinker.reset();
    moveThinker.reset();
    sonarThinker.reset();
    
    
    Player.time("search.process");
    Player.search.process(this, state);
    Player.time("search.fill best");
    Player.search.fillBestOrders(orders);
    Player.time("Return from search");
    
    if (state.cooldowns.sonarCooldown() == 0) {
      List<Order> sonars = sonarThinker.calculateOrders(oracle, state);
      if (!sonars.isEmpty()) {
        state.cooldowns.resetSonar();
        orders.addOrder(sonars.get(0));
      }
    }
    return orders;
  }

  public List<Order> think(Node node) {
    State state = node.state;
    List<Order> ol = new ArrayList<>();

    
    if (!node.hasSilence() && state.cooldowns.silenceCooldown() == 0) {
      //System.err.println("Calculatingf silences "+node.debugOrders());
      ol.addAll(silenceThinker.calculateOrders(oracle, state));
    }
    if (!node.hasMine() && state.cooldowns.mineCooldown() == 0) {
      ol.addAll(mineThinker.calculateOrders(oracle, state));
    }
    if (!node.hasTrigger()) {
      ol.addAll(triggerThinker.calculateOrders(oracle, state));
    }
    if (!node.hasMove()) {
      ol.addAll(moveThinker.calculateOrders(oracle, state));
    }
    if (!node.hasSilence() && !node.hasTorpedo() && state.cooldowns.torpedoCooldown() == 0) { // no torpedo after silence
      ol.addAll(torpedoThinker.calculateOrders(oracle, state));
    }
    if (isSurfacePossible && !node.hasSilence() && !node.hasSurface()) { // no surface after silence 
      ol.add(Order.surface(state.myPos));
    }
    return ol;
  }

  private boolean needSilence(Node node) {
    
    /* Si on a toucher et qu'on est visible */
    if ((node.hasTorpedo() && node.state.fastDetector.count() <= 5) || oracle.isInDanger()) {
//      System.err.println("Need silence "+node.hasTorpedo()+" / "+oracle.dangerTimer);
      return true;
    }
    return false;
  }
  
  public double scoreState(Node node) {
    return scorer.calculate(state, node, oracle);
  }

	@Override
	public TrailMapper getOppMapper() {
		return oracle.oppMapper;
	}
	
}
