package hypersonic.entities;

import hypersonic.State;
import hypersonic.utils.CacheUtil;
import hypersonic.utils.P;

public class Item extends Entity {
  public static CacheUtil<Item> cache = new CacheUtil<>();
  static {
    for (int i=0;i<10000;i++) {
      cache.push(new Item(null, i, null, i, i));
    }
  }
  
  public int type;

  private Item(State board, int owner, P position, int type, int ignored) {
    super(board, owner, EntityType.ITEM, position);
    this.type = type;
  }

  public Item duplicate(State board) {
    Item i;
    if (cache.isEmpty()) {
      i = new Item(board, owner, position, type, 0);
    } else {
      i = cache.pop();
      i.board = board;
      i.owner = owner;
      i.position = position;
      i.type = type;
    }
    return i;
  }
  
  public static Item create(State board, int owner, P position, int type, int unused) {
    Item i;
    if (cache.isEmpty()) {
      i = new Item(board, owner, position, type, 0);
    } else {
      i = cache.pop();
      i.board = board;
      i.owner = owner;
      i.position = position;
      i.type = type;
    }
    return i;
  }
  
}
