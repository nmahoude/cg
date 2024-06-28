package utg2019.world;

import java.util.Scanner;

import trigonometryInt.Point;
import utg2019.Owner;
import utg2019.Player;
import utg2019.world.entity.EntityType;

public class World {
  public static final int WIDTH = 30;
  public static final int HEIGHT = 15;
  public static final int MAX_OFFSET = WIDTH * HEIGHT;
  public static MapCell mapCells[] = new MapCell[WIDTH*HEIGHT];
  
  static { // init
    for (int y=0;y<HEIGHT;y++) {
      for (int x=0;x<WIDTH;x++) {
        Point coord= Point.get(x, y);
        mapCells[coord.offset] = new MapCell(x,y);
      }
    }
    MapCell.init(WIDTH, HEIGHT, mapCells);
  }
  
  public Team teams[] = new Team[2];
  private long cellsBitArray[] = new long[World.MAX_OFFSET];

  public World() {
    teams[0] = new Team(Owner.ME);
    teams[1] = new Team(Owner.OTHER);
  }

  public void read(Scanner in) {
    for (int i=0;i<2;i++) {
      teams[i].read(in);
    }
    Player.start = System.currentTimeMillis();
    if (Player.DEBUG_INPUT) {
      System.err.println();
    }

    for (int y=0;y<HEIGHT;y++) {
      for (int x=0;x<WIDTH;x++) {
        Point coord= Point.get(x, y);
        String oreStr = in.next();
        String holeStr = in.next();

        cellsBitArray[coord.offset] = CellData.reset(cellsBitArray[coord.offset]);
        if (oreStr.charAt(0) == '?') {
          cellsBitArray[coord.offset] &= ~CellData.KNOWN_MASK;
          cellsBitArray[coord.offset] = CellData.setOre(cellsBitArray[coord.offset], 0);
        } else {
          cellsBitArray[coord.offset] |= CellData.KNOWN_MASK;
          cellsBitArray[coord.offset] = CellData.setOre(cellsBitArray[coord.offset], oreStr.charAt(0) - '0');
        }
        cellsBitArray[coord.offset] = CellData.setHole(cellsBitArray[coord.offset], holeStr.charAt(0) != '0');
      }
    }
    
    int entityCount = in.nextInt();
    teams[0].radarCooldown = in.nextInt();
    teams[0].trapCooldown = in.nextInt();
    if (Player.DEBUG_INPUT) {
      System.err.println(String.format("%d", entityCount));
      System.err.println(String.format("%d %d", teams[0].radarCooldown, teams[0].trapCooldown));
    }
    
    // reinit trucks
    teams[0].resetFE();
    teams[1].resetFE();
    
    for (int i = 0; i < entityCount; i++) {
      int id = in.nextInt();
      int typeInt = in.nextInt();
      int x = in.nextInt();
      int y = in.nextInt();
      int itemInt = in.nextInt();
      if (Player.DEBUG_INPUT) {
        System.err.println(String.format("%d %d %d %d %d", id, typeInt, x, y, itemInt));
      }
      
      EntityType type = EntityType.valueOf(typeInt);
      Point pos = (x != -1) ? Point.get(x, y) : Point.Invalid; 
      EntityType item = EntityType.valueOf(itemInt);

      if (type == EntityType.ALLY_ROBOT) {
        teams[0].addRobot(id, pos, item);
      } else if (type == EntityType.ENEMY_ROBOT) {
        teams[1].addRobot(id, pos, item);
      } else if (type == EntityType.RADAR) {
        putRadar(pos);
      } else if (type == EntityType.TRAP) {
        putTrap(pos);
        Player.trapAdvisor.addOwnTrap(pos);
      }
    }
  }

  public void copyFrom(World model) {
    teams[0].copyFrom(model.teams[0]);
    teams[1].copyFrom(model.teams[1]);
    System.arraycopy(model.cellsBitArray , 0, cellsBitArray, 0, World.MAX_OFFSET);
  }
  
  public void putRadar(Point center) {
    cellsBitArray[center.offset] = CellData.setRadar(cellsBitArray[center.offset], true);
    setHole(center.offset);

    updateRadar(center, +1);
  }
  
  public void removeRadar(Point center) {
    cellsBitArray[center.offset] = CellData.setRadar(cellsBitArray[center.offset], false);

    updateRadar(center, -1);
  }
  
  private void updateRadar(Point center, int dr) {
    // TODO precompute the cells touch by radar at every cells (bitArray)
    for (int dy = -4; dy <= 4; dy++) {
      for (int dx = -4; dx <= 4; dx++) {
        Point pos = Point.getSecured(center.x+dx, center.y+dy);
        if (pos != Point.Invalid && pos.x > 0) {
          if (center.distance(pos) > 4) continue;
          
          if (dr > 0) {
            cellsBitArray[pos.offset] = CellData.setCurrentlyKnown(cellsBitArray[pos.offset]);
          } else {
            cellsBitArray[pos.offset] = CellData.setCurrentlyKnown(cellsBitArray[pos.offset], false);
          }
        }
      }
    }
  }

  public boolean isCurrentlyKnown(Point center) {
    return isCurrentlyKnown(center.offset);
  }

  public boolean isCurrentlyKnown(int offset) {
    return CellData.isCurrentlyKnown(cellsBitArray[offset]);
  }
  
  public boolean hasRadar(Point pos) {
    return hasRadar(pos.offset);
  }
  public boolean hasTrap(Point pos) {
    return CellData.hasTrap(cellsBitArray[pos.offset]);
  }

  public int getOre(Point pos) {
    return getOre(pos.offset);
  }

  public int getOre(int offset) {
    return CellData.getOre(cellsBitArray[offset]);
  }

  public void setOre(Point target, int value) {
    cellsBitArray[target.offset] = CellData.setOre(cellsBitArray[target.offset], value); 
  }

  public boolean hasHole(Point pos) {
    return hasHole(pos.offset);
  }

  public boolean hasHole(int offset) {
    return CellData.hasHole(cellsBitArray[offset]);
  }

  public boolean hasTrap(int offset) {
    return CellData.hasTrap(cellsBitArray[offset]);
  }

  public boolean hasRadar(int offset) {
    return CellData.hasRadar(cellsBitArray[offset]);
  }

  public void setHole(int offset) {
    cellsBitArray[offset] = CellData.setHole(cellsBitArray[offset], true);
  }

  public void setCurrentlyKnown(Point point) {
    cellsBitArray[point.offset] = CellData.setCurrentlyKnown(cellsBitArray[point.offset]);
  }

  public void putTrap(int offset) {
    cellsBitArray[offset] = CellData.setTrap(cellsBitArray[offset], true);
    setHole(offset);
  }

  public void removeTrap(int offset) {
    cellsBitArray[offset] = CellData.setTrap(cellsBitArray[offset], false);
  }

  
  public void setHole(Point point) {
    setHole(point.offset);
  }

  public void putTrap(Point point) {
    putTrap(point.offset);
  }

  public void removeTrap(Point point) {
    removeTrap(point.offset);
  }
}
