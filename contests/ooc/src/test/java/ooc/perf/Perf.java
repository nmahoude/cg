package ooc.perf;

import ooc.Oracle;
import ooc.P;
import ooc.Player;
import ooc.PlayerTest;
import ooc.State;
import ooc.ai.search.Search;
import ooc.ai.search.SearchAI;
import ooc.trailmapper.TNCache;
import ooc.trailmapper.TrailNode;

public class Perf {
  public static void main(String[] args) {
		
    PlayerTest.emptyMap();
    
    Search search = new Search();

    
    Oracle oracle = new Oracle(Player.map);
    State state = new State();
    state.cooldowns.chargeAll();
    
    SearchAI ai = new SearchAI(state, oracle);
    state.attachMap(Player.map);
    oracle.dangerTimer = 3;
    
    oracle.oppMapper.clearCurrentLayer();
    TrailNode reality = TNCache.pop();
    reality.currentPos = P.get(6, 7);
    oracle.oppMapper.currentLayer[oracle.oppMapper.currentLayerFE++] = reality;
    oracle.oppMapper.teardownTurn();
    
    state.myPos = P.get(7, 7);
    
    for (int i=0;i<100_000;i++) {
      search.process(ai, state);
    }
  }
}
