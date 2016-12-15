package fantasticBitsMulti.units;

import fantasticBitsMulti.Collision;
import fantasticBitsMulti.Player;
import fantasticBitsMulti.ag.AGSolution;
import fantasticBitsMulti.spells.Accio;
import fantasticBitsMulti.spells.Flipendo;
import fantasticBitsMulti.spells.Obliviate;
import fantasticBitsMulti.spells.Petrificus;
import fantasticBitsMulti.spells.Spell;
import trigonometry.Point;

public class Wizard extends Unit {

  public static Spell[] spells = new Spell[4];
  public int team;
  
  int sgrab;
  Snaffle ssnaffle;
  
  int spell;
  Unit spellTarget;

  public Wizard(int team) {
    super(EntityType.WIZARD, 400.0, 1, 0.75);
    this.team = team;

    snaffle = null;
    grab = 0;

    spells[Spell.OBLIVIATE] = new Obliviate(this);
    spells[Spell.PETRIFICUS] = new Petrificus(this);
    spells[Spell.ACCIO] = new Accio(this);
    spells[Spell.FLIPENDO] = new Flipendo(this);

    spellTarget = null;
  }

  public void grabSnaffle(Snaffle snaffle) {
    grab = 4;
    snaffle.carrier = this;
    this.snaffle = snaffle;

    // Stop the accio spell if we have one
    Spell accio = Wizard.spells[Spell.ACCIO];
    if (accio.duration != 0 && accio.target.id == snaffle.id) {
      accio.duration = 0;
      accio.target = null;
    }
  }
  
  public void apply(int move) {
    if (snaffle != null) {
      double coef = 500.0 * (1.0 / snaffle.mass); // TODO WHY NOT PRECALCULATE ?
      snaffle.vx += Player.cosAngles[move] * coef;
      snaffle.vy += Player.sinAngles[move] * coef;
    } else {
      vx += Player.cosAngles[move] * 150.0;
      vy += Player.sinAngles[move] * 150.0;
    }
  }
  
  
  public void output(int move, int spellTurn, int spell, Unit target) {
    if (spellTurn == 0 && spells[spell].duration == Spell.SPELL_DURATION[spell]) {
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

    if (Player.mana < cost || target.dead) {
      return false;
    }

    Player.mana -= cost;

    this.spell = spell;
    spellTarget = target;

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
    sgrab = grab;
    ssnaffle = snaffle;
  }

  public void reset() {
    super.reset();
    grab = sgrab;
    snaffle = ssnaffle;
  }

  public void bounce(Unit u) { 
    if (u.type == EntityType.SNAFFLE) {
      Snaffle target = (Snaffle) u;
      if (snaffle == null && grab != 0 && !target.dead && target.carrier == null) {
        grabSnaffle(target);
      }
    } else {
      if (u.type == EntityType.BLUDGER) {
        ((Bludger) u).last = this;
      }

      super.bounce(u);
    }
  }

  public void play() { 
    // Relacher le snaffle qu'on porte dans tous les cas
    if (snaffle != null) {
      snaffle.carrier = null;
      snaffle = null;
    }
  }
  
  @Override
  public void end()  { 
    super.end();

    if (grab != 0) {
      grab -= 1;

      if (grab == 0) {
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

    if (snaffle != null) {
      snaffle.position = this.position;
      snaffle.vx = vx;
      snaffle.vy = vy;
    }

    if (spellTarget != null) {
      spells[spell].cast(spellTarget);
      spellTarget = null;
    }
  }


  @Override
  public void print() { 
    System.err.print("Wizard " + id + " " + position + " " + vx + " " + vy + " " + speed() + " " + grab + " | ");

    if (snaffle != null) {
      System.err.print("Snaffle " + snaffle.id + " | ");
    }

    for (int i = 0; i < 4; ++i) {
      spells[i].print();
    }
    System.err.println("");
  }

  public void updateSnaffle() {
    if (state != 0) {
      for (int i = 0; i < Player.snafflesFE; ++i) {
        Snaffle snaffle = Player.snaffles[i];

        if (snaffle.position.equals(this.position) && snaffle.vx == vx && snaffle.vy == vy) {
          this.snaffle = snaffle;
          snaffle.carrier = this;
        }
      }

      grab = 3;
    } else {
      if (grab != 0) {
        grab -= 1;
      }
      snaffle = null;
    }
  }

  public void apply(AGSolution solution, int turn, int index) {
    if (index == 1) {
      if (solution.spellTurn1 == turn) {
        if (!Player.myWizard1.cast(solution.spell1, solution.spellTarget1)) {
          Player.myWizard1.apply(solution.moves1[turn]);
        }
      } else {
        Player.myWizard1.apply(solution.moves1[turn]);
      }
    } else {
      if (solution.spellTurn2 == turn) {
        if (!Player.myWizard2.cast(solution.spell2, solution.spellTarget2)) {
          Player.myWizard2.apply(solution.moves2[turn]);
        }
      } else {
        Player.myWizard2.apply(solution.moves2[turn]);
      }
    }
  }

}
