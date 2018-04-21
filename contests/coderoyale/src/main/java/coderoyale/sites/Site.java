package coderoyale.sites;

import coderoyale.Player;
import coderoyale.Pos;
import coderoyale.units.Queen;

public class Site {

  private static final int QUEEN_RADIUS = 30;

  public Site(int siteId, int x, int y, int radius) {
    this.id = siteId;
    this.radius = radius;
    pos.x = x;
    pos.y = y;
  }
  public int id;
  public Pos pos = new Pos();
  public int radius;
  public int gold;
  
  public Structure structure = Structure.NONE;
  public int maxMineSize;
  
  
  @Override
  public String toString() {
    return "(id= "+id+")";
  }
  
  public boolean canTrain() {
    return structure.type == Structure.BARRACK && ((Barrack)structure).turnBeforeTrain <= 0;
  }

  public boolean imNotOwner() {
    return !imOwner();
  }

  public boolean imOwner() {
    return structure.owner == 0;
  }
  public boolean isBuidling() {
    return structure.type == Structure.BARRACK && ((Barrack)structure).turnBeforeTrain > 0;
  }

  public boolean isInRange(Queen me) {
    // TODO review this condition, it may be farther
    return this.pos.dist(me.pos) <= 10 + (this.radius + QUEEN_RADIUS);
  }

  public boolean isTower() {
    return structure.type == Structure.TOWER;
  }

  public boolean buildTower() {
    System.out.println("BUILD " + this.id + " TOWER");
    return true;
  }

  public boolean moveTo() {
    System.out.println("MOVE " + this.pos);
    return true;
  }

  public boolean buildKnightBarrack() {
    System.out.println("BUILD " + this.id + " BARRACKS-KNIGHT");
    return true;
  }

  public boolean buildMine() {
    if (this.isInRange(Player.me) && !this.isAMine() ) {
      System.out.println("BUILD " + this.id + " MINE");
      return true;
    } else {
      return false;
    }
  }

  public boolean upgradeMine() {
    System.err.println("Upgrading mine ...");
    if (this.isInRange(Player.me) && this.isAMine() && !this.maxMined()) {
      System.out.println("BUILD " + this.id + " MINE");
      return true;
    } else {
      return false;
    }
  }

  public boolean buildGiant() {
    System.out.println("BUILD " + this.id + " BARRACKS-GIANT");
    return true;
  }

  public boolean isAMine() {
    return structure.type == Structure.MINE;
  }
  
  public boolean isGiant() {
    return structure.type == Structure.BARRACK && structure.subtype == Structure.GIANT;
  }

  public boolean maxMined() {
    return isAMine() && ((Mine)structure).incomeRate >= maxMineSize;
  }

  public boolean isNotBuildable(Queen me) {
    return isTower() && imNotOwner();
  }

}
