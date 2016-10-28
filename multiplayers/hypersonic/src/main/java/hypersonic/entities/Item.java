package hypersonic.entities;

import hypersonic.utils.P;

public class Item extends Entity {

  public Item(P position, int type, int ignored) {
    super(EntityType.ITEM, position);
  }
}
