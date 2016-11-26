package fantasticBits.fb.referee;

import fantasticBits.fb.ai.AI;
import fantasticBits.fb.state.GameState;

/**
 * Play a game with multiple AI
 */
public class Referee {
  AI[] ais = new AI[2];
  GameState currentState;
  private Result[] results;
  
  public void setAi(int index, AI ai) {
    ais[index] = ai;
  }
  
  public void playOneGame() {
    init();
    initGameState();
    while (noWinner()) {
      currentState.initRound();
      for (int i=0;i<ais.length;i++) {
        ais[i].setGameState(currentState);
        ais[i].think();
      }

      GameState nextState = currentState.duplicate();
      for (int i=0;i<ais.length;i++) {
        nextState.applyMove(ais[i].getNextMove());
      }
      nextState.tearDown();
      currentState = nextState;
    }
  }

  private void init() {
    results = new Result[ais.length];
    for (int i =0;i<ais.length;i++) {
      results[i].ai = ais[i];
      results[i].score = 0.0;
    }
  }

  private boolean noWinner() {
    return false;
  }

  public Result[] getResults() {
    return results;
  }
  
  private void initGameState() {
    // TODO Auto-generated method stub
    
  }
}
