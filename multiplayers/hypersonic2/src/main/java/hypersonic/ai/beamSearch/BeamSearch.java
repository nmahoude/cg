package hypersonic.ai.beamSearch;

import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.entities.Bomberman;

public class BeamSearch {
  final static int DEPTH = Player.DEPTH;
  
  private String message;
  private static Move[] bestMoves = new Move[DEPTH];

  public static boolean dropEnnemyBombs;
  BeamLayer layers[] = new BeamLayer[DEPTH];
  {
    layers[0] = new BeamLayer(1);
    layers[1] = new BeamLayer(10);
    layers[2] = new BeamLayer(100);
    layers[3] = new BeamLayer(1000);
    for (int i=4;i<DEPTH;i++) {
      layers[i] = new BeamLayer(10000);
    }
  }
  
  public void think(State model) {
    dropEnnemyBombs = true;
    
    layers[0].depth = 0;
    layers[0].nodes[0].state.copyFrom(model);
    layers[0].nodesFE = 1;
    
    int depth;
    for (depth = 1;depth<DEPTH;depth++) {
      layers[depth].buildFromParent(layers[depth-1]);
      if (System.currentTimeMillis() - Player.startTime > 90) {
        break;
      }
    }

    message = ""+depth + " - "+(System.currentTimeMillis() - Player.startTime)+" ms";
//    for (int i=0;i<depth;i++) {
//      System.err.println("Element @ "+i+" is "+layers[i].nodesFE);
//    }
    BeamNode node = layers[depth-1].nodes[0]; // the best of last layer
//    List<Move> complete = new ArrayList<>();
    while (node.parent.parent != null) {
//      complete.add(node.moveToHere);
      node = node.parent;
    }
//    Collections.reverse(complete);
//    System.err.println("Moves : "+complete);
    bestMoves[0] = node.moveToHere;
  }
  
  public void ouput(State currentState) {
    final Move move = bestMoves[0];
    outputMove(currentState.players[Player.myId], move, message);
  }
  
  private void outputMove(final Bomberman me, final Move move, String message) {
    int newX = me.position.x + move.dx;
    int newY = me.position.y + move.dy;
    if (move.dropBomb) {
      System.out.println("BOMB "+newX+" "+newY+ " "+message);
    } else {
      System.out.println("MOVE "+newX+" "+newY+ " "+message);
    }
  }

}
