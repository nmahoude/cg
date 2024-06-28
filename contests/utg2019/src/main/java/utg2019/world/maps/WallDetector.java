package utg2019.world.maps;

import trigonometryInt.Point;
import utg2019.world.World;

/**
 * Detects Wall of traps in the 1st 4 cols
 * @author TH5893
 *
 */
public class WallDetector {

  public void update(int[] trapsMap) {

    for (int x=1;x<5;x++) {

      int y = 0;
      int startY=0;
      int length = 0;
      while (y < World.HEIGHT+1) {
        Point pos = Point.getSecured(x, y);
        if (y == World.HEIGHT || trapsMap[pos.offset] == 0) {
          // end of wall
          if (length > 1) {
            System.err.println("There is a wall at col="+x+", y from "+startY+" to "+(y-1));
          } else {
            // just an isolated trap
          }
          startY = y; // reset start y
          length = 0;
        } else {
          if (length == 0) {
            startY = y;
          }
          length++; // wall is continuing
        }
          
        y++;
      }
    }
  }
}
