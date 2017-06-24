package ww;

import java.util.Scanner;

public class Player {
  static GameState state = new GameState();

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    state.readInit(in);

    int round = 0;
    int s = 0;
    // game loop
    while (true) {
      state.readRound(in);
      round++;

      String best[] = new String[state.unitsPerPlayer];
      int bestScore[] = new int[state.unitsPerPlayer];
      
      for (int i = 0; i < state.unitsPerPlayer; i++) {
        int unitX = state.unitX[i];
        int unitY = state.unitY[i];

        bestScore[i] = Integer.MIN_VALUE;
        
        int actionsLeft = 0; // count possible actions for the agent, if too few, we force this agent to move (boost his score)
        for (Dir dir : Dir.values()) {
          int dirX = unitX+dir.dx;
          int dirY = unitY+dir.dy;
          if (!state.isValid(dirX, dirY)) continue;
          int deltaHeight = state.getHeight(dirX,dirY) - state.getHeight(unitX,unitY);
          if (deltaHeight > 1 ) continue;
        
          
          boolean onePushOrBuild = false;
          if (state.isOccupied(i, dirX, dirY)) {
            if (state.isFriendly(i, dirX, dirY)) continue;
            
            // Push&Build action score calculation :
            int moveScore = 0
                // juste get our current height as there is no score++ on push 
                + 20*state.getHeight(unitX, unitY); 
            
            for (Dir push : dir.pushDirections()) {
              int pushedX = dirX+push.dx;
              int pushedY = dirY+push.dy;
              if (!state.isValid(pushedX, pushedY)) continue;
              if (state.isOccupied(i, pushedX, pushedY)) continue;
              
              int deltaY = state.getHeight(dirX, dirY)-state.getHeight(pushedX, pushedY);
              if (deltaY < 0) continue;
        
              onePushOrBuild = true;
              int pushScore = 0
                  // just under 1 floor up for me so I prefer climbing, but 2 stairs fall is better
                  + 15 * deltaY
                  // If opp is on lvl 3 and we push it <= than lvl 1, it's a very good move
                  + (state.getHeight(dirX, dirY) == 3 ? 1000*Math.max(deltaY-1,0) : 0); 
              
              if ( pushScore > bestScore[i]) {
                bestScore[i] = pushScore;
                best[i] = "PUSH&BUILD "+i+" "+dir.toString()+" "+push.toString();
                System.err.println("for "+i+" pos = "+unitX+" "+unitY+" best is "+best[i]);
                System.err.println("Scores are  : move = "+moveScore+" , push= "+pushScore);
              }
            }
          } else {
            // Move&Build action score calculation :
            int moveScore = 0
                // our next height
                + 20*state.getHeight(dirX, dirY) 
                // if we go on a lvl 3 height next, it's a big bonus (score 1 point)
                + (state.getHeight(dirX, dirY) == 3 ? 1000 : 0);

            for (Dir dirBlock : Dir.values()) {
              int blockX = dirX+dirBlock.dx;
              int blockY = dirY+dirBlock.dy;
              if (!state.isValid(blockX, blockY)) continue;
              if (state.isOccupied(i, blockX, blockY)) continue;
              
              int deltaY = state.getHeight(blockX,blockY) + 1 - state.getHeight(dirX, dirY);
              onePushOrBuild=true;
              int buildScore = 0
                  // height of the next block we put
                  + state.getHeight(blockX,blockY) 
                  // Malus if we go to height 4
                  + (state.getHeight(blockX,blockY) == 3 ? -5 : 0)
                  // Malus if the block is higher than our next position
                  - (deltaY > 1 ? 100 : 0);
              
              if (moveScore + buildScore > bestScore[i]) {
                bestScore[i] = moveScore + buildScore;
                best[i]="MOVE&BUILD "+i+" "+dir.toString()+" "+dirBlock.toString();
                //System.err.println("for "+i+" pos = "+unitX+" "+unitY+" best is "+best[i]);
                //System.err.println("Scores are  : move = "+moveScore+" , build= "+buildScore);
              }
            }
          }
          if (onePushOrBuild) actionsLeft++;
        }
        System.err.println("Agent "+i+" has "+actionsLeft+" actions left");
        if (actionsLeft >0 && actionsLeft < 3) {
          bestScore[i] += 1_000_000; // force player to move
        }
      }

      if (bestScore[0] > bestScore[1]) {
        System.out.println(best[0]);
      } else {
        System.out.println(best[1]);
      }
      s++;
    }
  }
}