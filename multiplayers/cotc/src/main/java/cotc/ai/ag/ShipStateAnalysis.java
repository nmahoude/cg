package cotc.ai.ag;

import cotc.entities.Ship;

public class ShipStateAnalysis {
  public Ship closestEnemy;

  public boolean enemyAtStern[] = new boolean[2]; // do we have a ship that can takes a mine
  public boolean canMove[] = new boolean[2]; // can the ship move ?
  public boolean mineAt[] = new boolean[2]; // is there a mine in front ?
  
  public void debug() {
    System.err.println("enemy at stern "+enemyAtStern[0]+" "+enemyAtStern[1]);
    System.err.println("canMoves: "+canMove[0]+" "+canMove[1]);
    System.err.println("mines: "+mineAt[0]+" "+mineAt[1]);
    System.err.println("Closest enemy : "+closestEnemy.id);
  }
}
