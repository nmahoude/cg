package uttt.state;

public class PossibleMoveCache {

  public static int possibleMoves[] = new int[9*512*81];
  public static int possibleMovesFE[] = new int[512];

  static {
    for (int mask = 0; mask < 512; mask++) {
      possibleMovesFE[mask] = 0;
      for (int d = 1; d <= 0b100_000_000; d *= 2) {
        if ((mask & d) == 0) {
          for (int grid = 0; grid < 9; grid++) {
            int baseY = grid << 9;
            possibleMoves[512*81*grid+81*mask+possibleMovesFE[mask]] = baseY + d;
          }
          possibleMovesFE[mask]++;
        }
      }
    }
  }
}
