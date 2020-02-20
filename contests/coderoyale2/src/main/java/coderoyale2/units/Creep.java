package coderoyale2.units;

import coderoyale2.Constants;
import coderoyale2.Player;
import coderoyale2.structures.Structure;

public class Creep extends Unit {
  public static final int KNIGHT = 0;
  public static final int ARCHER = 1;
  public static final int GIANT = 2;

  private int type;
  public int attackRange;
  public int damage;
  public int speed;
  
  public Creep(int owner) {
    super(owner);
  }

  public void setType(int type) {
    this.type = type;
    
    if (type == KNIGHT) {
      attackRange = 0;
      damage = 1;
      speed = 100;
      health = 30;
      radius = 20;
      mass = 4;
    } else if (type == ARCHER) {
      attackRange = 200;
      damage = 2; // TODO giant ?
      speed = 75;
      health = 45;
      radius = 25;
      mass = 9;
    } else if (type == GIANT) {
      attackRange = 0;
      damage = 80;
      speed = 50;
      health = 200;
      radius = 40;
      mass = 20;
    }
  }
  public void backup() {
    super.backup();
  }
  public void restore() {
    super.restore();
  }

  public void move(double frame) {
    if (type == Structure.KNIGHT) {
      moveKnightCreep(frame);
    } else {
      throw new RuntimeException("move other creeps");
    }
  }

  // TODO resolve the few pixels off ...
  private void moveKnightCreep(double frame) {
    Queen enemyQueen = Player.queens[1-owner];
    
    // move toward enemy queen, if not yet in range
    if (location.distanceTo(enemyQueen.location) > radius + enemyQueen.radius + attackRange) {
      double vx, vy;
      vx = enemyQueen.location.x - location.x;
      vy = enemyQueen.location.y - location.y;
      // resize to 3.0
      double length = Math.sqrt(vx*vx+vy*vy);
      
      double vcoeff;
      if (speed * frame > length) {
      } else { 
        vcoeff = (speed * frame) / length; 
        vx = vcoeff * vx;
        vy = vcoeff * vy;
      }
      
      location.x = location.x + vx;
      location.y = location.y + vy;
    }
    
  }

  public void dealDamage() {
    if (type == KNIGHT) {
      dealKnightDamage();
    } else {
      throw new RuntimeException("dealDamage for other");
    }
  }

  private void dealKnightDamage() {
    Queen enemyQueen = Player.queens[1-owner];

    if (location.distanceTo(enemyQueen.location) < radius + enemyQueen.radius + attackRange + Constants.TOUCHING_DELTA) {
      enemyQueen.health -= Constants.KNIGHT_DAMAGE;
    }    
  }

  public void damage(int d) {
    health = health-d;
  }

}
