package calm.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import calm.Player;
import calmBronze.Item;
import util.P;

public class Board {
  public Map<Table, Integer> tables = new HashMap<>();
  public int ovenContents;
  public int ovenTimer;

  public void read(State state, Scanner in) {
    this.tables.clear();
    int numTablesWithItems = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.println("\"" + numTablesWithItems + Player.DEBUG_EOL);
    }
    for (int i = 0; i < numTablesWithItems; i++) {
      int tableX = in.nextInt();
      int tableY = in.nextInt();
      Table table = new Table(tableX, tableY);
      String itemStr = in.next();
      tables.put(table, Item.getFromString(itemStr));
      if (Player.DEBUG_INPUT) {
        System.err.println("\"" + tableX + " " + tableY + " " + itemStr + Player.DEBUG_EOL);
      }
    }
    String ovenContentsStr = in.next();
    ovenContents = Item.getFromString(ovenContentsStr);
    int ovenInitialTimer = in.nextInt();
    ovenTimer = state.turn + ovenInitialTimer; // TODO check for +1/-1 shift
    if (Player.DEBUG_INPUT) {
      System.err.println("\"" + ovenContentsStr + " " + ovenInitialTimer + Player.DEBUG_EOL);
    }
  }

  public void copyFrom(Board model) {
    this.tables.clear();
    this.tables.putAll(model.tables);
    ovenContents = model.ovenContents;
    ovenTimer = model.ovenTimer;
  }

  public void updateOven(int turn) {
    if (ovenContents != 0) {
      if (ovenTimer <= turn) {
        // bake or dead
        if (ovenContents == Item.DOUGH) {
          ovenContents = Item.CROISSANT;
          ovenTimer += 10;
        } else if (ovenContents == Item.RAW_TART) {
          ovenContents = Item.BLUEBERRIES_TART;
          ovenTimer += 10;
        } else {
          ovenTimer = 0;
          ovenContents = 0; // burned
        }
      }
    }
  }

  public int getInteractiveNeighborsTable(State state, Agent agent, Table[] tablesArray) {
    int tablesFE = 0;
    for (Entry<Table, Integer> entry : tables.entrySet()) {
      Table t = entry.getKey();
      if (agent.canUseRelativeToDistance(t) && Item.canBeUseWithAgent(entry.getValue(), state, agent)) {
        tablesArray[tablesFE++] = t;
      }
    }
    return tablesFE;
  }

  private static Table falseTable = new Table(0,0);
  public Table getTable(P pos) {
    for (Entry<Table, Integer> entry: tables.entrySet()) {
      if (entry.getKey().pos == pos) {
        falseTable.item = entry.getValue();
        return falseTable;
      }
    }
    return null;
  }
}
