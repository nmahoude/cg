package thales.physics;

import thales.Entity;
import thales.Player;
import thales.UFO;

public class Simulation {
  public static final double EPSILON = 0.01;
  public static int COLLISION_CACHE = 100_000;
  
  private static Collision fake = Collision.buildFake();
  
  public static Collision[] collisionsCache;
  public static Collision[] collisions;
  public static Collision[] tempCollisions;
  
  public static int collisionsCacheFE = 0;
  public static int collisionsFE = 0;
  public static int tempCollisionsFE = 0;
  private Collision next;
  
  
  public Simulation() {
    collisionsCache = new Collision[COLLISION_CACHE];
    collisions = new Collision[COLLISION_CACHE];
    tempCollisions = new Collision[COLLISION_CACHE];
    
    for (int i=0;i<COLLISION_CACHE;i++) {
      collisionsCache[i] = new Collision();
    }
  }

  public void playTurn() {
    move();
  }

  public void move() {
    Player.updateUFOs();
    
    double t=0;
    collisionsCacheFE = 0;
    collisionsFE = 0;
    Entity a, b;

    next = fake;
    for (int i=0;i<4;i++) {
      Entity entity = Player.entities[i];
      Collision c = collisionsCache[collisionsCacheFE];
      c = entity.collisionOnWall(t, c);
      if ( c != null) {
        collisions[collisionsFE++] = c;
        collisionsCacheFE++;
        if( next.t > c.t) {
          next = c;
        }
      }

      for (int j=i+1;j<6;j++) {
        Entity other = Player.entities[j];
        if (other.radius < 0) continue;
        if (other == entity ) continue;
        if (other.type == Entity.FLAG && other.myTeam == entity.myTeam) continue;
        
        c = collisionsCache[collisionsCacheFE];
        c = entity.collision(other, t, c);
        if ( c != null) {
          collisions[collisionsFE++] = c;
          collisionsCacheFE++;
          if( next.t > c.t) {
            next = c;
          }
        }
      }    
    }
    
    while (t < 1.0) {
      if (next == fake) {
        for (int i=0;i<4;i++) {
          Entity entity = Player.entities[i];
          if (entity.radius <= 0) continue;
          entity.move(1.0 - t);
          entity.end();
          ((UFO)entity).score();
        }
        break;
      } 
      
      // move all entities to the time of collision
      double delta = next.t - t;
      for (int i=0;i<4;i++) {
        Entity entity = Player.entities[i];
        if (entity.radius < 0) continue;
        entity.move(delta);
      }

      t = next.t;
      
      // handle the collision
      if (next.dir != 0) {
        next.a.bounce(next.dir);
      } else if (next.b.type == Entity.FLAG) {
        ((UFO)next.a).grabFlag();
      } else {
        checkToRemoveFlag(((UFO)next.a), ((UFO)next.b));
        next.a.bounce(next.b);
      }

      a = next.a;
      b = next.b;
      next = fake;

      // get back all non a or b collisions (still valid)
      tempCollisionsFE = 0;
      for (int i=0;i<collisionsFE;i++) {
        Collision col = collisions[i];
        if (col.a == a || col.b == b || col.a == b || col.b == a) continue;
        tempCollisions[tempCollisionsFE++] = col;
        //col.t -= delta;
        if (col.t < next.t) {
          next = col;
        }
      }
      //swap collisions
      Collision[] temp = collisions;
      collisions = tempCollisions;
      collisionsFE = tempCollisionsFE;
      tempCollisions = temp;

      if (a.radius > 0) {
        Collision c = collisionsCache[collisionsCacheFE];
        c = a.collisionOnWall(t, c);
        if ( c != null) {
          collisions[collisionsFE++] = c;
          collisionsCacheFE++;
          if( next.t > c.t) {
            next = c;
          }
        }

        for (int j=0;j<6;j++) {
          Entity other = Player.entities[j];
          if (other.radius < 0) continue;
          if (other == a ) continue;
          if (other.type == Entity.FLAG && other.myTeam == a.myTeam) continue;


          c = collisionsCache[collisionsCacheFE];
          c = a.collision(other, t, c);
          if ( c != null) {
            collisions[collisionsFE++] = c;
            collisionsCacheFE++;
            if( next.t > c.t) {
              next = c;
            }
          }
        }      
      }
      if (b != null && b.radius > 0 && b.type != Entity.FLAG) {
        Collision c = collisionsCache[collisionsCacheFE];
        c = b.collisionOnWall(t, c);
        if ( c != null) {
          collisions[collisionsFE++] = c;
          collisionsCacheFE++;
          if( next.t > c.t) {
            next = c;
          }
        }

        for (int j=0;j<6;j++) {
          Entity other = Player.entities[j];
          if (other.radius < 0) continue;
          if (other == b ) continue;
          if (other.type == Entity.FLAG && other.myTeam == b.myTeam) continue;

          c = collisionsCache[collisionsCacheFE];
          c = b.collision(other, t, c);
          if ( c != null) {
            collisions[collisionsFE++] = c;
            collisionsCacheFE++;
            if( next.t > c.t) {
              next = c;
            }
          }
        }     
      }
    }
  }

  private void checkToRemoveFlag(UFO a, UFO b) {
    if (!a.flag && !b.flag) return;
    
    double speed2_A = a.vx*a.vx + a.vy*a.vy;
    double speed2_B = b.vx*b.vx + b.vy*b.vy;
    if (speed2_A > speed2_B) {
      if (b.flag) b.removeFlag();
    } else {
      if (a.flag) a.removeFlag();
    }
  }

}

