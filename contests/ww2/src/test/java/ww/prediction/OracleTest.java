package ww.prediction;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ww.Agent;
import ww.Cell;
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
          Point.get(2, 1), // Don't see it anymore
          Point.get(1, 2)
          );
      
      TU.setHeights(currentState, 6,
          "000000",
          "010000",
          "000000",
          "000000",
          "000000",
          "000000");
      TU.setAgents(currentState, 
          Point.get(0, 0),
          Point.get(0, 5),
          Point.get(2,1),
          Point.get(1,2)
          );
      
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[2]);
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[3]);
      oracle.updateSimulated(simulatedState, new Move(null));
      oracle.guessFrom(currentState);
      
      assertThat(currentState.agents[2].position, is (Point.get(2,1)));
      assertThat(currentState.agents[3].position, is (Point.get(1,2)));
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
      
      assertThat(oracle.possiblePositions[2].size(), is(1));
      assertThat(oracle.possiblePositions[2], hasItem (Point.get(1, 2)));
      assertThat(oracle.possiblePositions[3].size(), is(1));
      assertThat(oracle.possiblePositions[3], hasItem (Point.get(5, 5)));
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
      
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[2]);
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[3]);
      
      oracle.updateSimulated(simulatedState, new Move(null));
      oracle.guessFrom(currentState);

      assertThat(oracle.possiblePositions[2].size(), is(1));
      assertThat(oracle.possiblePositions[2], hasItem (Point.get(2, 2)));
      assertThat(oracle.possiblePositions[3].size() > 1 , is(true));

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
      
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[2]);
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[3]);
      oracle.updateSimulated(simulatedState, new Move(null));
      oracle.guessFrom(currentState);

      assertThat(oracle.possiblePositions[2].size(), is(1));
      assertThat(oracle.possiblePositions[2], hasItem (Point.get(2, 2)));
      assertThat(oracle.possiblePositions[3].size() > 1 , is(true));
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
      
      assertThat(oracle.possiblePositions[2].size(), is(1));
      assertThat(oracle.possiblePositions[2], hasItem (Point.get(5 ,1)));
      assertThat(oracle.possiblePositions[3].size() , is(1));
      assertThat(oracle.possiblePositions[3], hasItem (Point.get(3 ,1)));
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
      
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[2]);
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[3]);
      oracle.updateSimulated(simulatedState, new Move(null));
      oracle.guessFrom(currentState);
      
      
      assertThat(oracle.possiblePositions[2].size(), is(1));
      assertThat(oracle.possiblePositions[2], hasItem (Point.get(2 ,2)));
      assertThat(oracle.possiblePositions[3].size() > 1 , is(true));
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
      
      TU.fillAllPossiblePositionsWith(oracle.possiblePositions[3], Arrays.asList(Point.get(3, 3)));
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[3]);
      oracle.updateSimulated(simulatedState, TU.getPush(simulatedState.agents[0], Dir.SE, Dir.SE));
      oracle.guessFrom(currentState);
      
      assertThat(currentState.agents[2].position, is (Point.get(2, 2)));
      assertThat(currentState.agents[3].position, is (Point.unknown));
    }
    
  }

  public static class Moves {
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
    public void move_simpleOne() throws Exception {
      TU.setHeights(simulatedState, 6,
          "000000",
          "000000",
          "000000",
          "000000",
          "000000",
          "000001");
      TU.setAgents(simulatedState, 
          Point.get(0, 0),
          Point.get(5, 4),
          Point.get(-1, -1), // Don't see it anymore
          Point.get(-1, -1)
          );
      
      TU.setHeights(currentState, 6,
          "000000",
          "000000",
          "000000",
          "001000",
          "000000",
          "000001");
      TU.setAgents(currentState, 
          Point.get(0, 0),
          Point.get(5, 4),
          Point.get(-1,-1),
          Point.get(-1,-1)
          );
      
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[2]);
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[3]);
      oracle.updateSimulated(simulatedState, TU.getMove(simulatedState.agents[0], Dir.N, Dir.N));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.possiblePositions[2].size() > 1, is (true));
      assertThat(oracle.possiblePositions[3].size() > 1, is (true));
    }
  
    @Test
    public void move_canFindWhichOneHasMove() throws Exception {
      TU.setHeights(simulatedState, 6,
          "000000",
          "000000",
          "000000",
          "000000",
          "000000",
          "000001");
      TU.setAgents(simulatedState, 
          Point.get(0, 0),
          Point.get(5, 4),
          Point.get(-1, -1), // Don't see it anymore
          Point.get(-1, -1)
          );
      
      TU.setHeights(currentState, 6,
          "000000",
          "000000",
          "000000",
          "001000",
          "000000",
          "000001");
      TU.setAgents(currentState, 
          Point.get(0, 0),
          Point.get(5, 4),
          Point.get(-1,-1),
          Point.get(-1,-1)
          );
      
      TU.fillAllPossiblePositions(simulatedState, oracle.possiblePositions[2]);
      TU.fillAllPossiblePositionsWith(oracle.possiblePositions[3], Arrays.asList(
          Point.get(4, 0),
          Point.get(5, 0),
          Point.get(5, 1)
          ));
      
      oracle.updateSimulated(simulatedState, TU.getMove(simulatedState.agents[0], Dir.N, Dir.N));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.possiblePositions[2].size() , is (8));
      assertThat(oracle.possiblePositions[3].size() , is (3));
    }

    @Test
    public void move_canFindWhoFurtherRestricted() throws Exception {
      TU.setHeights(simulatedState, 6,
          "000000",
          "000000",
          "000000",
          "000000",
          "000000",
          "000001");
      TU.setAgents(simulatedState, 
          Point.get(0, 0),
          Point.get(4, 5),
          Point.get(-1, -1), // Don't see it anymore
          Point.get(-1, -1)
          );
      
      TU.setHeights(currentState, 6,
          "000000",
          "000000",
          "000000",
          "001000",
          "000000",
          "000001");
      TU.setAgents(currentState, 
          Point.get(0, 0),
          Point.get(4, 5),
          Point.get(-1,-1),
          Point.get(-1,-1)
          );
      
      TU.fillAllPossiblePositionsWith(oracle.possiblePositions[2], Arrays.asList(
          Point.get(0, 2),
          Point.get(1, 2),
          Point.get(2, 2),
          Point.get(4, 5),
          Point.get(4, 3), 
          Point.get(0, 0) // not in the range, should be discarded
          ));
      TU.fillAllPossiblePositionsWith(oracle.possiblePositions[3], Arrays.asList(
          Point.get(4, 0),
          Point.get(5, 0),
          Point.get(5, 1)
          ));
      
      oracle.updateSimulated(simulatedState, TU.getMove(simulatedState.agents[0], Dir.N, Dir.N));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.possiblePositions[2].size() , is (5));
      assertThat(oracle.possiblePositions[2] , not(hasItem (Point.get(3, 4))));
      assertThat(oracle.possiblePositions[2] , not(hasItem (Point.get(0, 0))));
      assertThat(oracle.possiblePositions[3].size() , is (3));
      assertThat(oracle.possiblePositions[3].size() , is (3));
    }

    @Test
    public void push_shouldNotLooseFormerPositionPossibilities() throws Exception {
      TU.setHeights(simulatedState, 5,
          "10021",
          "13310",
          "33330",
          "02232",
          "00100");
      TU.setAgents(simulatedState, 
          Point.get(2, 0),
          Point.get(2, 3),
          Point.get(-1, -1),
          Point.get(-1, -1)
          );
      
      TU.setHeights(currentState, 5,
          "10021",
          "13310",
          "33330",
          "02332",
          "00100");
      TU.setAgents(currentState, 
          Point.get(2, 0),
          Point.get(3, 4),
          Point.get(-1, -1),
          Point.get(-1, -1)
          );
      
      TU.fillAllPossiblePositionsWith(oracle.possiblePositions[2], Arrays.asList(
          Point.get(0,1),
          Point.get(3,2),
          Point.get(1,4),
          Point.get(1,2),
          Point.get(1,3),
          Point.get(2,2)
          ));
      TU.fillAllPossiblePositionsWith(oracle.possiblePositions[3], Arrays.asList(
          Point.get(3,2),
          Point.get(1,4),
          Point.get(1,2),
          Point.get(1,3),
          Point.get(2,2),
          Point.get(2,3)
          ));
      oracle.updateSimulated(simulatedState, TU.getMove(simulatedState.agents[0], Dir.NW, Dir.NE));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.possiblePositions[2].size() >0 , is (true));
      assertThat(oracle.possiblePositions[3].size() >0 , is (true));
    }
    
    @Test
    public void move_simpleMoveFromKnownOpp_buildOnHeight3() throws Exception {
      TU.setHeights(simulatedState, 5,
          "10021",
          "02311",
          "24430",
          "12330",
          "12201");
      TU.setAgents(simulatedState, 
          Point.get(4, 3),
          Point.get(2, 3),
          Point.get(0, 3),
          Point.get(3, 4)
          );
      
      TU.setHeights(currentState, 5,
          "10021",
          "02311",
          "24430",
          "12340",
          "12201");
      TU.setAgents(currentState, 
          Point.get(4, 3),
          Point.get(2, 3),
          Point.get(-1, -1),
          Point.get(4, 4)
          );
      
      oracle.updateSimulated(simulatedState, TU.getMove(simulatedState.agents[0], Dir.N, Dir.N));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.possiblePositions[2].size() , is (1));
      assertThat(oracle.possiblePositions[2], hasItem(Point.get(0, 3)));
      assertThat(oracle.possiblePositions[3].size() , is (1));
      assertThat(oracle.possiblePositions[3], hasItem(Point.get(4, 4)));
    }
    
    @Test
    public void move_shouldNotLostTraceOfTheStillAgent() throws Exception {
      TU.setHeights(simulatedState, 5,
          "10021",
          "01300",
          "13320",
          "02000",
          "00000");
      TU.setAgents(simulatedState, 
          Point.get(2, 0),
          Point.get(0, 3),
          Point.get(1, 1),
          Point.get(2, 3)
          );
      
      TU.setHeights(currentState, 5,
          "10021",
          "02300",
          "13320",
          "02000",
          "00000");
      TU.setAgents(currentState, 
          Point.get(2, 0),
          Point.get(0, 3),
          Point.get(0, 2),
          Point.get(-1, -1)
          );
      
      oracle.updateSimulated(simulatedState, TU.getMove(simulatedState.agents[0], Dir.N, Dir.N));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.possiblePositions[2].size() , is (1));
      assertThat(oracle.possiblePositions[2], hasItem(Point.get(0, 2)));
      assertThat(oracle.possiblePositions[3].size() , is (1));
      assertThat(oracle.possiblePositions[3], hasItem(Point.get(2, 3)));
    }
    
    @Test
    public void move_cancelMovedByHim() throws Exception {
      TU.setHeights(simulatedState, 5,
          "10031",
          "12433",
          "34443",
          "23341",
          "02221");
      TU.setAgents(simulatedState, 
          Point.get(0, 3),
          Point.get(1, 4),
          Point.get(-1, -1),
          Point.get(-1, -1)
          );
      
      TU.setHeights(currentState, 5,
          "10031",
          "12433",
          "34443",
          "23341",
          "02221");
      TU.setAgents(currentState, 
          Point.get(0, 3),
          Point.get(1, 4),
          Point.get(-1, -1),
          Point.get(1, 3)
          );
      
      TU.fillAllPossiblePositionsWith(oracle.possiblePositions[2], Arrays.asList(
          Point.get(0, 2),
          Point.get(0, 0),
          Point.get(1, 1),
          Point.get(0, 1),
          Point.get(1, 0)));
      TU.fillAllPossiblePositionsWith(oracle.possiblePositions[3], Arrays.asList(
          Point.get(0, 2),
          Point.get(0, 0),
          Point.get(1, 1),
          Point.get(0, 1),
          Point.get(2, 3),
          Point.get(1, 0)));
      oracle.updateSimulated(simulatedState, TU.getMove(simulatedState.agents[0], Dir.N, Dir.N));
      oracle.guessFrom(currentState);
      
      assertThat(oracle.possiblePositions[2].size() , is (5));
      assertThat(oracle.possiblePositions[3].size() , is (1));
      assertThat(oracle.possiblePositions[3], hasItem(Point.get(1, 3)));
    }
  }
}
