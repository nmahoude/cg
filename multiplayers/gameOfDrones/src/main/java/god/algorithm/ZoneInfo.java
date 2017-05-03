package god.algorithm;

import java.util.ArrayList;
import java.util.List;

import god.entities.Drone;
import god.entities.Zone;

public class ZoneInfo {
  public Zone zone;
  public List<Drone> affectedDrones = new ArrayList<>();
  int tta;    // time of arrival for last drone 
  int points; // points until the end (n² - tta²)  /2
}
