package pokerChipRace;

import java.util.Scanner;

import cgcollections.arrays.FastArray;
import pokerChipRace.entities.Entity;

public class Player {
  public static GameState state = new GameState();

  public static void main(String[] args) {
    Player player = new Player();
    Entity entity = null;

    Scanner in = new Scanner(System.in);
    state.myId = in.nextInt(); // your id (0 to 4)
    in.nextLine();

    // game loop
    int round = 0;
    while (true) {
      round++;

      int playerChipCount = in.nextInt();
      in.nextLine();
      state.entityCount = in.nextInt(); // The total number of entities on the
                      
      state.myChips =  new FastArray<>(Entity.class, playerChipCount);
      state.allChips = new FastArray<>(Entity.class, state.entityCount);
      
      in.nextLine();

      for (int i = 0; i < state.entityCount; i++) {
        int id = in.nextInt(); // Unique identifier for this entity
        int owner = in.nextInt(); // The owner of this entity (-1 for neutral
                                  // droplets)
        float radius = in.nextFloat(); // the radius of this entity
        float x = in.nextFloat(); // the X coordinate (0 to 799)
        float y = in.nextFloat(); // the Y coordinate (0 to 514)
        float vx = in.nextFloat(); // the speed of this entity along the X axis
        float vy = in.nextFloat(); // the speed of this entity along the Y axis
        in.nextLine();

        entity = new Entity(id, owner);
        entity.update(x, y, radius, vx, vy);
        entity.debug();
        state.allChips.add(entity);
        if (owner == state.myId) {
          state.myChips.add(entity);
        }
        // update
        entity.update(x, y, radius, vx, vy);
      }

      for (Entity ent : state.myChips) {
        System.out.println("WAIT");
      }
    }
  }
}
