package theAccountant;

import trigonometry.Point;

public class Movable {

  GameEngine gameEngine;
  Point p;
  final int maxMove;
  
  public Movable(GameEngine gameEngine, int maxMove) {
    super();
    this.gameEngine = gameEngine;
    this.maxMove = maxMove;
  }

  boolean move(Point target) {
    if (target.equals(p)) {
      return true;
    } else {
      int vecx = (int)(target.x-p.x);
      int vecy = (int)(target.y-p.y);
      double norm = Math.sqrt(vecx*vecx+vecy*vecy);
      if (norm > Wolff.WOLFF_MOVE) {
        p = new Point((int)(p.x + 1.0*maxMove / norm * vecx)
                ,(int)(p.y + 1.0*maxMove / norm * vecy));
        return false;
      } else {
        p = target;
        return true;
      }
    }
  }

}
