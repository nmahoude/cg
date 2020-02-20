package hypersonic.ai.bfs;

import java.util.Arrays;
import java.util.Comparator;

import hypersonic.Move;
import hypersonic.Player;
import hypersonic.State;
import hypersonic.entities.Bomberman;

public class BFS {
  
  private static final int BEAM_WIDTH = 1000;
  static       Comparator<BFSNode> hashSorter = new Comparator<BFSNode>() {
    @Override
    public int compare(BFSNode o1, BFSNode o2) {
      return Double.compare(o2.state.hash, o1.state.hash
          );
    }
  };

  private int collisions;
  private int missedCollisions;
  private Move bestMove;
  BFSNode currentLayer[] = new BFSNode[1005];
  BFSNode hashedLayer[] = new BFSNode[10005];
  BFSNode nextLayer[] = new BFSNode[10005];

  
  public void think(State model) {
    BFSNode root = BFSCache.pop();
    root.state.copyFrom(model);
    
    int currentLayerFE = 0;
    int hashedLayerFE = 0;
    int nextLayerFE = 0;
    currentLayer[currentLayerFE++] = root;
    
    int depth = 1;
    while (true) {
      int sim = 0;
      System.err.println("time spent : "+ (System.currentTimeMillis()-Player.startTime));
      for (int c=0;c<currentLayerFE;c++) {
        BFSNode current = currentLayer[c];
        sim++;
        current.generateChildren(false && depth == 1);
        for (int i=0;i<current.movesFE;i++) {
          BFSNode child = BFSCache.nodes[current.firstChildIndex + i];
          if (child.state.players[Player.myId].isDead) continue;
          nextLayer[nextLayerFE++] = child;
        }
        
        if ((sim &0b111) == 0 && System.currentTimeMillis()- Player.startTime > 90) {
          depth = 100;
          break;
        }
      }
      
      System.err.println("************************");
      System.err.println("nextLayer : "+nextLayerFE);
      if (nextLayerFE == 0) {
        break;
      }

      Arrays.sort(nextLayer, 0, nextLayerFE, hashSorter);
      
      int elements = 0;
      hashedLayerFE = 0;
      long lastHash = -1;
      for (int nl=0;nl<nextLayerFE;nl++) {
        BFSNode next = nextLayer[nl];
        if (lastHash == next.state.hash) {
          collisions++;
          continue;
        }
        lastHash = next.state.hash;
        hashedLayer[hashedLayerFE++] = next;
      }
      
      Arrays.sort(hashedLayer, 0, hashedLayerFE,new Comparator<BFSNode>() {
        @Override
        public int compare(BFSNode o1, BFSNode o2) {
          return Double.compare(o2.score, o1.score
              );
        }
      });

      
      currentLayerFE=0;
      elements = 0;
      for (int h=0;h<hashedLayerFE;h++) {
        BFSNode next = hashedLayer[h];
        currentLayer[currentLayerFE++] = next;
        elements++;
        if (elements > BEAM_WIDTH) {
          break;
        }
      }
      nextLayerFE = 0;
      hashedLayerFE = 0;
      
      
      if (System.currentTimeMillis()- Player.startTime > 90) {
        break;
      }
      System.err.println("layer "+depth+" stats :");
      System.err.println("Collisions : "+collisions +"  / missed : " + missedCollisions);
      
      System.err.println("nextLayer contains "+ currentLayerFE);
      
      depth ++;
      missedCollisions = 0;
      collisions = 0;
      if (depth >= 6) break;
    }
    System.err.println("Last layer contains : "+currentLayerFE);
    System.err.println("In "+(System.currentTimeMillis()-Player.startTime)+" ms");
    if (currentLayerFE == 0) {
      bestMove = Move.STAY;
    } else {
      BFSNode current = currentLayer[0];
      System.err.println("best score is "+current.score);
      while (current.parent.parent != null) {
        current = current.parent;
      }
      bestMove = current.moveToHere;
    }
  }

  public void reset() {
    BFSCache.reset();
  }
  
  public void ouput(State state) {
    outputMove(state.players[Player.myId], bestMove, "wandering...");
  }
  private void outputMove(final Bomberman me, final Move move, String message) {
    int newX = me.position.x + move.dx;
    int newY = me.position.y + move.dy;
    if (move.dropBomb) {
      System.out.println("BOMB " + newX + " " + newY + " " + message);
    } else {
      System.out.println("MOVE " + newX + " " + newY + " " + message);
    }
  }
}
