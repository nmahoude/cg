package ww.perf;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.Dir;
import ww.GameState;
import ww.Player;
import ww.TU;
import ww.sim.Move;
import ww.sim.Simulation;
import ww.think.NodePOC;
import ww.think.Think;

public class Perf {
  
  GameState state;
  Simulation simulation;

  @Before
  public void setup() {
    state = new GameState();
    simulation = new Simulation(state);
  }
  
  @Test
  public void perf() {
    state.size = 6;
    state.readInit(new Scanner("" + state.size + " 2"));
    TU.setHeights(state, 
      "344444",
      "333434",
      "..34..",
      ".3..3.",
      ".0101.",
      "000002");
    TU.setAgent(state, 0,1,3);
    TU.setAgent(state, 1,1,1);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    
    
    Player.state = state;
    GameState.startTime = System.currentTimeMillis()+1_000_000;
    state.legalActionDepth0NodeCache.clear();
    for (int i=0;i<2;i++) {
      for (Dir dir1 : Dir.getValues()) {
        for (Dir dir2 : Dir.getValues()) {
          Move move = new Move(state.agents[i]);
          move.dir1 = dir1;
          move.dir2 = dir2;
          NodePOC node = new NodePOC(1);
          node.move = move;
          state.legalActionDepth0NodeCache.add(node);
        }
      }
    }
    for (int i=0;i<500;i++) {
      new Think(state).think(3);
    }
  }
}
