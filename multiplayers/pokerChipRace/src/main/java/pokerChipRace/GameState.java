package pokerChipRace;

import cgcollections.arrays.FastArray;
import pokerChipRace.entities.Entity;

public class GameState {
  public static final float WIDTH = 800;
  public static final float HEIGHT = 515;
  
  public int playerCount;
  public int myId ;
  public int entityCount;
  public FastArray<Entity> myChips = new FastArray<>(Entity.class, 6);
  public Entity[] chips = new Entity[10000];
  public int entityFE = 0;
  public int _entityFE = 0;
  
  public GameState() {
    for (int i=0;i<chips.length;i++) {
      chips[i] = new Entity(i, -1);
    }
    entityFE = 0;
  }
  
  public Entity getInitialChip(int id) {
    entityFE = Math.max(entityFE, id+1);
    return chips[id];
  }
  public void backup() {
    _entityFE = entityFE;
    for (int i=0;i<entityFE;i++) {
      Entity entity = chips[i];
      entity.backup();
    }
  }

  public void restore() {
    entityFE = _entityFE;
    for (int i=0;i<entityFE;i++) {
      Entity entity = chips[i];
      entity.restore();
    }
  }
  
  
  public Entity getNewChip() {
    return chips[entityFE++];
  }
  
  public boolean isGameWin() {
    for (int i=0;i<entityFE;i++) {
      Entity e = chips[i];
      if (e.owner == -1) break;
      if (e.owner != myId && !e.isDead()) {
        return false;
      }
    }
    return true;
  }
  public boolean isGameLost() {
    for (int i=0;i<myChips.length;i++) {
      Entity e = myChips.elements[i];
      if (!e.isDead()) {
        return false;
      }
    }
    return true;
  }
}
