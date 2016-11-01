package hypersonic.entities;

import hypersonic.Board;
import hypersonic.utils.P;

public class Item extends Entity {
  public final int type;

  public Item(Board board, int owner, P position, int type, int ignored) {
    super(board, owner, EntityType.ITEM, position);
    this.type = type;
  }
}
