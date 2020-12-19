package fantasticBitsMulti.units;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.simulation.Action;
import fantasticBitsMulti.simulation.Collision;
import fantasticBitsMulti.spells.Accio;
import fantasticBitsMulti.spells.Flipendo;
import fantasticBitsMulti.spells.Obliviate;
import fantasticBitsMulti.spells.Petrificus;
import fantasticBitsMulti.spells.Spell;

public class Wizard extends Unit {

  private static final double WIZZARD_MOVE_COEFF = 150.0;
  private static final double SNAFFLE_MOVE_COEFF = 500.0 * (1.0 / 0.5/*snaffle mass*/);
  public Spell[] spells = new Spell[4];
  public int team;
  
  int sgrab;
  Snaffle ssnaffle;
  
  public Wizard(int team) {
    super(EntityType.WIZARD, 400.0, 1, 0.75);
    this.team = team;

    snaffle = null;
    turnsBeforeGrabbingAgain = 0;

    spells[Spell.OBLIVIATE] = new Obliviate(this);
    spells[Spell.PETRIFICUS] = new Petrificus(this);
    spells[Spell.ACCIO] = new Accio(this);
    spells[Spell.FLIPENDO] = new Flipendo(this);

  }

  public void grabSnaffle(Snaffle snaffle) {
    turnsBeforeGrabbingAgain = 4;
    snaffle.carrier = this;
    this.snaffle = snaffle;

    // Stop the accio spell if we have one
    Spell accio = spells[Spell.ACCIO];
    if (accio.duration != 0 && accio.target.id == snaffle.id) {
      accio.duration = 0;
      accio.target = null;
    }
  }
  
  public void apply(Action action) {
    if (Player.DEBUG_SIM) {
      System.err.println("Wizard "+id+" action : "+action);
    }
    if (snaffle != null) {
      if (action.type == Action.TYPE_THROW) {
        snaffle.vx += action.cosAngle * action.thrust;
        snaffle.vy += action.sinAngle * action.thrust;
      }
      
      snaffle.carrier = null;
      snaffle = null;
      turnsBeforeGrabbingAgain = 4; // TODO or 3 ?
    }

    if (action.type == Action.TYPE_CAST) {
      cast(action.spellId, action.target);
    } else if (action.type == Action.TYPE_MOVE) {
      vx += action.cosAngle * action.thrust;
      vy += action.sinAngle * action.thrust;
    }
  }

  public void output(int move, int spellTurn, int spell, Unit target) {
    if (spellTurn == 0) {
      if (spell == Spell.OBLIVIATE) {
        System.out.print("OBLIVIATE ");
      } else if (spell == Spell.PETRIFICUS) {
        System.out.print("PETRIFICUS ");
      } else if (spell == Spell.ACCIO) {
        System.out.print("ACCIO ");
      } else if (spell == Spell.FLIPENDO) {
        System.out.print("FLIPENDO ");
      }

      System.out.println(""+target.id);
      return;
    }

    // Adjust the targeted point for this angle
    // Find a point with the good angle
    double px = position.x + Player.cosAngles[move] * 10000.0;
    double py = position.y + Player.sinAngles[move] * 10000.0;

    if (snaffle != null) {
      System.out.println("THROW " +Math.round(px) +" " + Math.round(py) + " 500");
    } else {
      System.out.println("MOVE " + Math.round(px) + " " + Math.round(py) + " 150");
    }
  }

  public boolean cast(int spell, Unit target) {
    int cost = Spell.SPELL_COST[spell];

    if (Player.myMana < cost || target.dead) {
      return false;
    }

    Player.myMana -= cost;

    spells[spell].cast(target);

    return true;
  }
  
  @Override
  public Collision collision(Unit u, double from) {
    if (u.type == EntityType.SNAFFLE) {
      u.radius = -1.0;
      Collision result = super.collision(u, from);
      u.radius = 150.0;

      return result;
    } else {
      return super.collision(u, from);
    }
  }

  public void save() {
    super.save();
    sgrab = turnsBeforeGrabbingAgain;
    ssnaffle = snaffle;
  }

  public void reset() {
    super.reset();
    turnsBeforeGrabbingAgain = sgrab;
    snaffle = ssnaffle;
  }

  public void bounce(Unit u) { 
    if (u.type == EntityType.SNAFFLE) {
      Snaffle target = (Snaffle) u;
      if (snaffle == null && turnsBeforeGrabbingAgain == 0 && !target.dead && target.carrier == null) {
        grabSnaffle(target);
      }
    } else {
      if (u.type == EntityType.BLUDGER) {
        ((Bludger) u).last = this;
      }

      super.bounce(u);
    }
  }

  @Override
  public void end()  { 
    super.end();

    if (turnsBeforeGrabbingAgain != 0) {
      turnsBeforeGrabbingAgain --;

      if (turnsBeforeGrabbingAgain == 0) {
            // Check if we can grab a snaffle
        for (int i = 0; i < Player.snafflesFE; ++i) {
          Snaffle snaffle = Player.snaffles[i];

          if (!snaffle.dead && snaffle.carrier == null && this.position.squareDistance(snaffle.position) < 159201.0) {
            grabSnaffle(snaffle);
            break;
          }
        }
      }
    }
  }


  @Override
  public void print() { 
    System.err.print("Wizard " + id + " " + position + " " + vx + " " + vy + " " + speed() + " " + turnsBeforeGrabbingAgain + " | ");

    if (snaffle != null) {
      System.err.print("Snaffle " + snaffle.id + " | ");
    }

    for (int i = 0; i < 4; ++i) {
      spells[i].print();
    }
    System.err.println("");
  }

  public void updateSnaffle() {
    if (state != 0 /* just grab a snaffle */) {
      for (int i = 0; i < Player.snafflesFE; ++i) {
        Snaffle snaffle = Player.snaffles[i];

        if (snaffle.position.equals(this.position) && snaffle.vx == vx && snaffle.vy == vy) {
          this.snaffle = snaffle;
          snaffle.carrier = this;
          break;
        }
      }

      turnsBeforeGrabbingAgain = 3; // grab for 3 turns
    } else {
      if (turnsBeforeGrabbingAgain > 0) {
        turnsBeforeGrabbingAgain -= 1;
      }
      snaffle = null; // in any case the wizard drop the snaffle
    }
  }

}
