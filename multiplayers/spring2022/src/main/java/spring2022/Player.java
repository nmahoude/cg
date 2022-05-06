package spring2022;


import fast.read.FastReader;
import spring2022.ai.AI;

public class Player {
  private static final int WARMUP_TIME = 600;
  public static boolean inversed = false;
  
  public static int turn = 0;
  public static long start;
  
  public State state = new State();
  TerrainHistory terrain = new TerrainHistory();
  
  public static void main(String[] args) {
    FastReader in = new FastReader(System.in);

    new Player().play(in);
  }

  private void play(FastReader in) {
    readGlobal(in);
    
    while(true) {
      readTurn(in);
      
      think();
    }
  }

  private void think() {
    TriAction action = new AI().think(state);
    action.output();
  }

  public void readGlobal(FastReader in) {
    state.readGlobal(in);
  }
  
  public void readTurn(FastReader in) {
    UnitPool.reset();
    
    Player.turn++;
    state.read(in);
    System.err.println("turn "+ Integer.toString(turn));
  
    terrain.update(state);
    //terrain.debug();
    
    if (turn == 1) {
      Player.start = System.currentTimeMillis() + WARMUP_TIME;
    } else {
      Player.start = System.currentTimeMillis();
    }
  }
  
}
