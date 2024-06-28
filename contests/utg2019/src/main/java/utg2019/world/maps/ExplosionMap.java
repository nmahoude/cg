package utg2019.world.maps;

import trigonometryInt.Point;
import utg2019.world.MapCell;
import utg2019.world.World;
import utg2019.world.entity.Robot;

/** For each turn, recalculate potential explosions chains*/
public class ExplosionMap {
  private static final int MAX_EXPLOSIONS_IDS = 200;
  
  public int explosionIdsFE = 0;
  public int explosionsIdsMap[][] = new int[MAX_EXPLOSIONS_IDS][World.MAX_OFFSET]; // explosion id -> list of points in the explosion 
  public int explosionsIdsMapFE[] = new int[MAX_EXPLOSIONS_IDS];
  public int explosionPosIds[][] = new int[World.MAX_OFFSET][4];
  public int explosionPosIdsFE[] = new int[World.MAX_OFFSET];
  public int explosionsCentersOffsets[][] = new int[MAX_EXPLOSIONS_IDS][50];
  public int explosionsCentersOffsetsFE[] = new int[MAX_EXPLOSIONS_IDS];
  public int explosionsCentersCount[] = new int[MAX_EXPLOSIONS_IDS];
  
  private World world = new World();
  
  public void update(World model, TrapAdvisor trapAdvisor) {
    // prepartion, fill the world with potential traps
    world.copyFrom(model);
    for (int i=0;i<MAX_EXPLOSIONS_IDS;i++) {
      explosionsCentersOffsetsFE[i] = 0;
      explosionsCentersCount[i] = 0;
      explosionsIdsMapFE[i] = 0;
    }
    explosionIdsFE = 0;
    
    for (int y = 0; y < 15; y++) {
      for (int x = 0; x < 30; x++) {
        Point pos = Point.get(x, y);
        
        explosionPosIdsFE[pos.offset] = 0;
        
        if (trapAdvisor.isDangerous(pos)) {
          world.putTrap(pos.offset);
        }
      }
    }
    for (int y = 0; y < 15; y++) {
      for (int x = 0; x < 30; x++) {
        Point pos = Point.get(x, y);
        if (world.hasTrap(pos.offset)) {
          explodeTrap(world, pos.offset, explosionIdsFE++);
        }
      }
    }
  }

  private void explodeTrap(World world, int offset, int explosionId) {
    boolean needExplosion = world.hasTrap(offset);
    world.setHole(offset);

    boolean needRegistration;
    needRegistration = true;
    for (int i=0;i<explosionsIdsMapFE[explosionId];i++) {
      if (explosionsIdsMap[explosionId][i] == offset) {
        needRegistration = false;
        break;
      }
    }
    if (needRegistration) {
      explosionsIdsMap[explosionId][explosionsIdsMapFE[explosionId]++] = offset;
    }
    
    needRegistration = true;
    for (int i=0;i<explosionPosIdsFE[offset];i++) {
      if (explosionPosIds[offset][i] == explosionId) {
        needRegistration = false;
        break;
      }
    }
    if (needRegistration) {
      explosionPosIds[offset][explosionPosIdsFE[offset]++] = explosionId;
    }
    
    if (needExplosion) {
      world.removeTrap(offset);

      explosionsCentersOffsets[explosionId][explosionsCentersOffsetsFE[explosionId]++] = offset;
      explosionsCentersCount[explosionId]++; 
      for (MapCell mc : World.mapCells[offset].neighbors) {
        explodeTrap(world, mc.pos.offset, explosionId);
      }
    }
  }
  
  public void debug() {
    Oracle.debugMap("Explosions map ("+explosionPosIdsFE+")", offset -> explosionPosIdsFE[offset]>1 ? 
                                                                                      "*" : 
                                                                                      explosionPosIdsFE[offset] == 0 ? 
                                                                                            " " : 
                                                                                            ""+(char)('A'+explosionPosIds[offset][0]));
  }

  /** Return the point to dig to trigger the explosion explosionMapId, from pos */
  public Point getPointToTrigger(World current, int explosionId) {
    for (int i=0;i<explosionsCentersOffsetsFE[explosionId];i++) {
      int offset = explosionsCentersOffsets[explosionId][i];
      Point center = Point.getFromOffset(offset);
      for (int r=0;r<5;r++) {
        Robot robot =current.teams[0].robots[r];
        if (robot.pos.distance(center) == 1 ) {
          return center;
        }
      }
    }
    System.err.println("**ERREUR ** Didn't found the position to trigger "+explosionId);
    return Point.Invalid;
  }
}
