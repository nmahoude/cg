package cotc.ai.minimax;

import java.util.Random;

import cotc.GameState;
import cotc.ai.ag.FeatureWeight;
import cotc.ai.ag.StateAnalyser;
import cotc.entities.Ship;
import cotc.game.Simulation;

public class MiniMax {

  private static final MiniMaxNode fake = new MiniMaxNode(Double.NEGATIVE_INFINITY);
  public static Random rand = new Random(System.currentTimeMillis());
  
  static GameState myState;
  static GameState hisState;
  static StateAnalyser myAnalyser;
  static StateAnalyser hisAnalyser;

  private Simulation simulation;
  FeatureWeight weighs = new FeatureWeight();
  
  public void setState(GameState myState, GameState hisState) {
    this.myState = myState;
    this.hisState = hisState;
  }

  public MiniMaxNode evolve(long stopTime) {
    int beams = 0;
    updateChampions(myState);
    updateChampions(hisState);

    myAnalyser = new StateAnalyser();
    myAnalyser.analyse(myState);
    myAnalyser.debug();
    hisAnalyser = new StateAnalyser();
    hisAnalyser.analyse(myState);
    hisAnalyser.debug();

    MiniMaxNode rootNode = new MiniMaxNode(0);
    while (System.currentTimeMillis() < stopTime) {
      rootNode.sendOneBeam(5, myState, hisState);
      myState.restore();
      hisState.restore();
      beams++;
    }    
    return rootNode.getBestChild();
  }

  private void updateChampions(GameState state) {
    Ship best = null;
    int bestHealth = 0;
    for (int s=0;s<state.teams[0].ships.FE;s++) {
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
