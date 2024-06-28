package fall2023;

import fast.read.FastReader;

public class Player {
  // debugs
  public static final boolean DEBUG_TRIANGULATION = false;
  public static final boolean DEBUG_EXPECTED_POINTS = false;
  public static final boolean DEBUG_SIMULATION_UPDATE= false;
  public static final boolean DEBUG_SURFACE_ESTIMATE= false;
  public static final boolean DEBUG_CHECK_SYMMETRY = false;
  public static final boolean DEBUG_SCORE_MINIMAX = true;

  // fonctionnalités
  public static final boolean KILL_FISH_DISABLED = true;
  
  public static State state = new State();
  public static AIInterface ai = new AI();
  
  public static void main(String[] args) {
    FastReader in = new FastReader();
    
    State.setCanBeInitial();
    State.readInit(in);
    
    new Player().play(in);
  }


  private void play(FastReader in) {
    while(true) {
     
      State previous = state;
      state = new State();
      state.previousState = previous;
      state.read(in);
      
      Action[] actions = ai.think(state);
      
      Drone drone1 = state.myDrones[0];
      System.out.println(actions[drone1.id].output(drone1));
      
      Drone drone2 = state.myDrones[1];
      System.out.println(actions[drone2.id].output(drone2));
      
      state.saveOptionals();
      State.turn++;
    }
  }
}
