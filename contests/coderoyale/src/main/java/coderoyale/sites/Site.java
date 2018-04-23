package coderoyale.sites;

import coderoyale.CommandException;
import coderoyale.Player;
import coderoyale.Pos;
import coderoyale.units.Disk;
import coderoyale.units.Queen;

public class Site extends Disk {

  private static final int QUEEN_RADIUS = 30;

  public Site(int siteId, int x, int y, int radius) {
    super(x, y, radius);
    this.id = siteId;
  }
  public int id;
  private int gold;
  
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
    if (this.isTower()) {
      return false;
    }
    throw CommandException.success("BUILD " + this.id + " TOWER");
  }

  public boolean upgradeTower() {
    if (!this.isInRange(Player.me)) {
      System.err.println("not in range, aborting");
      return false;
    }
    if (this.getTower().life > 600) {
      System.err.println("life sufficient");
      return false;
    }

    throw CommandException.success("BUILD " + this.id + " TOWER");
  }

  
  public boolean moveTo() {
    if (this.isInRange(Player.me)) return false;
    throw CommandException.success("MOVE " + this.pos);
  }

  public boolean buildKnightBarrack() {
    throw CommandException.success("BUILD " + this.id + " BARRACKS-KNIGHT");
  }

  public boolean buildMine() {
    if (this.isInRange(Player.me) && !this.isAMine() ) {
      throw CommandException.success("BUILD " + this.id + " MINE");
    } else {
      return false;
    }
  }

  public boolean upgradeMine() {
    System.err.println("Upgrading mine ...");
    if (this.isInRange(Player.me) && this.isAMine() && !this.maxMined()) {
      throw CommandException.success("BUILD " + this.id + " MINE");
    } else {
      return false;
    }
  }

  public boolean buildGiant() {
    throw CommandException.success("BUILD " + this.id + " BARRACKS-GIANT");
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

  public Tower getTower() {
    return (Tower)structure;
  }

  public boolean noBuilding() {
    return structure == Structure.NONE;
  }

  public boolean isBarrack() {
    return structure.type == Structure.BARRACK;
  }

  public boolean hasGold() {
    return gold > 0;
  }

  public void updateGold(int gold) {
    this.gold = gold;
  }

  public boolean inTerritoryOf(Queen queen) {
    return queen.onHomeSide(this.pos.x);
  }
}
