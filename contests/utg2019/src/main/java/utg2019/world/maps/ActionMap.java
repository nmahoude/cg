package utg2019.world.maps;

import trigonometryInt.Point;
import utg2019.Player;
import utg2019.sim.Action;
import utg2019.sim.Item;
import utg2019.world.World;

public class ActionMap {
  static Action allMoves[] = new Action[World.MAX_OFFSET];
  static Action allDigs[] = new Action[World.MAX_OFFSET];
  
  static int actionsFE[] = new int[World.MAX_OFFSET];
  static Action actions[][] = new Action[World.MAX_OFFSET][100];
  
  static public void init() {
    for (int y = 0; y < 15; y++) {
      for (int x = 0; x < 30; x++) {
        allMoves[30*y+x] = Action.move(x,y);
        allDigs[30*y+x] = Action.dig(x,y);
      }
    }    
    
    int offset;
    for (int y = 0; y < 15; y++) {
      offset = 30*y;
      actions[offset][actionsFE[offset]++] = Action.request(Item.RADAR);
      actions[offset][actionsFE[offset]++] = Action.request(Item.TRAP);
      
      for (int x = 0; x < 30; x++) {
        Point center = Point.get(x,y);
        offset = center.offset;
        actions[offset][actionsFE[offset]++] = Action.doWait();

        // all moves
        for (int dy = -4; dy <= 4; dy++) {
          for (int dx = -4; dx <= 4; dx++) {
            Point pos = Point.getSecured(x+dx, y+dy);
            if (pos != Point.Invalid && pos.distance(center) <= 4) {
              actions[offset][actionsFE[offset]++] = allMoves[pos.offset];
            }
          }
        }
        
        // all digs
        for (int dy = -1; dy <= 1; dy++) {
          for (int dx = -1; dx <= 1; dx++) {
            Point pos = Point.getSecured(x+dx, y+dy);
            if (pos != Point.Invalid && x+dx != 0 && pos.distance(center) <= 1) {
              actions[offset][actionsFE[offset]++] = allDigs[pos.offset];
            }
          }
        }
      }
    }
  }

  public static Action getRandomAction(Point pos) {
    int max = actionsFE[pos.offset];
    return actions[pos.offset][Player.random.nextInt(max)];
  }
}
