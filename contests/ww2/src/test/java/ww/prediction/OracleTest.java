package ww.prediction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import ww.Dir;
import ww.GameState;
import ww.Point;
import ww.TU;
import ww.sim.Move;

public class OracleTest {

  public static class Pushed {
    GameState simulatedState;
    GameState currentState;
    Oracle oracle;
    
    @Before
    public void setup() {
      GameState.size = 6;
      simulatedState = new GameState();
      currentState = new GameState();
      oracle = new Oracle(currentState);
    }
    
    @Test
    public void pushedButStillKnown() throws Exception {
      TU.setHeights(simulatedState, 6,
          "000000",
          "000000",
          "000000",
          "000000",
          "000000",
          "000000");
      TU.setAgents(simulatedState, 
          Point.get(1, 1),
          Point.get(0, 5),
          Point.get(3,4), // Don't see it anymore
          Point.get(5,5)
          );
      
      TU.setHeights(currentState, 6,
          "000000",
          "000000",
          "000000",
          "000000",
          "000000",
          "000000");
      TU.setAgents(currentState, 
          Point.get(0, 0),
          Point.get(0, 5),
          Point.get(3,4),
          Point.get(5,5)
          );
      
      oracle.guessFrom(currentState);
      
      assertThat(currentState.agents[2].position, is (Point.get(3, 4)));
      assertThat(currentState.agents[3].position, is (Point.get(5, 5)));
    }
    
    @Test
    public void pushedButOnyOneKnewn() throws Exception {
      TU.setHeights(simulatedState, 6,
          "000000",
          "000000",
          "000000",
          "000000",
          "000000",
          "000000");
      TU.setAgents(simulatedState, 
          Point.get(1, 1),
          Point.get(0, 5),
          Point.get(1, 2), 
          Point.get(5,5)
          );
      
      TU.setHeights(currentState, 6,
          "000000",
          "000000",
          "000000",
          "000000",
          "000000",
          "000000");
      TU.setAgents(currentState, 
          Point.get(0, 0), // pushed back to 0,0
          Point.get(0, 5),
          Point.get(-1,-1),
          Point.get(-1,-1)// Don't see it anymore
          );
      
      oracle.updateSimulated(simulatedState, new Move(null));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.guessedPosition[0], is (Point.get(1, 2)));
      assertThat(oracle.guessedPosition[1], is (Point.get(5, 5)));
    }
    
    @Test
    public void pushed_noneNone_butCanInfer() throws Exception {
      TU.setHeights(simulatedState, 6,
          "000000",
          "004000",
          "040000",
          "000000",
          "000000",
          "000000");
      TU.setAgents(simulatedState, 
          Point.get(1, 1),
          Point.get(0, 5),
          Point.get(-1, -1), 
          Point.get(-1, -1)
          );
      
      TU.setHeights(currentState, 6,
          "000000",
          "014000",
          "040000",
          "000000",
          "000000",
          "000000");
      TU.setAgents(currentState, 
          Point.get(0, 0), // pushed back to 0,0
          Point.get(0, 5),
          Point.get(-1,-1),
          Point.get(-1,-1)// Don't see it anymore
          );
      
      oracle.updateSimulated(simulatedState, new Move(null));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.guessedPosition[0], is (Point.get(2, 2)));
      assertThat(oracle.guessedPosition[1], is (Point.get(-1, -1)));
    }
    
    @Test
    public void pushed_noneKnown_butCanInferWithFriendVision() throws Exception {
      // the other see a potential cell that is valid, so don't block it from infering singleSpot
      TU.setHeights(simulatedState, 6,
          "000000",
          "004000",
          "000000", //<- (1,3) can be a spot, but check by agent[1]
          "000000",
          "000000",
          "000000");
      TU.setAgents(simulatedState, 
          Point.get(1, 1),
          Point.get(0, 3),
          Point.get(-1, -1), 
          Point.get(-1, -1)
          );
      
      TU.setHeights(currentState, 6,
          "000000",
          "014000",
          "000000",
          "000000",
          "000000",
          "000000");
      TU.setAgents(currentState, 
          Point.get(0, 0), // pushed back to 0,0
          Point.get(0, 3),
          Point.get(-1,-1),
          Point.get(-1,-1)// Don't see it anymore
          );
      
      oracle.updateSimulated(simulatedState, new Move(null));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.guessedPosition[0], is (Point.get(2, 2)));
      assertThat(oracle.guessedPosition[1], is (Point.get(-1, -1)));
    }
    
    @Test
    public void beenPushed_onlyOneMoveCanDoTheAction() {
      TU.setHeights(simulatedState, 6, 
          "000000",
          "000000",
          "000000",
          "000000",
          "000000",
          "000000");
      TU.setAgent(simulatedState, 0,1,5);
      TU.setAgent(simulatedState, 1,4,1);
      TU.setAgent(simulatedState, 2,5,1);
      TU.setAgent(simulatedState, 3,3,1); // I know everyone
        
      TU.setHeights(currentState, 6,
          "000000",
          "000010",
          "000000",
          "000000",
          "000000",
          "000000");
      TU.setAgent(currentState, 0,1,5);
      TU.setAgent(currentState, 1,3,0);
      TU.setAgent(currentState, 2,-1,-1);
      TU.setAgent(currentState, 3,3,1);
      
      oracle.updateSimulated(simulatedState, new Move(null));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.guessedPosition[0], is (Point.get(5 ,1)));
      assertThat(oracle.guessedPosition[1], is (Point.get(3, 1)));
    }
    
    @Test
    public void beenPushed_onlyOneMoveCanDoThePush_EvenIfWeDontKnowThePositions() {
      TU.setHeights(simulatedState, 6, 
          "000000",
          "444444",
          "000000",
          "444444",
          "000000",
          "000000");
      TU.setAgent(simulatedState, 0,1,5);
      TU.setAgent(simulatedState, 1,1,2);
      TU.setAgent(simulatedState, 2,-1,-1);
      TU.setAgent(simulatedState, 3,-1,-1); // I know everyone
        
      TU.setHeights(currentState, 6,
          "000000",
          "444444",
          "010000",
          "444444",
          "000000",
          "000000");
      TU.setAgent(currentState, 0,1,5);
      TU.setAgent(currentState, 1,0,2);
      TU.setAgent(currentState, 2,-1,-1);
      TU.setAgent(currentState, 3,-1,-1);
      
      oracle.updateSimulated(simulatedState, new Move(null));
      oracle.guessFrom(currentState);
      
      
      assertThat(oracle.guessedPosition[0], is (Point.get(2, 2)));
      assertThat(oracle.guessedPosition[1], is (Point.unknown));
    }
  }
  
  public static class Push {
    GameState simulatedState;
    GameState currentState;
    Oracle oracle;
    
    @Before
    public void setup() {
      GameState.size = 6;
      simulatedState = new GameState();
      currentState = new GameState();
      oracle = new Oracle(currentState);
    }
    
    @Test
    public void push_invalidated() throws Exception {
      TU.setHeights(simulatedState, 6,
          "000000",
          "000000",
          "001000",
          "000000",
          "000000",
          "000000");
      TU.setAgents(simulatedState, 
          Point.get(1, 1),
          Point.get(0, 5),
          Point.get(3, 3), // Don't see it anymore
          Point.get(-1, -1)
          );
      
      TU.setHeights(currentState, 6,
          "000000",
          "000000",
          "000000",
          "000000",
          "000000",
          "000001");
      TU.setAgents(currentState, 
          Point.get(1, 1),
          Point.get(0, 5),
          Point.get(2, 2),
          Point.get(-1,-1)
          );
      
      oracle.updateSimulated(simulatedState, TU.getPush(simulatedState.agents[0], Dir.SE, Dir.SE));
      oracle.guessFrom(currentState);
      
      assertThat(currentState.agents[2].position, is (Point.get(2, 2)));
      assertThat(currentState.agents[3].position, is (Point.unknown));
    }
    
  }
}
