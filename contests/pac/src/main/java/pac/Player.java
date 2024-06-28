package pac;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import pac.agents.Pacman;
import pac.map.Map;
import pac.map.Pos;
import pac.minimax.Minimax;
import pac.simpleai.AI;

public class Player {
  public static boolean inGame;
  
  
  public static final boolean DEBUG         = true;
  public static boolean DEBUG_TU             = DEBUG & false;
  public static final boolean DEBUG_PAC_INFO = DEBUG & false;

  public static final boolean DEBUG_AI      = DEBUG & false;
  public static final boolean DEBUG_PELLETS = DEBUG & false;
  public static final boolean DEBUG_SEEING  = DEBUG & false;
  private static final boolean DEBUG_ORACLE  = DEBUG & false;
  public static final boolean DEBUG_ORACLE_POS  = DEBUG & false;
  public static final boolean DEBUG_PELLET_DECAY = DEBUG & false;
  public static final boolean DEBUG_PACMIND = DEBUG & false;
  public static final boolean DEBUG_FORBIDEN = DEBUG & false;
  public static boolean DEBUG_MINIMAX = DEBUG & false;
  private static final boolean DEBUG_PACMAN_MINIMAX_RESULT = false;
  public static final boolean DEBUG_DEADENDOPTIMIZER = DEBUG & false;
  
  
  public static final boolean BR_ENTER_DEADEND_ON_DANGER = false;
  public static final boolean BR_AVOID_CROSSING = false;
  public static final boolean BR_DECREMENT_PELLETS_VALUE = false;
  public static final boolean BR_REMOVE_UNREACHABLE_SUPERPELLETS = false;
	public static final boolean BR_REMOVE_PELLETS_FROM_HIDDEN_POSITIONS = true;
  
  public static long start;
  public static int turn = 0;
  public static Map map = new Map();
  public static Oracle oracle = new Oracle();
  public static Minimax minimax = new Minimax(4);
  public static Random random = ThreadLocalRandom.current();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    inGame = true;
    new Player().play(in);
  }

  public Player() {
  }
  
  
  public void play(Scanner in) {
    readGlobal(in);

    while (true) {
      readTurn(in);
    }
  }

	public void readTurn(Scanner in) {
		turn++;
		state.read(in);
		if (turn == 1) {
		  start+= 500;
		}
		oracle.update(state);
		//oracle.debugPotentialPosition(5+4);
		
		simpleAI.think();
		output = "";
		for (int i=0;i<5;i++) {
		  Pacman pacman = state.pacmen[i];
		  if (pacman.pos == Pos.INVALID) continue;
		  
		  output+=pacman.output()+" |";
		}
		System.err.println("Time : "+(System.currentTimeMillis() - start)+" ms");
		System.out.println(output);
		
	}

	public void readGlobal(Scanner in) {
		if (turn == 0) {
      state = new State();
      
      simpleAI = new AI(state);
  
      map.read(in);
      state.init();
    }
	}

  static final int PAC_INDEX = 0;
  static final int TURN = 300;


  private State state;


  private AI simpleAI;


  private String output;
  public static boolean debugAI(Pacman pacman) {
    return DEBUG_AI || (Player.turn >= TURN && pacman.index == PAC_INDEX);
  }
  public static boolean debugOracle() {
    return DEBUG_ORACLE ;//|| Player.turn >= TURN ;
  }

  public static boolean debugPacmanMinimaxResult() {
    return DEBUG_PACMAN_MINIMAX_RESULT || (Player.turn >= TURN);
  }
  
  public static boolean debugMinimax() {
    return DEBUG_MINIMAX; // || (Player.turn == TURN);
  }

  public static boolean debugBestPath(Pacman pacman) {
    return false; //pacman.index == 2;
  }

  public static boolean debugDFSOptimizer() {
    return turn >= TURN;
  }

  public String getOutputs() {
    return output;
  }

}
