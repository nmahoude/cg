package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ww.sim.Move;
import ww.sim.Simulation;

public class AgentEvaluatorTest {
  GameState state;
  Simulation simulation;

  @Before
  public void setup() {
    state = new GameState();
    simulation = new Simulation(state);
    AgentEvaluator.ae.state = state;
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

    AgentEvaluator ae = new AgentEvaluator(state, state.agents[1]);
    
    assertThat(ae.dangerousCliffs() < 0 , is(true));
  }
  
  @Test
  @Ignore
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
    
    
    Agent agent = state.agents[1];
    Move move = TU.getMove(agent, Dir.N, Dir.S);
    simulation.simulate(move);
    double tscore1 = AgentEvaluator.score(state);

    AgentEvaluator ae1 = new AgentEvaluator(state, agent);
    double score1 = ae1.score(agent);

    simulation.undo(move);
    simulation.simulate(TU.getMove(agent, Dir.N, Dir.N));
    double tscore2 = AgentEvaluator.score(state);

    AgentEvaluator ae2 = new AgentEvaluator(state, agent);
    double score2 = ae1.score(agent);

    assertThat(score1 > score2 , is(true));
  }
  
  @Test
  public void test_dontLetHimScore() {
    state.size = 5;
    state.readInit(new Scanner("" + state.size + " 2"));


    TU.setHeights(state, 
      "00000",
      "00300",
      "00220",
      "00010",
      "00000");
    TU.setAgent(state, 0,1,1);
    TU.setAgent(state, 1,3,3);
    TU.setAgent(state, 2,1,2);
    TU.setAgent(state, 3,2,2);
    
    
    Agent agent = state.agents[3];
    Move move = TU.getMove(agent, Dir.N, Dir.S);
    simulation.simulate(move);
    double tscore1 = AgentEvaluator.score(state);

    AgentEvaluator ae1 = new AgentEvaluator(state, agent);
    double score1 = ae1.score(agent);

    simulation.undo(move);
    simulation.simulate(TU.getMove(agent, Dir.S, Dir.E));
    double tscore2 = AgentEvaluator.score(state);

    AgentEvaluator ae2 = new AgentEvaluator(state, agent);
    double score2 = ae1.score(agent);

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
    
    Agent agent = state.agents[0];
    Move move = TU.getMove(agent, Dir.NE, Dir.SW);
    simulation.simulate(move);
    double tscore1 = AgentEvaluator.score(state);
    AgentEvaluator ae1 = new AgentEvaluator(state, agent);
    double score1 = ae1.score(agent);

    simulation.undo(move);
    simulation.simulate(TU.getMove(agent, Dir.N, Dir.N));
    double tscore2 = AgentEvaluator.score(state);

    AgentEvaluator ae2 = new AgentEvaluator(state, agent);
    double score2 = ae1.score(agent);

    assertThat(score2 > score1 , is(true));
  }

  @Test
  public void dontBlockTheTwo() {
    state.size = 5;
    state.readInit(new Scanner("" + state.size + " 2"));
    TU.setHeights(state, 
      "22312",
      "33344",
      "12444",
      "04443",
      "03233");
    TU.setAgent(state, 0,4,3);
    TU.setAgent(state, 1,3,4);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    
    Agent agent = state.agents[1];
    Move move = TU.getMove(agent, Dir.W, Dir.W);
    simulation.simulate(move); // W W block the my agents
    double tscore1 = AgentEvaluator.score(state);
    AgentEvaluator ae1 = new AgentEvaluator(state, agent);
    double score1 = ae1.score(agent);

    simulation.undo(move);
    simulation.simulate(TU.getMove(agent, Dir.W, Dir.E)); // W E should be better because it only blocks p0
    double tscore2 = AgentEvaluator.score(state);

    AgentEvaluator ae2 = new AgentEvaluator(state, agent);
    double score2 = ae1.score(agent);

    assertThat(tscore2 > tscore1 , is(true));
  }
  
  @Test
  public void bestActionIsBlockingTheOthers() {
    state.size = 5;
    state.readInit(new Scanner("" + state.size + " 2"));
    TU.setHeights(state, 
      "11212",
      "32141",
      "01420",
      "04444",
      "03322");
    TU.setAgent(state, 0,0,3);
    TU.setAgent(state, 1,4,0);
    TU.setAgent(state, 2,3,4);
    TU.setAgent(state, 3,2,4);
    
    Agent agent = state.agents[0];
    Move move = TU.getMove(agent, Dir.S, Dir.N);
    simulation.simulate(move); //<<-random action
    double tscore1 = AgentEvaluator.score(state);

    simulation.undo(move);
    simulation.simulate(TU.getMove(agent, Dir.S, Dir.E)); // should block both opponents in a little space
    double tscore2 = AgentEvaluator.score(state);

    assertThat(tscore2 > tscore1 , is(true));
  }
  
  @Test
  public void dontChooseTheBlockingPath() {
    state.size = 7;
    state.readInit(new Scanner("" + state.size + " 2"));
    TU.setHeights(state, 
      "...2...",
      "..033..",
      ".30443.",
      "0034333",
      ".33443.",
      "..344..",
      "...3...");
    TU.setAgent(state, 0,5,3);
    TU.setAgent(state, 1,4,3);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    
    //AgentEvaluator.debug = true;
    
    Agent agent = state.agents[1];
    Move move = TU.getMove(agent, Dir.NE, Dir.NW);
    simulation.simulate(move); //<<-random action
    double tscore1 = AgentEvaluator.score(state);

    simulation.undo(move);
    simulation.simulate(TU.getMove(agent, Dir.NE, Dir.SE)); // should block both opponents in a little space
    double tscore2 = AgentEvaluator.score(state);

    assertThat(tscore2 > tscore1 , is(true));
  }
}
