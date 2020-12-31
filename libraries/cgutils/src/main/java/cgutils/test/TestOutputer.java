package cgutils.test;

public class TestOutputer {
  public static boolean ACTIVE = true;
  
  public static final String EOF = "\n";
  
  public static void output(Object... values) {
    if (!ACTIVE) return;
    
    System.err.print("+\"");
    for (int i=0;i<values.length;i++) {
      
      Object value = values[i];
      if (value instanceof char[]) {
        char[] charValue = (char[])value;
        int index = 0;
        while (index < charValue.length && charValue[index] != '\n') {
          System.err.print(charValue[index++]);
        }
      } else {
        System.err.print(value);
      }
      System.err.print(" ");
    }
    System.err.println("\"+EOF");
  }

  public static void outputCommand(String command) {
    if (!ACTIVE) return;
    System.err.println(command);
  }

}