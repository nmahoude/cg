package ww;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class DivinationTest {
  Divination divination;
  private GameState currentState;
  
  @Before
  public void setup() {
    currentState = new GameState();
    
  }
  
  private void initDivination() {
    currentState.backup();
    divination = new Divination(currentState);
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
    
    divination.updatePrediction(previous);
    divination.guessFrom(currentState);
    
    assertThat(divination.guessedPosition[0], is (Point.get(0,5)));
    assertThat(divination.guessedPosition[1], is (Point.unknown));
  }
}
