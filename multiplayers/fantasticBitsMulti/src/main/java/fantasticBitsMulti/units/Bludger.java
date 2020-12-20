package fantasticBitsMulti.units;

import fantasticBitsMulti.Player;

public class Bludger extends Unit {
  public Wizard last;
  public Wizard slast;
  public int ignore[] = new int[2];
  
  public Bludger() {
    super (EntityType.BLUDGER, 200.0,8,0.9);
    
    this.last = this.slast = null;
    ignore[0] = -1;
    ignore[1] = -1;
  }
  
  @Override
  public void print() {
    System.err.print("Bludger " + id + " " +position+ " " + vx + " " + vy + " " + speed() + " " + ignore[0] + " " + ignore[1] + " | ");

    if (last != null) {
      System.err.print("Last " + last.id + " | ");
    }
    System.err.println("");
  }

  @Override
  public void save() {
    super.save();
    slast = last;
  }

  @Override
  public void reset() {
    super.reset();
    last = slast;
    ignore[0] = -1;
    ignore[1] = -1;
  }

  @Override 
  public void bounce(Unit u) {
    if (u.type == EntityType.WIZARD) {
      last = (Wizard) u;
    }
    super.bounce(u);
  }

  public void play() {
    // Find our target
    Wizard target = null;
    double d = Double.MAX_VALUE;
  
    for (int i = 0; i < 4; ++i) {
      Wizard wizard = Player.state.wizards[i];
  
      if ((last != null && last.id == wizard.id) || wizard.team.id == ignore[0] || wizard.team.id == ignore[1]) {
        continue;
      }
  
      double d2 = position.squareDistance(wizard.position);
  
      if (target == null || d2 < d) {
        d = d2;
        target = wizard;
      }
    }
  
    if (target != null) {
      thrust(1000.0, target.position.x, target.position.y, Math.sqrt(d));
    }
  
    ignore[0] = -1;
    ignore[1] = -1;
  }

}
