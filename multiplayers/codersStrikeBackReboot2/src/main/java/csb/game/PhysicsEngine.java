package csb.game;

import csb.entities.CheckPoint;
import csb.entities.Entity;
import csb.entities.Pod;
import csb.entities.Type;

public class PhysicsEngine {
  public Pod pods[];
  public CheckPoint checkPoints[];

  public boolean collisionSimualtion = true;
  public static final int COLLISION_CACHE = 100;
  public Collision[] collisionCache = new Collision[COLLISION_CACHE];
  public Collision[] collisionNewCache = new Collision[COLLISION_CACHE];
  public int CollisionFE;
  /**
   * perfect simulation of the CG game Engine (w/r to movement, collision)
   * 
   * 
   * input : speed and direction of Pods are updated
   * output : new position of pods, direction, speed & new checkpoint updated
   */
  public void simulate() {
    Collision nextCollision;
    double t = 0.0;
    
    // 1st round of collision (full)
    nextCollision = Collision.noCollision;
    CollisionFE = 0;
    for (int i=0;i<4;i++) {
      Pod pod = pods[i];

      nextCollision = podCheckPointCollision(nextCollision, t, pod); 
     
      if (collisionSimualtion) {
        for (int j=i+1;j<pods.length;j++) {
          Collision collision = Entity.collision(pod, pods[j], t);
          if (collision != Collision.noCollision) {
            collisionCache[CollisionFE++] = collision;
          }
          if (collision != null && nextCollision.t > collision.t) {
            nextCollision = collision;
          }
        }
      }
    }    

    while(t < 1.0) {
      if (nextCollision == Collision.noCollision) {
        moveToEndOfSimulation(t);
        break;
      } else {
        double delta = nextCollision.t - t;
        for (int i=0;i<4;i++) {
          pods[i].move(delta);
        }

        t = nextCollision.t;
        if (nextCollision.b.type == Type.CHECKPOINT) {
          calculateNextCheckpoint(nextCollision);
          // disable this collision and find the next one in cache, collision with a checkpoint change nothing!
          nextCollision.t = 10;
          nextCollision = findNearestCollisionFromCache();
        } else { /* POD vs POD*/
          nextCollision.a.bounce(nextCollision.b);
          Pod a = (Pod)nextCollision.a;
          Pod b = (Pod)nextCollision.b;
          nextCollision = disableCollisions(a, b);
          // redo the collision with unit a & b
          nextCollision = podCheckPointCollision(nextCollision, t, a); 
          nextCollision = podCheckPointCollision(nextCollision, t, b); 
          if (collisionSimualtion) {
            // collision for a
            for (int j=0;j<pods.length;j++) {
              if (a.id == pods[j].id) continue; // don't test self  collision
              Collision collision = Entity.collision(a, pods[j], t);
              if (collision != Collision.noCollision) {
                collisionCache[CollisionFE++] = collision;
              }
              if (collision != null && nextCollision.t > collision.t) {
                nextCollision = collision;
              }
            }
            // collision for b
            for (int j=0;j<pods.length;j++) {
              if (b.id == pods[j].id || pods[j].id == a.id) continue; // don't test self  collision
              Collision collision = Entity.collision(a, pods[j], t);
              if (collision != Collision.noCollision) {
                collisionCache[CollisionFE++] = collision;
              }
              if (collision != null && nextCollision.t > collision.t) {
                nextCollision = collision;
              }
            }
          }
        }
      }
    }

    for (int i=0;i<4;i++) {
      pods[i].end();
    }
    // add timeout of the team of team leaders
    pods[0].team.timeout++;
    pods[2].team.timeout++;
  }
  
  private Collision disableCollisions(Entity a, Entity b) {
    Collision best = Collision.noCollision;
    int collisionNewFE=0;
    for (int i=0;i<CollisionFE;i++) {
      if (collisionCache[i].a != a && collisionCache[i].a != b 
          & collisionCache[i].b != a || collisionCache[i].b != b ) {
        collisionNewCache[collisionNewFE++] = collisionCache[i];
        if (best.t > collisionCache[i].t) {
          best = collisionCache[i];
        }
      }
    }
    collisionCache = collisionNewCache;
    CollisionFE = collisionNewFE;
    return best;
  }

  private Collision findNearestCollisionFromCache() {
    Collision best = Collision.noCollision;
    for (int i=0;i<CollisionFE;i++) {
      if (collisionCache[i].t < best.t) {
        best = collisionCache[i];
      }
    }
    return best;
  }
  
  private void moveToEndOfSimulation(double t) {
    double delta = 1.0 - t;
    for (Pod pod : pods) {
      pod.move(delta);
    }
  }
  private void calculateNextCheckpoint(Collision nextCollision) {
    Pod pod = (Pod) nextCollision.a;

    pod.team.timeout = 0;
    pod.nextCheckPointId++;
    if (pod.nextCheckPointId == checkPoints.length) {
      pod.nextCheckPointId = 0;
    }
    if (pod.nextCheckPointId == 1) {
      pod.lap++;
    }
  }

  private Collision podCheckPointCollision(Collision nextCollision, double t, Pod pod) {
    pod.radius = 1;// sale hack pour ne considerer que le centre du pod
    Collision collision = Entity.collision(pod, checkPoints[pod.nextCheckPointId], t);
    if (collision != Collision.noCollision) {
      collisionCache[CollisionFE++] = collision;
    }

    if (nextCollision.t > collision.t) {
      nextCollision = collision;
    }
    pod.radius = 400;
    return nextCollision;
  }
}
