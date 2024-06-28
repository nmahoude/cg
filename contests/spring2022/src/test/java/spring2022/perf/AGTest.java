package spring2022.perf;

import fast.read.FastReader;
import spring2022.Player;
import spring2022.State;
import spring2022.ag.AG;

public class AGTest {
  static AG ag = new AG();
  
  public static void main(String[] args) {
    // PErF ! 
    
    
    State state = new State();
    state.readGlobal(FastReader.fromString("0 0 3 "));
    state.read(FastReader.fromString("""
^3 304 3 536
^14
^0 1 6053 4062 0 0 -1 -1 -1 -1 -1
^1 1 2828 5652 0 0 -1 -1 -1 -1 -1
^2 1 13000 4975 0 0 -1 -1 -1 -1 -1
^3 2 11722 4773 0 1 -1 -1 -1 -1 -1
^4 2 12087 5502 0 0 -1 -1 -1 -1 -1
^95 0 6251 4427 0 0 5 -321 -237 0 1
^102 0 11135 5495 0 0 2 240 319 0 2
^103 0 6637 3202 0 0 12 -240 -319 0 1
^106 0 4821 6444 0 0 2 -274 291 0 0
^109 0 5560 3501 0 0 8 -163 -365 0 1
^111 0 2569 6235 0 0 19 -347 -198 0 1
^113 0 7425 3103 0 0 23 145 -372 0 0
^114 0 11155 3829 0 0 23 180 356 0 2
^115 0 6475 5171 0 0 23 -180 -356 0 1
        """.replace("^", "") ));
    
    // warmup
    for (int i=0;i<200;i++) {
      Player.start = System.currentTimeMillis();
      ag.think(state);
    }
    
    System.err.println("Warmup is finished, let's play now");
    
    for (int i=0;i<10_000;i++) {
      Player.start = System.currentTimeMillis();
      ag.think(state);
    }
    
    System.err.println("Finished");
    
  }
}
