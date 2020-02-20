package ww.prediction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import ww.Dir;
import ww.GameState;
import ww.Point;
import ww.TU;
import ww.prediction.Divination;
import ww.sim.Move;
import ww.sim.Simulation;

public class DivinationTest {
  Divination divination;
  private GameState currentState;
  private Simulation simulation;
  
  @Before
  public void setup() {
    currentState = new GameState();
    simulation = new Simulation(currentState);
  }
  
  private void initDivination() {
    divination = new Divination(currentState);
    divination.setDebug(true);
  }
  @Test
  public void whenWeKnowEverything_contraryToJohnSnow() {
    TU.setHeights(currentState, 6,
      "344444",
      "333434",
      "..34..",
      ".3..3.",
      ".0101.",
      "000002");
    TU.setAgent(currentState, 0,1,3);
    TU.setAgent(currentState, 1,1,1);
    TU.setAgent(currentState, 2,3,3);
    TU.setAgent(currentState, 3,4,4);

    initDivination();
    
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(3, 3)));
    assertThat(divination.guessedPosition[1], is (Point.get(4, 4)));
  }

  @Test
  public void oneKnown_noFormerInformation() {
    
    TU.setHeights(currentState, 6,
      "344444",
      "333434",
      "..34..",
      ".3..3.",
      ".0101.",
      "000002");
    TU.setAgent(currentState, 0,1,3);
    TU.setAgent(currentState, 1,1,1);
    TU.setAgent(currentState, 2,3,3);
    TU.setAgent(currentState, 3,-1,-1);
    initDivination();
    
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(3, 3)));
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
  
  @Test
  public void noKnown_noFormerInformation() {
    TU.setHeights(currentState, 6,
      "344444",
      "333434",
      "..34..",
      ".3..3.",
      ".0101.",
      "000002");
    TU.setAgent(currentState, 0,1,3);
    TU.setAgent(currentState, 1,1,1);
    TU.setAgent(currentState, 2,-1, -1);
    TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.unknown));
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
  
  @Test
  public void onlyOnePossibility_andOneMissing() {
    GameState previous = new GameState();
    TU.setHeights(previous, 6, 
        "440000", // <- the only cell not visible !
        "440000",
        "444400",
        "444400",
        "444444",
        "044444");
      TU.setAgent(previous, 0,3,0);
      TU.setAgent(previous, 1,5,2);
      TU.setAgent(previous, 2,0,5);
      TU.setAgent(previous, 3,-1,-1);
    
    TU.setHeights(currentState, 6,
      "440000", // <- the only cell not visible !
      "440000",
      "444400",
      "444400",
      "444444",
      "044444");
    TU.setAgent(currentState, 0,3,0);
    TU.setAgent(currentState, 1,5,2);
    TU.setAgent(currentState, 2,0,5);
    TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    divination.updateSimulated(previous, new Move(previous.agents[0]));
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(0,5)));
    assertThat(divination.guessedPosition[1], is (Point.get(5,0)));
  }
  
  @Test
  public void twoPossibilities_andTwoMissing() {
    TU.setHeights(currentState, 6,
      "440000", // <- the cell not visible !
      "440000",
      "444400",
      "444400",
      "444444",
      "044444"); // <- here too
    TU.setAgent(currentState, 0,3,0);
    TU.setAgent(currentState, 1,5,2);
    TU.setAgent(currentState, 2,-1, -1);
    TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(0,5)));
    assertThat(divination.guessedPosition[1], is (Point.get(5,0)));
  }
  
  @Test
  public void threePossibilities_andTwoMissing() {
    TU.setHeights(currentState, 6,
      "440000", // <- the cell not visible !
      "440000",
      "444400",
      "444400",
      "444444",
      "004444"); // <- here too
    TU.setAgent(currentState, 0,3,0);
    TU.setAgent(currentState, 1,5,2);
    TU.setAgent(currentState, 2,-1, -1);
    TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.unknown));
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
  
  @Test
  public void lockAgentIfBlocked() {
    TU.setHeights(currentState, 6,
      "000000", 
      "000000",
      "444000",
      "404000", // locked in the hole
      "444000",
      "000000");
    TU.setAgent(currentState, 0,3,0);
    TU.setAgent(currentState, 1,5,2);
    TU.setAgent(currentState, 2,1, 3);
    TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPositionLocked[0], is (true));
    assertThat(divination.guessedPositionLocked[1], is (false));
  }
  
  @Test
  public void dontForgetLockedAgent() {
    TU.setHeights(currentState, 6,
      "000000", 
      "000000",
      "444000",
      "404000", // locked in the hole
      "444000",
      "000000");
    TU.setAgent(currentState, 0,3,0);
    TU.setAgent(currentState, 1,5,2);
    TU.setAgent(currentState, 2,1, 3);
    TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    divination.guessedPositionLocked[1] = true;
    divination.guessedPosition[1] = Point.get(5, 0);
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(1, 3)));
    assertThat(divination.guessedPosition[1], is (Point.get(5,  0)));
  }
  
  @Test
  public void givenConstruction_onlyOnePossibility() {
    GameState previous = new GameState();
    TU.setHeights(previous, 6, 
        "440000", // <- the cell not visible !
        "440000",
        "444400",
        "444400",
        "444444",
        "004444" // <- 2 pos here 
        );
    TU.setAgent(previous, 0,3,0);
    TU.setAgent(previous, 1,5,2);
    TU.setAgent(previous, 2,-1,-1);
    TU.setAgent(previous, 3,-1,-1);
    
    TU.setHeights(currentState, 6,
      "440000", // <- the cell not visible !
      "440000",
      "444400",
      "444400",
      "444444",
      "014444"); // <- construction appears on (1, 5)
    TU.setAgent(currentState, 0,3,0);
    TU.setAgent(currentState, 1,5,2);
    TU.setAgent(currentState, 2,-1,-1);
    TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    
    divination.updateSimulated(previous, new Move(previous.agents[0]));
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(0,5)));
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
  
  @Test
  /*
   * the divination guessed that the agent 0 clones itself
   */
  public void dontCloneTheOpponentAgents() {
    GameState previous = new GameState();
    TU.setHeights(previous, 5, 
        "11040",
        "14343",
        "34443",
        "34443",
        "44113"
        );
    TU.setAgent(previous, 0,3,0);
    TU.setAgent(previous, 1,4,2);
    TU.setAgent(previous, 2,2,0);
    TU.setAgent(previous, 3,-1,-1);
    
    TU.setHeights(currentState, 5,
        "11040",
        "14443",
        "34443",
        "34443",
        "44113");
      TU.setAgent(currentState, 0,0,1);
      TU.setAgent(currentState, 1,0,0);
      TU.setAgent(currentState, 2,1,0);
      TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    
    divination.updateSimulated(previous,new Move(previous.agents[0]));
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(1,0)));
    assertThat(divination.guessedPosition[1], is(not(Point.get(2, 0))));
  }
  
  @Test
  public void agentAlreadyDoneCantHaveDoneTheConstruction() {
    GameState previous = new GameState();
    TU.setHeights(previous, 6, 
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000"
        );
    TU.setAgent(previous, 0,0,0);
    TU.setAgent(previous, 1,0,1);
    TU.setAgent(previous, 2,5,5); // we know him
    TU.setAgent(previous, 3,-1,-1); // we don't know him 
    
    TU.setHeights(currentState, 6,
        "000001",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000"
      );
      TU.setAgent(currentState, 0,0,1);
      TU.setAgent(currentState, 1,0,0);
      TU.setAgent(currentState, 2,-1,-1);
      TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    
    divination.updateSimulated(previous, new Move(previous.agents[0]));
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(5, 5)));
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
  
  @Test
  public void bug_1() throws Exception {
    GameState previous = new GameState();
    TU.setHeights(previous, 5, 
        "00020",
        "22201",
        "02230",
        "01031",
        "00001"
        );
    TU.setAgent(previous, 0,1,0);
    TU.setAgent(previous, 1,2,3);
    TU.setAgent(previous, 2,-1,-1);
    TU.setAgent(previous, 3,0,4);
    
    TU.setHeights(currentState, 5,
        "00020",
        "22201",
        "02230",
        "02031",
        "00001"
      );
    TU.setAgent(currentState, 0,1,0);
    TU.setAgent(currentState, 1,2,3);
    TU.setAgent(currentState, 2,-1,-1);
    TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    divination.updateSimulated(previous, new Move(previous.agents[0]));
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.unknown));
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
  
  @Test
  public void agentAlreadyDone_ontheConstructionSite_canHaveDoneTheConstruction() {
    GameState previous = new GameState();
    TU.setHeights(previous, 6, 
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000"
        );
    TU.setAgent(previous, 0,0,0);
    TU.setAgent(previous, 1,0,1);
    TU.setAgent(previous, 2,5,5); // we know him
    TU.setAgent(previous, 3,-1,-1); // we don't know him 
    
    TU.setHeights(currentState, 6,
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000001"
      );
      TU.setAgent(currentState, 0,0,0);
      TU.setAgent(currentState, 1,0,1);
      TU.setAgent(currentState, 2,-1,-1);
      TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    
    divination.updateSimulated(previous,new Move(previous.agents[0]));
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (not(Point.get(5, 5))));
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
  
  @Test
  public void dontDuplicateFoundAgents() {
    
    TU.setHeights(currentState, 5,
      "10332",
      "34430",
      "44430",
      "31031",
      "00001");
    TU.setAgent(currentState, 0,0,3);
    TU.setAgent(currentState, 1,0,1);
    TU.setAgent(currentState, 2,1,3);
    TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(1, 3)));
    assertThat(divination.guessedPosition[1], is (not(Point.get(1, 3))));
  }
  
  @Test
  public void stateCorrectAfterPush() {
    TU.setHeights(currentState, 5,
        "10332",
        "34430",
        "44430",
        "31031",
        "00001");
    TU.setAgent(currentState, 0,0,3);
    TU.setAgent(currentState, 1,0,1);
    TU.setAgent(currentState, 2,1,3);
    TU.setAgent(currentState, 3,-1,-1);
    
    initDivination();
    Move move = TU.getMove(currentState.agents[0], Dir.E, Dir.SE);
    simulation.simulate(move);
    
    assertThat(currentState.agents[2].position, is (Point.get(2, 4)));
  }
  
  @Test
  public void beenPushed_onlyOneMoveCanDoTheAction() {
    GameState previous = new GameState();
    TU.setHeights(previous, 6, 
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000");
    TU.setAgent(previous, 0,1,5);
    TU.setAgent(previous, 1,4,1);
    TU.setAgent(previous, 2,5,1);
    TU.setAgent(previous, 3,3,1); // I know everyone
      
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
    
    initDivination();
    
    divination.updateSimulated(previous, new Move(previous.agents[0]) );
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(5 ,1)));
    assertThat(divination.guessedPosition[1], is (Point.get(3, 1)));
  }
  
  @Test
  public void beenPushed_onlyOneMoveCanDoThePush_EvenIfWeDontKnowThePositions() {
    GameState previous = new GameState();
    TU.setHeights(previous, 6, 
        "000000",
        "444444",
        "000000",
        "444444",
        "000000",
        "000000");
    TU.setAgent(previous, 0,1,5);
    TU.setAgent(previous, 1,1,2);
    TU.setAgent(previous, 2,-1,-1);
    TU.setAgent(previous, 3,-1,-1); // I know everyone
      
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
    
    initDivination();
    
    divination.updateSimulated(previous, new Move(previous.agents[0]) );
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(2, 2)));
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
  
  @Test
  public void doPush_detectInvalidatedMove_stillSeeIt() {
    GameState previous = new GameState();
    TU.setHeights(previous, 6, 
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000");
    TU.setAgents(previous, 
        Point.get(1, 1),
        Point.get(0, 5),
        Point.get(2, 1), // I will push him, but failed
        Point.unknown
        );
    
    GameState simulatedState = TU.createFromGameState(previous); 
    Move move = TU.getMove(simulatedState.agents[0], Dir.E, Dir.E);
    new Simulation(simulatedState).simulate(move);
    
    TU.setHeights(currentState, 6,
        "000000",
        "000000",
        "000000",
        "000000",
        "000010",
        "000000");
    TU.setAgents(currentState, 
        Point.get(1, 1),
        Point.get(0, 2),
        Point.get(2, 1), // I still see him
        Point.unknown
        );

    
    initDivination();
    
    divination.updateSimulated(simulatedState, move );
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(2, 1)));
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
  
  @Test
  public void doPush_detectInvalidatedMove_wontSeeItAfter() {
    GameState previous = new GameState();
    TU.setHeights(previous, 6, 
        "000000",
        "000000",
        "000000",
        "000000",
        "000000",
        "000000");
    TU.setAgents(previous, 
        Point.get(1, 1),
        Point.get(0, 5),
        Point.get(2, 1), // I will push him, but failed
        Point.unknown
        );
    
    GameState simulatedState = TU.createFromGameState(previous); 
    Move move = TU.getMove(simulatedState.agents[0], Dir.E, Dir.E);
    new Simulation(simulatedState).simulate(move);
    
    TU.setHeights(currentState, 6,
        "000000",
        "000000",
        "000000",
        "000000",
        "000010",
        "000000");
    TU.setAgents(currentState, 
        Point.get(1, 1),
        Point.get(0, 5),
        Point.unknown, // Don't see it anymore
        Point.unknown
        );

    
    initDivination();
    
    divination.updateSimulated(simulatedState, move );
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.unknown)); // currentState is incompatible with a validatedPush
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
}
