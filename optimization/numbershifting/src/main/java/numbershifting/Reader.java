package numbershifting;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Reader {

  public static void main(String[] args) {
    String input = "Code for next level (level 11): qffmkiddphmqutkptjhwpnraapcrjodh\r\n"
        + "8 5\r\n"
        + "0 0 0 0 0 0 0 0\r\n"
        + "7 0 1 0 0 0 6 0\r\n"
        + "3 0 1 0 0 0 0 0\r\n"
        + "6 0 0 0 2 1 9 0\r\n"
        + "2 0 0 0 0 0 0 0";
    
    Scanner in = new Scanner(input);

    String level = in.nextLine();
    System.out.println(level);
    
    State state = new State();
    state.read(in);
    state.debug();
  
    findSol(state);
    
    if (! solution.isEmpty()) {
      System.out.println("case xx:");
      for (Move m : solution) {
        System.out.println("System.out.println(\""+m.output()+"\");");
      }
      System.out.println("break;");
    } else {
      System.out.println("no sol ?");
    }
  }

  static boolean stop = false;
  static List<Move> solution = new ArrayList<>();
  
  private static void findSol(State state) {
    if (state.elementCount == 0) {
      System.out.println("Found a solution  !!! ");
      stop = true;
    }
    
    
    List<Move> moves = state.possibleMoves();
    for (Move m : moves) {
      state.apply(m);
      
      findSol(state);
      if (stop) {
        solution.add(0, m);
        return;
      }
      
      state.unapply(m);
    }
  }
  
  
  
  
}
