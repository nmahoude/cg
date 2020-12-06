package calm;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Map {
  public static final int S2 = 11 * 7;

  public Item dishwasher;
  public Item strawberries;
  public Item blueberries;
  public Item chopper;
  public Item ovenAsEquipment;
  public Item dough;
  public Item bell;
  public Item icecream;

  public List<P> tables = new ArrayList<>();
  public Item items[] = new Item[7 * 11];
  public int cells[] = new int[7 * 11];
  public int distances[] = new int[Map.S2*Map.S2*Map.S2];

  public int staticItemsFE;


  public Map() {
    for (int i = 0; i < S2; i++) {
      items[i] = new Item(P.fromIndex(i));
    }
  }

  public void read(Scanner in) {
    staticItemsFE = 0;

    in.nextLine();
    for (int y = 0; y < 7; y++) {
      String kitchenLine = in.nextLine();
      for (int x = 0; x < 11; x++) {
        char value = kitchenLine.charAt(x);

        P pos = P.get(x, y);
        if (value == '.' || value == '0' || value == '1' || value == ' ') {
          cells[pos.offset] = 0;
          continue;
        } else {
          cells[pos.offset] = 1;
          tables.add(pos);
        }

        int mask = ItemMask.fromLetter(value);
        if (mask == 0) continue;
        
        items[staticItemsFE].reset(mask);
        items[staticItemsFE].pos = pos;
        if (items[staticItemsFE].isEqDishwasher()) {
          this.dishwasher = items[staticItemsFE];
          items[staticItemsFE].mask = ItemMask.DISH;
        }
        if (items[staticItemsFE].isEqStrawberries()) {
          this.strawberries = items[staticItemsFE];
          items[staticItemsFE].mask = ItemMask.STRAWBERRIES;
        }
        if (items[staticItemsFE].isEqOven()) {
          this.ovenAsEquipment = items[staticItemsFE];
        }
        if (items[staticItemsFE].isEqChopper()) {
          this.chopper = items[staticItemsFE];
        }
        if (items[staticItemsFE].isEqDough()) {
          this.dough = items[staticItemsFE];
          items[staticItemsFE].mask = ItemMask.DOUGH;
        }
        if (items[staticItemsFE].isEqBell()) {
          this.bell = items[staticItemsFE];
        }
        if (items[staticItemsFE].isEqIceCream()) {
          this.icecream = items[staticItemsFE];
          items[staticItemsFE].mask = ItemMask.ICE_CREAM;
        }
        if (items[staticItemsFE].isEqBlueberries()) {
          this.blueberries = items[staticItemsFE];
          items[staticItemsFE].mask = ItemMask.BLUEBERRIES;
        }
        staticItemsFE++;
      }
    }
    initDistances();

  }

  private static int _d[][] = new int[][] { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };

  private void initDistances() {
    for (int i = 0; i < distances.length; i++) {
      distances[i] = Integer.MAX_VALUE;
    }

    for (int oppY = 0; oppY < 7; oppY++) {
      for (int oppX = 0; oppX < 11; oppX++) {
        P oppPos = P.get(oppX, oppY);
        if (oppPos.offset != 0 && cells[oppPos.offset] != 0) continue;
        calculateDistancesFor(oppPos);
      }
    }
  }

  private void calculateDistancesFor(P oppPos) {
    for (int y = 0; y < 7; y++) {
      for (int x = 0; x < 11; x++) {
        P from = P.get(x, y);
        
        if (from == oppPos) continue;
        if (cells[from.offset] != 0) continue;

        int currentDist = 0;
        int currentStep = 4 - 1;
        List<P> toVisit = new ArrayList<>();
        List<P> visited = new ArrayList<>();
        toVisit.add(from);

        while (!toVisit.isEmpty()) {
          List<P> nextPos = new ArrayList<>();
          for (P current : toVisit) {
            visited.add(current);
            for (int dy = -1; dy <= 1; dy++) {
              for (int dx = -1; dx <= 1; dx++) {
                P table = P.get(current.x + dx, current.y + dy);
                if (table == P.INVALID) continue;
                if (cells[table.offset] != 1)
                  continue;
                if (visited.contains(table))
                  continue;

                distances[oppPos.offset + Map.S2 * from.offset + Map.S2 * Map.S2 * table.offset] = Math.min(distances[oppPos.offset + Map.S2 * from.offset + Map.S2 * Map.S2 * table.offset], currentDist);
                visited.add(table);
              }
            }
            distances[oppPos.offset + Map.S2 * from.offset + Map.S2 * Map.S2 * current.offset] = Math.min(distances[oppPos.offset + Map.S2 * from.offset + Map.S2 * Map.S2 * current.offset], currentDist);

            for (int d = 0; d < 4; d++) {
              P next = P.get(current.x + _d[d][0], current.y + _d[d][1]);
              if (next == oppPos) continue;
              if (next != P.INVALID && cells[next.offset] == 0 && !visited.contains(next)) {
                nextPos.add(next);
              }
            }
          }
          toVisit.clear();
          toVisit.addAll(nextPos);

          currentStep++;
          if (currentStep == 4) {
            currentDist++;
            currentStep = 0;
          }
        }
      }
    }
  }

  public int distanceFromTo(P from, P to, P him) {
    if (him == P.INVALID) {
      return distances[0 + Map.S2 * from.offset + Map.S2 * Map.S2 * to.offset];
    } else {
      return distances[him.offset + Map.S2 * from.offset + Map.S2 * Map.S2 * to.offset];
    }
  }

}
