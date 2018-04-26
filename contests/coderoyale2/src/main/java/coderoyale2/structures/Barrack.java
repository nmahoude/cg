package coderoyale2.structures;

import coderoyale2.Player;
import coderoyale2.units.Creep;
import coderoyale2.units.Site;

public class Barrack extends Structure {

  public int turnBeforeTrain;

  private int _turnBeforeTrain;

  public Barrack(Site site) {
    super(site);
  }
  
  public void backup() {
    super.backup();
    _turnBeforeTrain = turnBeforeTrain;
  }
  
  public void restore() {
    super.restore();
    turnBeforeTrain = _turnBeforeTrain;
  }

  public void onComplete() {
    int creepCount;
    if (this.subtype == Structure.KNIGHT) {
      creepCount = 4;
    } else  if (this.subtype == Structure.ARCHER) {
      creepCount = 2;
    } else {
      creepCount = 1;
    }
    
    for (int i=0;i<creepCount;i++) {
      Creep newCreep = (Creep)Player.all[Player.creepsFE++];
      newCreep.setType(this.subtype);
      newCreep.owner = owner;
      newCreep.location.x = this.attachedTo.location.x + i;
      newCreep.location.y = this.attachedTo.location.y + i;
      // TODO code it
      //it.location = it.location.towards(barracks.owner.enemyPlayer.queenUnit.location, 30.0)
    }
    // TODO code it
    //fixCollisions(allEntities())    
  }
}
