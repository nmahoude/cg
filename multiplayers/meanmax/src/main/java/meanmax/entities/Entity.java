package meanmax.entities;

import java.util.Scanner;

import meanmax.Game;
import meanmax.simulation.Collision;
import trigo.Position;
import trigo.Speed;

public class Entity {
  static double EPSILON = 0.00001;
  static double MIN_IMPULSE = 30.0;
  static double IMPULSE_COEFF = 0.5;
  
  public int type;
  public int unitId;
  
  public Position position = new Position();
  public Speed speed = new Speed();
  public double radius = 400.0;
  public double mass;
  public double friction;
  public boolean dead = true;

  private Position b_position = new Position();
  private Speed b_speed = new Speed();
  private double b_radius;
  private double b_mass;
  private double b_friction;
  private boolean b_dead = false;

  public void backup() {
    b_position.copyFrom(position);
    b_speed.copyFrom(speed);
    
    b_radius = radius;
    b_mass = mass;
    b_friction = friction;
    b_dead = dead;
  }
  public void restore() {
    position.copyFrom(b_position);
    speed.copyFrom(b_speed);
    radius = b_radius;
    mass = b_mass;
    friction = b_friction;
    dead = b_dead;
  }
  
  /** movemement asked **/
  public Position wantedThrustTarget = null;
  public int wantedThrustPower = 0;
  public final boolean collider;

  public Entity(int type, double mass, double friction, boolean collider) {
    
    this.mass = mass;
    this.friction = friction;
    this.position.x = 0;
    this.position.y = 0;
    this.type = type;
    this.collider = collider;
  }
  
  public void read(Scanner in) {
    dead = false;
    mass = in.nextFloat();
    radius = in.nextInt();
    position.x = in.nextInt();
    position.y = in.nextInt();
    speed.vx = in.nextInt();
    speed.vy = in.nextInt();
    int extra = in.nextInt();
    int extra2 = in.nextInt();
    readExtra(extra, extra2);

    if (Game.DEBUG_INPUT) {
      System.err.println(""+mass+ "," + radius+ ","+position.x+","+position.y+","+speed.vx+","+speed.vy+","+extra+","+extra2+");");
    }
    backup();
  }

  void readExtra(int extra, int extra2) {
  }

  public final Collision getCollisionWithWall(Collision collision) {
    // no collision with wall for tankers
    if (type == Game.TANKER) {
      return Collision.NO_COLLISION;
    }
    
    // Search collision with map border
    // Resolving: sqrt((x + t*vx)^2 + (y + t*vy)^2) = MAP_RADIUS - radius <=>
    // t^2*(vx^2 + vy^2) + t*2*(x*vx + y*vy) + x^2 + y^2 - (MAP_RADIUS -
    // radius)^2 = 0
    // at^2 + bt + c = 0;
    // a = vx^2 + vy^2
    // b = 2*(x*vx + y*vy)
    // c = x^2 + y^2 - (MAP_RADIUS - radius)^2
    
    double a = speed.length2();
    if (a <= 0.0) {
      return Collision.NO_COLLISION;
    }

    double b = 2.0 * (position.x * speed.vx + position.y * speed.vy);
    double c = position.length2() - (Game.MAP_RADIUS - radius) * (Game.MAP_RADIUS - radius);
    double delta = b * b - 4.0 * a * c;

    if (delta <= 0.0) {
      return Collision.NO_COLLISION;
    }

    double t = (-b + Math.sqrt(delta)) / (2.0 * a);

    if (t <= 0.0 || t>1.0) {
      return Collision.NO_COLLISION;
    }

    collision.update(t, this);
    return collision;
  }
  
  public final Collision getCollisionWithEntity(Entity u, Collision collision, double remainingTime) {

    // quick check for evicting entities : projection of move shadow
    double posx1Min = position.x-radius + Math.min(0 , remainingTime*speed.vx);
    double posx2Max = u.position.x+u.radius + Math.max(0 , remainingTime*u.speed.vx);
    if (posx1Min > posx2Max) return Collision.NO_COLLISION;

    double posx1Max = position.x+radius + Math.max(0, remainingTime*speed.vx);
    double posx2Min = u.position.x-u.radius + Math.min(0 , remainingTime*u.speed.vx);
    if (posx2Min > posx1Max) return Collision.NO_COLLISION;

    double posy1Min = position.y-radius + Math.min(0, remainingTime*speed.vy);
    double posy2Max = u.position.y+u.radius + Math.max(0, remainingTime*u.speed.vy);
    if (posy1Min > posy2Max) return Collision.NO_COLLISION;

    double posy1Max = position.y+radius + Math.max(0, remainingTime*speed.vy);
    double posy2Min = u.position.y-u.radius + Math.min(0, remainingTime*u.speed.vy);
    if (posy2Min > posy1Max) return Collision.NO_COLLISION;

    // Check instant collision
    if ((u.position.x-position.x)*(u.position.x-position.x) + (u.position.y-position.y)*(u.position.y-position.y) 
        <= (radius + u.radius)*(radius + u.radius)) {
      collision.update(0.0, this, u);
      return collision;
    }

    // Change referencial
    // Unit u is now at point (0, 0) with a speed vector of (0, 0)
    double x2 = position.x - u.position.x;
    double y2 = position.y - u.position.y;
    double r2 = radius + u.radius;
    double vx2 = speed.vx - u.speed.vx;
    double vy2 = speed.vy - u.speed.vy;

    // Resolving: sqrt((x + t*vx)^2 + (y + t*vy)^2) = radius <=> t^2*(vx^2 +
    // vy^2) + t*2*(x*vx + y*vy) + x^2 + y^2 - radius^2 = 0
    // at^2 + bt + c = 0;
    // a = vx^2 + vy^2
    // b = 2*(x*vx + y*vy)
    // c = x^2 + y^2 - radius^2

    double a = vx2 * vx2 + vy2 * vy2;

    if (a <= 0.0) {
      // motionless or // speed
      return Collision.NO_COLLISION;
    }

    double b = 2.0 * (x2 * vx2 + y2 * vy2);
    double c = x2 * x2 + y2 * y2 - r2 * r2;
    double delta = b * b - 4.0 * a * c;

    // quick check for evicting entities : Opposite speed won't make them collide
    if (delta < 0.0 || b > 0) {
      return Collision.NO_COLLISION;
    }

    double t = (-b - Math.sqrt(delta)) / (2.0 * a);

    if (t <= 0.0 || t > remainingTime) {
      return Collision.NO_COLLISION;
    }

    collision.update(t, this, u);
    return collision;
  }
  
//Bounce between 2 units
  public final void bounce(Entity u) {
    double mcoeff = (mass + u.mass) / (mass * u.mass);
    double nx = position.x - u.position.x;
    double ny = position.y - u.position.y;
    double nxnysquare = nx * nx + ny * ny;
    double dvx = speed.vx - u.speed.vx;
    double dvy = speed.vy - u.speed.vy;
    double product = (nx * dvx + ny * dvy) / (nxnysquare * mcoeff);
    double fx = nx * product;
    double fy = ny * product;
    double m1c = 1.0 / mass;
    double m2c = 1.0 / u.mass;

    speed.vx -= fx * m1c;
    speed.vy -= fy * m1c;
    u.speed.vx += fx * m2c;
    u.speed.vy += fy * m2c;

    fx = fx * IMPULSE_COEFF;
    fy = fy * IMPULSE_COEFF;

    // Normalize vector at min or max impulse
    double impulse = Math.sqrt(fx * fx + fy * fy);
    double coeff = 1.0;
    if (impulse > EPSILON && impulse < MIN_IMPULSE) {
      coeff = MIN_IMPULSE / impulse;
    }

    fx = fx * coeff;
    fy = fy * coeff;

    speed.vx -= fx * m1c;
    speed.vy -= fy * m1c;
    u.speed.vx += fx * m2c;
    u.speed.vy += fy * m2c;

    double diff = (position.dist(u.position) - radius - u.radius) / 2.0;
    if (diff <= 0.0) {
      // Unit overlapping. Fix positions.
      moveTo(u.position, diff - EPSILON);
      u.moveTo(this.position, diff - EPSILON);
    }
  }

  // Bounce with the map border
  public final void bounce() {
    double mcoeff = 1.0 / mass;
    double nxnysquare = position.length2();
    double product = (position.x * speed.vx + position.y * speed.vy) / (nxnysquare * mcoeff);
    double fx = position.x * product;
    double fy = position.y * product;

    speed.vx -= fx * mcoeff;
    speed.vy -= fy * mcoeff;

    fx = fx * IMPULSE_COEFF;
    fy = fy * IMPULSE_COEFF;

    // Normalize vector at min or max impulse
    double impulse = Math.sqrt(fx * fx + fy * fy);
    double coeff = 1.0;
    if (impulse > EPSILON && impulse < MIN_IMPULSE) {
      coeff = MIN_IMPULSE / impulse;
    }

    fx = fx * coeff;
    fy = fy * coeff;
    speed.vx -= fx * mcoeff;
    speed.vy -= fy * mcoeff;

    double diff = position.dist(Game.WATERTOWN) + radius - Game.MAP_RADIUS;
    if (diff >= 0.0) {
      // Unit still outside of the map, reposition it
      moveTo(Game.WATERTOWN, diff + EPSILON);
    }
  }

  void moveTo(Position p, double distance) {
    double d = position.dist(p);

    if (d < EPSILON) {
      return;
    }

    double dx = p.x - position.x;
    double dy = p.y - position.y;
    double coef = distance / d;

    this.position.x += dx * coef;
    this.position.y += dy * coef;
  }
  
  public final double distance2(Entity e) {
    return (e.position.x-position.x)*(e.position.x-position.x) + (e.position.y-position.y)*(e.position.y-position.y);
  }
  
  public final double distance(Entity e) {
    return Math.sqrt((e.position.x-position.x)*(e.position.x-position.x) + (e.position.y-position.y)*(e.position.y-position.y));
  }
  public final double distance(Position p) {
    return Math.sqrt((p.x-position.x)*(p.x-position.x) + (p.y-position.y)*(p.y-position.y));
  }
  
  public void thrust(Position p, int power) {
    double distance = distance(p);

    if (Math.abs(distance) <= EPSILON) {
      return;
    }

    double coef = (((double) power) / mass) / distance;
    speed.vx += (p.x - this.position.x) * coef;
    speed.vy += (p.y - this.position.y) * coef;
  }

  public final void move(double t) {
    position.x += speed.vx * t;
    position.y += speed.vy * t;
  }

  static public final int round(double x) {
    if (x > 0) {
      return (int) (x + 0.5d);
    } else {
      return (int) (x - 0.5d);
    }
//    int s = x < 0 ? -1 : 1;
//    return s * (int) Math.round(s * x);
  }
  
  public void adjust() {
      position.x = position.x > 0 ? (int)( position.x + 0.5d) : (int)( position.x - 0.5d);
      position.y = position.y > 0 ? (int)( position.y + 0.5d) : (int)( position.y - 0.5d);

      if (isInDoofSkill()) {
        // No friction if we are in a doof skill effect
        speed.vx = speed.vx > 0 ? (int)( speed.vx + 0.5d) : (int)( speed.vx - 0.5d);
        speed.vy = speed.vy > 0 ? (int)( speed.vy + 0.5d) : (int)( speed.vy - 0.5d);
      } else {
        double newSpeedx = speed.vx * (1.0 - friction);
        double newSpeedy = speed.vy * (1.0 - friction);
        speed.vx = newSpeedx > 0 ? (int)( newSpeedx + 0.5d) : (int)( newSpeedx - 0.5d);
        speed.vy = newSpeedy > 0 ? (int)( newSpeedy + 0.5d) : (int)( newSpeedy - 0.5d);
      }
    }

  public boolean isInDoofSkill() {
    for (int i=0;i<Game.seDoofs_FE;i++) {
      Entity e = Game.seDoofs[i];
      if (e.dead) continue;
      if (isInRange(e, e.radius + radius)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isInTarSkill() {
    for (int i=0;i<Game.skillEffects_FE;i++) {
      Entity e = Game.skillEffects[i];
      if (e.dead) continue;
      if (e.type == Game.SKILL_EFFECT_TAR && isInRange(e, e.radius + radius)) {
        return true;
      }
    }
    return false;
  }


  public boolean isInRange(Entity e, double range) {
    return e != this && distance2(e) <= range * range;
  }
  
  @Override
  public String toString() {
    return String.format("id=%d", unitId);
  }

  public boolean isInWreck() {
    for(int i=0;i<Game.wrecks_FE;i++) {
      Wreck wreck = Game.wrecks[i];
      if (wreck.dead || wreck == this) continue;
      if (this.isInRange(wreck, wreck.radius)) return true;
    }
    return false;
  }
  
  
}
