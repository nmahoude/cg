package codeBusters;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import cgcollections.arrays.FastArray;
import codeBusters.ai.AI;
import codeBusters.entities.Buster;
import codeBusters.entities.Ghost;
import codeBusters.entities.MoveType;
import codeBusters.entities.State;

public class Player {
	public static final int WIDTH = 16000;
  public static final int HEIGHT = 9000;

  public static final P TEAM0_BASE = new P(0,0);
  public static final P TEAM1_BASE = new P(WIDTH, HEIGHT);
  public static final P CENTER = new P(WIDTH/2, HEIGHT/2);

  public static final int BASE_RANGE_2 = 1600 * 1600;
  public static final int FOG_DISTANCE_2 = 2200 * 2200;
  public static final int RANGE_TO_BUST_GHOST_2 = 1760 * 1760;
  public static final int RANGE_LIMIT_TO_BUST_GHOST_2 = 900 * 900;
  public static final int BUSTER_RANGE_2 = 1760 * 1760;
  public static final int STUN_RANGE = 1760;
  public static final int STUN_RANGE_2 =  STUN_RANGE * STUN_RANGE;
  public static final int MOVE_DISTANCE = 800;
  public static final int MOVE_DISTANCE_2 = MOVE_DISTANCE * MOVE_DISTANCE;
  private static final int RADAR_RANGE_2 = 4400 * 4400;
  
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
  public static Grid grid;
  
	public static int turn = 0;
  public static boolean seenAllGhost = false;
	public static boolean catchHalfGhost = false;
	
	public static Set<Ghost> ghostsToRescue = new HashSet<>();
	public static int myPoints = 0;
	
  public static void main(String args[]) {
  	grid = new Grid(100,WIDTH, HEIGHT);
  	
  	AI ai = new AI();
    Scanner in = new Scanner(System.in);
    init(in);
    
    // game loop
    while (true) {
    	
    	turn++;
      readTurn(in);
      
      // think
      
      updateGridStatus();
      updateGhostsStatus();

      //debugGhosts();
      debugBustersRadarZone();
      
      ai.think();
      
      for (Buster buster : myTeam.elements) {
      	if (buster.action.type == MoveType.STUN) {
      		buster.stunCooldown = 20;
      	}
      	if (buster.action.type == MoveType.RELEASE && buster.carried.isReleaseInBase()) {
      		myPoints++;
        	if (myPoints>= ghostCount / 2) {
        		System.err.println("Catch half the ghost, only one more to win !");
        		catchHalfGhost = true;
        	}
      		Ghost ghost = buster.carried;
      		ghost.position = P.NOWHERE;
      		ghost.state = State.BASE;
      	}
        buster.action.output();
      }
    }
  }

	private static void debugBustersRadarZone() {
		// debug the number of checkpoint seen by each buster
//		System.err.println("Debug checkpoints");
		for (Buster buster : myTeam) {
			int notseen = 0;
			int totalTurnNotSeen = 0;
			for (CheckPoint cp : grid.checkpoints) {
				if (cp.lastSeenTurn == turn) continue;
				int dist2 = cp.position.dist2(buster.position);
				if (dist2 <= Player.RADAR_RANGE_2 & dist2 >= Player.BUSTER_RANGE_2 ) {
					notseen++;
					totalTurnNotSeen+=(turn-cp.lastSeenTurn);
				}
			}
//			System.err.println("Infos for "+buster);
//			System.err.println("   checkpoints not seen this turn : "+notseen);
//			System.err.println("   checkpoints * last seen turn "+totalTurnNotSeen);
			buster.notSeenAround = totalTurnNotSeen;
			
			
		}
		
	}

	private static void debugGhosts() {
		int ghostInFog = 0;
		for (Ghost ghost : ghosts.elements) {
			System.err.println(ghost);
			ghost.onIt.stream().forEach(b -> System.err.println("  "+b));
		  if (ghost.state == State.IN_FOG) {
		  	ghostInFog++;
		  }
		}
		System.err.println("Ghosts in fog : "+ghostInFog);
	}

	private static void readTurn(Scanner in) {
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
		    ghost.position = new P(x, y);

		    ghost.lastSeenTurn = turn;

		    if (entityId != 0 && ghost.state== State.START) {
		    	int antipodId = entityId % 2 == 1 ? entityId+1 : entityId -1;
		    	Ghost antipod = ghosts.elements[antipodId];
		    	if (antipod.state == State.START) {
		    		antipod.state = State.IN_FOG;
		    		antipod.energy = state;
		    		antipod.position = new P (WIDTH-x, HEIGHT-y);
		    	}
		    }
		    ghost.state = State.FREE;
		    ghost.energy = state;
		    ghost.bustersOnIt = value;
		  } else {
		    if (entityId >= bustersPerPlayer) entityId -=bustersPerPlayer;
		    Buster buster = entityType == myTeamId ? myTeam.elements[entityId] : hisTeam.elements[entityId];
		    buster.position = new P(x, y);
		    
		    buster.lastSeenTurn = turn;
		    
		    if (state == 2 && buster.state != 2) {
		      buster.stunned = 20; // newly stunned
		    }
		    if (state == 3) {
		    	Ghost bustGhost = ghosts.get(value);
		    	bustGhost.onIt.add(buster);
		    }
		    buster.state = state;
		    buster.value = value;
		    if (state == 1 && value != -1) {
		      Ghost carriedGhost = ghosts.elements[value];
		      carriedGhost.state = State.BUSTED;
		      carriedGhost.position = buster.position;
		      carriedGhost.energy = 0;
		      buster.carried = carriedGhost;
		    } else {
		      buster.carried = Ghost.noGhost;
		      if (state == 2) {
		        buster.stunned = value;
		      }
		    }
		  }
		}
	}

	private static void init(Scanner in) {
		bustersPerPlayer = in.nextInt();
    ghostCount = in.nextInt();
    myTeamId = in.nextInt();
    myBase = myTeamId == 0 ? TEAM0_BASE : TEAM1_BASE;
    hisBase = myTeamId == 1 ? TEAM0_BASE : TEAM1_BASE;
    
    ghosts = new FastArray<>(Ghost.class, ghostCount);
    for (int i=0;i<ghostCount;i++) {
      Ghost ghost = new Ghost();
      ghost.state = State.START;
      ghost.id = i;
      ghosts.add(ghost);
    }
    myTeam = new FastArray<>(Buster.class, bustersPerPlayer);
    hisTeam = new FastArray<>(Buster.class, bustersPerPlayer);
    for (int i=0;i<bustersPerPlayer;i++) {
      Buster buster = new Buster();
      buster.id = i + (myTeamId == 1 ? bustersPerPlayer : 0);
      buster.team = myTeamId;
      myTeam.add(buster);
      
      buster = new Buster();
      buster.id = i + (myTeamId == 0 ? bustersPerPlayer : 0);
      buster.team = 1-myTeamId;
      hisTeam.add(buster);
    }
	}

  private static void updateGridStatus() {
  	grid.update(myTeam);
  }

  private static void updateGhostsStatus() {
		seenAllGhost = true;

		for (Ghost ghost : ghosts.elements) {
  		if (ghost.state == State.START) {
  			seenAllGhost = false;
  		}
  		
  		
  		if (ghost.state != State.FREE) {
  			ghostsToRescue.remove(ghost);
  		}
  		
  		
  		for (Buster buster : myTeam.elements) {
        if (ghost.state == State.IN_FOG 
            && ghost.position.dist2(buster.position) < RANGE_TO_BUST_GHOST_2) {
          ghost.state = State.UNKNOWN; // it's not there anymore
          ghost.position = P.NOWHERE;
        }
      }
    }
  }

  private static void reinitMyBuster() {
    for (Buster buster : myTeam.elements) {
      if (buster.stunCooldown > 0) buster.stunCooldown--;
      if (buster.stunned > 0) buster.stunned--;
    }
  }

  private static void reinitOpponentBusters() {
    for (Buster buster : hisTeam.elements) {
      buster.position = P.NOWHERE;
      if (buster.stunCooldown > 0) buster.stunCooldown--;
      if (buster.stunned > 0) buster.stunned--;
    }
  }

  private static void reinitGhostsState() {
    for (Ghost ghost : ghosts.elements) {
			ghost.onIt.clear();
			
      if (ghost.state != State.UNKNOWN && ghost.state != State.START) {
        ghost.state = State.IN_FOG;
      }
    }
  }
}