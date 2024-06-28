package spring2022;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ControlOracleTest {

  
  @Test
  void simpleRecalculate() throws Exception {
    
    ControlOracle oracle = new ControlOracle();
    
    Unit unit = new Unit();
    unit.id = 0;
    unit.pos.copyFrom(2637,6946);
    unit.speed.set(396,54);
    
    oracle.updatePos(0, unit, List.of(new Pos(17630, 9000)));
    
    assertThat(oracle.positions[0]).isEqualTo(new Pos(3034, 7001));
    assertThat(oracle.vx[0]).isEqualTo(396);
    assertThat(oracle.vy[0]).isEqualTo(54);
    
  }
}
