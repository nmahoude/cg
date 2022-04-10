package othello;

public class Depth1AI {
  State work = new State();
  
  State workForHim = new State();

  private int oppMoveCount;
  
  public String think(State originalState) {
    
    double bestScore = Double.NEGATIVE_INFINITY;
    Pos bestPos = Pos.VOID;
    String bestAction = "";

    for (int y = 0; y < State.BOARDSIZE; y++) {
      for (int x = 0; x < State.BOARDSIZE; x++) {
        Pos pos = Pos.from(x, y);

        if (originalState.tileAt(x,y) != -1) continue;
        boolean hasAtLeastAnOppTile = originalState.hasOppNeighbor(pos, State.myId);
        if (! hasAtLeastAnOppTile) continue;
        
        work.copyFrom(originalState);
        int swapped = work.putTile(x, y, State.myId);
        
        if (swapped == 0) continue;

//        System.err.println("State : ");
//        work.debug();
        
        
        
        double score = 0.0;
        
        score = eval(work, pos, swapped, State.myId);
        

        // check his next ply
        double nextPlayerScore = maxTilesNextPlay(work, State.oppId, false);
        score -= nextPlayerScore;
        if (oppMoveCount == 0) {
          score += 10_000;
        } else {
          score -= 0.1 * oppMoveCount;
        }
        
        System.err.println("Testing : "+pos+ " with score "+score);
        if (score > bestScore) {
          bestScore = score;
          bestPos = Pos.from(x,y);
          bestAction = State.toBoardCoordinates(x, y);
        }
      }
    }
    
    /* debug next ply */
    work.copyFrom(originalState);
    work.putTile(bestPos.x, bestPos.y, State.myId);
    double nextPlayerScore = maxTilesNextPlay(work, State.oppId, false);
    System.err.println("   ****** nest ply he can get "+nextPlayerScore+ " tiles back");
    
    
    
    return bestAction;

  }

  private double eval(State state, Pos pos, int swapped, int id) {
    double score = 0.0;
    if (Player.turn > 30) {
      score += state.scores[id];
      score -= state.scores[1-id];
    }
    
    double positionalAdvantage = OthelloEval.positionalAdvantage(pos);
    score += positionalAdvantage * 0.5;
    
    
    int myNeighbors = work.countNeighbors(pos, id);
    int oppNeighbors = work.countNeighbors(pos, 1-id);
    score += 0.2 * myNeighbors;
    score -= 0.1 * oppNeighbors;
    
    if (myNeighbors + oppNeighbors == 8) {
      score += 1.0; // not a frontier disc
    }
    
    return score;
  }

  private double maxTilesNextPlay(State current, int id, boolean debug) {
    State work = new State();
    double bestScore = Double.NEGATIVE_INFINITY;
    
    oppMoveCount = 0;
    for (int y = 0; y < State.BOARDSIZE; y++) {
      for (int x = 0; x < State.BOARDSIZE; x++) {
        Pos pos = Pos.from(x, y);

        if (current.tileAt(x,y) != -1) continue;
        boolean hasAtLeastAnOppTile = current.hasOppNeighbor(pos, id);
        if (! hasAtLeastAnOppTile) continue;
        
        oppMoveCount++;
        
        work.copyFrom(current);
        int count = work.putTile(x, y, id);
        double score = eval(work, pos, count, id);
        
        if (debug) System.err.println("@"+pos+" => "+count +" => "+score);
        if (score > bestScore) {
          bestScore = score;
        }
      }
    }
    
    return bestScore;
  }

  private boolean isCorner(Pos pos) {
    if (pos == Pos.from(0, 0)) return true;
    if (pos == Pos.from(7, 0)) return true;
    if (pos == Pos.from(0, 7)) return true;
    if (pos == Pos.from(7, 7)) return true;

    return false;
  }

  private boolean isCaseC(Pos pos) {
    if (pos == Pos.from(1,0)) return true;
    if (pos == Pos.from(0,1)) return true;

    if (pos == Pos.from(6,0)) return true;
    if (pos == Pos.from(7,1)) return true;
    
    if (pos == Pos.from(1,7)) return true;
    if (pos == Pos.from(0,6)) return true;

    if (pos == Pos.from(6,7)) return true;
    if (pos == Pos.from(7,6)) return true;
    
    return false;
  }

  private boolean isCaseX(Pos pos) {
    if (pos == Pos.from(1,1)) return true;
    if (pos == Pos.from(6,1)) return true;
    if (pos == Pos.from(1,6)) return true;
    if (pos == Pos.from(6,6)) return true;
    
    return false;
  }
}
