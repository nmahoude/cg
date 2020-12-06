package calm;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class State {
  public Agent me = new Agent(this);
  public Agent him = new Agent(this);

  public Item items[] = new Item[Map.S2];
  public int tables[] = new int[Map.S2];
  public int itemsFE = 0;
  
  public int ovenTimer;
  public Item ovenContents = new Item(P.INVALID);
  public boolean ovenIsMine = false;
  
  public Desert deserts[];
  private int desertsFE = 0;
  
  
  public State() {
    
  }
  
  
  public void read(Scanner in) {
    for (int i=0;i<Map.S2;i++) {
      tables[i] = 0;
    }
    
    me.read(in);
    him.read(in);

    
    int numTablesWithItems = in.nextInt(); // the number of tables in the kitchen that currently hold an item
    
    for (int i=0;i<Player.map.staticItemsFE;i++) {
      tables[items[i].pos.offset] = items[i].mask;
    }
    itemsFE = Player.map.staticItemsFE;
    for (int i = 0; i < numTablesWithItems; i++) {
      P pos = P.get(in.nextInt(), in.nextInt());
      String maskStr = in.next();
      int mask = ItemMask.fromString(maskStr);
      
      tables[pos.offset] = mask;

      items[itemsFE].mask = mask;
      items[itemsFE].pos = pos;
      itemsFE++;
    }
    
    ovenContents.reset(ItemMask.fromString(in.next()));
    ovenTimer = in.nextInt();
    if (ovenContents.isEmpty()) {
      ovenIsMine = false;
    }
    System.err.println(ovenIsMine ? "oven content is mine " : "oven content not mine");
    int numCustomers = in.nextInt(); // the number of customers currently waiting for food
    desertsFE = 0;
    for (int i = 0; i < numCustomers; i++) {
      int mask = ItemMask.fromString(in.next());
      int customerAward = in.nextInt();
      deserts[desertsFE++].set(mask, customerAward);
    }
    
    if (Player.DEBUG_TABLES) {
      System.err.println("Debug read, content of items");
      System.err.println("Num tables with items "+numTablesWithItems);
      for (int i=0;i<itemsFE;i++) {
        Item item = items[i];
        System.err.println("  "+item);
      }
    }
  }

  public void addItemToTables(P pos, int itemMask) {
    Item item = items[pos.offset];
    item.reset(itemMask);
  }
  
  public void readInit(Scanner in) {
    int numAllCustomers = in.nextInt();
    deserts = new Desert[numAllCustomers];
    
    for (int i = 0; i < numAllCustomers; i++) {
      int mask = ItemMask.fromString(in.next());
      int customerAward = in.nextInt();
      deserts[i] = new Desert();
      deserts[i].set(mask, customerAward);
    }
    
    Player.map.read(in);
    for (int i=0;i<Player.map.staticItemsFE;i++) {
      items[i] = Player.map.items[i];
    }
    for (int i=Player.map.staticItemsFE;i<items.length;i++) {
      items[i] = new Item(P.INVALID);
    }
  }


  public boolean hasCroissant() {
    return has(ItemMask.CROISSANT);
  }
  public boolean hasChoppedStrawberries() {
    return has(ItemMask.CHOPPED_STRAWBERRIES);
  }  
  
  public boolean has(int mask) {
    for (int i=0;i<itemsFE;i++) {
      if (!items[i].isFloor() && (items[i].mask & ~ItemMask.DISH) == mask) return true;
    }
    return false;
  }


  public Item getCroissant(Agent agent) {
    return get(ItemMask.CROISSANT);
  }
  public Item getChoppedStrawberries(Agent agent) {
    return get(ItemMask.CHOPPED_STRAWBERRIES);
  }

  Item get(int mask) {
    for (int i=0;i<itemsFE;i++) {
      if (!items[i].isFloor() && (items[i].mask & ~ItemMask.DISH) == mask) return items[i];
    }
    return null;
  }

  public List<Item> getAll(int mask) {
    List<Item> compatibleItems = new ArrayList<>();
    for (int i=0;i<itemsFE;i++) {
      if (items[i].mask == mask) {
        compatibleItems.add(items[i]);
      }
    }
    return compatibleItems;
  }


  public Item getItem(int mask) {
    for (int i=0;i<itemsFE;i++) {
      if (!items[i].isFloor() && items[i].mask == mask) return items[i];
    }
    
    return null;
  }

  public List<Item> getItemsCompatibleWith(Desert desert) {
    List<Item> compatibleItems = new ArrayList<>();
    for (int i=0;i<itemsFE;i++) {
      if (!items[i].isFloor() && (items[i].mask & ~desert.item.mask) == 0) {
        compatibleItems.add(items[i]);
      }
    }
    
    return compatibleItems;
  }


  public Item getDishCompatible(Desert desert, Item hands, int mask) {
    for (int i=0;i<itemsFE;i++) {
      if (hands.hasPlate() && items[i].hasPlate()) continue;
      if (!items[i].isFloor() && (items[i].mask & ~desert.item.mask) == 0 && (items[i].mask & mask) != 0) {
        return items[i];
      }
    }
    
    if ((ovenContents.mask & mask) != 0) {
      return Player.map.ovenAsEquipment;
    }
    
    return null;
  }

  public P findClosestEmptyTable(State state) {
    P origin = state.me.pos;
    System.err.println("Find closest empty table @ "+origin);
    P best = null;
    int bestDistance = Integer.MAX_VALUE;
    
    for (P pos : Player.map.tables) {
      boolean found = false;
      for (int i=0;i<itemsFE;i++) {
        if (items[i].pos == pos && items[i].mask != 0) {
          //System.err.println("Found item @ "+pos+", cant use it with "+items[i].toString());
          found =true;
          break;
        }
      }
      if (!found) {
        int distance = Player.map.distanceFromTo(origin, pos, state.him.pos);
        //System.err.println("No items found @ "+pos);
        if (distance < bestDistance) {
          best = pos;
          bestDistance = distance;
        }
      }
    }

    return best;
  }
  
  public List<Item> getDishCompatibleWithDesertWhenHolding(Desert desert, Item hands) {
    if (Player.DEBUG_PICKING) System.err.println("get Dishes Compatible ? "+ desert.item+" vs "+ItemMask.output(hands.mask));
    List<Item> compatibleItems = new ArrayList<>();
    for (int i=0;i<itemsFE;i++) {
      if (!items[i].isFloor() && items[i].mask != 0 && (items[i].mask & ~desert.item.mask) == 0 && (items[i].mask & hands.mask) == 0) {
        if(Player.DEBUG_PICKING) System.err.println("  Item "+items[i]+" @"+items[i].pos+" is compatible");
        compatibleItems.add(items[i]);
      }
    }
    
    if (Player.DEBUG_PICKING && compatibleItems.isEmpty()) System.err.println("nothing compatible");
    return compatibleItems;
  }


  public int distance(P... positions) {
    int distance = 0;
    P current = this.me.pos;
    for (P p : positions) {
      distance+= Player.map.distanceFromTo(current, p, him.pos);
      current = p;
    }
    return distance;
  }



}
