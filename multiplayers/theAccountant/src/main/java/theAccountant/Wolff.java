package theAccountant;


public class Wolff extends Movable{
  static final int WOLFF_MOVE = 1000;
  int score;
  
  public Wolff(GameEngine engine) {
    super(engine, 1000);
  }

  int getPotentialDamage(Enemy enemy) {
    double x = p.distTo(enemy.p);
    return (int)Math.round(125_000 / Math.pow(x, 1.2));
  }
  void damage(Enemy enemy) {

    enemy.lifePoints -= getPotentialDamage(enemy);
    if (enemy.lifePoints <= 0) {
      enemy.lifePoints = 0;
      gameEngine.removeEnemy(enemy);
    }
  }
  public void shoot(int id) {
    Enemy enemy = gameEngine.findEnemyById(id);
    damage(enemy);
  }

  public Wolff duplicate(GameEngine newEngine) {
    Wolff w = new Wolff(newEngine);
    w.p = p;
    w.score = score;
    return w;
  }

}
