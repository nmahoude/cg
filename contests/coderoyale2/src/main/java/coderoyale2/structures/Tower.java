package coderoyale2.structures;

import coderoyale2.Constants;
import coderoyale2.units.Creep;
import coderoyale2.units.Queen;
import coderoyale2.units.Site;

public class Tower extends Structure {

  public int attackRadius;
  public int life;
  public double area;
  
  private int _attackRadius;
  private int _life;
  public double _area;

  public Tower(Site site) {
    super(site);
  }

  public void backup() {
    super.backup();
    _attackRadius = attackRadius;
    _life = life;
    _area = area;
  }
  public void restore() {
    super.restore();
    life = _life;
    attackRadius = _attackRadius;
    area = _area;
  }

  public void updateRadius() {
    area = Math.PI * attackRadius * attackRadius;
  }
  public void damageQueen(Site obstacle, Queen target) {
    double shotDistance = target.location.distanceTo(obstacle.location) - obstacle.radius;
    double differenceFromMax = attackRadius - shotDistance;
    int damage = (int)(Constants.TOWER_QUEEN_DAMAGE_MIN + (differenceFromMax / Constants.TOWER_QUEEN_DAMAGE_CLIMB_DISTANCE));
    target.damage(damage);    
  }

  public void damageCreep(Site site, Creep target) {
    double shotDistance = target.location.distanceTo(site.location) - site.radius;
    double differenceFromMax = attackRadius - shotDistance;
    int damage = (int)(Constants.TOWER_CREEP_DAMAGE_MIN + (differenceFromMax / Constants.TOWER_CREEP_DAMAGE_CLIMB_DISTANCE));
    target.damage(damage);    
  }
}
