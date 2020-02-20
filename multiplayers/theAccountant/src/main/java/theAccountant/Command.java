package theAccountant;

public class Command {
  enum Type {
    MOVE, SHOOT
  }
  final Type type;
  public Command(Type type) {
    super();
    this.type = type;
  }
  String get() {
    return "Command";
  }
}
