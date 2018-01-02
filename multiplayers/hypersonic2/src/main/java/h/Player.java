package h;

import java.util.Scanner;

import h.entities.Agent;
import h.simulation.Simulation;

public class Player {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int width = in.nextInt();
    int height = in.nextInt();
    int myId = in.nextInt();

    GameState state = new GameState();
    
    // game loop
    while (true) {
      state.init();
      state.readBoard(in);
      
      int entities = in.nextInt();
      for (int i = 0; i < entities; i++) {
        int entityType = in.nextInt();
        int owner = in.nextInt();
        int x = in.nextInt();
        int y = in.nextInt();
        int param1 = in.nextInt();
        int param2 = in.nextInt();

        Cell cell = state.cells[x][y];
        
        if (entityType == 0) {
          // agent
          state.agentsFE++;
          Agent agent = state.agents[owner];
          agent.bombLeft = param1;
          agent.bombRange = param2;
          agent.setPosition(cell);
          if (myId == owner) {
            state.me = agent;
          }
        } else if (entityType == 1) {
          // bomb
          state.addBomb(x, y, owner, param1, param2);
        } else {
          // item
          if (param1 == 1) {
            cell.flagExtraRangeItem();
          } else {
            cell.flagExtraBombItem();
          }
        }
      }
      state.backup();
      
      //debugExplodingBombsPrediction(state);
      Simulation simulation = new Simulation(state);
      simulation.simulate();
      
      System.out.println("MOVE 6 5");
    }
  }

  private static void debugExplodingBombsPrediction(GameState state) {
    for (int i=0;i<8;i++) {
      int number = state.bombsCalendarFE[i];
      System.err.println("bombs exploding at t="+i+" => "+number);
    }
  }
}
