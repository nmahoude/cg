package hypersonic.entities;

import hypersonic.utils.P;

public class Bomberman extends Entity {

  public int id;
  public int bombsLeft;
  public int currentRange;
  
  public Bomberman(int owner, P position, int bombsLeft, int currentRange) {
    super(EntityType.PLAYER, position);
    this.bombsLeft = bombsLeft;
    this.currentRange = currentRange;
    this.id = owner;
  }
}
