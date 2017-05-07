package pokerChipRace;

import java.util.Scanner;

import cgcollections.arrays.FastArray;
import cgutils.random.FastRandom;
import pokerChipRace.ai.AG;
import pokerChipRace.ai.AGSolution;
import pokerChipRace.entities.Entity;
import trigonometry.Vector;

public class Player {
  public static GameState state = new GameState();
  private static long start;
  public static final FastRandom rand = new FastRandom(17);
  
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

      // reset all entities
      for (int i=0;i<state.entityFE;i++) {
        state.getInitialChip(i).radius = -1;
      }
      int playerChipCount = in.nextInt();
      start = System.currentTimeMillis();
      in.nextLine();
      state.entityCount = in.nextInt(); // The total number of entities on the
                      
      state.myChips =  new FastArray<>(Entity.class, playerChipCount);
      
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

        entity = state.getInitialChip(id);
        entity.update(owner, x, y, radius, vx, vy);
        state.playerCount = Math.max(state.playerCount, owner+1);
        //entity.debug();
        if (owner == state.myId) {
          state.myChips.add(entity);
        }
      }

      // AG
      AG ag = new AG();
      
      AGSolution sol = ag.getSolution(state, start+140);
      
      for (int i=0;i<state.myChips.length;i++) {
        Entity myChip = state.myChips.elements[i];
        Vector dir = sol.angleToDir(i);
        if (dir == null) {
          System.out.println("WAIT");
        } else {
          System.out.println(""+(int)(myChip.x + dir.vx)+" "+(int)(myChip.y + dir.vy));
        }
      }
    }
  }
}
