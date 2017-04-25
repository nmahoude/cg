package god.entities;

public class Zone extends Entity {
  int radius = 100;
  int drones[] = new int[4]; // drone count for each player
  int controlledBy; // id of player controlling the zone (0->3)
}
