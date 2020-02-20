package coderoyale;

public class CommandException extends RuntimeException {
  String command;
  String comment;
  boolean result;
  
  public static CommandException  success(String command) {
    CommandException ce = build(command, "");
    ce.result = true;
    return ce;
  }

  public static CommandException  failure(String command) {
    CommandException ce = build(command, "");
    ce.result = true;
    return ce;
  }

  private static CommandException build(String command, String comment) {
    CommandException ce = new CommandException();
    ce.command =  command;
    ce.comment = comment;
    return ce;
  }

  public void output() {
    if (this.result) {
      if (!"".equals(this.comment)) {
        System.err.println(this.comment);
      }
      if (this.result) {
        System.err.println("Command was a success");
      } else {
        System.err.println("Command was a FAILURE");
      }
      System.out.println(this.command);
    }
  }

}
