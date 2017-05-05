package pokerChipRace;

import cgcollections.arrays.FastArray;
import pokerChipRace.entities.Entity;

public class GameState {
  public static final float WIDTH = 800;
  public static final float HEIGHT = 515;
  
  public int myId ;
  public int entityCount;
  public FastArray<Entity> myChips = new FastArray<>(Entity.class, 6);
  private Entity[] allChips = new Entity[10000];
  public int entityFE = 0;
  public int _entityFE = 0;
  
  public GameState() {
    for (int i=0;i<allChips.length;i++) {
      allChips[i] = new Entity(i, -1);
    }
    entityFE = 0;
  }
  
  public Entity getChip(int id) {
    entityFE = Math.max(entityFE, id+1);
    return allChips[id];
  }
  public void backup() {
    _entityFE = entityFE;
    for (int i=0;i<entityFE;i++) {
      Entity entity = allChips[i];
      if (entity.isDead()) continue;
      entity.backup();
    }
  }

  public void restore() {
    entityFE = _entityFE;
    for (int i=0;i<entityFE;i++) {
      Entity entity = allChips[i];
      entity.restore();
    }
  }
  
  
  public Entity getNewChip() {
    return allChips[entityFE++];
  }
}
