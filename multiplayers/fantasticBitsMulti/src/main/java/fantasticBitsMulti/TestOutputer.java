package fantasticBitsMulti;

public class TestOutputer {
  public static final String EOF = "\n";
  
  public static void output(Object... values) {
    System.err.print("+\"");
    for (int i=0;i<values.length;i++) {
      System.err.print(values[i]+" ");
    }
    System.err.println("\"+EOF");
  }

  public static void outputCommand(String command) {
    System.err.println(command);
  }

}
