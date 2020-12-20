package fantasticBitsMulti;

import java.util.Scanner;

import fantasticBitsMulti.spells.Spell;
import fantasticBitsMulti.units.Bludger;
import fantasticBitsMulti.units.EntityType;
import fantasticBitsMulti.units.Snaffle;
import fantasticBitsMulti.units.Unit;
import fantasticBitsMulti.units.Wizard;

public class State {
  public int unitsFE = 0;
  public Unit[] units = new Unit[20];
  public Unit unitsById[] = new Unit[24];
  
  public Wizard[] wizards = new Wizard[4];

  public Bludger[] bludgers = new Bludger[2];
  private int bludgersFE;
  
  public Snaffle[] snaffles = new Snaffle[10];
  public int snafflesFE;

  public Spell spells[] = new Spell[4*4];
  public Unit spellTargets[][] = new Unit[4][20];
  public int spellTargetsFE[] = new int[4];

  public TeamInfo[] teamInfos = new TeamInfo[2];
  {
    teamInfos[0] = new TeamInfo(0);
    teamInfos[1] = new TeamInfo(1);
  }
  
  
  public final int myTeam;
  
  
  public State(int myTeam) {
    this.myTeam = myTeam;
    createWizards();
    createBludgers();
    createPoles();
    unitsFE = 10;

    int spellsFE = 0;
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 4; ++j) {
        spells[spellsFE++] = wizards[j].spells[i];
      }
    }
  }


  public void read(Scanner in) {
    bludgersFE = 0;
    snafflesFE = 0;
    resetSnaffles();

    teamInfos[0].read(in);
    Player.updateStartAfter1stRead();
    teamInfos[1].read(in);
    
    int myWizzardFE = 0;
    int oppWizzardFE = 2;
    
    int entities = in.nextInt();
    TestOutputer.output(entities);
    for (int i = 0; i < entities; i++) {
      int id = in.nextInt();
      String entity = in.next();
      EntityType entityType = EntityType.valueOf(entity);
      int x = in.nextInt();
      int y = in.nextInt();
      int vx = in.nextInt();
      int vy = in.nextInt();
      int state = in.nextInt();
      
      // System.err.println("createUnit("+id+", \""+entity+"\", "+x+", "+y+", "+vx+", "+vy+", "+state+");");
      TestOutputer.output(id, entity, x, y, vx, vy, state);
      Unit unit = null;
      if (entityType == EntityType.WIZARD)  {
        unit = wizards[myWizzardFE++];
      } else if (entityType == EntityType.OPPONENT_WIZARD) {
        unit = wizards[oppWizzardFE++];
      } else if (entityType == EntityType.SNAFFLE) {
        if (Player.turn == 0) {
          unit = new Snaffle();
        } else {
          unit = unitsById[id];
        }
        units[unitsFE++] = unit;
        snaffles[snafflesFE++] = (Snaffle)unit;
      } else if (entityType == EntityType.BLUDGER) {
        unit = bludgers[bludgersFE++];
      }
      unit.update(id, x, y, vx, vy, state);
    }
    
    if (Player.turn == 0) {
      Player.victory = (snafflesFE / 2 ) + 1;
      affectUnitsToUnitsById();
    }

    // Mise à jour des carriers et des snaffles
    updateWizardsAndSnaffles();
    updateBludgersSpells();
    updatePetrificus();
    updateSnaffleSpells();
    

    for (int i = 0; i < 16; ++i) {
      spells[i].checkTarget();
    }

    backupState();
    for (int i=0;i<4;i++) {
      if (wizards[i].snaffle != null) {
        TestOutputer.outputCommand("Player.wizards["+i+"].snaffle"+wizards[i].snaffle.id);
      }
    }
    TestOutputer.outputCommand("Bludger 0 last is "+bludgers[0].last);
    TestOutputer.outputCommand("Bludger 1 last is "+bludgers[1].last);

  }
  
  
  public void backupState() {
    teamInfos[0].backup();
    teamInfos[1].backup();
    for (int i = 0; i < unitsFE; ++i) {
      units[i].save();
    }

    for (int i = 0; i < 16; ++i) {
      spells[i].save();
    }
  }
  
  public void restoreState() {
    teamInfos[0].restore();
    teamInfos[1].restore();
    
    for (int i = 0; i < unitsFE; ++i) {
      units[i].reset();
    }

    for (int i = 0; i < 16; ++i) {
      spells[i].reset();
    }
  }


  public void updateBludgersSpells() {
    // Bludgers pour tous les sorts
    for (int i = 0; i < 4; ++i) {
      spellTargets[i][0] = bludgers[0];
      spellTargets[i][1] = bludgers[1];
      spellTargetsFE[i] = 2;
    }    
  }

  private void createWizards() {
    wizards[0] = new Wizard(teamInfos[0]);
    wizards[1] = new Wizard(teamInfos[0]);
    wizards[2] = new Wizard(teamInfos[1]);
    wizards[3] = new Wizard(teamInfos[1]);
    units[0] = wizards[0];
    units[1] = wizards[1];
    units[2] = wizards[2];
    units[3] = wizards[3];
  }

  private void createBludgers() {
    bludgers[0] = new Bludger();
    bludgers[1] = new Bludger();
    units[4] = bludgers[0];
    units[5] = bludgers[1];
  }

  private void createPoles() {
    units[6] = Player.poles[0];
    units[7] = Player.poles[1];
    units[8] = Player.poles[2];
    units[9] = Player.poles[3];
  }

  public void updateWizardsAndSnaffles() {
    for (int i = 0; i < 4; ++i) {
      wizards[i].updateSnaffle();
    }
  }

  public void affectUnitsToUnitsById() {
    for (int i=0;i<unitsFE;++i) {
      unitsById[units[i].id] = units[i];
    }
  }

  private void resetSnaffles() {
    for (int i = 0; i < 24; ++i) {
      Unit u = unitsById[i];

      if (u != null && u.type == EntityType.SNAFFLE) {
        u.dead = true;
        u.carrier = null;
      }
    }
  }


  public void updateSnaffleSpells() {
    // Snaffles pour tous les sorts sauf obliviate
    for (int i = 1; i < 4; ++i) {
      for (int j = 0; j < snafflesFE; ++j) {
        spellTargets[i][spellTargetsFE[i]++] = snaffles[j];
      }
    }
  }

  public void updatePetrificus() {
    spellTargets[Spell.PETRIFICUS][spellTargetsFE[Spell.PETRIFICUS]++] = wizards[2];
    spellTargets[Spell.PETRIFICUS][spellTargetsFE[Spell.PETRIFICUS]++] = wizards[3];
    spellTargets[Spell.FLIPENDO][spellTargetsFE[Spell.FLIPENDO]++] = wizards[2];
    spellTargets[Spell.FLIPENDO][spellTargetsFE[Spell.FLIPENDO]++] = wizards[3];
  }


  public void goal(double x) {
    if (myTeam == 0) {
      if (x > 8000) {
        teamInfos[0].score += 1;
      } else {
        teamInfos[1].score += 1;
      }
    } else {
      if (x > 8000) {
        teamInfos[1].score += 1;
      } else {
        teamInfos[1].score += 1;
      }
    }
    
  }


  public void updateMana() {
    if (teamInfos[0].mana != 100) {
      teamInfos[0].mana += 1;
    }
    if (teamInfos[1].mana != 100) {
      teamInfos[1].mana += 1;
    }
    
  }

}
