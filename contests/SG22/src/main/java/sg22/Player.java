package sg22;

import fast.read.FastReader;
import sg22.Actions.Action;
import sg22.ais.HardCodedAI;
import sg22.ais.SmartAI;
import sg22.nodes.NodeCache;

public class Player {
  public static final boolean DEBUG_POSSIBLE_MOVES = false;
  public static final boolean DEBUG_COLLISION = false;
  public static boolean DEBUG_RELEASES = false;
  
  
  
  private static final Simulator SIMULATOR = new Simulator();

  private static final int WARMUP_TIME = 600;



  
  public static int turn = 0;
  public static long start;
  public State state = new State();
//  public HardCodedAI ai = new HardCodedAI();
  public SmartAI ai = new SmartAI();
  public HardCodedAI hardCodedAi = new HardCodedAI();

  static int rememberThrowCount = 0;
  static int rememberGivecount= 0;
  static int rememberPlayCount = 0;
  static int rememberCardToGet = -1;
  public static boolean IMPlayerOne = true;
  
  public static void main(String[] args) {
    FastReader in = new FastReader(System.in);

    new Player().play(in);
  }

  private void play(FastReader in) {
    readGlobal(in);
    
    while(true) {
      NodeCache.reset();
      
      readTurn(in);
      
      think();
    }
  }

  private void think() {
    Action action = ai.think(state);
    
    // debug the result of the action ... 
    State w = new State();
    w.copyFrom(state);
    SIMULATOR.simulate(w, action);
    
    rememberThrowCount = w.throwCount;
    rememberGivecount = w.giveCount;
    rememberPlayCount = w.playCount;
    rememberCardToGet = w.cardToGet;
    
    
    
    System.err.println("Result expected : ");
    w.debugLite();
    
    System.err.println("My action : " + action);
    System.out.println(action);
    

//    System.err.println("****** HARDCODED ******");
//    Action hardCoded = hardCodedAi.think(state);
//    System.out.println(hardCoded);
    
  }

  public void readGlobal(FastReader in) {
    state.readGlobal(in);
  }
  
  public void readTurn(FastReader in) {
    Player.turn++;

    state.read(in);
    state.applyRemember(rememberThrowCount, rememberGivecount, rememberPlayCount, rememberCardToGet);
    if (state.agents[0].location == -1 && state.agents[1].location != -1) {
      IMPlayerOne = false;
    }
    state.readPossibleMoves(in); // to read them, but later

    rememberThrowCount = 0;
    rememberGivecount = 0;
    rememberPlayCount = 0;
    rememberCardToGet = -1;
        
    
    
    
    System.err.println("turn : "+turn);

    if (turn == 1) {
      Player.start = System.currentTimeMillis() + WARMUP_TIME;
    } else {
      Player.start = System.currentTimeMillis();
    }
  }
  
}
