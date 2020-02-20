package cotc.tests;

public class InvalidInputException extends Exception {

  public InvalidInputException(String string, String line) {
    super(string + " -> "+line);
  }

}
