package god.algorithm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import god.entities.Drone;
import god.entities.Zone;

public class OptimalSubSetTest {

  @Test
  public void oneZone() throws Exception {
    Drone drone = new Drone(0, 0, 0);
    drone.owner = 0;
    Zone zone = new Zone(1000,1000);
    
    OptimalSubSet algo = new OptimalSubSet();
    List<ZoneInfo> result = algo.optimize(Arrays.asList(zone), Arrays.asList(drone), 100);
    
    assertThat(result.size(), is (1));
    ZoneInfo info = result.get(0);
    assertThat(info.zone, is(zone));
    assertThat(info.affectedDrones.size(), is(1));
    assertThat(info.affectedDrones.get(0), is(drone));
  }
  
  @Test
  public void twoZoneWithClearDrones() throws Exception {
    Drone drone = new Drone(0, 0, 0);
    Zone zone = new Zone(0,0);
    
    Drone drone2 = new Drone(1000, 1000, 0);
    Zone zone2 = new Zone(1000,1000);

    OptimalSubSet algo = new OptimalSubSet();
    List<ZoneInfo> result = algo.optimize(Arrays.asList(zone, zone2), Arrays.asList(drone, drone2), 100);
    
    assertThat(result.size(), is (2));
    ZoneInfo info = result.get(0);
    assertThat(info.zone, is(zone2));
    assertThat(info.affectedDrones.size(), is(1));
    assertThat(info.affectedDrones.get(0), is(drone2));

    info = result.get(1);
    assertThat(info.zone, is(zone));
    assertThat(info.affectedDrones.size(), is(1));
    assertThat(info.affectedDrones.get(0), is(drone));
}
  
  
}
