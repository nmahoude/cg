package fantasticBitsMulti.simulation;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.ag.AG;
import fantasticBitsMulti.ag.AGSolution;
import fantasticBitsMulti.units.EntityType;
import fantasticBitsMulti.units.Snaffle;
import fantasticBitsMulti.units.Unit;

public class Simulation {
  public static int depth = 0;
  private static final int COLLISION_SIZE = 100000;
  public static int collisionsCacheFE = 0;
  public static Collision[] collisionsCache;
  static int collisionsFE = 0;
  static Collision[] collisions;
  static int tempCollisionsFE = 0;
  static Collision[] tempCollisions;

  public static int smyMana;
  public static int smyScore;
  public static int shisScore;
  

  static Collision fake = new Collision();
  static {
    fake.t = 1000.0;

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

  public static void dummies() {
    if (Player.hisWizard1.snaffle != null) {
      Player.hisWizard1.snaffle.thrust(500.0, Player.hisGoal.x, Player.hisGoal.y, Player.hisWizard1.position.distTo(Player.hisGoal));
    } else {
      Snaffle target = null;
      double targetD = Double.MAX_VALUE;
      double d;

      for (int i = 0; i < Player.snafflesFE; ++i) {
        Snaffle snaffle = Player.snaffles[i];

        if (!snaffle.dead) {
          d = Player.hisWizard1.position.squareDistance(snaffle.position);

          if (d < targetD) {
            targetD = d;
            target = snaffle;
          }
        }
      }

      if (target != null) {
        Player.hisWizard1.thrust(150.0, target.position.x, target.position.y, Math.sqrt(targetD));
      }
    }

    if (Player.hisWizard2.snaffle != null) {
      Player.hisWizard2.snaffle.thrust(500.0, Player.hisGoal.x, Player.hisGoal.y, Player.hisWizard2.position.squareDistance(Player.hisGoal));
    } else {
      Snaffle target = null;
      double targetD = Double.MAX_VALUE;
      double d;

      for (int i = 0; i < Player.snafflesFE; ++i) {
        Snaffle snaffle = Player.snaffles[i];

        if (!snaffle.dead) {
          d = Player.hisWizard2.position.squareDistance(snaffle.position);

          if (d < targetD) {
            targetD = d;
            target = snaffle;
          }
        }
      }

      if (target != null) {
        Player.hisWizard2.thrust(150.0, target.position.x, target.position.y, Math.sqrt(targetD));
      }
    }
  }
  private static double eval() {
    double energy = 0;

    energy -= distanceToClosestSnaffle();
    energy -= snafflesNearOppGoal();
    //energy += snaffleAvgPosition();
    //energy += wizardDistanceToSnaffles();
    energy += distanceBetweenMyWizards();
    
    // score! 
    energy += 1_000_000*(Player.myScore-Player.hisScore);
    energy += Player.myMana * 200;
    
//    energy += 500*(
//        + (Player.myWizard1.snaffle != null ? 1 : 0)
//        + (Player.myWizard2.snaffle != null ? 1 : 0)
//        - (Player.hisWizard1.snaffle != null ? 1 : 0)
//        - (Player.myWizard2.snaffle != null ? 1 : 0)
//        );
//        
    // last one !
    if (Player.myScore >= Player.victory) {
      energy = Double.POSITIVE_INFINITY;
    }
    // ------------------------
    return energy;
  }

  private static double snafflesNearOppGoal() {
    double dist = 0;
    for (int i=0;i<Player.snafflesFE;i++) {
      Snaffle snaffle = Player.snaffles[i];
      dist +=snaffle.position.distTo(Player.myGoal);
    }
    return dist;
  }

  private static double distanceToClosestSnaffle() {
    Snaffle closest1 = null, closest2 = null;
    double bestDist1 = Double.POSITIVE_INFINITY, bestDist2 = Double.POSITIVE_INFINITY;
    
    for (int i=0;i<Player.snafflesFE;i++) {
      Snaffle snaffle = Player.snaffles[i];
      double dist1 = Player.myWizard1.position.distTo(snaffle.position);
      double dist2 = Player.myWizard2.position.distTo(snaffle.position);
      if (dist1 < bestDist1) { bestDist1 = dist1; closest1 = snaffle; }
      if (dist2 < bestDist2) { bestDist2 = dist2; closest2 = snaffle; }
    }
    return bestDist1 + bestDist2;
  }

  private static double distanceBetweenMyWizards() {
    double distBetweenWizards = Player.myWizard1.position.distTo(Player.myWizard2.position);
    return 0.01*distBetweenWizards ;
  }

  private static double wizardDistanceToSnaffles(double energy) {
    double wizardAvgDist = 0;
    for (int i=0;i<Player.snafflesFE;i++) {
      wizardAvgDist += Player.myWizard1.position.distTo(Player.snaffles[i].position);
      wizardAvgDist += Player.myWizard2.position.distTo(Player.snaffles[i].position);
    }
    energy -= wizardAvgDist / (2 * 16_000 *Player.snafflesFE) ;
    return energy;
  }

  private static double snaffleAvgPosition(double energy) {
    double avgPos = 0;
    for (int i=0;i<Player.snafflesFE;i++) {
      avgPos+=Player.snaffles[i].position.distTo(Player.myGoal);
    }
    energy -= avgPos / (16_000 *Player.snafflesFE) ;
    return energy;
  }
  
  public static void reset() {
    for (int i = 0; i < Player.unitsFE; ++i) {
      Player.units[i].reset();
    }

    for (int i = 0; i < 16; ++i) {
      Player.spells[i].reset();
    }

    Player.myMana = smyMana;
    Player.myScore = smyScore;
    Player.hisScore = shisScore;
  }


  public static void simulate(AGSolution solution) {
    Player.energy = 0;
    depth = 0;

    Player.myWizard1.apply(solution, 0, 1);
    Player.myWizard2.apply(solution, 0, 2);
    dummies();

    play();
    depth = 1;

    solution.energy = eval() * 0.1;

    for (int i = 1; i < AG.DEPTH; ++i) {
      Player.myWizard1.apply(solution, i, 1);
      Player.myWizard2.apply(solution, i, 2);
      dummies();

      play();
      depth += 1;
    }

    solution.energy += eval();

    reset();
  }

  public static void play() {
    for (int i = 0; i < 4; ++i) {
      Player.spells[i].apply();
    }

    Player.bludgers[0].play();
    Player.bludgers[1].play();
    Player.wizards[0].play();
    Player.wizards[1].play();
    Player.wizards[2].play();
    Player.wizards[3].play();

    for (int i = 5; i < 16; ++i) {
      Player.spells[i].apply();
    }

    move();

    for (int i = 0; i < Player.unitsFE; ++i) {
      Player.units[i].end();
    }

    if (Player.myMana != 100) {
      Player.myMana += 1;
    }

  }
  private static void move() {
    double t = 0.0;
    double delta;

    Collision next = fake;
    collisionsCacheFE = 0;
    collisionsFE = 0;
    tempCollisionsFE = 0;

    Collision col;
    Unit a;
    Unit b;
    Unit u;
    int i, j;

    // Get first collisions
    for (i = 0; i < Player.unitsFE; ++i) {
      a = Player.units[i];

      col = a.collision(t);

      if (col != null) {
        collisions[collisionsFE++] = col;

        if (col.t < next.t) {
          next = col;
        }
      }

      for (j = i + 1; j < Player.unitsFE; ++j) {
        b = Player.units[j];

        if (a.can(b)) {
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
      if (next == fake) { // no collision found
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

        if (next.dir != 0) {
          next.a.bounce(next.dir);
        } else {
          next.a.bounce(next.b);
        }

        a = next.a;
        b = next.b;

        // Invalid previous collisions for the concerned units and get new ones
        next = fake;

        for (i = 0; i < collisionsFE; ++i) {
          col = collisions[i];

          if (!mustErase(col, a, b)) {
            if (col.t < next.t) {
              next = col;
            }

            tempCollisions[tempCollisionsFE++] = col;
          }
        }

        Collision[] temp = tempCollisions;
        tempCollisions = collisions;
        collisions = temp;

        collisionsFE = tempCollisionsFE;
        tempCollisionsFE = 0;

        // Find new collisions for a
        col = a.collision(t);
        if (col != null) {
          //System.err.println("Found a new collision with walls at "+col.t);
          collisions[collisionsFE++] = col;

          if (col.t < next.t) {
            next = col;
          }
        }

        for (i = 0; i < Player.unitsFE; ++i) {
          u = Player.units[i];

          if (a.id != u.id && a.can(u)) {
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
          col = b.collision(t);

          if (col != null) {
            collisions[collisionsFE++] = col;

            if (col.t < next.t) {
              next = col;
            }
          }

          for (i = 0; i < Player.unitsFE; ++i) {
            u = Player.units[i];

            if (b.id != u.id && b.can(u)) {
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
  
  private static boolean mustErase(Collision col, Unit a, Unit b) {
    if (a.id == col.a.id) {
      return true;
    }

    if (b != null && col.b != null) {
      if (a.id == col.b.id
        || b.id == col.a.id
        || b.id == col.b.id) {
        return true;
      }
    } else if (b != null) {
      if (b.id == col.a.id) {
        return true;
      }
    } else if (col.b != null) {
      if (a.id == col.a.id) {
        return true;
      }
    }
    return false;
  }

  public static void save() {
    smyMana = Player.myMana;
    smyScore = Player.myScore;
    shisScore = Player.hisScore;
  }
}
