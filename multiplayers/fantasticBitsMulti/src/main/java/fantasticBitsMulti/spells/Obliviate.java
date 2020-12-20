package fantasticBitsMulti.spells;

import fantasticBitsMulti.units.Bludger;
import fantasticBitsMulti.units.Wizard;

public class Obliviate extends Spell {

  public Obliviate(Wizard caster) {
    super(OBLIVIATE, caster);
  }

  @Override
  public void effect() {
    ((Bludger)target).ignore[caster.team.id] = caster.team.id;
  }
}
