package fantasticBitsMulti.spells;

import fantasticBitsMulti.units.Wizard;

public class Petrificus extends Spell {

  public Petrificus(Wizard caster) {
    super(PETRIFICUS, caster);
  }

  @Override
  public void effect() {
    target.vx = 0.0;
    target.vy = 0.0;
  }

  @Override
  public void print() {
  }

  
  
}
