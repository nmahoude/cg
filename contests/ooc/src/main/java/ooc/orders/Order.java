package ooc.orders;

import ooc.Direction;
import ooc.OOCMap;
import ooc.P;

public class Order {
  public static final Order NONE = new Order(null);
  
  static Order moves[] = new Order[30];
  static Order silences[] = new Order[30];
  static Order torpedoes[] = new Order[OOCMap.S2];
  static Order triggers[] = new Order[OOCMap.S2];
  static Order surfaces[] = new Order[16];
  static Order sonars[] = new Order[16];
  static Order mines[] = new Order[4];
  
  static {
    for (int d=0;d<4;d++) {
      mines[d] = new Order(OrderTag.MINE, d);
    }
    for (int i=0;i<16;i++) {
      surfaces[i] = new Order(OrderTag.SURFACE, i);
      sonars[i] = new Order(OrderTag.SONAR, i);
    }
    
    for (int d=0;d<4;d++) {
      for (int c=0;c<5;c++) {
        moves[d + c * 5] = new Order(OrderTag.MOVE, d, P.get(c, c));
      }
      for (int l=0;l<5;l++) {
        silences[d + l*5] = new Order(OrderTag.SILENCE, d, P.get(l, l));
      }
    }
    
    for (int i=0;i<OOCMap.S2;i++) {
      torpedoes[i] = new Order(OrderTag.TORPEDO, P.getFromOffset(i));
      triggers[i] = new Order(OrderTag.TRIGGER, P.getFromOffset(i));
    }
  }
  
  
  
  public final OrderTag tag;
  public final int value;
  public final P pos;
  
  private Order(OrderTag tag) {
    super();
    this.tag = tag;
    this.value = -1;
    this.pos = P.I;
  }

  private Order(OrderTag tag, int value) {
    super();
    this.tag = tag;
    this.value = value;
    this.pos = P.I;
  }

  private Order(OrderTag tag, int value, P pos) {
    super();
    this.tag = tag;
    this.value = value;
    this.pos = pos;
  }

  private Order(OrderTag tag, P pos) {
    super();
    this.tag = tag;
    this.value = -1;
    this.pos = pos;
  }

  public static Order move(Direction dir, Charge charge) {
    return moves[dir.direction + charge.index * 5];
  }
  public static Order move(int dir, Charge charge) {
    return moves[dir + charge.index * 5];
  }
  
  public static Order surface(P pos) {
    return surfaces[pos.sector];
  }
  public static Order surface(int sector) {
    return surfaces[sector];
  }
  
	public static Order torpedo(P target) {
	  return torpedoes[target.o];
	}

  public static Order torpedo(P target, boolean after) {
    return torpedoes[target.o];
  }
  
  public static Orders parse(String opponentOrders) {
    Orders orders = new Orders();

    String ordersStr[];
    if (opponentOrders.contains("|")) {
      ordersStr = opponentOrders.split("\\|");
    } else {
      ordersStr = new String[1];
      ordersStr[0] = opponentOrders;
    }
    
    for (String order : ordersStr) {
      order = order.trim();
      if (order.startsWith("MOVE")) {
        String dirAsString = order.substring(5, 6);
        int dir = indexFromDir(dirAsString);
        orders.addOrder(move(dir, Charge.UNKNWON));
      } else if (order.startsWith("SURFACE")) {
        int oppLastSeenSector = Integer.parseInt(order.substring(8, 9));
        orders.addOrder(surface(oppLastSeenSector));
      } else if (order.startsWith("SONAR ")) {
        int askedSector = Integer.parseInt(order.substring(6));
        orders.addOrder(sonar(askedSector));
      } else if (order.startsWith("TORPEDO ")) {
        String posAsString = order.substring(8).trim();
        String values[] = posAsString.split(" ");
        P pos = P.get(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
        orders.addOrder(torpedo(pos));
      } else if (order.startsWith("MINE")) {
        orders.addOrder(mine(0));
      } else if (order.startsWith("SILENCE")) {
        orders.addOrder(silence(Direction.NORTH, 0));
      } else if (order.startsWith("TRIGGER ")) {
        String posAsString = order.substring(8).trim();
        String values[] = posAsString.split(" ");
        P pos = P.get(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
        orders.addOrder(trigger(pos));
      } else if (order.equals("NA")) {
      }
    }
    
    return orders;
  }
  
  
  @Override
  public String toString() {
    return tag.toString()+" " +value+" "+pos;
  }
  
  public String output() {
    switch(tag) {
    case MOVE : return "MOVE "+P.dirs[value]+" "+Charge.fromIndex(pos.x);
    case SURFACE : return "SURFACE";
    case TORPEDO : return "TORPEDO "+pos.x+" "+pos.y;
    case SONAR : return "SONAR "+value;
    case SILENCE : return "SILENCE "+P.dirs[value]+" "+pos.x;
    case MINE : return "MINE "+P.dirs[value];
    case TRIGGER : return "TRIGGER "+pos.x+" "+pos.y;
    }
    return "TAG "+tag+"not found";
  }
  
  public static int indexFromDir(String dir) {
    switch(dir) {
      case "N" : return 0;
      case "E" : return 1;
      case "S" : return 2;
      case "W" : return 3;
    }
    return -1;
  }

  public static Order sonar(int sector) {
    return sonars[sector];
  }

	public static Order silence(Direction dir, int length) {
	  return silences[dir.direction + length*5];
	}

  public static Order mine(Direction direction) {
    return mines[direction.direction];
  }

  public static Order mine(int direction) {
    return mines[direction];
  }

  public void accept(OrderVisitor visitor) {
    switch(tag) {
    case MOVE : visitor.usedMove(this); break;
    case SURFACE : visitor.usedSurface(this); break;
    case TORPEDO : visitor.usedTorpedo(this); break;
    case SONAR : visitor.usedSonar(this); break;
    case SILENCE : visitor.usedSilence(this); break;
    case MINE : visitor.usedMine(this); break;
    case TRIGGER : visitor.usedTrigger(this); break;
    }
    
  }

  public static Order trigger(P minePos) {
    return triggers[minePos.o];
  }

}
