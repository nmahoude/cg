package meanmax.simulation;

import meanmax.Game;
import meanmax.Player;
import meanmax.entities.Doof;
import meanmax.entities.Entity;
import meanmax.entities.SkillEffect;
import meanmax.entities.Tanker;
import meanmax.entities.Wreck;

public class Simulation {
  public static int COLLISION_CACHE = 150;
  
  private static final int MAX_COLLISION = 150;
  public static Collision[] collisionsCache;
  public static int collisionsCacheFE;
  public static Collision[] tempCollisions;
  public static int tempCollisionsFE = 0;
  
  private static Collision collisions[];
  public static int collisionsFE = 0;

  static {
    collisionsCacheFE = 0;
    collisionsCache = new Collision[COLLISION_CACHE];
    for (int i=0;i<COLLISION_CACHE;i++) {
      collisionsCache[i] = new Collision();
    }
    
    collisions = new Collision[MAX_COLLISION];
    tempCollisions= new Collision[MAX_COLLISION];
  }
  
  public void simulate(Action actions[]) {
    applySol(actions);
    simulate();
  }
  
  private void applySol(Action actions[]) {
    for (int i=0;i<3*3;i++) {
      int playerIndex = (int)(i / 3);
      Entity entity = Game.entities[i];
      Action action = actions[i];
      
      if (action.thrust >= 0 ) {
        entity.wantedThrustTarget = action.target;
        entity.wantedThrustPower = action.thrust;
      } else {
        entity.wantedThrustPower = 0;
        
        if (entity.type == Game.REAPER) {
          if (Game.players[playerIndex].rage >= 30) {
            Game.players[playerIndex].rage -= 30;
            SkillEffect effect = Game.skillEffects[Game.skillEffects_FE++];
            Game.entities[Game.entities_FE++] = effect;
            effect.type = Game.SKILL_EFFECT_TAR;
            effect.duration = 3;
            effect.dead = false;
            effect.position.copyFrom(action.target);
          } 
        } else if (entity.type == Game.DESTROYER) {
          if (Game.players[playerIndex].rage >= 60) {
            Game.players[playerIndex].rage -= 60;
            SkillEffect effect = Game.skillEffects[Game.skillEffects_FE++];
            Game.entities[Game.entities_FE++] = effect;
            effect.type = Game.SKILL_EFFECT_GRENADE;
            effect.duration = 1;
            effect.dead = false;
            effect.position.copyFrom(action.target);
          } 
        } else if (entity.type == Game.DOOF) {
          if (Game.players[playerIndex].rage >= 30) {
            Game.players[playerIndex].rage -= 30;
            SkillEffect effect = Game.skillEffects[Game.skillEffects_FE++];
            Game.entities[Game.entities_FE++] = effect;
            Game.seDoofs[Game.seDoofs_FE++] = effect;
            effect.type = Game.SKILL_EFFECT_OIL;
            effect.duration = 3;
            effect.dead = false;
            effect.position.copyFrom(action.target);
          }
        } else {
          throw new RuntimeException("unknown entity type : "+entity.type);
        }
      }
    }
  }

  public void simulate() {
    applySkillEffects();
    applyTankerThrust();
    applyLootersThrust();

    resolveCollisions();

    updateTankers();
    harvestWater();
    adjustRoundsAndFriction();
    generateRage();
    restoreMasses();
  }

  private void resolveCollisions() {
    collisionsCacheFE = 0;
    collisionsFE = 0;
    
    double t = 0.0;
    Collision collision = getFirstCollision();
    while (collision.t + t <= 1.0) {
      double delta = collision.t;
      if (delta > 0.0) {
        for (int i=0;i<Game.entities_FE;i++) {
          Entity entity = Game.entities[i];
          if (!entity.dead && entity.collider) entity.move(delta);
        }
        t += collision.t;
      }

      playCollision(collision);

      Collision nextCollision = evictEntitiesCollisions(collision, delta);
      if (!collision.a.dead) {
        nextCollision = getNextCollision(nextCollision, collision.a, 1.0-t);
      }
      if (collision.b != null && !collision.b.dead) {
        nextCollision = getNextCollision(nextCollision, collision.b, 1.0-t);
      }
      collision = nextCollision;
    }

    // No more collision. Move units until the end of the round
    double delta = 1.0 - t;
    for (int i=0;i<Game.entities_FE;i++) {
      Entity entity = Game.entities[i];
      if (!entity.dead) entity.move(delta);
    }
  }

  private void applyLootersThrust() {
    for (int i=0;i<9;i++) {
      Entity looter = Game.entities[i];
      if (looter.wantedThrustPower != 0) {
        looter.thrust(looter.wantedThrustTarget, looter.wantedThrustPower);
      }
    }
  }

  private void applyTankerThrust() {
    for (int i=0;i<Game.tankers_FE;i++) {
      Tanker t = Game.tankers[i];
      if (!t.dead) t.play();
    }
  }

  private void applySkillEffects() {
    for (int i=0;i<Game.skillEffects_FE;i++) {
      SkillEffect effect = Game.skillEffects[i];
      if (!effect.dead) effect.apply();
    }
  }

  private void updateTankers() {
    for (int i=0;i<Game.tankers_FE;i++) {
      Tanker tanker = Game.tankers[i];
      if (tanker.dead) continue;
      double distance2 = tanker.distance2(Game.WATERTOWN);
      boolean full = tanker.isFull();

      if (distance2 <= Game.WATERTOWN_RADIUS_2 && !full) {
        // A non full tanker in watertown collect some water
        tanker.water += 1;
        tanker.mass += Tanker.TANKER_MASS_BY_WATER;
      } else if (distance2 >= (Tanker.TANKER_SPAWN_RADIUS + tanker.radius)*(Tanker.TANKER_SPAWN_RADIUS + tanker.radius) && full) {
        // Remove too far away and not full tankers from the game
        tanker.dead = true;
      }
    }
  }

  private void harvestWater() {
    for (int pindex=0;pindex<3;pindex++) {
      Player p = Game.players[pindex];
      if (p.reaper.isInDoofSkill()) continue;
      for (int i=0;i<Game.wrecks_FE;i++) {
        Wreck wreck = Game.wrecks[i];
        if (wreck.dead) continue;
        wreck.harvest(p);
      }
    }
  }

  private void restoreMasses() {
    for (int i=0;i<Game.entities_FE;i++) {
      Entity entity = Game.entities[i];
      while (entity.mass >= Game.REAPER_SKILL_MASS_BONUS) {
        entity.mass -= Game.REAPER_SKILL_MASS_BONUS;
      }
    }
  }

  private void generateRage() {
    for (int i=0;i<3;i++) {
      Player player = Game.players[i];
      player.rage = Math.min(Doof.MAX_RAGE, player.rage + player.doof.sing());
    }
  }
  
  private void playCollision(Collision collision) {
    if (collision.b == null) {
      // Bounce with border
      collision.a.bounce();
    } else {
      Tanker deadTanker = collision.getDeadTankerInCollision();

      if (deadTanker != null) {
        // A destroyer kill a tanker
        deadTanker.die();
      } else {
        // Bounce between two units
        collision.a.bounce(collision.b);
      }
    }
  }

  protected void adjustRoundsAndFriction() {
    for (int i=0;i<Game.entities_FE;i++) {
      Entity entity = Game.entities[i];
      if (entity.dead) continue;
      entity.adjust();
    }
  }

  Collision getFirstCollision() {
    Collision collision;
    Collision result = Collision.NO_COLLISION;
    collisionsFE = 0;
    
    Collision nextCollision = collisionsCache[collisionsCacheFE];
    for (int i = 0; i < Game.entities_FE; ++i) {
      
      Entity entity = Game.entities[i];
      if (!entity.collider || entity.dead) continue;
      // Test collision with map border first
      collision = entity.getCollisionWithWall(nextCollision);
      if (collision != Collision.NO_COLLISION) {
        nextCollision = collisionsCache[++collisionsCacheFE];
        collisions[collisionsFE++] = collision;
        if (collision.t < result.t) {
          result = collision;
        }
      }

      for (int j = i + 1; j < Game.entities_FE; ++j) {
        Entity b = Game.entities[j];
        if (!b.collider || b.dead) continue;
        
        collision = entity.getCollisionWithEntity(b, nextCollision, 1.0);
        if (collision != Collision.NO_COLLISION) {
          nextCollision = collisionsCache[++collisionsCacheFE];
          collisions[collisionsFE++] = collision;
          if (collision.t < result.t) {
            result = collision;
          }
        }
      }
    }
    
    return result;
  }

  private final Collision getNextCollision(Collision currentNext, Entity entity, double remainingTime) {
    Collision collision;
    Collision nextCollision = collisionsCache[collisionsCacheFE];
    
    // Test collision with map border first
    collision = entity.getCollisionWithWall(nextCollision);
    if (collision != Collision.NO_COLLISION) {
      nextCollision = collisionsCache[++collisionsCacheFE];
      collisions[collisionsFE++] = collision;
      if (collision.t < currentNext.t) {
        currentNext = collision;
      }
    }

    for (int j = 0; j < Game.entities_FE; ++j) {
      Entity b = Game.entities[j];
      if (b == entity) continue;
      if (!b.collider || b.dead) continue;
      
      collision = entity.getCollisionWithEntity(b, nextCollision, remainingTime);
      if (collision != Collision.NO_COLLISION) {
        nextCollision = collisionsCache[++collisionsCacheFE];
        collisions[collisionsFE++] = collision;
        if (collision.t < currentNext.t) {
          currentNext = collision;
        }
      }
    }
    return currentNext;
  }

  private final Collision evictEntitiesCollisions(Collision result, double minusT) {
    Collision next = Collision.NO_COLLISION;
    tempCollisionsFE = 0;
    for (int i=0;i<collisionsFE;i++) {
      Collision collision = collisions[i];
      if (result.a == collision.a || result.a == collision.b) continue;
      if (result.b != null && (result.b == collision.a || result.b == collision.b)) continue;
      
      tempCollisions[tempCollisionsFE++] = collision;
      collision.t -=minusT; // remove the time of the current collision
      if (collision.t < next.t) {
        next = collision;
      }
    }
    Collision[] temp = collisions;
    collisions = tempCollisions;
    collisionsFE = tempCollisionsFE;
    tempCollisions = temp;
    return next;
  }
}
