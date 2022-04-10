package othello;

public class Depth1AI {
  State work = new State();
  
  public String think(State originalState) {
    System.err.println("Current state : ");
    originalState.debug();
    
    double bestScore = Double.NEGATIVE_INFINITY;
    String bestAction = "";

    for (int y = 0; y < State.BOARDSIZE; y++) {
      for (int x = 0; x < State.BOARDSIZE; x++) {
        Pos pos = Pos.from(x, y);

        if (originalState.tileAt(x,y) != -1) continue;
        boolean hasAtLeastAnOppTile = originalState.hasOppNeighbor(pos, State.myId);
        if (! hasAtLeastAnOppTile) continue;
        
        work.copyFrom(originalState);
        int swapped = work.putTile(x, y, State.myId);
        System.err.println("Swapped @ "+pos+" is "+swapped);
        
        if (swapped == 0) continue;

        System.err.println("Testing : "+pos+ " with opp swapped tiles "+swapped);
        System.err.println("State : ");
        work.debug();
        
        
        
        double score = 0.0;
        
        score +=  swapped;
        
        if (isCaseX(pos)) score -= 4;
        if (isCaseC(pos)) score -= 4;
        
        score += 0.2 * work.countNeighbors(pos, State.myId);
        score -= 0.2 * work.countNeighbors(pos, State.oppId);
        
//        if (x % 2 == 0)
//          score += 2;
//        if (y % 2 == 0)
//          score += 2;
//
//        score += 4 * Math.abs(State.boardSize /2 - x);
//        score += 4 * Math.abs(State.boardSize /2 - y);
//
//        int count = state.countNeighbors(x, y, State.id);
//        score += 10 * count;
        
        if (score != bestScore) {
          bestScore = score;
          bestAction = State.toBoardCoordinates(x, y);
        }
      }
    }
    return bestAction;

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
    if (pos == Pos.from(7,1)) return true;
    if (pos == Pos.from(1,7)) return true;
    if (pos == Pos.from(7,7)) return true;
    
    return false;
  }
}
