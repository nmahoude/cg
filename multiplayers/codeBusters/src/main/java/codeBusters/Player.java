package codeBusters;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import cgcollections.arrays.FastArray;
import codeBusters.entities.Buster;
import codeBusters.entities.Ghost;
import codeBusters.entities.State;
import codeBusters.entities.som.ReturnToBase;
import codeBusters.entities.som.Wander;

public class Player {
  public static final int BASE_RANGE_2 = 1600 * 1600;
  public static final int GHOST_RANGE_2 = 1760 * 1760;
  public static final int BUSTER_RANGE_2 = 1760 * 1760;
  
  public static Random rand = ThreadLocalRandom.current();
  public static int bustersPerPlayer;
  public static int ghostCount;
  public static int myTeamId;
  public static FastArray<Buster> myTeam;
  public static FastArray<Buster> hisTeam;
  public static FastArray<Ghost> ghosts;
  public static P myBase;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    bustersPerPlayer = in.nextInt();
    ghostCount = in.nextInt();
    myTeamId = in.nextInt();
    myBase = myTeamId == 0 ? new P(0,0) : new P(16000,9000);
    
    ghosts = new FastArray<>(Ghost.class, ghostCount);
    for (int i=0;i<ghostCount;i++) {
      Ghost ghost = new Ghost();
      ghost.state = State.UNKNOWN;
      ghost.id = i;
      ghosts.add(ghost);
    }
    myTeam = new FastArray<>(Buster.class, bustersPerPlayer);
    hisTeam = new FastArray<>(Buster.class, bustersPerPlayer);
    for (int i=0;i<bustersPerPlayer;i++) {
      Buster buster = new Buster();
      buster.id = i + (myTeamId == 1 ? bustersPerPlayer : 0);
      buster.team = myTeamId;
      buster.stateOfMind = new Wander(buster);
      myTeam.add(buster);
      buster = new Buster();
      buster.id = i + (myTeamId == 0 ? bustersPerPlayer : 0);
      buster.team = 1-myTeamId;
      hisTeam.add(buster);
    }
    
    // game loop
    while (true) {
      int entities = in.nextInt(); // the number of busters and ghosts visible  to you
      reinitGhostsState();   
      reinitMyBuster();
      reinitOpponentBusters();
      
      for (int i = 0; i < entities; i++) {
        int entityId = in.nextInt(); // buster id or ghost id
        int x = in.nextInt();
        int y = in.nextInt(); // position of this buster / ghost
        int entityType = in.nextInt(); // the team id if it is a buster, -1 if it is a ghost.
                                       
        int state = in.nextInt(); // For busters: 0=idle, 1=carrying a ghost, 2 = stunned.
        int value = in.nextInt(); // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.
        
        if (entityType == -1) {
          Ghost ghost = ghosts.elements[entityId];
          ghost.position.x = x;
          ghost.position.y = y;
          ghost.state = State.FREE;
        } else {
          if (entityId >= bustersPerPlayer) entityId -=bustersPerPlayer;
          Buster buster = entityType == myTeamId ? myTeam.elements[entityId] : hisTeam.elements[entityId];
          buster.position.x = x;
          buster.position.y = y;
          if (state == 2 && buster.state != 2) {
            buster.stunned = 20;
          }
          buster.state = state;
          buster.value = value;
          if (state == 1 && value != -1) {
            buster.carried = ghosts.elements[value];
            buster.carried.state = State.BUSTED;
          } else {
            buster.carried = Ghost.noGhost;
            if (state == 2) {
              buster.stunned = value;
              buster.stateOfMind = new Wander(buster);
            }
          }
        }
      }
      
      // debug
      int ghostInFog = 0;
      for (Ghost ghost : ghosts.elements) {
        if (ghost.state == State.IN_FOG) ghostInFog++;
      }
      System.err.println("Ghosts in fog : "+ghostInFog);
      
      for (Buster buster : myTeam.elements) {
        if (buster.state == 1 && !(buster.stateOfMind instanceof ReturnToBase)) {
          buster.stateOfMind = new ReturnToBase(buster);
        }
        int minDist = Integer.MAX_VALUE;
        Ghost nearest;
        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(buster.stateOfMind.output()); // MOVE x y | BUST id | RELEASE
      }
    }
  }

  private static void reinitMyBuster() {
    for (Buster buster : myTeam.elements) {
      if (buster.stun > 0) buster.stun--;
      if (buster.stunned > 0) buster.stunned--;
    }
  }

  private static void reinitOpponentBusters() {
    for (Buster buster : hisTeam.elements) {
      buster.position.x = -1;
    }
  }

  private static void reinitGhostsState() {
    for (Ghost ghost : ghosts.elements) {
      if (ghost.state == State.FREE) {
        ghost.state = State.IN_FOG;
      }
      ghost.state = State.UNKNOWN; // TODO ne pas etre aussi brutal
    }
  }
}