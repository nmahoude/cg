package pokerChipRace.simulate;

import pokerChipRace.GameState;
import pokerChipRace.entities.Entity;
import trigonometry.Point;
import trigonometry.Vector;

public class Simulation {
  public static final double EPSILON = 0.01;
  public static int COLLISION_CACHE = 100_000;
  
  private static Collision fake = Collision.buildFake();
  GameState state;
  
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

  public void setGameState(GameState state) {
    this.state = state;
  }
  
  public void playTurn() {
    applyActions();
    move();
  }
  private void applyActions() {
    for (int i=0;i<state.entityFE;i++) {
      Entity entity = state.chips[i];
      if (entity.owner == -1) break; // neutral entities don't have actions
      if (entity.targetx < 0) continue; // WAIT command
      if (entity.radius <=0) continue; // dead entity
      
      // calculate future radius
      Point centerPos = new Point(entity.x,entity.y);
      Point target = new Point(entity.targetx, entity.targety);
      Vector dir = target.sub(centerPos).normalize();
      
      Entity droplet = state.getNewChip();
      entity.eject(droplet, dir);
    }
  }

  public void move() {
    double t=0;
    collisionsCacheFE = 0;
    collisionsFE = 0;
    Entity a, b;

    next = fake;
    for (int i=0;i<state.entityFE;i++) {
      Entity entity = state.chips[i];
      Collision c = collisionsCache[collisionsCacheFE];
      c = entity.collisionOnWall(t, c);
      if ( c != null) {
        collisions[collisionsFE++] = c;
        collisionsCacheFE++;
        if( next.t > c.t) {
          next = c;
        }
      }

      for (int j=i+1;j<state.entityFE;j++) {
        Entity other = state.chips[j];
        if (other.radius < 0) continue;
        if (other == entity ) continue;
        
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
        for (int i=0;i<state.entityFE;i++) {
          Entity entity = state.chips[i];
          if (entity.radius <= 0) continue;
          entity.move(1.0 - t);
        }
        break;
      } 
      
//      if (next.t < 0.0001) {
//        System.err.println("   @"+t+" collision : "+next);
//        next.a.debug();
//        if (next.b != null) {
//          next.b.debug();
//        }
//      }
      // move all entities to the time of collision
      double delta = next.t - t;
      for (int i=0;i<state.entityFE;i++) {
        Entity entity = state.chips[i];
        if (entity.radius < 0) continue;
        entity.move(delta);
      }

      t = next.t;
      
      // handle the collision
      if (next.dir != 0) {
        next.a.bounce(next.dir);
      } else {
        next.a.bounce(next.b);
      }

      // redo all the collision
      // TODO this is not perfomant
      
      a = next.a;
      b = next.b;
      next = fake;

      // get back all non a or b collisions (still valid)
      tempCollisionsFE = 0;
      for (int i=0;i<collisionsFE;i++) {
        Collision col = collisions[i];
        if (col.a == a || col.b == b || col.a == b || col.b == a) continue;
        tempCollisions[tempCollisionsFE++] = col;
        if (col.t < next.t) {
          next = col;
        }
      }
      //swap collisions
      collisions = tempCollisions;
      collisionsFE = tempCollisionsFE;

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

        for (int j=0;j<state.entityFE;j++) {
          Entity other = state.chips[j];
          if (other.radius < 0) continue;
          if (other == a ) continue;
          
          c = collisionsCache[collisionsCacheFE];
          c = a.collision(other, t, c);
          if ( c != null) {
            collisions[collisionsFE++] = c;
            collisionsCacheFE++;
            if( next.t > c.t) {
              next = c;
            }
          }
        }      }
      if (b != null && b.radius > 0) {
        Collision c = collisionsCache[collisionsCacheFE];
        c = b.collisionOnWall(t, c);
        if ( c != null) {
          collisions[collisionsFE++] = c;
          collisionsCacheFE++;
          if( next.t > c.t) {
            next = c;
          }
        }

        for (int j=0;j<state.entityFE;j++) {
          Entity other = state.chips[j];
          if (other.radius < 0) continue;
          if (other == b ) continue;
          
          c = collisionsCache[collisionsCacheFE];
          c = b.collision(other, t, c);
          if ( c != null) {
            collisions[collisionsFE++] = c;
            collisionsCacheFE++;
            if( next.t > c.t) {
              next = c;
            }
          }
        }      }
    }
  }

}
