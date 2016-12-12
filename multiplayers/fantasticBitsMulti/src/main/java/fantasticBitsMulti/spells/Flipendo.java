package fantasticBitsMulti.spells;

import fantasticBitsMulti.units.Wizard;

public class Flipendo extends Spell {

  public Flipendo(Wizard caster) {
    super(FLIPENDO, caster);
  }

  @Override
  public void effect() {
    double d = caster.position.distTo(target.position);

    if (d < 10.0) {
      return;
    }

    double dcoef = d*0.001;
    double power = 6000.0 / (dcoef*dcoef);

    if (power > 1000.0) {
      power = 1000.0;
    }

    dcoef = 1.0 / d;
    power = power / target.mass;
    target.vx += dcoef * power * (target.position.x - caster.position.x);
    target.vy += dcoef * power * (target.position.y - caster.position.y);
  }

  @Override
  public void print() {
    // TODO Auto-generated method stub
    
  }

}
