package coderoyale.units;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import coderoyale.CommandException;
import coderoyale.Player;
import coderoyale.Pos;
import coderoyale.sites.Barrack;
import coderoyale.sites.Mine;
import coderoyale.sites.Site;
import coderoyale.sites.Tower;

public class Queen extends Unit {
  public int gold;
  public int touchedSite;
  Pos initialPos = null;
  public int oppositeWallX;
  public int homeWallX;
  
  public List<Barrack> knightBarracks = new ArrayList<>();
  public List<Tower> towers = new ArrayList<>();
  public List<Mine> mines = new ArrayList<>();
  public List<Unit> creeps = new ArrayList<>();
  public int frontierX;

  
  public Queen() {
    super(0,0, -1);
  }
  
  public int closest(Site s1, Site s2) {
    return Double.compare(s1.pos.dist2(this.pos), s2.pos.dist2(this.pos));
  }
  public static class Action {
    public Action then(Supplier<Boolean> action) {
      boolean result = action.get();
      if (result == true) {
        return new NoDo();
      } else {
        return this;
      }
    }
    public boolean end() {
      return false;
    }
  }
  static class NoDo extends Action {
    @Override
    public Action then(Supplier<Boolean> action) {
      return this;
    }
    public boolean end() {
      return true;
    }
  }
  
  @Override
  public void updatePos(int x, int y) {
    if (initialPos == null) {
      initialPos = new Pos(x,y);
      this.oppositeWallX = (x < Player.WIDTH / 2 ) ? Player.WIDTH : 0;
      this.homeWallX = (x < Player.WIDTH / 2 ) ? 0 : Player.WIDTH;
    }
    super.updatePos(x, y);
  }
  private boolean canBuild(Site site) {
    return site.isInRange(this);
  }

  public Action action(Supplier<Boolean> action) {
    return new Action().then(action);
  }
  
  public Action moveTo(Site site) {
    if (this.canBuild(site)) {
      return new Action();
    } else {
      site.moveTo();
      return new NoDo();
    }
  }

  public Action moveTo(Pos position) {
    if (Player.me.pos.x == position.x && Player.me.pos.y == position.y) {
      return new Action();
    } else {
      throw CommandException.success("MOVE "+position.x+" "+position.y);
    }
  }
  
  public boolean onHomeSide(int x) {
    if (homeWallX == 0 && x < this.frontierX) return true;
    if (homeWallX == Player.WIDTH && x > this.frontierX) return true;
    return false;
  }
  
  public void reset() {
    mines.clear();
    towers.clear();
    knightBarracks.clear();
    creeps.clear();
  }

  public void calculateFrontierPosition() {
    List<Tower> farthestTowers = this.towers.stream()
        .sorted((t1, t2) -> Double.compare(
                                    Math.abs(t1.attachedTo.pos.x - 1920), 
                                    Math.abs(t2.attachedTo.pos.x - 1920)))
        .collect(Collectors.toList());

    if (farthestTowers.size() == 0) {
      frontierX = Player.WIDTH / 2;
    } else {
      int frontier = 0;
      int towers = Math.min(3, farthestTowers.size());
      for (int i=0;i<towers;i++) {
        frontier += farthestTowers.get(i).attachedTo.pos.x;
      }
      frontier /=towers;
      System.err.println("The frontier is x = " + frontier);
      frontierX = frontier;
    }
  }
  public void moveTowards(Pos towards) {
    double vx, vy;
    vx = towards.x - this.pos.x;
    vy = towards.y - this.pos.y;
    // resize to 3.0
    double length = Math.sqrt(vx*vx+vy*vy);
    
    double vcoeff;
    if (/*speed*/ 100 > length) {
    } else { 
      vcoeff = (/*speed*/100.0) / length; 
      vx = vcoeff * vx;
      vy = vcoeff * vy;
    }
    
    this.pos.x = (int) (this.pos.x + vx);
    this.pos.y = (int) (this.pos.y + vy);
  }

}
