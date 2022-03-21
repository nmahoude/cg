package samegame.ai;

import java.util.ArrayList;
import java.util.List;

import samegame.Player;
import samegame.Pos;
import samegame.State;

public class BeamSearch {
  public static final int MAX_LAYERS = 8;
  final static BSLayer[] layers = new BSLayer[MAX_LAYERS];
  static {
    for (int i=0;i<MAX_LAYERS;i++) {
      layers[i] = new BSLayer();
      layers[i].layer = i;
    }
  }
  
  public List<Pos> precalculatedPositions = new ArrayList<>();
  
  public Pos think(State original) {
    original.aiScore = 0;
    
    long start = System.currentTimeMillis();
    int maxIndex = 0;
    
    State best = original;
    
    layers[0].init(original);
    for (int i=1;i<MAX_LAYERS;i++) {
      layers[i].expand(layers[i-1]);
      System.err.println("Nodes count @ layer "+i+" is "+layers[i].statesFE);
      maxIndex = i;
      if (layers[i].statesFE == 0) {
        maxIndex = i-1;
        break;
      } else {
        if (layers[i].states[0].aiScore > best.aiScore) {
          best = layers[i].states[0];
        }
      }
    }
    
    long end = System.currentTimeMillis();
    System.err.println("Think in "+(end-start)+" ms");
  
    if (Player.turn == 1) {
      precalculatedPositions.clear();
      State current = layers[maxIndex].states[0];
      while (current != original) {
        precalculatedPositions.add(0, current.picked);
        current = current.parent;
      }
    } else if (precalculatedPositions.isEmpty()){
      precalculatedPositions.clear();
      precalculatedPositions.add(best.childOf(original).picked);
    }
    
    return precalculatedPositions.remove(0);
    
  }
}
