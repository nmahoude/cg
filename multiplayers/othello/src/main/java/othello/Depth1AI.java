package othello;

public class Depth1AI {
  State work = new State();
  
  public String think(State originalState) {
    System.err.println("Current state : ");
    originalState.debug();
    
    int bestScore = Integer.MIN_VALUE;
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
        
        
        
        int score = 0;
        
        score +=  swapped;
        
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
}
