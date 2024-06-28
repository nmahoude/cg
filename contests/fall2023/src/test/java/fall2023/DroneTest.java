package fall2023;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DroneTest {

  @Nested
  static class TurnsToSurface {
    @Test
    void justAbove() throws Exception {
      Drone drone = new Drone(0);
      drone.pos.y = 501;
      assertThat(drone.estimateTurnsToSurface()).isEqualTo(1);
    }
    
    @Test
    void twoTurnsAbove() throws Exception {
      Drone drone = new Drone(0);
      drone.pos.y = 1101;
      assertThat(drone.estimateTurnsToSurface()).isEqualTo(2);
    }
    
  }
}
