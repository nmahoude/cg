package meanmax.simulation;

import meanmax.Game;
import meanmax.entities.Entity;
import meanmax.entities.Reaper;

public class BeamEvaluator {
  public static final int ANGLE_COUNT = 8;
  
  Collision collision = new Collision();
  
  public int evaluate() {
    Reaper reaper = Game.players[0].reaper;
    double oldvx = reaper.speed.vx;
    double oldvy = reaper.speed.vy;
    
    int freeSpots = 0;
    for (int angle=0;angle<ANGLE_COUNT;angle++) {
      reaper.speed.vx = 1000.0* Math.cos(2.0 * Math.PI * angle / ANGLE_COUNT);
      reaper.speed.vy = 1000.0* Math.sin(2.0 * Math.PI * angle / ANGLE_COUNT);
      
      boolean collide= false;
      for (int i=0;i<Game.entities_FE;i++) {
        Entity u = Game.entities[i];
        if (u == reaper) continue;
        if (u.dead) continue;
        if (!u.collider) continue;
        
        Collision col = reaper.getCollisionWithEntity(u, collision, 1.0);
        if (col.t < 1.0) {
          collide = true;
        }
      }
      if (!collide) {
        freeSpots++;
      } else {
      }
    }
    reaper.speed.vx = oldvx;
    reaper.speed.vy = oldvy;
    
    return freeSpots;
  }
}
