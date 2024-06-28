package spring2023;

import fast.read.FastReader;
import spring2023.ais.AI;
import spring2023.ais.OldAI3;
import spring2023.map.Cell;
import spring2023.map.Map;

public class Player {



  public static boolean LOCAL_DEBUG = false;

  public static boolean DEBUG_AI = false;
  public static boolean DEBUG_ACC = false;
  public static boolean DEBUG_OPTIMIZER = false;
  public static boolean DEBUG_SIMULATION = false;
  
  public static State state;
  public static int turn = 0;
  public static AI ai = new OldAI3();

  public static long start;
  
  
  public static void main(String[] args) {
    FastReader in = new FastReader(System.in);
    
    new Player().play(in);
  }

  int beacons[] = new int[Map.MAX_CELLS];

  private void play(FastReader in) {
    State.readInit(in);
    this.state = new State();
    
    while(true) {
      turn++;
      state.read(in);
      start = System.currentTimeMillis();
      
      resetBeacons();
      
      long start = System.currentTimeMillis();
      ai.think(state, beacons);
      long end = System.currentTimeMillis();
      System.err.println("Thinking in "+(end-start)+" ms");
      
      String output = "";
      for (Cell c : state.cells) {
        if (beacons[c.index]> 0 ) {
          output += "BEACON "+c.index+" "+beacons[c.index]+";";
        }
      }
      
      if (output.isEmpty()) {
        System.out.println("WAIT");
      } else {
        System.out.println(output);
      }
    }
    
    
  }

  private void resetBeacons() {
    Action.reset();
    for (int i=0;i<Map.MAX_CELLS;i++) {
      beacons[i] = 0;
    }
  }

}
