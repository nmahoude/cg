package codeBusters;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import cgcollections.arrays.FastArray;
import codeBusters.entities.Buster;
import codeBusters.entities.Ghost;
import codeBusters.entities.State;
import codeBusters.entities.som.Defense;
import codeBusters.entities.som.Radar;
import codeBusters.entities.som.ReturnToBase;
import codeBusters.entities.som.TowerDefense;
import codeBusters.entities.som.Wander;

public class Player {
  public static final int WIDTH = 16000;
  public static final int HEIGHT = 9000;

  public static final int BASE_RANGE_2 = 1600 * 1600;
  public static final int FOG_DISTANCE_2 = 2200 * 2200;
  public static final int RANGE_TO_BUST_GHOST_2 = 1760 * 1760;
  public static final int BUSTER_RANGE_2 = 1760 * 1760;
  public static final int STUN_RANGE_2 = 1760 * 1760;
  
  public static Random rand = ThreadLocalRandom.current();
  public static int bustersPerPlayer;
  public static int ghostCount;
  public static int myTeamId;
  public static FastArray<Buster> myTeam;
  public static FastArray<Buster> hisTeam;
  public static FastArray<Ghost> ghosts;
  public static P myBase;
  public static P hisBase;
  public static int myScore;
  
  public static int[][] grid = new int[160][90]; // zone of 100;
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    bustersPerPlayer = in.nextInt();
    ghostCount = in.nextInt();
    myTeamId = in.nextInt();
    myBase = myTeamId == 0 ? new P(0,0) : new P(WIDTH, HEIGHT);
    hisBase = myTeamId == 1 ? new P(0,0) : new P(WIDTH, HEIGHT);
    
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
      if (i == 0) {
        buster.stateOfMind = new Radar(buster);
      } else {
        buster.stateOfMind = new Wander(buster);
      }
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
        
        System.err.println("read("+entityId+","+x+","+y+","+entityType+","+state+","+value+");");
        if (entityType == -1) {
          Ghost ghost = ghosts.elements[entityId];
          ghost.position.x = x;
          ghost.position.y = y;
          ghost.state = State.FREE;
          ghost.energy = state;
          ghost.bustersOnIt = value;
        } else {
          if (entityId >= bustersPerPlayer) entityId -=bustersPerPlayer;
          Buster buster = entityType == myTeamId ? myTeam.elements[entityId] : hisTeam.elements[entityId];
          buster.position.x = x;
          buster.position.y = y;
          if (state == 2 && buster.state != 2) {
            buster.stunned = 20; // newly stunned
          }
          buster.state = state;
          buster.value = value;
          if (state == 1 && value != -1) {
            Ghost carriedGhost = ghosts.elements[value];
            carriedGhost.state = State.BUSTED;
            carriedGhost.position.x = x;
            carriedGhost.position.y = y;
            buster.carried = carriedGhost;
          } else {
            buster.carried = Ghost.noGhost;
            if (state == 2) {
              buster.stunned = value;
              buster.stateOfMind = new Wander(buster);
            }
          }
        }
      }
      updateGridStatus();
      updateGhostsStatus();
      // debug
      int ghostInFog = 0;
      for (Ghost ghost : ghosts.elements) {
        if (ghost.state == State.IN_FOG) ghostInFog++;
      }
      System.err.println("Ghosts in fog : "+ghostInFog);
      
      if (myScore == ghostCount / 2 ) {
        Buster weGotGhost = null;
        for (Buster buster : myTeam.elements) {
          if (buster.carried != Ghost.noGhost) weGotGhost = buster;
        }
        if (weGotGhost == null) {
          for (Buster buster : myTeam.elements) {
            buster.stateOfMind = new TowerDefense(buster);
          }
        } else {
          // we got 
          for (Buster buster : myTeam.elements) {
            if (buster.carried == Ghost.noGhost) {
              buster.stateOfMind = new Defense(buster, weGotGhost);
            } else {
              buster.stateOfMind = new ReturnToBase(buster);
            }
          }
        }
      } else {
        for (Buster buster : myTeam.elements) {
          if (buster.stateOfMind.done == true) {
            buster.stateOfMind = new Wander(buster);
          }
          if (buster.state == 1 && !(buster.stateOfMind instanceof ReturnToBase)) {
            buster.stateOfMind = new ReturnToBase(buster);
          }
        }        
      }

      for (Buster buster : myTeam.elements) {
        System.out.println(buster.stateOfMind.output()); // MOVE x y | BUST id | RELEASE
      }
    }
  }

  private static void updateGridStatus() {
    for (Buster buster : myTeam.elements) {
      for (int x=0;x<160;x++) {
        for (int y=0;y<90;y++) {
          if ((x-buster.position.x) * (x-buster.position.x) + (y-buster.position.y) * (y-buster.position.y)  < FOG_DISTANCE_2) {
            grid[x][y] = 1; // seen
          }
        }
      }
    }
  }

  private static void updateGhostsStatus() {
    for (Buster buster : myTeam.elements) {
      for (Ghost ghost : ghosts.elements) {
        if (ghost.state == State.IN_FOG 
            && ghost.position.dist2(buster.position) < RANGE_TO_BUST_GHOST_2) {
          ghost.state = State.UNKNOWN; // it's not there anymore
        }
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
      if (buster.stun > 0) buster.stun--;
      if (buster.stunned > 0) buster.stunned--;
    }
  }

  private static void reinitGhostsState() {
    for (Ghost ghost : ghosts.elements) {
      if (ghost.state != State.UNKNOWN) {
        ghost.state = State.IN_FOG;
      }
    }
  }
}