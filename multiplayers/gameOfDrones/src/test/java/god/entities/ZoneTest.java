package god.entities;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ZoneTest {

  @Test
  public void incomming() throws Exception {
    Zone zone = new Zone(0, 0);
    Drone drone1 = new Drone(1, 200, 200);
    drone1.update(180, 180);
    drone1.owner = 0;
    
    zone.allDronesInOrder.add(drone1);
    
    zone.updateDrones();
    
    assertThat(zone.incomming_drones[drone1.owner], is (1));
  }
}
