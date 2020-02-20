package fantasticBitsMulti.simulation;

import org.junit.Test;

import fantasticBitsMulti.Player;
import fantasticBitsMulti.ag.AG;
import fantasticBitsMulti.ag.AGSolution;
import fantasticBitsMulti.units.EntityType;
import fantasticBitsMulti.units.Snaffle;
import fantasticBitsMulti.units.Unit;

public class SimulationTest {

  @Test
  public void test1() throws Exception {
    Player.myMana = 100;
    Player.init(1);
    
    createUnit(2, "WIZARD", 1141, 1991, -51, -2, 1);
    createUnit(3, "WIZARD", 1555, 1059, 24, 301, 0);
    createUnit(0, "OPPONENT_WIZARD", 1147, 5093, 517, -576, 0);
    createUnit(1, "OPPONENT_WIZARD", 8852, 3459, -409, 134, 0);
    createUnit(6, "SNAFFLE", 1141, 1991, -51, -2, 1);
    createUnit(7, "SNAFFLE", 5180, 5640, 109, -13, 0);
    createUnit(8, "SNAFFLE", 2469, 7002, 0, 0, 0);
    createUnit(9, "BLUDGER", 1294, 6299, -406, -283, 0);
    createUnit(10, "BLUDGER", 1879, 5955, -425, -15, 0);

    if (Player.turn == 0) {
      Player.victory = (Player.snafflesFE / 2 ) + 1;
      Player.affectUnitsToUnitsById();
    }

    // Mise à jour des carriers et des snaffles
    Player.updateWizardsAndSnaffles();
    Player.updateBludgersSpells();
    Player.updatePetrificus();
    Player.updateSnaffleSpells();
    
    for (int i = 0; i < Player.unitsFE; ++i) {
      Player.units[i].save();
    }
    Simulation.save();

    for (int i = 0; i < 16; ++i) {
      Player.spells[i].checkTarget();
      Player.spells[i].save();
    }

    Player.start = System.nanoTime();
    AGSolution evolution = AG.evolution();
    
    System.err.println("After evolution : " + evolution.moves2[0]);
  }

  private void createUnit(int id, String entityTypeStr, int x, int y, int vx, int vy, int state) {
    EntityType entityType = EntityType.valueOf(entityTypeStr);
    Unit unit = null;
    int bludgersFE=0;
    
    if (entityType == EntityType.WIZARD || entityType == EntityType.OPPONENT_WIZARD)  {
      unit = Player.wizards[id];
    } else if (entityType == EntityType.SNAFFLE) {
      unit = new Snaffle();
      Player.units[Player.unitsFE++] = unit;
      Player.snaffles[Player.snafflesFE++] = (Snaffle)unit;
    } else if (entityType == EntityType.BLUDGER) {
      unit = Player.bludgers[bludgersFE++];
    }
    unit.update(id, x, y, vx, vy, state);
    
  }
}
