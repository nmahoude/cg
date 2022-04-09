package othello;

public class Depth1AI {
  State work = new State();
  
  public String think(State originalState) {

    int bestScore = Integer.MIN_VALUE;
    String bestAction = "";

    for (int y = 0; y < State.BOARDSIZE; y++) {
      for (int x = 0; x < State.BOARDSIZE; x++) {
        if (originalState.grid[x][y] != -1) continue;
        
        work.copyFrom(originalState);
        int swapped = work.putTile(x, y, State.id);
        if (swapped == 0) continue;
        
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
