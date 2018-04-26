package coderoyale2.units;

import coderoyale2.Constants;
import coderoyale2.Player;
import coderoyale2.Point;
import coderoyale2.structures.Barrack;
import coderoyale2.structures.Mine;
import coderoyale2.structures.Structure;
import coderoyale2.structures.Tower;

public class Site extends Unit {

  public final int siteId;
  public int maxMineSize;

  public int gold;
  public Structure structure;

  private int _gold;
  private Structure _structure;

  public Site(int siteId, Point point, int radius) {
    super(-1);
    this.health = Integer.MAX_VALUE;
    this.siteId = siteId;
    this.location = point;
    this.radius = radius;
    this.mass = 0.0;
  }

  public void backup() {
    super.backup();
    _gold = gold;
    _structure = structure;
    structure.backup();
  }

  public void restore() {
    super.restore();
    gold = _gold;
    structure = _structure;
    structure.restore();
  }

  public void act() {
    if (structure.type == Structure.MINE) {
      Mine mine = (Mine) structure;
      int cash = Math.min(mine.incomeRate, gold);
      Player.queens[structure.owner].gold += cash;
      this.gold -= cash;
      if (gold <= 0) {
        structure = Structure.NONE;
      }
    } else if (structure.type == Structure.TOWER) {
      Tower tower = (Tower)structure;
      Creep closestEnemy = null;
      double bestDist = Double.POSITIVE_INFINITY;
      for (int i=Player.creepsStart;i<Player.creepsFE;i++) {
        Creep c = (Creep)Player.all[i];
        if (c.health < 0) continue;
        if (c.owner == tower.owner) continue;
        double dist = c.location.distance2To(this.location);
        if (dist < bestDist) {
          bestDist = dist;
          closestEnemy = c;
        }
      }
      Queen enemyQueen = Player.queens[1-structure.owner];
      if (closestEnemy != null && closestEnemy.location.distanceTo(this.location) < tower.attackRadius) {
        tower.damageCreep(this, closestEnemy);
      }
      if (closestEnemy == null && enemyQueen.location.distanceTo(this.location) < tower.attackRadius) {
        tower.damageQueen(this, enemyQueen);
      }
      tower.life -= Constants.TOWER_MELT_RATE;
      // TODO precalculate ?
      tower.attackRadius = (int)(Math.sqrt((health * Constants.TOWER_COVERAGE_PER_HP + tower.area) / Math.PI));

      if (tower.life < 0) {
        // tower is dead
        structure = Structure.NONE;
      }
    } else if (structure.type == Structure.BARRACK) {
      Barrack barrack = (Barrack) structure;
      if (barrack.turnBeforeTrain > 0)
        barrack.turnBeforeTrain--;
      if (barrack.turnBeforeTrain == 0) {
        barrack.onComplete();
      }
    }
  }
}
