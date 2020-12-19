package fantasticBitsMulti.simulation;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.units.EntityType;
import fantasticBitsMulti.units.Snaffle;
import fantasticBitsMulti.units.Unit;

public class Simulation {
  private static final int COLLISION_SIZE = 1000;
  public static int collisionsCacheFE = 0;
  public static Collision[] collisionsCache;
  
  static Collision[] collisions;
  static int collisionsFE = 0;

  static Collision[] tempCollisions;
  static int tempCollisionsFE = 0;

  static Collision NO_COLLISION = new Collision();
  static {
    NO_COLLISION.t = 1000.0;
    initCollisionsCache();
  }

  private static void initCollisionsCache() {
    collisionsCache = new Collision[COLLISION_SIZE];
    collisions      = new Collision[COLLISION_SIZE];
    tempCollisions  = new Collision[COLLISION_SIZE];
    for (int i = 0; i < COLLISION_SIZE; ++i) {
      collisionsCache[i] = new Collision();
    }
  }

  static Scorer scorer = new Scorer();
  
  // resolve one turn based on wizards actions
  public static void simulate(Action... actions) {
    for (int i=0;i<4;i++) {
      Player.wizards[i].apply(actions[i]);
    }
    
    play();
  }
  
  public static void play() {
    // update obliviate spells first
    for (int i = 0; i < 4; ++i) {
      Player.spells[i].apply();
    }

    // then update bludgers
    Player.bludgers[0].play();
    Player.bludgers[1].play();
    

    // then update all other spells
    for (int i = 4+1; i < 16; ++i) {
      Player.spells[i].apply();
    }

    // Resolve collision & do move
    move();

    // end of player move, round the speed & positions
    for (int i = 0; i < Player.unitsFE; ++i) {
      Player.units[i].end();
    }

    // update global mana
    if (Player.myMana != 100) {
      Player.myMana += 1;
    }

  }
  private static void move() {
    double t = 0.0;
    double delta;

    Collision next = NO_COLLISION;
    collisionsCacheFE = 0;
    collisionsFE = 0;
    tempCollisionsFE = 0;

    Collision col;
    Unit a;
    Unit b;
    Unit u;
    int i, j;

    // Get first collisions
    if (Player.DEBUG_SIM) {
      System.err.println("Will move: ");
      for (i = 0; i < Player.unitsFE; ++i) {
        System.err.println("    "+Player.units[i]);
      }
    }
    for (i = 0; i < Player.unitsFE; ++i) {
      a = Player.units[i];

      // collision contre les murs
      col = a.wallCollision(t);
      if (col != null) {
        collisions[collisionsFE++] = col;
        if (col.t < next.t) {
          next = col;
        }
      }

      // collision contre les autres units
      for (j = i + 1; j < Player.unitsFE; ++j) {
        b = Player.units[j];
        if (a.canCollide(b)) {
          col = a.collision(b, t);
          if (col != null) {
            collisions[collisionsFE++] = col;
            if (col.t < next.t) {
              next = col;
            }
          }
        }
      }
    }
    
    while (t < 1.0) {
      if (next == NO_COLLISION) { // no collision found
        for (i = 0; i < Player.unitsFE; ++i) {
          Player.units[i].move(1.0 - t);
        }

        break;
      } else {
        // Move to the collision time
        delta = next.t - t;
        for (i = 0; i < Player.unitsFE; ++i) {
          Player.units[i].move(delta);
        }

        t = next.t;
        //System.err.println("Advancing t to "+t + " collision is "+next.dir);

        next.a.bounce(next.b); // contre une autre entity

        a = next.a;
        b = next.b;

        // Invalid previous collisions for the concerned units and get new ones
        next = NO_COLLISION;

        for (i = 0; i < collisionsFE; ++i) {
          col = collisions[i];

          if (!mustErase(col, a, b)) {
            if (col.t < next.t) {
              next = col;
            }

            tempCollisions[tempCollisionsFE++] = col;
          }
        }

        // swap collision to avoid copying
        Collision[] temp = tempCollisions;
        tempCollisions = collisions;
        collisions = temp;

        collisionsFE = tempCollisionsFE;
        tempCollisionsFE = 0;

        // Find new collisions for a with walls
        col = a.wallCollision(t);
        if (col != null) {
          //System.err.println("Found a new collision with walls at "+col.t);
          collisions[collisionsFE++] = col;

          if (col.t < next.t) {
            next = col;
          }
        }

        // check other units
        for (i = 0; i < Player.unitsFE; ++i) {
          u = Player.units[i];

          if (a.id != u.id && a.canCollide(u)) {
            col = a.collision(u, t);

            if (col != null) {
              //System.err.println("Found a new collision w "+a.id+" with unit "+col.b.id+" at "+col.t);
              if (u.type == EntityType.SNAFFLE) {
                Snaffle snaffle = (Snaffle)u;
                //System.err.println("collision with snaffle. ==> "+snaffle.carrier);
              }
              collisions[collisionsFE++] = col;

              if (col.t < next.t) {
                next = col;
              }
            }
          }
        }

        // Find new collisions for b
        if (b != null) {
          col = b.wallCollision(t);

          if (col != null) {
            collisions[collisionsFE++] = col;

            if (col.t < next.t) {
              next = col;
            }
          }

          for (i = 0; i < Player.unitsFE; ++i) {
            u = Player.units[i];

            if (b.id != u.id && b.canCollide(u)) {
              col = b.collision(u, t);

              if (col != null) {
                collisions[collisionsFE++] = col;

                if (col.t < next.t) {
                  next = col;
                }
              }
            }
          }
        }
      }
    }
  }
  
  private static boolean mustErase(Collision col, Unit a, Unit b ) {
    // check if we are not wall and not a pole (cause we don't move) based on id (risky business)
    if (a.id < 20 && (a.id == col.a.id || a.id == col.b.id)) {
      return true;
    } else if (b.id < 20 && (b.id == col.a.id || b.id == col.b.id)) {
      return true;
    } else {
      return false;
    }
  }
}
