package numbershifting;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Reader {

  public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
    Scanner in = new Scanner(new File(Reader.class.getClassLoader().getResource("level.txt").toURI()));

    String levelStr = in.nextLine();
    String levelAsString = levelStr.substring(levelStr.indexOf("(")+7, levelStr.indexOf(")"));
    System.out.println("level  :"+levelAsString);
    int level = Integer.parseInt(levelAsString);
    System.out.println(levelStr);
    
    State state = new State();
    state.read(in);
    state.debug();
  
    findSol(state);
    
    if (! solution.isEmpty()) {
      System.out.println("case "+level+":");
      System.out.println("System.err.println(\""+levelStr+"\");");
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
  static long sims = 0;
  
  private static void findSol(State state) {
    if (state.elementCount == 0) {
      System.out.println("Found a solution  !!! ");
      stop = true;
    }
    
    
    List<Move> moves = state.possibleMoves();
    for (Move m : moves) {
      sims++;
      if ((sims & 0b1111111111111111111) == 0) {
        System.err.println(sims);
      }
      boolean notDeadEnd = state.apply(m);
      
      if (!notDeadEnd) {
        state.unapply(m);
        return;
      } else {
        findSol(state);
        if (stop) {
          solution.add(0, m);
          return;
        }
        
        state.unapply(m);
      }
    }
  }
  
  
  
  
}
