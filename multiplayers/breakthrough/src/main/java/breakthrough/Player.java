package breakthrough;

import fast.read.FastReader;

public class Player {

  public static int turn;
  State state = new State();
  public static boolean firstPlayer = true;

  public static void main(String args[]) {
    FastReader in = new FastReader(System.in);

    new Player().play(in);
  }

  private void play(FastReader in) {
    firstPlayer = true;
    turn = 0;

    // game loop
    while (true) {
      turn++;

      state.read(in);
    }
  }
}
