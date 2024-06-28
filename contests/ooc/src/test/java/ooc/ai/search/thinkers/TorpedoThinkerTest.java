package ooc.ai.search.thinkers;

import org.junit.jupiter.api.Test;

import ooc.Oracle;
import ooc.Player;
import ooc.PlayerTest;
import ooc.P;
import ooc.State;
import ooc.trailmapper.TNCache;
import ooc.trailmapper.TorpedoHitStat;
import ooc.trailmapper.TrailNode;

public class TorpedoThinkerTest {

  
  @Test
  void debug() throws Exception {
    PlayerTest.emptyMap();
    State state = new State();
    state.attachMap(Player.map);
    
    Oracle oracle = new Oracle(Player.map);
    oracle.oppMapper.clearCurrentLayer();

    
    state.myPos = P.get(2, 10);
    
    addOppPos(oracle, P.get(0, 12));
    addOppPos(oracle, P.get(1, 12));
    addOppPos(oracle, P.get(3, 9));
    oracle.oppMapper.teardownTurn();
    
    TorpedoThinker sut = new TorpedoThinker();

    TorpedoHitStat stat = new TorpedoHitStat();
    oracle.oppMapper.updateStatisticalDamageAt(stat, P.get(0, 12));
    oracle.oppMapper.updateStatisticalDamageAt(stat, P.get(1, 12));
    
    sut.calculateOrders(oracle, state);
  }

  private void addOppPos(Oracle oracle, P pos) {
    TrailNode tn = TNCache.pop();
    tn.reset();
    tn.currentPos = pos;
    
    oracle.oppMapper.currentLayer[oracle.oppMapper.currentLayerFE++] = tn;
  }
}
