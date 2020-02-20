package uttt.mcts;

import uttt.Player;
import uttt.state.State2;

public class PossibleMovesAccelerator {
  int possibleMoves[] = new int[9*9];
  int possibleMovesFE[] = new int[9];
  int total = 0;

  public void build(State2 state) {
    for (int i=0;i<9;i++) {
      possibleMovesFE[i] = 0;
    }
    total = 0;
    total += getPossibleMovesForGrid(0, state.cells[0]);
    total += getPossibleMovesForGrid(1, state.cells[1]);
    total += getPossibleMovesForGrid(2, state.cells[2]);
    total += getPossibleMovesForGrid(3, state.cells[3]);
    total += getPossibleMovesForGrid(4, state.cells[4]);
    total += getPossibleMovesForGrid(5, state.cells[5]);
    total += getPossibleMovesForGrid(6, state.cells[6]);
    total += getPossibleMovesForGrid(7, state.cells[7]);
    total += getPossibleMovesForGrid(8, state.cells[8]);
  }

  public int pickAMove() {
    int rand = Player.random.nextInt(total);
    for (int gDecal=0;gDecal<9;gDecal++) {
      int max = possibleMovesFE[gDecal];
      rand -= max;
      if (rand < 0) {
        // ok it's in this interval
        rand = rand + max;
        int value = possibleMoves[9*gDecal+rand];
        possibleMoves[9*gDecal+rand] = possibleMoves[9*gDecal+max-1];
        possibleMovesFE[gDecal] = max-1;
        total--;
        return value;
      }
    }
    System.out.println("never here");
    return 0;
  }
  
  public int pickAMove(int gDecal) {
    int max = possibleMovesFE[gDecal];
    int rand = Player.random.nextInt(max);
    int value = possibleMoves[9*gDecal+rand];
    possibleMoves[9*gDecal+rand] = possibleMoves[9*gDecal+max-1];
    possibleMovesFE[gDecal] = max-1;
    total--;
    return value;
  }
  
  private final int getPossibleMovesForGrid(int decal, int mask) {
    int all = State2.complete(mask);

    if (all == State2.ALL_MASK)
      return 0; // no possible move

    int baseY = decal << 16;

    int total = 0;
    for (int d = 1; d <= 0b100_000_000; d *= 2) {
      if ((all & d) == 0) {
        possibleMoves[9*decal + possibleMovesFE[decal]++] = baseY + d;
        total++;
      }
    }
    return total;
  }
}
