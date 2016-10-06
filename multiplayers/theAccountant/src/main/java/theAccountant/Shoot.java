package theAccountant;


public class Shoot extends Command {

  public Shoot(Enemy enemy) {
    super(Command.Type.SHOOT);
    this.enemy = enemy;
  }

  Enemy enemy;
  @Override
  String get() {
    return "SHOOT "+enemy.id;
  }
  @Override
  public int hashCode() {
    return enemy.id;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Shoot other = (Shoot) obj;
    return other.enemy.id == this.enemy.id;
  }

}
