package theAccountant;

import trigonometry.Point;

public class Move extends Command{

  public Move(Point target) {
    super(Command.Type.MOVE);
    this.target = target;
  }

  Point target;
  @Override
  String get() {
    return "MOVE "+(int)target.x+" "+(int)target.y;
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((target == null) ? 0 : target.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Move other = (Move) obj;
    if (target == null) {
      if (other.target != null)
        return false;
    } else if (!target.equals(other.target))
      return false;
    return true;
  }

}
