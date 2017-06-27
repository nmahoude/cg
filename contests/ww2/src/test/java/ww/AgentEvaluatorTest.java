package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ww.sim.Simulation;

public class AgentEvaluatorTest {
  GameState state;
  Simulation simulation;

  @Before
  public void setup() {
    state = new GameState();
    simulation = new Simulation();

  }

  @Test
  public void dontStayInDangerousCliff() {
    state.size = 5;
    state.readInit(new Scanner("" + state.size + " 2"));
    TU.setHeights(state, 
        "11111",
        "10423",
        "00423",
        "00210",
        "00000");
    TU.setAgent(state, 0,1,1);
    TU.setAgent(state, 1,4,2);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,4,1);
    state.backup();

    AgentEvaluator ae = new AgentEvaluator(state, state.agents[1]);
    
    assertThat(ae.dangerousCliffs() < 0 , is(true));
  }
  
  @Test
  public void test() {
    state.size = 5;
    state.readInit(new Scanner("" + state.size + " 2"));


    TU.setHeights(state, 
      "00011",
      "00000",
      "02000",
      "02000",
      "11110");
    TU.setAgent(state, 0,3,0);
    TU.setAgent(state, 1,1,3);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,0,3);
    state.backup();
    
    
    
    Agent agent = state.agents[1];
    simulation.simulate(TU.getMove(agent, Dir.N, Dir.S), true);
    double tscore1 = AgentEvaluator.score(state);

    AgentEvaluator ae1 = new AgentEvaluator(state, agent);
    double score1 = ae1.score(state, agent);

    state.restore();
    simulation.simulate(TU.getMove(agent, Dir.N, Dir.N), true);
    double tscore2 = AgentEvaluator.score(state);

    AgentEvaluator ae2 = new AgentEvaluator(state, agent);
    double score2 = ae1.score(state, agent);

    assertThat(score1 > score2 , is(true));
  }
  
  @Test
  @Ignore
  public void wowDontBlockYourselfMan() {
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
    state.backup();
    
    Agent agent = state.agents[0];
    simulation.simulate(TU.getMove(agent, Dir.NE, Dir.SW), true);
    double tscore1 = AgentEvaluator.score(state);
    AgentEvaluator ae1 = new AgentEvaluator(state, agent);
    double score1 = ae1.score(state, agent);

    state.restore();
    simulation.simulate(TU.getMove(agent, Dir.N, Dir.N), true);
    double tscore2 = AgentEvaluator.score(state);

    AgentEvaluator ae2 = new AgentEvaluator(state, agent);
    double score2 = ae1.score(state, agent);

    assertThat(score2 > score1 , is(true));
  }

}
