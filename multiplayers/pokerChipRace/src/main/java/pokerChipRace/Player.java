package pokerChipRace;

import java.util.Scanner;

import cgcollections.arrays.FastArray;
import cgutils.random.FastRandom;
import pokerChipRace.ai.AG;
import pokerChipRace.ai.AGSolution;
import pokerChipRace.entities.Entity;
import trigonometry.Vector;

public class Player {
  public static final boolean debug = false;
  public static final FastRandom rand = new FastRandom(System.currentTimeMillis());

  public static GameState state = new GameState();
  private static long start;
  
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
        state.getInitialChip(i).setDead();
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
        if (debug) {
          entity.debug();
        }
        if (owner == state.myId) {
          state.myChips.add(entity);
        }
      }

      // AG
      calculateAGDepth();
      AG ag = new AG();
      
      AGSolution best;
      best = ag.getSolutionAG(state, round == 0 ? start+500 : start+130);
      if (debug) {
        best.debug();
      }
      for (int i=0;i<state.myChips.length;i++) {
        Entity myChip = state.myChips.elements[i];
        Vector dir = best.angleToDir(0, i);
        if (dir == null) {
          System.out.println("WAIT");
        } else {
          System.out.println(""+(int)(myChip.x + dir.vx)+" "+(int)(myChip.y + dir.vy));
        }
      }
    }
  }

  private static void calculateAGDepth() {
    int depth = 20;
    if (state.entityCount > 50) {
        depth = 10;
    } else if (state.entityCount > 40) {
        depth = 12;
    } else if (state.entityCount > 30) {
        depth = 16;
    }
    AGSolution.DEPTH = depth;
  }
}
