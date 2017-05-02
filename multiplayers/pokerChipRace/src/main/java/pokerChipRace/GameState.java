package pokerChipRace;

import cgcollections.arrays.FastArray;
import pokerChipRace.entities.Entity;

public class GameState {
  public static final float WIDTH = 800;
  public static final float HEIGHT = 515;
  
  public int myId ;
  public int entityCount;
  public FastArray<Entity> myChips = new FastArray<>(Entity.class, 6);
  public FastArray<Entity> allChips;
}
