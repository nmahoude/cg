package pokerChipRace.simulate;

import pokerChipRace.GameState;
import pokerChipRace.entities.Entity;
import trigonometry.Point;
import trigonometry.Vector;

public class Simulation {
  public static final double EPSILON = 0.01;
  public static int COLLISION_CACHE = 1000;
  
  private static Collision fake = Collision.buildFake();
  GameState state;
  
  public static Collision[] collisionsCache;
  public static int collisionsFE = 0;
  
  public Simulation(GameState state) {
    this.state = state;
    collisionsCache = new Collision[COLLISION_CACHE];
    for (int i=0;i<COLLISION_CACHE;i++) {
      collisionsCache[i] = new Collision();
    }
  }

  public void playTurn() {
    applyActions();
    move();
  }
  private void applyActions() {
    for (int i=0;i<state.allChips.length;i++) {
      Entity entity = state.allChips.elements[i];
      if (entity.owner == -1) break; // neutral entities don't have actions
      if (entity.targetx < 0) continue; // WAIT command
      if (entity.radius <=0) continue; // dead entity
      
      // calculate future radius
      Point centerPos = new Point(entity.x,entity.y);
      Point target = new Point(entity.targetx, entity.targety);
      Vector dir = target.sub(centerPos).normalize();
      
      Entity droplet = new Entity(state.allChips.length, -1);
      entity.eject(droplet, dir);
      state.allChips.add(droplet);
    }
  }

  public void move() {
    double t=0;
    Collision collision;
    Collision c;
    Collision next = fake;
    collisionsFE = 0;
    
    for (int i=0;i<state.allChips.length;i++) {
      Entity entity = state.allChips.elements[i];
      if (entity.radius <= 0) continue;
      
      collision = collisionsCache[collisionsFE];
      c = entity.collisionOnWall(t, collision);
      if ( c != null) {
        collisionsFE++;
        if( next.t > c.t) {
          next = c;
        }
      }

      for (int j=i+1;j<state.allChips.length;j++) {
        Entity other = state.allChips.elements[j];
        if (other.radius <= 0) continue;
  
        collision = collisionsCache[collisionsFE];
        c = entity.collision(other, t, collision);
        if ( c != null) {
          collisionsFE++;
          if( next.t > c.t) {
            next = c;
          }
        }
      }
    }
    
    while (t < 1.0) {
      if (next == fake) {
        for (int i=0;i<state.allChips.length;i++) {
          Entity entity = state.allChips.elements[i];
          if (entity.radius <= 0) continue;
          entity.move(1.0 - t);
        }
        break;
      } 
      
      // move all entities to the time of collision
      double delta = next.t - t;
      for (int i=0;i<state.allChips.length;i++) {
        Entity entity = state.allChips.elements[i];
        if (entity.radius <= 0) continue;
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
      collisionsFE = 0;
      next = fake;
      for (int i=0;i<state.allChips.length;i++) {
        Entity entity = state.allChips.elements[i];
        if (entity.radius <= 0) continue;
        
        collision = collisionsCache[collisionsFE];
        c = entity.collisionOnWall(t, collision);
        if ( c != null) {
          collisionsFE++;
          if( next.t > c.t) {
            next = c;
          }
        }

        for (int j=i+1;j<state.allChips.length;j++) {
          Entity other = state.allChips.elements[j];
          if (other.radius <= 0) continue;

          collision = collisionsCache[collisionsFE];
          c = entity.collision(other, t, collision);
          if ( c != null) {
            collisionsFE++;
            if( next.t > c.t) {
              next = c;
            }
          }
        }
      }
    }
  }
}
