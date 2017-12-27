package meanmax.entities;

import meanmax.Game;

public class SkillEffect extends Entity {
  static int DESTROYER_NITRO_GRENADE_POWER = 1000;
  static double REAPER_SKILL_MASS_BONUS = 10.0;
  
  public int duration;
  private int b_duration;

  public SkillEffect() {
    super(Game.SKILL_EFFECT_TAR, 0.0, 0.0, false);
  }

  @Override
  public void backup() {
    b_duration = duration;
    super.backup();
  }
  @Override
  public void restore() {
    duration = b_duration;
    super.restore();
  }
  public void apply() {
    duration -= 1;
    if (duration <=0 ) dead = true;

    if (type == Game.SKILL_EFFECT_TAR) {
      for (int i=0;i<Game.entities_FE;i++) {
        Entity entity = Game.entities[i];
        if (entity.dead) continue;
        if (isInRange(entity, radius + entity.radius)) {
          entity.mass += REAPER_SKILL_MASS_BONUS;
        }
      }
    } else if (type == Game.SKILL_EFFECT_GRENADE) {
      for (int i=0;i<Game.entities_FE;i++) {
        Entity entity = Game.entities[i];
        if (entity.dead) continue;
        if (entity.type == Game.WRECK) continue;
        if (isInRange(entity, radius + entity.radius)) {
          entity.thrust(this.position, -DESTROYER_NITRO_GRENADE_POWER);
        }
      }
    } else if (type == Game.SKILL_EFFECT_OIL) {
      // later (in the simulation
    }
  }

  void readExtra(int extra, int extra2) {
    duration = extra;
  }
}
