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
    Player.init(0);
    
    createUnit(0, "WIZARD", 15170, 6558, -11, 375, 0);
    createUnit(1, "WIZARD", 15532, 3342, 12, -35, 0);
    createUnit(2, "OPPONENT_WIZARD", 14719, 1829, 25, 61, 0);
    createUnit(3, "OPPONENT_WIZARD", 9280, 2021, -198, 35, 0);
    createUnit(6, "SNAFFLE", 7429, 2372, -130, 23, 0);
    createUnit(7, "SNAFFLE", 15983, 3379, 0, 0, 0);
    createUnit(11, "BLUDGER", 15092, 4952, 50, 177, 0);
    createUnit(12, "BLUDGER", 13582, 2231, -36, -157, 3);    

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
    AG.evolution();
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
