package fantasticBits.fb.referee;

import fantasticBits.fb.ai.AI;
import fantasticBits.fb.state.GameState;

/**
 * Play a game with multiple AI
 */
public class Referee {
  private static final double WIN  = 2.0;
  private static final double DRAW = 1.0;
  private static final double LOST = 0.0;
  
  private static final int MAX_TURNS = 500;
  
  int turns = 0;
  AI[] ais = new AI[2];
  GameState currentState;
  private Result[] results;
  
  public void setAi(int index, AI ai) {
    ais[index] = ai;
  }
  
  public void playOneGame() {
    init();
    initGameState();
    while (!isFinished()) {
      turns++;
      currentState.initRound();
      for (int i=0;i<ais.length;i++) {
        ais[i].setGameState(currentState);
        ais[i].think();
      }

      GameState nextState = currentState.duplicate();
      for (int i=0;i<ais.length;i++) {
        nextState.applyMove(null, ais[i].getNextMove());
      }
      nextState.tearDown();
      currentState = nextState;
    }
    updateResults();
  }

  private void updateResults() {
    int alivePlayersCount = getAlivePlayersCount();
    for (int i =0;i<ais.length;i++) {
      AI ai = ais[i];
      if (ai.isAlive()) {
        results[i].score= alivePlayersCount == 1 ? DRAW : WIN;
      } else {
        results[i].score = alivePlayersCount == 0 ? DRAW : LOST;
      }
    }
  }

  private boolean isMoreThanOneAIAlive() {
    int aliveCount = getAlivePlayersCount();
    return aliveCount >=2;
  }

  private int getAlivePlayersCount() {
    int aliveCount=0;
    for (AI ai :ais) {
      if (ai.isAlive()) {
        aliveCount++;
      }
    }
    return aliveCount;
  }

  private void init() {
    results = new Result[ais.length];
    for (int i =0;i<ais.length;i++) {
      results[i] = new Result();
      results[i].ai = ais[i];
      results[i].score = DRAW;
    }
  }

  private boolean isFinished() {
    if (turns > MAX_TURNS) {
      return true;
    } else if (!isMoreThanOneAIAlive()){
      return true;
    }
    return false;
  }

  public Result[] getResults() {
    return results;
  }
  
  private void initGameState() {
    currentState = new GameState();
  }
}