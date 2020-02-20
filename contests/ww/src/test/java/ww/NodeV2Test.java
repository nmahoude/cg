package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import ww.sim.Move;
import ww.sim.Simulation;

public class NodeV2Test {
  GameState state;
  Simulation simulation;
  NodeV2 node;
  
  @Before
  public void setup() {
    state = new GameState();
    state.readInit(new Scanner("7 2"));
    state.startTime = System.currentTimeMillis();
    
    simulation = new Simulation();

    node = new NodeV2();
    node.state = state;
  }
  
  @Test
  public void test() {
    state.size = 5;
    TU.setAgent(state, 0,1,1);
    TU.setAgent(state, 1,3,0);
    TU.setAgent(state, 2,2,2);
    TU.setAgent(state, 3,1,2);
    TU.setHeights(state, 
      "00000",
      "00000",
      "00000",
      "00000",
      "00000");
    
    NodeV2 node = new NodeV2();
    node.calculateChilds(0, state);
    assertThat(node.bestAction, is(not(nullValue())));
  }
  
  @Test
  public void dontAcceptDefeatTooSoon() {
    state.size = 5;
    TU.setAgent(state, 0,0,0);
    TU.setAgent(state, 1,4,1);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,0,1);
    TU.setHeights(state, 
      "01143",
      "03243",
      "12213",
      "10000",
      "00000");
    
    NodeV2 node = new NodeV2();
    node.calculateChilds(0, state);
    assertThat(node.bestAction, is(not(nullValue())));
  }
  
  @Test
  public void dontGoInADangerousPosition() {
    state.size = 6;
    TU.setAgent(state, 0,5,3);
    TU.setAgent(state, 1,1,3);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,5,2);
    TU.setHeights(state, 
      "033344",
      "334444",
      "333343",
      "334440",
      "413334",
      "403334");
    
    Move move = TU.getMove(1, Dir.SE,  Dir.W);
    Simulation simulation = new Simulation();
    simulation.simulate(move, state);
    
    NodeV2 node = new NodeV2();
    node.state = state;
    int potentialBlocked = node.checkAgentPotentiallyBlocked();
    
    assertThat(potentialBlocked, is (2));
  }
  
  @Test
  public void dontLetYouSubjectToAttackUnlessNoGoodChoice_thenMakeThePoints() {
    /* player 1 did W, E and get pushed by ennemy, which blocks him immediately ! */
    /* BUT if he doesn't move, he can be blocked too, so it's a goodthing to take the risk */
    state.size = 6;
    TU.setAgent(state, 0,4,4);
    TU.setAgent(state, 1,3,0);
    TU.setAgent(state, 2,-1,-1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "423244",
      "004440",
      "404434",
      "000343",
      "000033",
      "000003");

    Move move = TU.getMove(1, Dir.W, Dir.E);
    simulation.simulate(move, state);
    
    assertThat(state.agents[1].score, is(1));
    boolean canBeBlocked = node.canBeBlocked(state.agents[1]);
    assertThat(canBeBlocked, is(true));
  }
  
  @Test
  public void dontGoNearAnEnemyAndACliff() {
    state.size = 5;
    TU.setAgent(state, 0,3,3);
    TU.setAgent(state, 1,2,2);
    TU.setAgent(state, 2,3,1);
    TU.setAgent(state, 3,-1,-1);
    TU.setHeights(state, 
      "00133",
      "01333",
      "01343",
      "00300",
      "00000");
    
    Move move = TU.getMove(1, Dir.N, Dir.SW);
    simulation.simulate(move, state);
    
    Eval eval = new Eval();
    
    Agent toCheck = state.agents[1];
    Dir oppWall = Dir.NW;
    int tx = toCheck.x + oppWall.dx;
    int ty = toCheck.y + oppWall.dy;
    
    int x = toCheck.x;
    int y = toCheck.y;
    int currentHeight = state.getHeight(x, y);

    toCheck.x = tx;
    toCheck.y = ty;
    state.setHeight(x, y, currentHeight+1);
    double score = eval.calculateScore(state, move);
    
  }
}
