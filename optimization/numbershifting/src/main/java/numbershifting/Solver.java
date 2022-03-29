package numbershifting;

import java.util.List;
import java.util.Scanner;

public class Solver {

  State state = new State();

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    new Solver().play(in);
  }

  private void play(Scanner in) {
    // Write an action using System.out.println()
    // To debug: System.err.println("Debug messages...");

    System.out.println("first_level");

    // game loop
    while (true) {
      state.read(in);

      List<Move> moves;
      do {
        moves = state.possibleMoves();
        if (moves.isEmpty()) break;
        Move chosenMove = moves.get(0);
        state.apply(chosenMove, false);
        System.out.println(chosenMove.output());
      } while(true);
      
    }
  }
}
