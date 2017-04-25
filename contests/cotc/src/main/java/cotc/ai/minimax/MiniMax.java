package cotc.ai.minimax;

import java.util.Random;

import cotc.GameState;
import cotc.ai.ag.FeatureWeight;
import cotc.ai.ag.StateAnalyser;
import cotc.entities.Ship;

public class MiniMax {
  public static Random rand = new Random(System.currentTimeMillis());
  
  private GameState state;
  private StateAnalyser myAnalyser;

  FeatureWeight weighs = new FeatureWeight();
  
  public void setState(GameState state) {
    this.state = state;
  }

  public MiniMaxNode evolve(long stopTime) {
    int beams = 0;
    updateChampions(state);

    myAnalyser = new StateAnalyser();
    myAnalyser.analyse(state);
    myAnalyser.debug();

    MiniMaxNode rootNode = new MiniMaxNode();
    
    while (System.currentTimeMillis() < stopTime) {
      rootNode.startMinimax(5, state);
      state.restore();
      beams++;
    }
    System.err.println("Minimax beams "+beams);
    return rootNode.getBestChild();
  }

  public MiniMaxNode evolve() {
    int beams = 0;
    updateChampions(state);

    myAnalyser = new StateAnalyser();
    myAnalyser.analyse(state);

    MiniMaxNode rootNode = new MiniMaxNode();
    
    for (int i=0;i<10;i++) {
      rootNode.startMinimax(2, state);
      state.restore();
      beams++;
    }
    System.err.println("Minimax beams "+beams);
    return rootNode.getBestChild();
  }

  private void updateChampions(GameState state) {
    Ship best = null;
    int bestHealth = 0;
    for (int s=0;s<state.teams[0].ships.length;s++) {
      Ship ship = state.teams[0].ships.elements[s];
      ship.champion = false;
      if (ship.health > bestHealth) {
        bestHealth = ship.health;
        best = ship;
      }
    }
    best.champion = true;
  }

}
