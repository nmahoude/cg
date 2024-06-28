package coif.units;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class UnitTest {

  @Test
  public void soldier1_canNOT_kill_soldier1() throws Exception {
    Unit soldier2 = buildSoldier(UnitType.SOLDIER_1);
    Unit soldier1 = buildSoldier(UnitType.SOLDIER_1);
    
    assertThat(soldier2.canKill(soldier1)).isFalse();
  }

  
  private Unit buildSoldier(UnitType type) {
    Unit unit = new Unit();
    unit.type = type;
    return unit;
  }


  @Test
  public void soldier2_can_kill_soldier1() throws Exception {
    Unit soldier1 = buildSoldier(UnitType.SOLDIER_1);
    Unit soldier2 = buildSoldier(UnitType.SOLDIER_2);
    
    assertThat(soldier2.canKill(soldier1)).isTrue();
  }
}
