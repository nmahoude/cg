package fantasticBits.fb.state;

import java.util.Scanner;

import fantasticBits.fb.ai.Move;

public class GameState {
  
  public GameState duplicate() {
    return null;
  }

  public void applyMove(Move move) {
    // from referee
  }

  public void initRound() {
    // if needed, from Referee
  }

  public void tearDown() {
    // if needed, from Referee
  }


  public void readRoundValue(Scanner scanner) {
    // from CG
  }

  public void readInitValues(Scanner scanner) {
    // from CG
  }

}
