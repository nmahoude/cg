import java.util.ArrayList;
import java.util.List;

class Player {

  static int dx[] = new int[] { 1, 0, -1, 0 };
  static int dy[] = new int[] { 0, -1, 0, 1 };

  static class Pos {
    int x, y;
    int dir;

    Pos(int x, int y) {
      this.x = x;
      this.y = y;
      this.dir = 0;
    }

    public int offset() {
      return (x + HALF_SIZE) + HALF_SIZE * 2 * (y + HALF_SIZE);
    }

    @Override
    public boolean equals(Object obj) {
      Pos pos = (Pos) obj;
      return pos.x == this.x && pos.y == this.y;
    }

    @Override
    public int hashCode() {
      return x + 1_000_000 * y;
    }

    public void applyInstruction(char instr) {
      switch (instr) {
      case 'B':
        x -= dx[dir];
        y -= dy[dir];
        break;
      case 'F':
        x += dx[dir];
        y += dy[dir];
        break;
      case 'R':
        dir++;
        if (dir == 4) dir = 0;
        break;
      case 'L':
        dir--;
        if (dir == -1)  dir = 3;
        break;
      }
    }
    
    public void undoInstr(char instr) {
      switch (instr) {
      case 'B':
        applyInstruction('F');
        break;
      case 'F':
        applyInstruction('B');
        break;
      case 'R':
        applyInstruction('L');
        break;
      case 'L':
        applyInstruction('R');
        break;
      }
    }
    
    void copyFrom(Pos p) {
      this.x = p.x;
      this.y = p.y;
      this.dir = p.dir;
    }

    public void set(int x, int y, int dir) {
      this.x = x;
      this.y = y;
      this.dir = dir;
    }
    
  }

  static final int HALF_SIZE = 5000;
  static int map[] = new int[HALF_SIZE * 2 * HALF_SIZE * 2];

  static Pos cache[] = new Pos[100_000];
  static {
    for (int i=0;i<cache.length;i++) {
      cache[i] = new Pos(0,0);
    }
  }

  static void buildCache(String instructions) {
    Pos current = new Pos(0,0);
    
    for (int i = 0; i < instructions.length(); i++) {
      current.applyInstruction(instructions.charAt(i));
      
      cache[i].copyFrom(current);
    }    
  }
  
  
  static boolean applyInstructions(String instructions, int indexOfInstructionToReplace, char instructionToReplace,
      Pos target, List<Pos> obstacles) {


    int start = 0;
    Pos current = new Pos(0, 0);

    if (instructionToReplace > 0)
    
    
    
    for (int i = start; i < instructions.length(); i++) {

      char instr;
      if (i == indexOfInstructionToReplace) {
        instr = instructionToReplace;
      } else {
        instr = instructions.charAt(i);
      }

      current.applyInstruction(instr);
      if (map[current.offset()] == 1) return false;
    }

    // System.err.println("Final pos = "+currentX+" "+currentY);
    return current.equals(target);

  }

  static String[] availableInstructions = new String[] { "BACK", "FORWARD", "TURN LEFT", "TURN RIGHT" };
  static char[] availableInstructionsAsLetter = new char[] { 'B', 'F', 'L', 'R' };

  public static String mapToInstruction(int i) {
    if (i == 'L')
      return "TURN LEFT";
    if (i == 'R')
      return "TURN RIGHT";
    if (i == 'F')
      return "FORWARD";
    if (i == 'B')
      return "BACK";
    return "YOLO";
  }

  /**
   * @param instructions The list of instructions as memorized by the mutant.
   * @param target       The coordinates (x, y) of the target.
   * @return A string respecting the given format to fix the mutant's path.
   */
  public static String findCorrectPath(String instructionsAsString, List<Integer> target,
      List<List<Integer>> obstacles) {
    // Write your code here
    // System.err.println(instructionsAsString.length());
    // System.err.println(instructionsAsString.length()+" "+instructionsAsString);
    System.err.println("Target : " + target.get(0) + " " + target.get(1));

    // if (1 == 1) return "Replace instruction "+89000+" with "+"FORWARD";

    List<Pos> positions = new ArrayList<>();
    Pos pos = new Pos(0, 0);
    for (List<Integer> posAsList : obstacles) {
      pos.x = posAsList.get(0);
      pos.y = posAsList.get(1);
      map[pos.offset()] = 1;
    }

    buildCache(instructionsAsString);

    String newInstruction = "";
    int indexInstruction = 0;

    stop: for (int i = 0; i < instructionsAsString.length(); i++) {
      if (i % 100 == 0) {
        System.err.println(i);
      }

      if (instructionsAsString.charAt(i) == 'X')
        continue;

      for (int j = 0; j < 4; j++) {
        char instr = availableInstructionsAsLetter[j];
        if (instr == instructionsAsString.charAt(j))
          continue;

        boolean result = applyInstructions(instructionsAsString, i, instr, new Pos(target.get(0), target.get(1)),
            positions);

        if (result) {

          newInstruction = availableInstructions[j];
          indexInstruction = i + 1;

          System.err.println("Trouvé ! " + instr + " @ " + indexInstruction);
          break stop;
        }

      }

    }

    return "Replace instruction " + indexInstruction + " with " + newInstruction;
  }

}
