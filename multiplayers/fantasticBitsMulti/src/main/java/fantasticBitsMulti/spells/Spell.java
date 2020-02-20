package fantasticBitsMulti.spells;

import fantasticBitsMulti.units.Unit;
import fantasticBitsMulti.units.Wizard;

public abstract class Spell {
  public static final int OBLIVIATE = 0;
  public static final int PETRIFICUS = 1;
  public static final int ACCIO = 2;
  public static final int FLIPENDO = 3;
  
  public static int SPELL_DURATION[] = new int[] { 3, 1, 6, 3};
  public static int SPELL_COST[] = new int[] { 5, 10, 20 ,20};
  
  public Wizard caster;
  public int duration;
  public Unit target;
  int type;
  
  int sduration;
  Unit starget;
  
  public Spell(int type, Wizard caster) {
    this.type = type;
    this.caster = caster;
    this.duration = 0;
  }

  public void reset() {
    duration = sduration;
    target = starget;
  }

  public abstract void effect();

  public void print() {
    if (duration > 0) {
      String typeAsStr = "";
      switch (type) {
        case OBLIVIATE:
          typeAsStr = "OBLIVIATE";
          break;
        case FLIPENDO:
          typeAsStr = "FLIPENDO";
          break;
        case ACCIO:
          typeAsStr = "ACCIO";
          break;
        case PETRIFICUS:
          typeAsStr = "PETRIFICUS";
          break;
          
      }
      System.err.println(""+type+" " + target.id + " " + duration + " | ");
    }

  }
 
  public void cast(Unit target) {
    this.target = target;
    duration = SPELL_DURATION[type];
  }

  public void apply() {
    if (duration != 0) {
      duration -= 1;
      if (!target.dead) {
        effect();
      }
    }
  }

  public void save() {
    sduration = duration;
    starget = target;
  }

  public void checkTarget() {
    if (duration != 0 || target == null || target.dead) {
      cancelSpell();
    }
  }

  private void cancelSpell() {
    target = null;
    duration = 0;
  }
}
