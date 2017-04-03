package codeBusters;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import codeBusters.entities.Buster;
import codeBusters.entities.Ghost;

public class Player {

  public static void main(String args[]) {
      Scanner in = new Scanner(System.in);
      int bustersPerPlayer = in.nextInt(); // the amount of busters you control
      int ghostCount = in.nextInt(); // the amount of ghosts on the map
      int myTeamId = in.nextInt(); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right

      // game loop
      while (true) {
        List<Ghost> ghosts = new ArrayList<>();
        List<Buster> busters= new ArrayList<>();
        
          int entities = in.nextInt(); // the number of busters and ghosts visible to you
          for (int i = 0; i < entities; i++) {
              int entityId = in.nextInt(); // buster id or ghost id
              int x = in.nextInt();
              int y = in.nextInt(); // position of this buster / ghost
              int entityType = in.nextInt(); // the team id if it is a buster, -1 if it is a ghost.
              int state = in.nextInt(); // For busters: 0=idle, 1=carrying a ghost.
              int value = in.nextInt(); // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.
              
              if (entityType == -1) {
                Ghost ghost = new Ghost();
                ghost.id = entityId;
                ghost.position.x = x;
                ghost.position.y = y;
                ghosts.add(ghost);
              } else {
                Buster buster = new Buster();
                buster.team = entityType;
                buster.id = entityId;
                buster.position.x = x;
                buster.position.y = y;
                buster.state = state;
                buster.value = value;
              }
          }
          for (Buster buster : busters) {
            if (buster.team != myTeamId) continue;
            
            int minDist = Integer.MAX_VALUE;
            Ghost nearest;
            for (Ghost ghost : ghosts) {
            }
              // Write an action using System.out.println()
              // To debug: System.err.println("Debug messages...");

              System.out.println("MOVE 8000 4500"); // MOVE x y | BUST id | RELEASE
          }
      }
  }
}